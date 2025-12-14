package tm.ilnar.delivery.adapters.in.http.exception;

public final class ConflictException extends ApiException {

    public ConflictException(libs.errs.Error error) {
        super(error);
    }
}
