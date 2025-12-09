package tm.ilnar.delivery.core.domain.services;

import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import org.springframework.stereotype.Service;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.order.Order;
import tm.ilnar.delivery.core.domain.model.order.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderDispatcherImpl implements OrderDispatcher {

    @Override
    public Result<Courier, Error> dispatch(Order order, List<Courier> couriers) {
        return validate(order, couriers)
            .flatMapToResult(() -> filterEligibleCouriers(order, couriers))
            .flatMap(eligibleCouriers -> selectFastestCourier(order, eligibleCouriers))
            .flatMap(fastestCourier ->
                order.assign(fastestCourier.getId()).toResult(fastestCourier)
            )
            .flatMap(fastestCourier ->
                fastestCourier.takeOrder(order).toResult(fastestCourier)
            );
    }

    private static UnitResult<Error> validate(Order order, List<Courier> couriers) {
        if (order == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));
        }
        if (couriers == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("couriers"));
        }
        if (couriers.isEmpty()) {
            return UnitResult.failure(Errors.couriersMustNotBeEmpty());
        }
        if (order.getStatus() != OrderStatus.CREATED) {
            return UnitResult.failure(Errors.orderMustBeCreated());
        }
        return UnitResult.success();
    }

    private static Result<List<Courier>, Error> filterEligibleCouriers(Order order, List<Courier> couriers) {
        List<Courier> result = new ArrayList<>();
        for (Courier courier : couriers) {
            Result<Boolean, Error> canTakeOrder = courier.canTakeOrder(order);
            if (canTakeOrder.isFailure()) {
                return Result.failure(canTakeOrder.getError());
            }
            if (!canTakeOrder.getValue()) {
                continue;
            }
            result.add(courier);
        }
        return Result.success(result);
    }

    private static Result<Courier, Error> selectFastestCourier(Order order, List<Courier> couriers) {
        var target = order.getLocation();

        Courier bestCourier = null;
        double bestDeliveryTime = Double.POSITIVE_INFINITY;

        for (Courier courier : couriers) {
            Result<Double, Error> timeToLocationResult = courier.calculateTimeToLocation(target);
            if (timeToLocationResult.isFailure()) {
                return Result.failure(timeToLocationResult.getError());
            }

            double deliveryTime = timeToLocationResult.getValue();
            if (bestCourier == null || deliveryTime < bestDeliveryTime) {
                bestCourier = courier;
                bestDeliveryTime = deliveryTime;
            }
        }

        return bestCourier != null
            ? Result.success(bestCourier)
            : Result.failure(Errors.noEligibleCourierFound());
    }

    public static class Errors {
        private static final String CODE = "order.dispatcher";

        public static Error couriersMustNotBeEmpty() {
            return Error.of(CODE + ".couriers.must.not.be.empty", "Список курьеров не должен быть пустым");
        }

        public static Error noEligibleCourierFound() {
            return Error.of(CODE + ".no.eligible.courier.found", "Не найден подходящий курьер");
        }

        public static Error orderMustBeCreated() {
            return Error.of(CODE + ".order.must.be.created", "Заказ должен быть в статусе Created");
        }
    }
}
