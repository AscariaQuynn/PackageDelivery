package cz.ascariaquynn.packagedelivery.services;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import cz.ascariaquynn.packagedelivery.config.AppOptionMissingException;
import cz.ascariaquynn.packagedelivery.config.AppOptions;
import cz.ascariaquynn.packagedelivery.model.InterimPackage;
import cz.ascariaquynn.packagedelivery.model.Package;
import cz.ascariaquynn.packagedelivery.services.exceptions.PackageServiceException;
import cz.ascariaquynn.packagedelivery.services.ui.HelpService;
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
import java.util.ArrayList;
import java.util.Comparator;
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
    private KillService killService;

    @Autowired
    private HelpService helpService;

    @Autowired
    private FeeService feeService;

    @Autowired
    private AppOptions appOptions;

    @Resource(name="optionPackages")
    private AppOption appOption;

    private List<Package> packages = new ArrayList<>();

    private Pattern pattern;

    private boolean packagesLoaded = false;

    @PostConstruct
    public void init() {
        logger.debug("Initializing...");
        helpService.addOption(appOption);
        pattern = Pattern.compile(appOption.getInputRegex());

        loadPackages();
    }

    private void loadPackages() {
        if(packagesLoaded) {
            return;
        }
        packagesLoaded = true;

        if (!appOptions.hasOption(appOption)) {
            killService.kill(new AppOptionMissingException("Option 'Packages' is required."), 1);
        }

        String packagesFilePath = appOptions.getOptionValue(appOption);
        logger.info("reading: " + packagesFilePath);
        try {
            Files.lines(Paths.get(packagesFilePath)).forEach(s -> addCandidate(s));
            logger.info("Added " + packages.size() + " packages.");
        } catch (NoSuchFileException e) {
            killService.kill(new PackageServiceException("Specified file '" + e.getFile() + "' does not exist.", e), 1);
        } catch (Exception e) {
            killService.kill(new PackageServiceException(e), 1);
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

    public boolean addCandidate(String inputText) {
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
        HashMap<String, InterimPackage> interimPackages = new HashMap<>();
        for(Package aPackage : packages) {
            InterimPackage interimPackage = interimPackages.get(aPackage.getPostCode());
            if(null == interimPackage) {
                interimPackages.put(aPackage.getPostCode(), interimPackage = new InterimPackage(aPackage.getPostCode()));
            }
            interimPackage.merge(aPackage);
        }

        TreeSet<InterimPackage> sorted = new TreeSet<>(new Comparator<InterimPackage>() {
            @Override
            public int compare(InterimPackage o1, InterimPackage o2) {
                return o2.getWeight().compareTo(o1.getWeight());
            }
        });
        for(InterimPackage interimPackage : interimPackages.values()) {
            sorted.add(interimPackage);
        }
        return sorted;
    }
}
