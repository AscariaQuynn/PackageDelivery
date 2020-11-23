package cz.ascariaquynn.packagedelivery.services.ui;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HelpService {

    @Value("${spring.application.name}")
    private String applicationName;

    private List<AppOption> appOptions = new ArrayList<>();

    public void addOption(AppOption appOption) {
        appOptions.add(appOption);
    }

    public void printHelp() {

        System.out.println(applicationName + " usage:");
        System.out.println(StringUtils.EMPTY);

        for(AppOption appOption : appOptions) {

            System.out.println(StringUtils.EMPTY);
            System.out.println(appOption);

        }

    }
}
