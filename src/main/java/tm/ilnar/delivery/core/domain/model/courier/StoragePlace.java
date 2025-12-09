package tm.ilnar.delivery.core.domain.model.courier;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import libs.ddd.Entity;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@jakarta.persistence.Entity
@Table(name = "storage_place")
@Getter
public class StoragePlace extends Entity<UUID> {

    private static final int MIN_TOTAL_VOLUME = 1;

    @Column(name = "name")
    private String name;

    @Column(name = "total_volume")
    private int totalVolume;

    @Column(name = "order_id")
    private UUID orderId;

    protected StoragePlace() {
        // for JPA
    }

    private StoragePlace(UUID id, String name, int totalVolume) {
        super(id);
        this.name = name;
        this.totalVolume = totalVolume;
        this.orderId = null;
    }

    public static Result<StoragePlace, Error> create(String name, int totalVolume) {
        if (name == null || name.isBlank()) {
            return Result.failure(GeneralErrors.valueIsEmpty("name"));
        }
        if (totalVolume < MIN_TOTAL_VOLUME) {
            return Result.failure(GeneralErrors.valueIsInvalid("totalVolume", "must be greater than 0"));
        }
        String trimmedName = name.trim();
        return Result.success(new StoragePlace(UUID.randomUUID(), trimmedName, totalVolume));
    }

    public boolean isOccupied() {
        return Objects.nonNull(orderId);
    }

    public boolean hasEnoughCapacityFor(int volume) {
        return volume <= totalVolume;
    }

    public UnitResult<Error> canStore(int volume) {
        if (volume <= 0) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("volume", "must be greater than 0"));
        }
        if (isOccupied()) {
            return UnitResult.failure(Error.of("STORAGE_OCCUPIED", "storage place is occupied"));
        }
        if (volume > totalVolume) {
            return UnitResult.failure(GeneralErrors.valueIsInvalid("volume", "must be less than or equal to " + totalVolume));
        }
        return UnitResult.success();
    }

    public UnitResult<Error> store(UUID orderId, int volume) {
        if (orderId == null) {
            return UnitResult.failure(GeneralErrors.valueIsEmpty("orderId"));
        }
        UnitResult<Error> canStoreResult = this.canStore(volume);
        if (canStoreResult.isFailure()) {
            return canStoreResult;
        }
        this.orderId = orderId;
        return UnitResult.success();
    }

    public UnitResult<Error> clear() {
        this.orderId = null;
        return UnitResult.success();
    }
}
