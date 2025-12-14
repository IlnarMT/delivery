package tm.ilnar.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.Result;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.kernel.Location;

import java.util.concurrent.ThreadLocalRandom;

@Service
public class LocationGeneratorImpl implements LocationGenerator {

    @Override
    public Result<Location, Error> getRandomLocation() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int x = r.nextInt(1, 11);
        int y = r.nextInt(1, 11);
        return Location.create(x, y);
    }
}
