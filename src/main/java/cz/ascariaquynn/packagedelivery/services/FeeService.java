package cz.ascariaquynn.packagedelivery.services;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import cz.ascariaquynn.packagedelivery.config.AppOptions;
import cz.ascariaquynn.packagedelivery.services.exceptions.FeeServiceException;
import cz.ascariaquynn.packagedelivery.services.ui.HelpService;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FeeService {

    private static final Logger logger = LogManager.getLogger(FeeService.class);

    @Autowired
    private KillService killService;

    @Autowired
    private HelpService helpService;

    @Autowired
    private AppOptions appOptions;

    @Resource(name="optionFees")
    private AppOption appOption;

    private Map<BigDecimal, BigDecimal> fees = new TreeMap<>();

    private Pattern pattern;

    private boolean feesLoaded = false;

    @PostConstruct
    public void init() {
        logger.debug("Initializing...");
        helpService.addOption(appOption);
        pattern = Pattern.compile(appOption.getInputRegex());

        loadFees();
    }

    private void loadFees() {
        if(feesLoaded) {
            return;
        }
        feesLoaded = true;

        if (!appOptions.hasOption(appOption)) {
            logger.info("Missing option for model file. Initialization interrupted as Fees are not required option.");
            return;
        }

        String feesFilePath = appOptions.getOptionValue(appOption);
        if (StringUtils.isEmpty(feesFilePath)) {
            logger.info("Fees File Path is empty. Initialization interrupted as Fees are not required option.");
            return;
        }

        logger.info("reading: " + feesFilePath);
        try {
            Files.lines(Paths.get(feesFilePath)).forEach(s -> {
                logger.info(s);

                Matcher matcher = pattern.matcher(s);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    BigDecimal weight = new BigDecimal(matcher.group(1));
                    BigDecimal fee = new BigDecimal(matcher.group(2));
                    addFee(weight, fee);
                }
                feesLoaded = true;
            });
        } catch (NoSuchFileException e) {
            killService.kill(new FeeServiceException("Specified file '" + e.getFile() + "' does not exist.", e), 1);
        } catch (Exception e) {
            killService.kill(new FeeServiceException(e), 1);
        }
    }

    public void addFee(BigDecimal weight, BigDecimal fee) {
        loadFees();
        fees.put(weight, fee);
    }

    public boolean hasFees() {
        loadFees();
        return !fees.isEmpty();
    }

    public BigDecimal getFee(BigDecimal weight) {
        loadFees();
        BigDecimal calculatedFeeWeight = BigDecimal.ZERO;
        for(BigDecimal feeWeight : fees.keySet()) {
            if(feeWeight.compareTo(weight) <= 0) {
                calculatedFeeWeight = feeWeight;
            } else {
                break;
            }
        }
        return fees.get(calculatedFeeWeight);
    }
}
