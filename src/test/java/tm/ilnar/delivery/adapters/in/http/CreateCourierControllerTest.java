package tm.ilnar.delivery.adapters.in.http;

import libs.errs.UnitResult;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import tm.ilnar.delivery.adapters.in.http.openapi.model.NewCourier;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommand;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommandHandler;

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
        NewCourier model = new NewCourier("Ivan", 1);
        when(createCourierCommandHandler.handle(any(CreateCourierCommand.class)))
                .thenReturn(UnitResult.success());

        // act
        ResponseEntity<Void> response = sut.createCourier(model);

        // assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}