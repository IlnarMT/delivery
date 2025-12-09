package tm.ilnar.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.List;

public interface OrderDispatcher {

    Result<Courier, Error> dispatch(Order order, List<Courier> couriers);
}
