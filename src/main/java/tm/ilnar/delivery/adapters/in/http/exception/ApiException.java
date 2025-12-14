package tm.ilnar.delivery.adapters.in.http.exception;

import lombok.Getter;

@Getter
public abstract class ApiException extends RuntimeException {

    private final libs.errs.Error error;

    protected ApiException(libs.errs.Error error) {
        super(error.toString());
        this.error = error;
    }

    public libs.errs.Error error() {
        return error;
    }
}
