package tm.ilnar.delivery.core.application.queries;

import libs.errs.Error;
import libs.errs.Result;

import java.util.List;

public interface CourierWithAssignedOrderQueryHandler {

    Result<List<CourierWithAssignedOrderQueryResponse>, Error> handle();
}
