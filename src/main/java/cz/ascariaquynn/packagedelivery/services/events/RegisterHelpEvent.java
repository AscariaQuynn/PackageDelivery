package cz.ascariaquynn.packagedelivery.services.events;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import cz.ascariaquynn.packagedelivery.services.HelpService;
import org.springframework.context.ApplicationEvent;

/**
 * Basic event that collects any help information from interested beans.
 */
public class RegisterHelpEvent extends ApplicationEvent {

    private HelpService helpService;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public RegisterHelpEvent(Object source, HelpService helpService) {
        super(source);

        this.helpService = helpService;
    }

    public void registerHelp(AppOption appOption) {
        helpService.addOption(appOption);
    }
}
