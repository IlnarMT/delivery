package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.ilnar.delivery.DomainEventPublisher;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;
import tm.ilnar.delivery.core.domain.services.OrderDispatcher;
import tm.ilnar.delivery.core.ports.CourierRepository;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssignOrderToCourierCommandHandlerImpl implements AssignOrderToCourierCommandHandler {

    private final OrderRepository orderRepository;
    private final OrderDispatcher orderDispatcher;
    private final CourierRepository courierRepository;
    private final DomainEventPublisher domainEventPublisher;

    @Transactional
    @Override
    public UnitResult<Error> handle() {
        return orderRepository.findAnyByStatus(OrderStatus.CREATED)
            .map(this::assignOrder)
            .orElseGet(UnitResult::success);
    }

    private UnitResult<Error> assignOrder(Order order) {
        List<Courier> freeCouriers = courierRepository.findAllWithFreeStorage();
        if (freeCouriers.isEmpty()) {
            Error err = Errors.noFreeCouriersForOrder(order.getId());
            log.info(err.toString());
            return UnitResult.failure(err);
        }

        Result<Courier, Error> dispatchedCourierResult = orderDispatcher.dispatch(order, freeCouriers);
        if (dispatchedCourierResult.isFailure()) {
            return UnitResult.failure(dispatchedCourierResult.getError());
        }

        Courier dispatchedCourier = dispatchedCourierResult.getValue();

        courierRepository.save(dispatchedCourier);
        orderRepository.save(order);
        domainEventPublisher.saveForPublish(List.of(dispatchedCourier, order));

        return UnitResult.success();
    }

    public static class Errors {
        private static final String CLASS_NAME = "AssignOrderToCourierCommandHandlerImpl";

        public static Error noFreeCouriersForOrder(UUID orderId) {
            return Error.of(
                CLASS_NAME + ".no.free.couriers",
                "no free couriers available for order " + orderId
            );
        }
    }
}
