package tm.ilnar.delivery.core.application.commands;

import libs.errs.Err;
import libs.errs.Error;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import tm.ilnar.delivery.core.domain.model.kernel.Speed;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateCourierCommand {

    private final String name;
    private final Speed speed;

    public static Result<CreateCourierCommand, Error> create(String name, int speed) {
        var validation = UnitResult.combine(
            Err.againstNullOrEmpty(name, "name"),
            Err.againstNull(speed, "speed")
        );
        if (validation.isFailure()) {
            return Result.failure(validation.getError());
        }

        Result<Speed, Error> speedCreateResult = Speed.create(speed);
        if (speedCreateResult.isFailure()) {
            return Result.failure(speedCreateResult.getError());
        }

        return Result.success(new CreateCourierCommand(name, speedCreateResult.getValue()));
    }
}
