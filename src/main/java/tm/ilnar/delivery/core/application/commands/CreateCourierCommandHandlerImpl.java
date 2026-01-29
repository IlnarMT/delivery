package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.ilnar.delivery.DomainEventPublisher;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.services.LocationGenerator;
import tm.ilnar.delivery.core.ports.CourierRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCourierCommandHandlerImpl implements CreateCourierCommandHandler {

    private final CourierRepository courierRepository;
    private final LocationGenerator locationGenerator;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    @Override
    public Result<Courier, Error> handle(CreateCourierCommand command) {
        Result<Location, Error> randomLocation = locationGenerator.getRandomLocation();
        if (randomLocation.isFailure()) {
            return Result.failure(randomLocation.getError());
        }

        Result<Courier, Error> createCourierResult =
            Courier.create(command.getName(), command.getSpeed(), randomLocation.getValue());
        if (createCourierResult.isFailure()) {
            return Result.failure(createCourierResult.getError());
        }

        courierRepository.save(createCourierResult.getValue());
        domainEventPublisher.publish(List.of(createCourierResult.getValue()));

        return Result.success(createCourierResult.getValue());
    }
}
