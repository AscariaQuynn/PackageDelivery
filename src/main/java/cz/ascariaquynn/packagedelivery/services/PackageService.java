package cz.ascariaquynn.packagedelivery.services;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import cz.ascariaquynn.packagedelivery.config.AppOptions;
import cz.ascariaquynn.packagedelivery.config.exceptions.AppOptionMissingException;
import cz.ascariaquynn.packagedelivery.model.InterimPackage;
import cz.ascariaquynn.packagedelivery.model.Package;
import cz.ascariaquynn.packagedelivery.repositories.FileRepository;
import cz.ascariaquynn.packagedelivery.services.events.RegisterHelpEvent;
import cz.ascariaquynn.packagedelivery.services.exceptions.PackageServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.math.BigDecimal;
import java.nio.file.NoSuchFileException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PackageService {

    private static final Logger logger = LogManager.getLogger(PackageService.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FeeService feeService;

    @Autowired
    private AppOptions appOptions;

    @Resource(name="optionPackages")
    private AppOption appOption;

    private List<Package> packages = new ArrayList<>();

    private Pattern pattern;

    private boolean packagesLoaded;

    @PostConstruct
    public void init() {
        logger.debug("Initializing...");
        packages.clear();
        pattern = Pattern.compile(appOption.getInputRegex());
        packagesLoaded = false;
    }

    @EventListener(RegisterHelpEvent.class)
    public void registerHelp(RegisterHelpEvent event) {
        event.registerHelp(appOption);
    }

    public void ensureLoaded() {
        loadPackages();
    }

    private void loadPackages() {
        if(packagesLoaded) {
            return;
        }
        packagesLoaded = true;

        if (!appOptions.hasOption(appOption)) {
            throw new AppOptionMissingException("Option 'Packages' is required.");
        }

        String packagesFilePath = appOptions.getOptionValue(appOption);
        if (StringUtils.isEmpty(packagesFilePath)) {
            throw new PackageServiceException("Packages File could not be loaded.");
        }


        logger.info("reading: " + packagesFilePath);
        try {
            fileRepository.loadLines(packagesFilePath).forEach(s -> addCandidate(s));
            logger.info("Added " + packages.size() + " packages.");
        } catch (NoSuchFileException e) {
            throw new PackageServiceException("Packages File does not exist.", e);
        } catch (Exception e) {
            throw new PackageServiceException(e);
        }
    }

    /**
     * Add package to list of packages.
     * @param postCode
     * @param weight
     */
    public void addPackage(String postCode, BigDecimal weight) {
        loadPackages();
        BigDecimal fee = feeService.hasFees() ? feeService.getFee(weight) : null;
        Package aPackage = new Package(postCode, weight, fee);
        logger.info("Adding package " + aPackage);
        packages.add(aPackage);
    }

    /**
     * Tries to add new package based on input text.
     * @param inputText
     * @return
     * @throws NullPointerException when input text is null
     */
    public boolean addCandidate(String inputText) {
        if(null == inputText) {
            throw new NullPointerException();
        }
        loadPackages();
        Matcher matcher = pattern.matcher(inputText);
        boolean matchFound = matcher.find();
        if(matchFound) {
            BigDecimal weight = new BigDecimal(matcher.group(1));
            String postCode = matcher.group(2);
            addPackage(postCode, weight);
        }
        return matchFound;
    }

    public Set<InterimPackage> getInterimResults() {
        loadPackages();
        HashMap<String, InterimPackage> interimPackages = new HashMap<>();
        for(Package aPackage : packages) {
            InterimPackage interimPackage = interimPackages.get(aPackage.getPostCode());
            if(null == interimPackage) {
                interimPackages.put(aPackage.getPostCode(), interimPackage = new InterimPackage(aPackage.getPostCode()));
            }
            interimPackage.merge(aPackage);
        }

        TreeSet<InterimPackage> sorted = new TreeSet<>((o1, o2) -> o2.getWeight().compareTo(o1.getWeight()));
        for(InterimPackage interimPackage : interimPackages.values()) {
            sorted.add(interimPackage);
        }
        return sorted;
    }
}
