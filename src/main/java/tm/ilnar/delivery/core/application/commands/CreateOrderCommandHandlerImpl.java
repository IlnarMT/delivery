package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.ilnar.delivery.DomainEventPublisher;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.ports.GeoClient;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CreateOrderCommandHandlerImpl implements CreateOrderCommandHandler {

    private final OrderRepository orderRepository;
    private final GeoClient geoClient;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    @Override
    public Result<Order, Error> handle(CreateOrderCommand command) {
        Optional<Order> orderOpt = orderRepository.findById(command.getOrderId());
        if (orderOpt.isPresent()) {
            return Result.success(orderOpt.get());
        }

        return geoClient.getLocation(command.getStreet())
            .flatMap(location -> Order.create(command.getOrderId(), location, command.getVolume()))
            .flatMap(order -> {
                orderRepository.save(order);
                domainEventPublisher.saveForPublish(List.of(order));
                return Result.success(order);
            });
    }
}