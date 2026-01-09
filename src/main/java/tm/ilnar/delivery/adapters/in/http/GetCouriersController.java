package tm.ilnar.delivery.adapters.in.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import tm.ilnar.delivery.adapters.in.http.exception.ConflictException;
import tm.ilnar.delivery.adapters.in.http.mappers.CourierMapper;
import tm.ilnar.delivery.adapters.in.http.openapi.api.GetCouriersApi;
import tm.ilnar.delivery.adapters.in.http.openapi.model.Courier;
import tm.ilnar.delivery.core.application.queries.CourierWithAssignedOrderQueryHandler;
import tm.ilnar.delivery.core.application.queries.CourierWithAssignedOrderQueryResponse;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class GetCouriersController implements GetCouriersApi {

    private final CourierWithAssignedOrderQueryHandler courierWithAssignedOrderQueryHandler;

    @Override
    public ResponseEntity<List<Courier>> getCouriers() {
        var handleResult = courierWithAssignedOrderQueryHandler.handle();

        if (handleResult.isFailure())
            throw new ConflictException(handleResult.getError());

        List<CourierWithAssignedOrderQueryResponse> response = handleResult.getValue();

        List<Courier> model = CourierMapper.INSTANCE.toHttp(response);

        return ResponseEntity.ok(model);
    }
}
