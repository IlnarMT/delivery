package tm.ilnar.delivery.core.domain.model.order;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.Getter;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCompletedDomainEvent;
import tm.ilnar.delivery.core.domain.model.order.events.OrderCreatedDomainEvent;

import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
public class Order extends Aggregate<UUID> {

    private static final int MIN_VOLUME = 0;

    private Location location;

    @Column(name = "volume")
    private int volume;

    @Column(name = "status")
    private OrderStatus status;

    @Column(name = "courier_id")
    private UUID courierId;

    protected Order() {
        // for JPA
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

        Order order = new Order(id, location, volume, null);
        order.raiseDomainEvent(new OrderCreatedDomainEvent(order));
        return Result.success(order);
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

        this.raiseDomainEvent(new OrderCompletedDomainEvent(this));
        return UnitResult.success();
    }

    public static class Errors {
        private static final String CLASS_NAME = Order.class.getSimpleName().toLowerCase();

        public static Error orderMustBeAssigned() {
            return Error.of( CLASS_NAME + ".order.must.be.assigned", "Заказ должен быть назначен на курьера");
        }
        public static Error orderAlreadyAssigned() {
            return Error.of(CLASS_NAME + ".order.already.assigned", "Заказ уже назначен на курьера");
        }
        public static Error orderAlreadyCompleted() {
            return Error.of(CLASS_NAME + ".order.already.completed", "Заказ уже завершён");
        }
    }
}
