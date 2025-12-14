package tm.ilnar.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import tm.ilnar.delivery.core.domain.model.kernel.Location;

public interface LocationGenerator {

    Result<Location, Error> getRandomLocation();
}
