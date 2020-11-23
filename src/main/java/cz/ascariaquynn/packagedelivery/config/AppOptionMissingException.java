package cz.ascariaquynn.packagedelivery.config;

public class AppOptionMissingException extends RuntimeException {

    public AppOptionMissingException(String message) {
        super(message);
    }

    public AppOptionMissingException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
