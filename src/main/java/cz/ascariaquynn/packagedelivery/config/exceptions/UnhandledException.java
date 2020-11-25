package cz.ascariaquynn.packagedelivery.config.exceptions;

public class UnhandledException extends RuntimeException {

    public UnhandledException(String message, Throwable cause) {
        super(message, cause);
    }
}
