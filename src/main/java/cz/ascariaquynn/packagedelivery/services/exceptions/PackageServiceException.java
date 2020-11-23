package cz.ascariaquynn.packagedelivery.services.exceptions;

public class PackageServiceException extends RuntimeException {

    public PackageServiceException(String message) {
        super(message);
    }

    public PackageServiceException(Throwable throwable) {
        super(throwable);
    }

    public PackageServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
