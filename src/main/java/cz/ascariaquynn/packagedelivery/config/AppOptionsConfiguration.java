package cz.ascariaquynn.packagedelivery.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppOptionsConfiguration {

    @Bean()
    @ConfigurationProperties("packagedelivery.options.packages")
    protected AppOption optionPackages() {
        return new AppOption();
    }


    @Bean()
    @ConfigurationProperties("packagedelivery.options.fees")
    protected AppOption optionFees() {
        return new AppOption();
    }
}
