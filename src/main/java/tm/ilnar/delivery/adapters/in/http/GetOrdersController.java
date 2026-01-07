package tm.ilnar.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tm.ilnar.delivery.adapters.in.http.exception.ConflictException;
import tm.ilnar.delivery.adapters.in.http.mappers.OrderMapper;
import tm.ilnar.delivery.adapters.in.http.openapi.api.GetOrdersApi;
import tm.ilnar.delivery.adapters.in.http.openapi.model.Order;
import tm.ilnar.delivery.core.application.queries.GetNotCompletedOrdersQueryHandler;
import tm.ilnar.delivery.core.application.queries.GetNotCompletedOrdersQueryResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetOrdersController implements GetOrdersApi {

    private final GetNotCompletedOrdersQueryHandler getNotCompletedOrdersQueryHandler;

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
