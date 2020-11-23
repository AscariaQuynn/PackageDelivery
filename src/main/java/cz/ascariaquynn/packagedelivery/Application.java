package cz.ascariaquynn.packagedelivery;

import cz.ascariaquynn.packagedelivery.services.ui.UiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class Application implements ApplicationRunner {

    @Autowired
    private UiService uiService;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    public static void exit(ApplicationContext applicationContext, final int exitCode) {
        SpringApplication.exit(applicationContext, () -> exitCode);
    }

    @Override
    public void run(ApplicationArguments args) {
        uiService.run();
    }
}
