package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import org.springframework.transaction.annotation.Transactional;

public interface MoveAllCouriersCommandHandler {

    UnitResult<Error> handle();
}
