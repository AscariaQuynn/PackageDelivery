package cz.ascariaquynn.packagedelivery.services.ui;

import cz.ascariaquynn.packagedelivery.model.InterimPackage;
import cz.ascariaquynn.packagedelivery.services.KillService;
import cz.ascariaquynn.packagedelivery.services.PackageService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Scanner;
import java.util.Set;

@Service
public class UiService {

    private static final Logger logger = LogManager.getLogger(UiService.class);

    private static final String EXIT_COMMAND = "quit";

    private static final int PRINT_INTERIM_DELAY = 60 * 1000;

    @Value("${spring.application.name}")
    private String applicationName;

    @Autowired
    private ThreadPoolTaskExecutor asyncExecutor;

    @Autowired
    private KillService killService;

    @Autowired
    private PackageService packageService;

    private Scanner scanner;

    private String asking = null;

    @PostConstruct
    public void init() {
        // Using Scanner for Getting Input from User
        scanner = new Scanner(System.in);
    }

    public void run() {
        logger.debug("Running UI...");
        runThreadPrintingInterimResults();

        say("Welcome to the " + applicationName + " app!",
                StringUtils.EMPTY,
                "This app will guide you through managing packages. ",
                "You can add packages and optionally see model calculated for them.",
                "Once every minute, data will be printed to the terminal.",
                StringUtils.EMPTY);

        printInterimResults();

        while(true) {
            // Read user input
            String inputText = ask("Enter weight and postcode:");

            if(EXIT_COMMAND.equalsIgnoreCase(inputText)) {
                break;
            }

            if(packageService.addCandidate(inputText)) {
                say("Added: '" + inputText + "'...",
                        StringUtils.EMPTY);
            } else {
                say("Wrong input. Please type weight as a number with optional",
                        "max 3 decimals, then space and 5 digit postal code.",
                        StringUtils.EMPTY);
            }
        }

        say("Bye!");
        killService.exitGracefully();
    }

    private void printInterimResults() {
        Set<InterimPackage> interimResults = packageService.getInterimResults();
        say("Printing interim results:");
        say(interimResults.toArray(new InterimPackage[0]));
        say(StringUtils.EMPTY);
    }

    @Async
    public void runThreadPrintingInterimResults() {
        asyncExecutor.execute(() -> {
            try {
                while (true) {
                    Thread.sleep(PRINT_INTERIM_DELAY);
                    printInterimResults();
                }
            } catch (InterruptedException e) {
                // Sleep interrupted
                if(e.getMessage().equals("sleep interrupted")) {
                    logger.info(e.getMessage());
                } else {
                    logger.error(e);
                }
            }
        });
    }

    public String ask(String line) {
        asking = line.trim() + " ";
        System.out.print(asking);
        String inputText = scanner.nextLine();
        asking = null;
        return inputText;
    }

    public void say(Object... text) {
        if(null != asking) {
            // Go back X lines
            System.out.print("\033[1A\r \r");
            // Print current saying
            for(Object line : text) {
                System.out.println(line);
            }
            // Go forward and reprint asked question (Unfortunately this will erase user input)
            System.out.print("\033[1B\r");
            // Re-ask
            System.out.print(asking);
        } else {
            for(Object line : text) {
                System.out.println(line);
            }
        }
    }
}
