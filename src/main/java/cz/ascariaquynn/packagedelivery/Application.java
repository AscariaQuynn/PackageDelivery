package cz.ascariaquynn.packagedelivery;

import cz.ascariaquynn.packagedelivery.controllers.UiController;
import cz.ascariaquynn.packagedelivery.services.KillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Application implements ApplicationRunner {

    private static KillService killService;

    @Autowired
    private UiController uiController;

    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(Application.class, args);
    }

    /**
     * Convenient method to shutdown without the need to inject KillService everywhere.
     * Also i think in this case, KillEvent would be unnecessary overkill.
     * @param message
     */
    public static void exit(String message) {
        killService.exit(message);
    }

    @Autowired
    public void setKillService(KillService killService) {
        // Unfortunately static one, but i think is not too evil this time
        Application.killService = killService;
    }

    @Override
    public void run(ApplicationArguments args) {
        uiController.run();
    }
}
