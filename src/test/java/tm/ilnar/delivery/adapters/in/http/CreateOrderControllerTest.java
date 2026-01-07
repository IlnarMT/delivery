package tm.ilnar.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tm.ilnar.delivery.adapters.in.http.openapi.model.CreateOrderResponse;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommand;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommandHandler;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateOrderControllerTest {

    private final CreateOrderCommandHandler createOrderCommandHandler = mock(CreateOrderCommandHandler.class);
    private final CreateOrderController sut = new CreateOrderController(createOrderCommandHandler);

    @Test
    void createOrder() {
        // arrange
        Result<Order, Error> orderResult = Order.create(UUID.randomUUID(), Location.create(1, 1).getValue(), 1);
        when(createOrderCommandHandler.handle(any(CreateOrderCommand.class)))
                .thenReturn(orderResult);

        // act
        ResponseEntity<CreateOrderResponse> response = sut.createOrder();

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}