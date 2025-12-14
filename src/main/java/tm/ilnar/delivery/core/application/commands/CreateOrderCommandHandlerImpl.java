package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;

    @Override
    public UnitResult<Error> handle(CreateOrderCommand command) {
        if (orderRepository.findById(command.getOrderId()).isPresent()) {
            return UnitResult.success();
        }

        return getRandomLocation()
            .flatMap(location -> Order.create(command.getOrderId(), location, command.getVolume()))
            .flatMapToUnit(order -> {
                orderRepository.save(order);
                return UnitResult.success();
            });
    }

    private Result<Location, Error> getRandomLocation() {
        ThreadLocalRandom r = ThreadLocalRandom.current();
        int x = r.nextInt(1, 11);
        int y = r.nextInt(1, 11);
        return Location.create(x, y);
    }
}