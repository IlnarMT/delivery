package tm.ilnar.delivery.core.ports;

import libs.errs.Error;
import libs.errs.Result;
import tm.ilnar.delivery.core.domain.model.kernel.Location;

public interface GeoClient {

    Result<Location, Error> getLocation(String street);
}
