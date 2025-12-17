package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.ports.GeoClient;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateOrderCommandHandlerImplTest {

    private final OrderRepository orderRepository = mock(OrderRepository.class);
    private final GeoClient geoClient = mock(GeoClient.class);
    private final CreateOrderCommandHandlerImpl sut = new CreateOrderCommandHandlerImpl(
        orderRepository, geoClient);

    @Test
    void shouldCreateNewOrderWhenOrderDoesNotExist() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        CreateOrderCommand command =
            CreateOrderCommand.create(orderId, "some_street", 3).getValue();

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());
        when(geoClient.getLocation("some_street")).thenReturn(Location.create(1, 2));

        // Act
        UnitResult<Error> result = sut.handle(command);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void shouldDoNothingWhenOrderAlreadyExists() {
        // Arrange
        UUID orderId = UUID.randomUUID();
        CreateOrderCommand command =
            CreateOrderCommand.create(orderId, "some_street", 3).getValue();

        Order existingOrder = mock(Order.class);
        when(orderRepository.findById(orderId)).thenReturn(Optional.of(existingOrder));

        // Act
        UnitResult<Error> result = sut.handle(command);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        verify(orderRepository, never()).save(any(Order.class));
    }
}