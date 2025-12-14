package tm.ilnar.delivery.adapters.in.http;

import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tm.ilnar.delivery.adapters.in.http.openapi.model.NewCourier;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommand;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommandHandler;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommand;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommandHandler;
import tm.ilnar.delivery.core.application.queries.CourierWithAssignedOrderQueryHandler;
import tm.ilnar.delivery.core.application.queries.GetNotCompletedOrdersQueryHandler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerTest {

    private final CreateCourierCommandHandler createCourierCommandHandler = mock(CreateCourierCommandHandler.class);
    private final CreateOrderCommandHandler createOrderCommandHandler = mock(CreateOrderCommandHandler.class);
    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler =
        mock(GetNotCompletedOrdersQueryHandler.class);
    private final CourierWithAssignedOrderQueryHandler courierWithAssignedOrderQueryHandler =
        mock(CourierWithAssignedOrderQueryHandler.class);
    private final Controller sut = new Controller(createCourierCommandHandler, createOrderCommandHandler,
        getNotCompletedOrdersQueryHandler, courierWithAssignedOrderQueryHandler);

    @Test
    void createCourier() {
        // arrange
        NewCourier model = new NewCourier("Ivan", 1);
        when(createCourierCommandHandler.handle(any(CreateCourierCommand.class)))
            .thenReturn(UnitResult.success());

        // act
        ResponseEntity<Void> response = sut.createCourier(model);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

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