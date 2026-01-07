package tm.ilnar.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tm.ilnar.delivery.adapters.in.http.exception.BadRequestException;
import tm.ilnar.delivery.adapters.in.http.exception.ConflictException;
import tm.ilnar.delivery.adapters.in.http.openapi.api.CreateCourierApi;
import tm.ilnar.delivery.adapters.in.http.openapi.model.NewCourier;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommand;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommandHandler;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreateCourierController implements CreateCourierApi {

    private final CreateCourierCommandHandler createCourierCommandHandler;

    @Override
    public ResponseEntity<Void> createCourier(NewCourier newCourier) {
        Result<CreateCourierCommand, Error> createCommandResult =
                CreateCourierCommand.create(newCourier.getName(), newCourier.getSpeed());

        if (createCommandResult.isFailure())
            throw new BadRequestException(createCommandResult.getError());

        var handleResult = createCourierCommandHandler.handle(createCommandResult.getValue());
        if (handleResult.isFailure())
            throw new ConflictException(handleResult.getError());

        return ResponseEntity.ok().build();
    }
}
