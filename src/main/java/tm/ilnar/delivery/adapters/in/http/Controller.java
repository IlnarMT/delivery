package tm.ilnar.delivery.adapters.in.http;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tm.ilnar.delivery.adapters.in.http.exception.BadRequestException;
import tm.ilnar.delivery.adapters.in.http.exception.ConflictException;
import tm.ilnar.delivery.adapters.in.http.mappers.CourierMapper;
import tm.ilnar.delivery.adapters.in.http.mappers.OrderMapper;
import tm.ilnar.delivery.adapters.in.http.openapi.api.DefaultApi;
import tm.ilnar.delivery.adapters.in.http.openapi.model.Courier;
import tm.ilnar.delivery.adapters.in.http.openapi.model.NewCourier;
import tm.ilnar.delivery.adapters.in.http.openapi.model.Order;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommand;
import tm.ilnar.delivery.core.application.commands.CreateCourierCommandHandler;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommand;
import tm.ilnar.delivery.core.application.commands.CreateOrderCommandHandler;
import tm.ilnar.delivery.core.application.queries.CourierWithAssignedOrderQueryHandler;
import tm.ilnar.delivery.core.application.queries.CourierWithAssignedOrderQueryResponse;
import tm.ilnar.delivery.core.application.queries.GetNotCompletedOrdersQueryHandler;
import tm.ilnar.delivery.core.application.queries.GetNotCompletedOrdersQueryResponse;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class Controller implements DefaultApi {

    private final CreateCourierCommandHandler createCourierCommandHandler;
    private final CreateOrderCommandHandler createOrderCommandHandler;
    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;
    private final CourierWithAssignedOrderQueryHandler courierWithAssignedOrderQueryHandler;

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

    @Override
    public ResponseEntity<Void> createOrder() {
        Result<CreateOrderCommand, Error> commandResult =
            CreateOrderCommand.create(UUID.randomUUID(), "Айтишная", 2);

        if (commandResult.isFailure())
            throw new BadRequestException(commandResult.getError());
        var command = commandResult.getValue();

        var handleResult = createOrderCommandHandler.handle(command);
        if (handleResult.isFailure())
            throw new ConflictException(handleResult.getError());

        return ResponseEntity.ok().build();

    }

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var handleResult = courierWithAssignedOrderQueryHandler.handle();

        if (handleResult.isFailure())
            throw new ConflictException(handleResult.getError());

        List<CourierWithAssignedOrderQueryResponse> response = handleResult.getValue();

        List<Courier> model = CourierMapper.INSTANCE.toHttp(response);

        return ResponseEntity.ok(model);
    }

    @Override
    public ResponseEntity<List<Order>> getOrders() {
        var handleResult = getNotCompletedOrdersQueryHandler.handle();

        if (handleResult.isFailure())
            throw new ConflictException(handleResult.getError());

        List<GetNotCompletedOrdersQueryResponse> response = handleResult.getValue();

        List<Order> model = OrderMapper.INSTANCE.toHttp(response);

        return ResponseEntity.ok(model);
    }
}
