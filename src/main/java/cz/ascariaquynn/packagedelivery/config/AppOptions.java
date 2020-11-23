package cz.ascariaquynn.packagedelivery.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AppOptions {

    @Autowired
    private ApplicationArguments args;

    public boolean hasOption(AppOption appOption) {
        return args.containsOption(appOption.getOpt()) || args.containsOption(appOption.getLongOpt());
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
