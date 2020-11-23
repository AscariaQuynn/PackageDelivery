package cz.ascariaquynn.packagedelivery.services;

import cz.ascariaquynn.packagedelivery.Application;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class KillService {

    private static final Logger logger = LogManager.getLogger(KillService.class);

    @Autowired
    private ApplicationContext applicationContext;

    public void kill(Throwable throwable, final int code) {
        logger.debug("killing...");
        logger.throwing(throwable);
        int exitCode = SpringApplication.exit(applicationContext, () -> code);
        System.exit(exitCode);
    }

    public void exitGracefully() {
        logger.debug("exiting gracefully...");
        Application.exit(applicationContext, 0);
    }
}
