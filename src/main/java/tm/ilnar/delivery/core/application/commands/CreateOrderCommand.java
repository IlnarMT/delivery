package tm.ilnar.delivery.core.application.commands;

import libs.errs.Error;
import libs.errs.Err;
import libs.errs.Result;
import libs.errs.UnitResult;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class CreateOrderCommand {

    private final UUID orderId;
    private final String street;
    private final int volume;

    public static Result<CreateOrderCommand, Error> create(UUID orderId, String street, int volume) {
        var validation = UnitResult.combine(
            Err.againstNullOrEmpty(orderId, "orderId"),
            Err.againstNullOrEmpty(street, "street"),
            Err.againstNegative(volume, "volume")
        );
        if (validation.isFailure()) {
            return Result.failure(validation.getError());
            }

        return Result.success(new CreateOrderCommand(orderId, street, volume));
    }
}
