package tm.ilnar.delivery.core.domain.model.kernel;

import libs.ddd.ValueObject;
import libs.errs.GeneralErrors;
import libs.errs.Error;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

import static java.lang.Math.abs;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Location extends ValueObject<Location> {

    private static final int X_MIN_VALUE = 1;
    private static final int X_MAX_VALUE = 10;
    private static final int Y_MIN_VALUE = 1;
    private static final int Y_MAX_VALUE = 10;

    private int x;
    private int y;

    private Location() {
    }

    public static Result<Location, Error> create(int x, int y) {
        if (x < X_MIN_VALUE || x > X_MAX_VALUE) {
            return Result.failure(GeneralErrors.valueIsInvalid("x", "must be between 1 and 10"));
        }
        if (y < Y_MIN_VALUE || y > Y_MAX_VALUE) {
            return Result.failure(GeneralErrors.valueIsInvalid("y", "must be between 1 and 10"));
        }

        return Result.success(new Location(x, y));
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.x, this.y);
    }

    public int distanceTo(Location location) {
        int xDistance = abs(this.x - location.x);
        int yDistance = abs(this.y - location.y);
        return xDistance + yDistance;
    }
}
