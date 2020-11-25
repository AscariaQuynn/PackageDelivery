package cz.ascariaquynn.packagedelivery.config.events;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import org.springframework.context.ApplicationEvent;

/**
 * Basic class that informs all interested beans that service with this option is added.
 */
public class AppOptionEvent extends ApplicationEvent {

    private AppOption appOption;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public AppOptionEvent(Object source, AppOption appOption) {
        super(source);

        this.appOption = appOption;
    }

    public AppOption getAppOption() {
        return appOption;
    }
}
