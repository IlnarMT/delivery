package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.UnitResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tm.ilnar.delivery.DomainEventPublisher;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;
import tm.ilnar.delivery.core.ports.CourierRepository;
import tm.ilnar.delivery.core.ports.OrderRepository;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MoveAllCouriersCommandHandlerImpl implements MoveAllCouriersCommandHandler {

    private final CourierRepository courierRepository;
    private final OrderRepository orderRepository;
    private final DomainEventPublisher domainEventPublisher;

    /**
     * Стратегия: "сделать максимум возможного".
     * Ошибки по отдельным заказам не прерывают обработку всех заказов
     */
    @Transactional
    @Override
    public UnitResult<Error> handle() {
        List<Order> assignedOrders = orderRepository.findAllByStatus(OrderStatus.ASSIGNED);

        UnitResult<Error> firstFailure = UnitResult.success();

        for (Order order : assignedOrders) {
            UnitResult<Error> moveResult = moveCourierForOrder(order);
            if (moveResult.isFailure()) {
                log.warn("Failed to move courier for order {}: {}",
                    order.getId(), moveResult.getError());
                if (firstFailure.isSuccess()) {
                    firstFailure = moveResult;
                }
            }
        }

        return firstFailure.isFailure()
            ? firstFailure
            : UnitResult.success();
    }

    private UnitResult<Error> moveCourierForOrder(Order order) {
        return courierRepository.findById(order.getCourierId())
            .map(courier -> {
                UnitResult<Error> moveResult = courier.move(order.getLocation());
                if (moveResult.isFailure()) {
                    return moveResult;
                }

                if (courier.getLocation().equals(order.getLocation())) {
                    order.complete();
                    courier.completeOrder(order.getId());

                    orderRepository.save(order);
                }

                courierRepository.save(courier);
                domainEventPublisher.publish(List.of(order));

                return UnitResult.<Error>success();
            })
            .orElseGet(() -> UnitResult.failure(
                Errors.cannotFindCourier(order.getCourierId(), order.getId())
            ));
    }

    public static class Errors {
        private static final String CLASS_NAME = "MoveAllCouriersCommandHandlerImpl";

        public static Error cannotFindCourier(UUID courierId, UUID orderId) {
            return Error.of(
                CLASS_NAME + ".cannot.find.courier",
                "can't find courier id " + courierId + " for order id " + orderId
            );
        }
    }
}
