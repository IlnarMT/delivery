package tm.ilnar.delivery.adapters.in.http;

import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommand;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommandHandler;

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
        when(createOrderCommandHandler.handle(any(CreateOrderCommand.class)))
                .thenReturn(UnitResult.success());

        // act
        ResponseEntity<Void> response = sut.createOrder();

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}