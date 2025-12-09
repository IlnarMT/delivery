package tm.ilnar.delivery.core.domain.model.courier;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.Getter;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Entity
@Table(name = "courier")
@Getter
public class Courier extends Aggregate<UUID> {

    private static final int MIN_SPEED = 0;
    private static final String BAG_STORAGE_PLACE_NAME = "bag";
    private static final int BAG_STORAGE_PLACE_VOLUME = 10;

    @Column(name = "name")
    private String name;

    @Column(name = "speed")
    private int speed;

    private Location location;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "courier_id", nullable = false)
    private final List<StoragePlace> storagePlaces = new ArrayList<>();

    protected Courier() {
        // for JPA
    }

    private Courier(UUID id, String name, int speed, Location location, StoragePlace storagePlace) {
        super(id);
        this.name = name;
        this.speed = speed;
        this.location = location;
        this.storagePlaces.add(storagePlace);
    }

    public static Result<Courier, Error> create(String name, int speed, Location location) {
        if (name == null || name.isBlank()) {
            return Result.failure(GeneralErrors.valueIsRequired("name"));
        }
        if (speed < MIN_SPEED) {
            return Result.failure(GeneralErrors.valueIsInvalid("speed", "must be >= " + MIN_SPEED));
        }
        if (location == null) {
            return Result.failure(GeneralErrors.valueIsRequired("location"));
        }
        var bagStoragePlace = StoragePlace.create(BAG_STORAGE_PLACE_NAME, BAG_STORAGE_PLACE_VOLUME);
        if (bagStoragePlace.isFailure()) {
            return Result.failure(bagStoragePlace.getError());
        }
        return Result.success(new Courier(UUID.randomUUID(), name, speed, location, bagStoragePlace.getValue()));
    }

    public UnitResult<Error> addStoragePlace(String name, int volume) {
        Result<StoragePlace, Error> storagePlaceResult = StoragePlace.create(name, volume);
        if (storagePlaceResult.isFailure()) {
            return UnitResult.failure(storagePlaceResult.getError());
        }
        this.storagePlaces.add(storagePlaceResult.getValue());
        return UnitResult.success();
    }

    public Result<Boolean, Error> canTakeOrder(Order order) {
        if (order == null) {
            return Result.failure(GeneralErrors.valueIsRequired("order"));
        }
        return findAvailableStoragePlace(order.getVolume())
            .map(storagePlace -> Result.<Boolean, Error>success(Boolean.TRUE))
            .orElse(Result.success(Boolean.FALSE));
    }

    private Optional<StoragePlace> findAvailableStoragePlace(int volume) {
        return storagePlaces.stream()
            .filter(storagePlace -> storagePlace.hasEnoughCapacityFor(volume) && !storagePlace.isOccupied())
            .min(Comparator.comparing(StoragePlace::getId));
    }

    public UnitResult<Error> takeOrder(Order order) {
        Result<Boolean, Error> canTakeOrderResult = this.canTakeOrder(order);
        if (canTakeOrderResult.isFailure()) {
            return UnitResult.failure(canTakeOrderResult.getError());
        }
        if (!canTakeOrderResult.getValue()) {
            return UnitResult.failure(Errors.cannotTakeOrder());
        }

        return findAvailableStoragePlace(order.getVolume())
            .map(storagePlace -> storagePlace.store(order.getId(), order.getVolume()))
            .orElse(UnitResult.failure(Errors.noFreeStoragePlace()));
    }

    public UnitResult<Error> completeOrder(UUID orderId) {
        if (orderId == null)
            return UnitResult.failure(GeneralErrors.valueIsRequired("orderId"));
        return this.storagePlaces.stream()
            .filter(storagePlace -> orderId.equals(storagePlace.getOrderId()))
            .findFirst()
            .map(storagePlace -> {
                storagePlace.clear();
                return UnitResult.<Error>success();
            })
            .orElseGet(() -> UnitResult.failure(Errors.cannotFindOrder()));
    }

    public Result<Double, Error> calculateTimeToLocation(Location targetLocation) {
        if (targetLocation == null) {
            return Result.failure(GeneralErrors.valueIsRequired("targetLocation"));
        }
        int distanceTo = this.location.distanceTo(targetLocation);
        double time = (double) distanceTo / speed;
        return Result.success(time);
    }

    public UnitResult<Error> move(Location target) {
        if (target == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("target"));
        }

        int difX = target.getX() - location.getX();
        int difY = target.getY() - location.getY();
        int cruisingRange = speed;

        int moveX = Math.max(-cruisingRange, Math.min(difX, cruisingRange));
        cruisingRange -= Math.abs(moveX);

        int moveY = Math.max(-cruisingRange, Math.min(difY, cruisingRange));

        Result<Location, Error> locationCreateResult = Location.create(
            location.getX() + moveX,
            location.getY() + moveY
        );

        if (locationCreateResult.isFailure()) {
            return UnitResult.failure(locationCreateResult.getError());
        }

        this.location = locationCreateResult.getValue();
        return UnitResult.success();
    }

    public static class Errors {
        private static final String CLASS_NAME = "courier";

        public static Error cannotFindOrder() {
            return Error.of(CLASS_NAME + ".cannot.find.order", "can't find order");
        }

        public static Error noFreeStoragePlace() {
            return Error.of(CLASS_NAME + ".no.free.storage.place", "no free storage place");
        }

        public static Error cannotTakeOrder() {
            return Error.of(CLASS_NAME + ".cannot.take.order", "can't take order");
        }
    }
}
