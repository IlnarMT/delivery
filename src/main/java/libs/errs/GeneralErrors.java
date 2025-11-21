package libs.errs;
import java.util.UUID;

public final class GeneralErrors {

    private GeneralErrors() {
        // Utility class, no instantiation
    }

    public static Error notFound(String name, Long id) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        String forId = (id == null) ? "" : " with Id '" + id + "'";
        return Error.of("record.not.found", "Record not found. Name: " + name + forId);
    }

    public static Error notFound(String name, UUID id) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        String forId = (id == null) ? "" : " with Id '" + id + "'";
        return Error.of("record.not.found", "Record not found. Name: " + name + forId);
    }

    public static Error valueIsInvalid(String name, String reason) {
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }

        String message = reason != null && !reason.isBlank()
                ? String.format("Value is invalid for %s: %s", name, reason)
                : String.format("Value is invalid for %s", name);

        return Error.of("value.is.invalid", message);
    }

    public static Error valueIsEmpty(String name) {
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }

        String message = String.format("Value is invalid for %s: %s", name, "Name must not be null or empty");

        return Error.of("value.is.invalid", message);
    }

    public static Error valueIsRequired(String name) {
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        return Error.of("value.is.required", "Value is required for " + name);
    }

    public static Error invalidLength(String name) {
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        return Error.of("invalid.string.length", "Invalid " + name + " length");
    }

    public static Error collectionIsTooSmall(int min, int current) {
        return Error.of(
                "collection.is.too.small",
                "The collection must contain " + min + " items or more. It contains " + current + " items."
        );
    }

    public static Error collectionIsTooLarge(int max, int current) {
        return Error.of(
                "collection.is.too.large",
                "The collection must contain " + max + " items or fewer. It contains " + current + " items."
        );
    }

    public static <T extends Comparable<T>> Error valueIsOutOfRange(String name, T value, T min, T max) {
        if (isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }

        String message = String.format(
                "Value %s for %s is out of range. Min value is %s, max value is %s.",
                value, name, min, max
        );

        return Error.of("value.is.out.of.range", message);
    }

    public static Error internalServerError(String message) {
        return Error.of("internal.server.error", message);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }
}
