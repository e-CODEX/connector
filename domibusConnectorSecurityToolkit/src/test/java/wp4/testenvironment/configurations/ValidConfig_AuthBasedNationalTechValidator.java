package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import wp4.testenvironment.NationalDummyImplementations.National_Authentication_TechnicalValidationService;


// SUB-CONF-03
public class ValidConfig_AuthBasedNationalTechValidator {
    // SUB-CONF-03 Variant 1
    public static ECodexTechnicalValidationService get_AuthBasedNationalTechValidator() {
        National_Authentication_TechnicalValidationService workingService =
                new National_Authentication_TechnicalValidationService();

        workingService.setValid_AllDataPresent();

        return workingService;
    }
}
