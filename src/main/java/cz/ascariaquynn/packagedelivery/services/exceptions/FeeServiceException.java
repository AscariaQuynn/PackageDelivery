package cz.ascariaquynn.packagedelivery.services.exceptions;

public class FeeServiceException extends RuntimeException {

    public FeeServiceException(String message) {
        super(message);
    }

    public FeeServiceException(Throwable throwable) {
        super(throwable);
    }

    public FeeServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
