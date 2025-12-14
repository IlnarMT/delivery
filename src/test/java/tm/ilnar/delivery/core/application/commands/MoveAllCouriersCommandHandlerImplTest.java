package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;
import tm.ilnar.delivery.core.ports.CourierRepository;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class MoveAllCouriersCommandHandlerImplTest {

    private final CourierRepository courierRepository = mock(CourierRepository.class);
    private final OrderRepository orderRepository = mock(OrderRepository.class);

    private final MoveAllCouriersCommandHandlerImpl sut =
        new MoveAllCouriersCommandHandlerImpl(courierRepository, orderRepository);

    @Test
    void shouldReturnSuccessWhenNoAssignedOrders() {
        // Arrange
        when(orderRepository.findAllByStatus(OrderStatus.ASSIGNED))
            .thenReturn(List.of());

        // Act
        UnitResult<Error> result = sut.handle();

        // Assert
        assertThat(result.isSuccess()).isTrue();
        verify(orderRepository, times(1)).findAllByStatus(OrderStatus.ASSIGNED);
        verifyNoInteractions(courierRepository);
    }

    @Test
    void shouldMoveCourierAndNotCompleteOrderWhenNotArrived() {
        // Arrange
        Courier courier = Courier.create("Ivan", 1, Location.create(7, 7).getValue()).getValue();
        Order order = Order.create(UUID.randomUUID(), Location.create(5, 5).getValue(), 7).getValue();;
        order.assign(courier.getId());

        when(orderRepository.findAllByStatus(OrderStatus.ASSIGNED))
            .thenReturn(List.of(order));
        when(courierRepository.findById(courier.getId()))
            .thenReturn(Optional.of(courier));

        // Act
        UnitResult<Error> result = sut.handle();

        // Assert
        assertThat(result.isSuccess()).isTrue();
        verify(orderRepository, times(1)).findAllByStatus(OrderStatus.ASSIGNED);
        verify(courierRepository, times(1)).findById(courier.getId());
        verify(courierRepository, times(1)).save(courier);
        verify(orderRepository, never()).save(order);
    }
}