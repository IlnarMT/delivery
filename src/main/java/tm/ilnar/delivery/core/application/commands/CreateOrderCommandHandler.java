package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import tm.ilnar.delivery.core.domain.model.order.Order;

public interface CreateOrderCommandHandler {

    Result<Order, Error> handle(CreateOrderCommand command);
}
