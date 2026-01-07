package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import tm.ilnar.delivery.core.domain.model.courier.Courier;

public interface CreateCourierCommandHandler {

    Result<Courier, Error> handle(CreateCourierCommand command);
}
