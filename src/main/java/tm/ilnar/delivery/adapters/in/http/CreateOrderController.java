package tm.ilnar.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tm.ilnar.delivery.adapters.in.http.exception.BadRequestException;
import tm.ilnar.delivery.adapters.in.http.exception.ConflictException;
import tm.ilnar.delivery.adapters.in.http.openapi.api.CreateOrderApi;
import tm.ilnar.delivery.adapters.in.http.openapi.model.CreateOrderResponse;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommand;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommandHandler;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CreateOrderController implements CreateOrderApi {

    private final CreateOrderCommandHandler createOrderCommandHandler;

    @Override
    public ResponseEntity<CreateOrderResponse> createOrder() {
        Result<CreateOrderCommand, Error> commandResult =
                CreateOrderCommand.create(UUID.randomUUID(), "Айтишная", 2);

        if (commandResult.isFailure())
            throw new BadRequestException(commandResult.getError());
        var command = commandResult.getValue();

        var handleResult = createOrderCommandHandler.handle(command);
        if (handleResult.isFailure())
            throw new ConflictException(handleResult.getError());

        CreateOrderResponse response = new CreateOrderResponse();
        response.setOrderId(handleResult.getValue().getId());

        return ResponseEntity.ok(response);
    }
}
