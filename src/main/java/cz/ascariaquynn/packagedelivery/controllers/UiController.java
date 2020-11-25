package cz.ascariaquynn.packagedelivery.controllers;

import cz.ascariaquynn.packagedelivery.Application;
import cz.ascariaquynn.packagedelivery.model.InterimPackage;
import cz.ascariaquynn.packagedelivery.services.PackageService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

@Controller
public class UiController {

    private static final Logger logger = LogManager.getLogger(UiController.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${packagedelivery.interimDelay}")
    private int printInterimDelay;

    @Value("${packagedelivery.exitCommand}")
    private String exitCommand;

    @Value("${packagedelivery.printCommand}")
    private String printCommand;

    @Autowired
    private PackageService packageService;

    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private Scanner scanner;

    private String asking = null;

    @PostConstruct
    public void init() {
        // Using Scanner for Getting Input from User
        scanner = new Scanner(System.in);
    }

    public void run() {
        logger.debug("Running UI...");
        packageService.ensureLoaded();
        runThreadPrintingInterimResults();

        say("Welcome to the " + applicationName + " app!",
                StringUtils.EMPTY,
                "This app will guide you through managing packages. ",
                "You can add packages and optionally see fees calculated for them.",
                "Once every minute, interim results will be printed to the terminal.",
                StringUtils.EMPTY,
                "Commands: print, exit",
                StringUtils.EMPTY);

        printInterimResults();

        while(true) {
            // Read user input
            String inputText = ask("Enter weight and postcode:");

            if(exitCommand.equalsIgnoreCase(inputText)) {
                break;
            }

            if(printCommand.equalsIgnoreCase(inputText)) {
                printInterimResults();
                continue;
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

        Application.exit("Bye!");
    }

    private void printInterimResults() {
        Set<InterimPackage> interimResults = packageService.getInterimResults();
        List<Object> saying = new LinkedList<>();
        saying.add("Printing interim results:");
        saying.addAll(interimResults);
        saying.add(StringUtils.EMPTY);
        say(saying.toArray(new Object[0]));
    }

    @Async
    public void runThreadPrintingInterimResults() {
        threadPoolTaskExecutor.execute(() -> {
            try {
                while (true) {
                    Thread.sleep(printInterimDelay);
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

    /**
     * Prints text to the terminal. In case of active asking for user input, this method will try to insert
     * text before question and then will reprint that question. Unfortunately this erases what user typed.
     * @param text
     */
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

    /**
     * Prints text and closes scanner. Because this method expects app to be killed, this class becomes unusable for UI services.
     * @param text
     */
    public void killSay(Object... text) {
        if(null != asking) {
            scanner.close();
            System.out.println(StringUtils.EMPTY);
        }
        for(Object line : text) {
            System.out.println(line);
        }
    }
}
