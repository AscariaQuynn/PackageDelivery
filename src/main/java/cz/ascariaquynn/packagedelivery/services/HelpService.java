package cz.ascariaquynn.packagedelivery.services;

import cz.ascariaquynn.packagedelivery.Util;
import cz.ascariaquynn.packagedelivery.config.AppOption;
import cz.ascariaquynn.packagedelivery.controllers.UiController;
import cz.ascariaquynn.packagedelivery.services.events.RegisterHelpEvent;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@Service
public class HelpService {

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UiController uiController;

    private TreeSet<AppOption> appOptions = new TreeSet<>((o1, o2) -> o1.compareTo(o2));

    @EventListener(ApplicationStartedEvent.class)
    public void applicationStartedEvent() {
        eventPublisher.publishEvent(new RegisterHelpEvent(this, this));
    }

    public void addOption(AppOption appOption) {
        appOptions.add(appOption);
    }

    public void printHelp() {
        // Create help message
        List<String> saying = new ArrayList<>();
        saying.add(StringUtils.EMPTY);
        saying.add(applicationName + " usage: java -jar " + Util.getAppFileName(applicationName));
        saying.add(StringUtils.EMPTY);
        for(AppOption appOption : appOptions) {
            saying.set(1, saying.get(1) + appOption.getPrintableArgFormatted());
            saying.add(appOption.getPrintableFormatted());
        }

        saying.add(StringUtils.EMPTY);
        // Say
        uiController.say(saying.toArray(new String[0]));
    }
}
