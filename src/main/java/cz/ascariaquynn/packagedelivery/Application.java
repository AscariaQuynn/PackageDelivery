package cz.ascariaquynn.packagedelivery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import cz.ascariaquynn.packagedelivery.controllers.UiController;
import cz.ascariaquynn.packagedelivery.services.KillService;

@EnableAsync
@SpringBootApplication
public class Application implements ApplicationRunner {

    private static KillService killService;

    private UiController uiController;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * Convenient method to shutdown without the need to inject KillService everywhere.
     * Also i think in this case, KillEvent would be unnecessary overkill.
     * @param message message to show on application quit
     */
    public static void exit(String message) {
        killService.exit(message);
    }

    @Autowired
    public void setKillService(KillService killService) {
        // Unfortunately static one, but i think is not too evil this time
        Application.killService = killService;
    }

    @Autowired
    public void setUiController(UiController uiController) {
        this.uiController = uiController;
    }

    @Override
    public void run(ApplicationArguments args) {
        uiController.run();
    }
}
