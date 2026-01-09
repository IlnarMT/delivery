package tm.ilnar.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tm.ilnar.delivery.adapters.in.http.openapi.model.CreateCourierResponse;
import tm.ilnar.delivery.adapters.in.http.openapi.model.NewCourier;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommand;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommandHandler;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.kernel.Speed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CreateCourierControllerTest {

    private final CreateCourierCommandHandler createCourierCommandHandler = mock(CreateCourierCommandHandler.class);
    private final CreateCourierController sut = new CreateCourierController(createCourierCommandHandler);

    @Test
    void createCourier() {
        // arrange
        NewCourier newCourier = new NewCourier("Ivan", 1);
        Result<Courier, Error> courierResult = Courier.create("Ivan", Speed.create(1).getValue(),
                Location.create(1, 1).getValue());
        when(createCourierCommandHandler.handle(any(CreateCourierCommand.class)))
                .thenReturn(courierResult);

        // act
        ResponseEntity<CreateCourierResponse> response = sut.createCourier(newCourier);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
    }
}