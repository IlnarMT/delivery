package libs.errs;

public final class UnitResult<E> {
    private final boolean isSuccess;
    private final E error;

    private UnitResult(boolean isSuccess, E error) {
        this.isSuccess = isSuccess;
        this.error = error;
    }

    public static <E> UnitResult<E> success() {
        return new UnitResult<>(true, null);
    }

    public static <E> UnitResult<E> failure(E error) {
        if (error == null) throw new IllegalArgumentException("Error must not be null on failure");
        return new UnitResult<>(false, error);
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public boolean isFailure() {
        return !isSuccess;
    }

    public E getError() {
        if (isSuccess)
            throw new IllegalStateException("Cannot get error from a successful result");
        return error;
    }

    public UnitResult<E> merge(UnitResult<E> other) {
        if (this.isFailure()) return this;
        if (other.isFailure()) return other;
        return this;
    }

    @SafeVarargs
    public static <E> UnitResult<E> combine(UnitResult<E>... results) {
        for (UnitResult<E> r : results) {
            if (r.isFailure()) return r;
        }
        return success();
    }

    public static <E> UnitResult<E> from(Result<Void, E> result) {
        return result.isSuccess()
                ? UnitResult.success()
                : UnitResult.failure(result.getError());
    }

    public Result<Void, E> toResult() {
        return isFailure()
                ? Result.failure(error)
                : Result.success();
    }

    @Override
    public String toString() {
        return isSuccess
                ? "Success"
                : "Failure(" + error + ")";
    }
}