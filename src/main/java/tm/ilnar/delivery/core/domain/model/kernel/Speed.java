package tm.ilnar.delivery.core.domain.model.kernel;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import libs.ddd.ValueObject;
import libs.errs.Error;
import libs.errs.GeneralErrors;
import libs.errs.Result;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Embeddable
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Speed extends ValueObject<Speed> {

    private static final int MIN_SPEED = 0;

    @Column(name = "speed")
    private int speed;

    protected Speed() {
        // for JPA
    }

    public static Result<Speed, Error> create(int speed) {
        if (speed < MIN_SPEED) {
            return Result.failure(GeneralErrors.valueIsInvalid("speed", "must be >= " + MIN_SPEED));
        }

        return Result.success(new Speed(speed));
    }

    @Override
    protected Iterable<Object> equalityComponents() {
        return List.of(this.speed);
    }
}
