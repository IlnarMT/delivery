package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.services.LocationGenerator;
import tm.ilnar.delivery.core.ports.CourierRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository courierRepository;
    private final LocationGenerator locationGenerator;

    @Override
    public UnitResult<Error> handle(CreateCourierCommand command) {
        Result<Location, Error> randomLocation = locationGenerator.getRandomLocation();
        if (randomLocation.isFailure()) {
            return UnitResult.failure(randomLocation.getError());
        }

        Result<Courier, Error> createCourierResult =
            Courier.create(command.getName(), command.getSpeed(), randomLocation.getValue());
        if (createCourierResult.isFailure()) {
            return UnitResult.failure(createCourierResult.getError());
        }

        courierRepository.save(createCourierResult.getValue());

        return UnitResult.success();
    }
}
