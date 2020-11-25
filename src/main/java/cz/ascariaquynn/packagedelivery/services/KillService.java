package cz.ascariaquynn.packagedelivery.services;

import cz.ascariaquynn.packagedelivery.controllers.UiController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class KillService {

    private static final Logger logger = LogManager.getLogger(KillService.class);

    @Autowired
    private ApplicationContext applicationContext;

    @Lazy
    @Autowired
    private UiController uiController;

    @Lazy
    @Autowired
    private HelpService helpService;

    public void exit(String message) {
        logger.debug("exiting gracefully...");
        uiController.killSay(message);
        SpringApplication.exit(applicationContext, () -> 0);
    }

    public void killWithHelp(Throwable throwable, final int code) {
        logger.debug("killing...");
        logger.throwing(throwable);
        uiController.killSay(throwable.getMessage());
        helpService.printHelp();
        int exitCode = SpringApplication.exit(applicationContext, () -> code);
        System.exit(exitCode);
    }

    public void kill(Throwable throwable, final int code) {
        logger.debug("killing...");
        logger.throwing(throwable);
        uiController.killSay(throwable.getMessage());
        int exitCode = SpringApplication.exit(applicationContext, () -> code);
        System.exit(exitCode);
    }
}
