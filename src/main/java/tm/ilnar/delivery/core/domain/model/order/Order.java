package tm.ilnar.delivery.core.domain.model.order;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.Getter;
import tm.ilnar.delivery.core.domain.model.courier.Courier;
import tm.ilnar.delivery.core.domain.model.kernel.Location;

import java.util.UUID;

@Getter
public class Order extends Aggregate<UUID> {

    private static final int MIN_VOLUME = 1;

    private Location location;

    private int volume;

    private OrderStatus status;

    private UUID courierId;

    private Order() {
    }

    private Order(UUID id, Location location, int volume, UUID courierId) {
        super(id);
        this.location = location;
        this.volume = volume;
        this.status = OrderStatus.CREATED;;
        this.courierId = courierId;
    }

    public static Result<Order, Error> create(UUID id, Location location, int volume) {
        if (id == null) {
            return Result.failure(GeneralErrors.valueIsRequired("id"));
        }
        if (location == null) {
            return Result.failure(GeneralErrors.valueIsRequired("location"));
        }
        if (volume < MIN_VOLUME) {
            return Result.failure(GeneralErrors.valueIsInvalid("volume", "must be less than or equal to " + MIN_VOLUME));
        }
        return Result.success(new Order(id, location, volume, null));
    }

    public UnitResult<Error> assign(UUID courierId) {
        if (courierId == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("courierId"));
        }
        if (status == OrderStatus.COMPLETED) return UnitResult.failure(Errors.orderAlreadyCompleted());
        if (status == OrderStatus.ASSIGNED)  return UnitResult.failure(Errors.orderAlreadyAssigned());

        this.courierId = courierId;
        this.status = OrderStatus.ASSIGNED;
        return UnitResult.success();
    }

    public UnitResult<Error> complete() {
        if (status == OrderStatus.CREATED)    return UnitResult.failure(Errors.orderMustBeAssigned());
        if (status == OrderStatus.COMPLETED)  return UnitResult.failure(Errors.orderAlreadyCompleted());

        this.status = OrderStatus.COMPLETED;
        return UnitResult.success();
    }

    public static class Errors {
        public static Error orderMustBeAssigned() {
            return Error.of("order.must.be.assigned", "Заказ должен быть назначен на курьера");
        }
        public static Error orderAlreadyAssigned() {
            return Error.of("order.already.assigned", "Заказ уже назначен на курьера");
        }
        public static Error orderAlreadyCompleted() {
            return Error.of("order.already.completed", "Заказ уже завершён");
        }
    }
}
