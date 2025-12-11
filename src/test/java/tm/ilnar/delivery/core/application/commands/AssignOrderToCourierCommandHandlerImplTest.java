package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;
import tm.ilnar.delivery.core.domain.services.OrderDispatcher;
import tm.ilnar.delivery.core.domain.services.OrderDispatcherImpl;
import tm.ilnar.delivery.core.ports.CourierRepository;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AssignOrderToCourierCommandHandlerImplTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final OrderDispatcher orderDispatcher = new OrderDispatcherImpl();
    private final CourierRepository courierRepository = mock(CourierRepository.class);
    AssignOrderToCourierCommandHandler sut =
        new AssignOrderToCourierCommandHandlerImpl(orderRepository, orderDispatcher, courierRepository);

    @Test
    void handle() {
        // Arrange
        Order order = createOrder(7);
        when(orderRepository.findAnyByStatus(OrderStatus.CREATED)).thenReturn(Optional.of(order));
        Courier courier = createCourier();
        when(courierRepository.findAllWithFreeStorage()).thenReturn(List.of(courier));

        // Act
        UnitResult<Error> result = sut.handle();

        // Assert
        assertThat(result.isSuccess()).isTrue();
        verify(courierRepository).save(courier);
        verify(orderRepository).save(order);
    }

    private static Order createOrder(int volume) {
        Location location = Location.create(5, 5).getValue();
        return Order.create(UUID.randomUUID(), location, volume).getValue();
    }

    private static Courier createCourier() {
        Location location = Location.create(7, 7).getValue();
        return Courier.create("Ivan", 1, location).getValue();
    }
}