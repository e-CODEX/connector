package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import wp4.testenvironment.NationalDummyImplementations.National_Authentication_TechnicalValidationService;


// SUB-CONF_06
public class InvalidConfig_AuthBasedNationalTechValidator {
    // SUB-CONF_06 Variant 1
    public static ECodexTechnicalValidationService get_AuthBasedNationalTechValidator_NullResult() {
        National_Authentication_TechnicalValidationService workingService =
                new National_Authentication_TechnicalValidationService();

        workingService.setInvalid_NullResult();

        return workingService;
    }

    // SUB-CONF_06 Variant 2
    public static ECodexTechnicalValidationService get_AuthBasedNationalTechValidator_EmptyResult() {
        National_Authentication_TechnicalValidationService workingService =
                new National_Authentication_TechnicalValidationService();

        workingService.setInvalid_EmptyResult();

        return workingService;
    }

    // SUB-CONF_06 Variant 3
    public static ECodexTechnicalValidationService get_AuthBasedNationalTechValidator_InvalidResult() {
        National_Authentication_TechnicalValidationService workingService =
                new National_Authentication_TechnicalValidationService();

        workingService.setInvalid_InvalidResult();

        return workingService;
    }
}
