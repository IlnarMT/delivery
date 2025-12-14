package tm.ilnar.delivery.adapters.in.http.exception;

public final class BadRequestException extends ApiException {
    public BadRequestException(libs.errs.Error error) {
        super(error);
    }
}
