package cz.ascariaquynn.packagedelivery.services;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import cz.ascariaquynn.packagedelivery.config.AppOptions;
import cz.ascariaquynn.packagedelivery.repositories.FileRepository;
import cz.ascariaquynn.packagedelivery.services.events.RegisterHelpEvent;
import cz.ascariaquynn.packagedelivery.services.exceptions.FeeServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class FeeService {

    private static final Logger logger = LogManager.getLogger(FeeService.class);

    @Autowired
    private FileRepository fileRepository;

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
        pattern = Pattern.compile(appOption.getInputRegex());
    }

    @EventListener(RegisterHelpEvent.class)
    public void registerHelp(RegisterHelpEvent event) {
        event.registerHelp(appOption);
    }

    private void loadFees() {
        if(feesLoaded) {
            return;
        }
        feesLoaded = true;

        if (!appOptions.hasOption(appOption)) {
            logger.info("Missing option for Fees file. Initialization interrupted as Fees are not required option.");
            return;
        }

        String feesFilePath = appOptions.getOptionValue(appOption);
        if (StringUtils.isEmpty(feesFilePath)) {
            logger.info("Fees File Path is empty. Initialization interrupted as Fees are not required option.");
            return;
        }

        logger.info("reading: " + feesFilePath);
        try {
            fileRepository.loadLines(feesFilePath).forEach(s -> addCandidate(s));
            logger.info("Added " + fees.size() + " fees.");
        } catch (NoSuchFileException e) {
            throw new FeeServiceException("Fees File does not exist.", e);
        } catch (Exception e) {
            throw new FeeServiceException(e);
        }
    }

    public void addFee(BigDecimal weight, BigDecimal fee) {
        loadFees();
        fees.put(weight, fee);
    }

    /**
     * Tries to add new fee based on input text.
     * @param inputText
     * @return
     * @throws NullPointerException when input text is null
     */
    public boolean addCandidate(String inputText) {
        if(null == inputText) {
            throw new NullPointerException();
        }
        loadFees();
        Matcher matcher = pattern.matcher(inputText);
        boolean matchFound = matcher.find();
        if(matchFound) {
            BigDecimal weight = new BigDecimal(matcher.group(1));
            BigDecimal fee = new BigDecimal(matcher.group(2));
            addFee(weight, fee);
        }
        return matchFound;
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
