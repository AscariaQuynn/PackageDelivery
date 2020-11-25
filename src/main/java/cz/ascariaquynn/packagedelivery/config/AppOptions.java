package cz.ascariaquynn.packagedelivery.config;

import cz.ascariaquynn.packagedelivery.config.exceptions.AppOptionMissingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppOptions {

    @Autowired
    private ApplicationArguments args;

    public boolean hasOption(AppOption appOption) {
        boolean has = args.containsOption(appOption.getOpt()) || args.containsOption(appOption.getLongOpt());
        if(has && appOption.hasArg()) {
            // Options with argument needs to have argument correctly specified
            List<String> values = args.getOptionValues(appOption.getOpt());
            if(null == values) {
                values = args.getOptionValues(appOption.getLongOpt());
            }
            has = null != values && !values.isEmpty();
        }
        return has;
    }

    public String getOptionValue(AppOption appOption) {
        List<String> values = args.getOptionValues(appOption.getOpt());
        if(null == values) {
            values = args.getOptionValues(appOption.getLongOpt());
        }
        if(null == values || values.isEmpty()) {
            throw new AppOptionMissingException("Option '" + appOption + "' was not found.");
        }
        return values.get(0);
    }
}
