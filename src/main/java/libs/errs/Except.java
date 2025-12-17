package libs.errs;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.UUID;

public final class Except {

    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    private Except() {
    }

    // Object
    public static <T> T againstNull(T obj, String paramName) {
        if (obj == null)
            throw new IllegalArgumentException(paramName + " must not be null");
        return obj;
    }

    // String
    public static String againstNullOrEmpty(String value, String paramName) {
        if (value == null || value.isBlank())
            throw new IllegalArgumentException(paramName + " must not be null or empty");
        return value;
    }

    // UUID
    public static UUID againstNullOrEmpty(UUID uuid, String paramName) {
        if (uuid == null || uuid.equals(EMPTY_UUID))
            throw new IllegalArgumentException(paramName + " must not be null or empty UUID");
        return uuid;
    }

    // Collection
    public static <T extends Collection<?>> T againstNullOrEmpty(T collection, String paramName) {
        if (collection == null || collection.isEmpty())
            throw new IllegalArgumentException(paramName + " must not be null or empty collection");
        return collection;
    }

    // Numbers >= 0
    public static int againstNegative(int value, String paramName) {
        if (value < 0)
            throw new IllegalArgumentException(paramName + " must be >= 0");
        return value;
    }

    public static long againstNegative(long value, String paramName) {
        if (value < 0L)
            throw new IllegalArgumentException(paramName + " must be >= 0");
        return value;
    }

    public static float againstNegative(float value, String paramName) {
        if (value < 0f)
            throw new IllegalArgumentException(paramName + " must be >= 0");
        return value;
    }

    public static double againstNegative(double value, String paramName) {
        if (value < 0d)
            throw new IllegalArgumentException(paramName + " must be >= 0");
        return value;
    }

    public static BigDecimal againstNegative(BigDecimal value, String paramName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException(paramName + " must be >= 0");
        return value;
    }

    // Numbers > 0
    public static int againstZeroOrNegative(int value, String paramName) {
        if (value <= 0)
            throw new IllegalArgumentException(paramName + " must be > 0");
        return value;
    }

    public static long againstZeroOrNegative(long value, String paramName) {
        if (value <= 0L)
            throw new IllegalArgumentException(paramName + " must be > 0");
        return value;
    }

    public static float againstZeroOrNegative(float value, String paramName) {
        if (value <= 0f)
            throw new IllegalArgumentException(paramName + " must be > 0");
        return value;
    }

    public static double againstZeroOrNegative(double value, String paramName) {
        if (value <= 0d)
            throw new IllegalArgumentException(paramName + " must be > 0");
        return value;
    }

    public static BigDecimal againstZeroOrNegative(BigDecimal value, String paramName) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(paramName + " must be > 0");
        return value;
    }

    // Range check
    public static int againstOutOfRange(int value, int min, int max, String paramName) {
        if (value < min || value > max)
            throw new IllegalArgumentException(paramName + " must be in range [" + min + ", " + max + "]");
        return value;
    }

    public static long againstOutOfRange(long value, long min, long max, String paramName) {
        if (value < min || value > max)
            throw new IllegalArgumentException(paramName + " must be in range [" + min + ", " + max + "]");
        return value;
    }

    public static float againstOutOfRange(float value, float min, float max, String paramName) {
        if (value < min || value > max)
            throw new IllegalArgumentException(paramName + " must be in range [" + min + ", " + max + "]");
        return value;
    }

    public static double againstOutOfRange(double value, double min, double max, String paramName) {
        if (value < min || value > max)
            throw new IllegalArgumentException(paramName + " must be in range [" + min + ", " + max + "]");
        return value;
    }

    public static BigDecimal againstOutOfRange(BigDecimal value, BigDecimal min, BigDecimal max, String paramName) {
        if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0)
            throw new IllegalArgumentException(paramName + " must be in range [" + min + ", " + max + "]");
        return value;
    }
}

