package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.ports.GeoClient;
import tm.ilnar.delivery.core.ports.OrderRepository;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final GeoClient geoClient;

    @Override
    public UnitResult<Error> handle(CreateOrderCommand command) {
        if (orderRepository.findById(command.getOrderId()).isPresent()) {
            return UnitResult.success();
        }

        return geoClient.getLocation(command.getStreet())
            .flatMap(location -> Order.create(command.getOrderId(), location, command.getVolume()))
            .flatMapToUnit(order -> {
                orderRepository.save(order);
                return UnitResult.success();
            });
    }
}