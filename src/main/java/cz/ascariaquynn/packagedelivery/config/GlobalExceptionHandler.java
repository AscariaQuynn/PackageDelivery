package cz.ascariaquynn.packagedelivery.config;

import cz.ascariaquynn.packagedelivery.Util;
import cz.ascariaquynn.packagedelivery.config.exceptions.AppOptionMissingException;
import cz.ascariaquynn.packagedelivery.config.exceptions.UnhandledException;
import cz.ascariaquynn.packagedelivery.services.KillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;

import java.lang.reflect.Method;

@Component
@ControllerAdvice
public class GlobalExceptionHandler {

    @Autowired
    private KillService killService;

    @EventListener(ApplicationFailedEvent.class)
    public void onApplicationFailedEvent(ApplicationFailedEvent event) {
        // Handle which exception to resolve, because Spring likes to wrap everything to IllegalStateException
        Throwable exception = event.getException();
        if(exception instanceof IllegalStateException && "Failed to execute ApplicationRunner".equals(exception.getMessage())) {
            exception = exception.getCause();
        }
        // Try to match method in this class with exception as parameter
        try {
            Method methodWithArg = Util.findMethod(this, exception);
            if(null != methodWithArg) {
                methodWithArg.invoke(this, exception);
            }
        } catch (Exception e) {
            killService.kill(e, 1);
        }
        // If no match is found, fallback to default
        killService.kill(new UnhandledException("Unhandled exception: " + event.getException().getMessage(),
                event.getException()), 1);
    }

    public void handleAppOptionMissingException(AppOptionMissingException e) {
        killService.killWithHelp(e, 1);
    }

    public void handleRuntimeException(RuntimeException e) {
        killService.kill(e, 1);
    }
}
