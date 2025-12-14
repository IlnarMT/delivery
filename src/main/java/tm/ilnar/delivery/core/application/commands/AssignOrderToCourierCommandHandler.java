package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;

public interface AssignOrderToCourierCommandHandler {

    UnitResult<Error> handle();
}
