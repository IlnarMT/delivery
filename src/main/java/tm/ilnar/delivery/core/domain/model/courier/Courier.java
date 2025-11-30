package tm.ilnar.delivery.core.domain.model.courier;

import libs.ddd.Aggregate;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import tm.ilnar.delivery.core.domain.model.kernel.Location;
import tm.ilnar.delivery.core.domain.model.order.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Courier extends Aggregate<UUID> {

    private static final int MIN_SPEED = 1;
    private static final String BAG_STORAGE_PLACE_NAME = "bag";
    private static final int BAG_STORAGE_PLACE_VOLUME = 10;

    private String name;

    private int speed;

    private Location location;

    private final List<StoragePlace> storagePlaces = new ArrayList<>();

    private Courier() {
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

    public UnitResult<Error> canTakeOrder(Order order) {
        if (order == null) {
            return UnitResult.failure(GeneralErrors.valueIsRequired("order"));
        }
        return UnitResult.from(findAvailableStoragePlaceFor(order.getVolume()));
    }

    private Result<StoragePlace, Error> findAvailableStoragePlaceFor(int volume) {
        boolean anyFits = storagePlaces.stream()
            .anyMatch(storagePlace -> storagePlace.hasEnoughCapacityFor(volume));
        if (!anyFits) {
            return Result.failure(Errors.noStoragePlaceFitsVolume());
        }

        return storagePlaces.stream()
            .filter(storagePlace -> !storagePlace.isOccupied())
            .filter(storagePlace -> storagePlace.hasEnoughCapacityFor(volume))
            .findFirst()
            .map(Result::<StoragePlace, Error>success)
            .orElseGet(() -> Result.failure(Errors.noFreeStoragePlace()));
    }

    public UnitResult<Error> takeOrder(Order order) {
        UnitResult<Error> canTakeOrderResult = this.canTakeOrder(order);
        if (canTakeOrderResult.isFailure()) {
            return canTakeOrderResult;
        }
        Result<StoragePlace, Error> storagePlaceResult = findAvailableStoragePlaceFor(order.getVolume());
        if (storagePlaceResult.isFailure()) {
            return UnitResult.failure(storagePlaceResult.getError());
        }
        StoragePlace storagePlace = storagePlaceResult.getValue();
        return storagePlace.store(order.getId(), order.getVolume());
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
        public static Error noAvailableStoragePlace() {
            return Error.of("courier.no.available.storage.place", "no available storage place");
        }

        public static Error cannotFindOrder() {
            return Error.of("courier.cannot.find.order", "can't find order");
        }

        public static Error noFreeStoragePlace() {
            return Error.of("courier.no.free.storage.place", "no free storage place");
        }
        public static Error noStoragePlaceFitsVolume() {
            return Error.of("courier.no.storage.place.fits.volume", "no storage place fits volume");
        }
    }
}
