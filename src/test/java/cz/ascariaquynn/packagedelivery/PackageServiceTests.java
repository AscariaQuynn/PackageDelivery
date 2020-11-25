package cz.ascariaquynn.packagedelivery;

import cz.ascariaquynn.packagedelivery.config.AppOption;
import cz.ascariaquynn.packagedelivery.config.AppOptions;
import cz.ascariaquynn.packagedelivery.config.exceptions.AppOptionMissingException;
import cz.ascariaquynn.packagedelivery.model.InterimPackage;
import cz.ascariaquynn.packagedelivery.repositories.FileRepository;
import cz.ascariaquynn.packagedelivery.services.FeeService;
import cz.ascariaquynn.packagedelivery.services.PackageService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Stream;

@RunWith(MockitoJUnitRunner.class)
public class PackageServiceTests {

    private static final String PACKAGE_POSTCODE = "25068";
    private static final BigDecimal PACKAGE_WEIGHT = new BigDecimal("15.8");
    private static final String PACKAGE_INPUT_REGEX = "^(\\d+\\.?\\d{0,3}) (\\d{5})$";
    private static final String PACKAGES_FILE_PATH = "some path";

    @InjectMocks
    private PackageService packageService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private FeeService feeService;

    @Mock
    private AppOptions appOptions;

    @Mock
    private AppOption appOption;

    @Before
    public void resetMocks() {
        Mockito.reset(feeService, fileRepository, appOptions, appOption);
    }

    @Test(expected = AppOptionMissingException.class)
    public void packageServiceOptionMissingTest() {
        Mockito.when(appOption.getInputRegex()).thenReturn(PACKAGE_INPUT_REGEX);

        packageService.init();
        packageService.ensureLoaded();
    }

    @Test
    public void packageServiceLoadPackagesTest() throws IOException {
        Mockito.when(appOptions.hasOption(Mockito.any())).thenReturn(true);
        Mockito.when(appOptions.getOptionValue(Mockito.any())).thenReturn(PACKAGES_FILE_PATH);
        Mockito.when(appOption.getInputRegex()).thenReturn(PACKAGE_INPUT_REGEX);
        Mockito.when(fileRepository.loadLines(PACKAGES_FILE_PATH))
                .thenReturn(Stream.of(PACKAGE_WEIGHT + " " + PACKAGE_POSTCODE));

        packageService.init();
        packageService.ensureLoaded();

        Set<InterimPackage> interimResultsActual = packageService.getInterimResults();

        Assert.assertEquals("There should be 1 record in Interim Results",
                1, interimResultsActual.size());

        InterimPackage interimPackageActual = interimResultsActual.stream().findFirst().get();
        Assert.assertEquals("Wrong Interim Package PostCode.", PACKAGE_POSTCODE, interimPackageActual.getPostCode());
        Assert.assertEquals("Wrong Interim Package Weight.", PACKAGE_WEIGHT, interimPackageActual.getWeight());
    }

    @Test
    public void packageServiceMergingTest() throws IOException {
        Mockito.when(appOptions.hasOption(Mockito.any())).thenReturn(true);
        Mockito.when(appOptions.getOptionValue(Mockito.any())).thenReturn(PACKAGES_FILE_PATH);
        Mockito.when(appOption.getInputRegex()).thenReturn(PACKAGE_INPUT_REGEX);
        Mockito.when(fileRepository.loadLines(PACKAGES_FILE_PATH))
                .thenReturn(Stream.of(PACKAGE_WEIGHT + " " + PACKAGE_POSTCODE,
                        PACKAGE_WEIGHT + " " + PACKAGE_POSTCODE));

        packageService.init();
        packageService.ensureLoaded();

        Set<InterimPackage> interimResultsActual = packageService.getInterimResults();

        // Two packages with identical postcode should be merged into one
        Assert.assertEquals("There should be 1 record in Interim Results",
                1, interimResultsActual.size());

        // Two packages with identical postcode should have their weight merged.
        InterimPackage interimPackageActual = interimResultsActual.stream().findFirst().get();
        Assert.assertEquals("Wrong Interim Package PostCode.", PACKAGE_POSTCODE, interimPackageActual.getPostCode());
        Assert.assertEquals("Wrong Interim Package Merged Weight.",
                PACKAGE_WEIGHT.add(PACKAGE_WEIGHT), interimPackageActual.getWeight());
    }
}
