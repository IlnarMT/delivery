package libs.errs;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public final class Err {

    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    private Err() {
    }

    // String
    public static UnitResult<Error> againstNullOrEmpty(String value, String paramName) {
        if (value == null || value.isBlank()) return UnitResult.failure(GeneralErrors.valueIsRequired(paramName));
        return UnitResult.success();
    }

    // UUID
    public static UnitResult<Error> againstNullOrEmpty(UUID uuid, String paramName) {
        if (uuid == null || uuid.equals(EMPTY_UUID))
            return UnitResult.failure(GeneralErrors.valueIsRequired(paramName));
        return UnitResult.success();
    }

    // Collection
    public static UnitResult<Error> againstNullOrEmpty(Collection<?> collection, String paramName) {
        if (collection == null || collection.isEmpty())
            return UnitResult.failure(GeneralErrors.valueIsRequired(paramName));
        return UnitResult.success();
    }

    // Numbers >= 0
    public static UnitResult<Error> againstNegative(int value, String paramName) {
        if (value < 0)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be >= 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstNegative(long value, String paramName) {
        if (value < 0L)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be >= 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstNegative(float value, String paramName) {
        if (value < 0f)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be >= 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstNegative(double value, String paramName) {
        if (value < 0d)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be >= 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstNegative(BigDecimal value, String paramName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be >= 0"));
        return UnitResult.success();
    }

    // Numbers > 0
    public static UnitResult<Error> againstZeroOrNegative(int value, String paramName) {
        if (value <= 0)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be > 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstZeroOrNegative(long value, String paramName) {
        if (value <= 0L)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be > 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstZeroOrNegative(float value, String paramName) {
        if (value <= 0f)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be > 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstZeroOrNegative(double value, String paramName) {
        if (value <= 0d)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be > 0"));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstZeroOrNegative(BigDecimal value, String paramName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0)
            return UnitResult.failure(GeneralErrors.valueIsInvalid(paramName,"must be > 0"));
        return UnitResult.success();
    }

    // Range check
    public static UnitResult<Error> againstOutOfRange(int value, int min, int max, String paramName) {
        if (value < min || value > max)
            return UnitResult.failure(GeneralErrors.valueIsOutOfRange(paramName, value, min, max));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstOutOfRange(long value, long min, long max, String paramName) {
        if (value < min || value > max)
            return UnitResult.failure(GeneralErrors.valueIsOutOfRange(paramName, value, min, max));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstOutOfRange(float value, float min, float max, String paramName) {
        if (value < min || value > max)
            return UnitResult.failure(GeneralErrors.valueIsOutOfRange(paramName, value, min, max));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstOutOfRange(double value, double min, double max, String paramName) {
        if (value < min || value > max)
            return UnitResult.failure(GeneralErrors.valueIsOutOfRange(paramName, value, min, max));
        return UnitResult.success();
    }

    public static UnitResult<Error> againstOutOfRange(BigDecimal value, BigDecimal min, BigDecimal max, String paramName) {
        if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0)
            return UnitResult.failure(GeneralErrors.valueIsOutOfRange(paramName, value, min, max));
        return UnitResult.success();
    }
}

