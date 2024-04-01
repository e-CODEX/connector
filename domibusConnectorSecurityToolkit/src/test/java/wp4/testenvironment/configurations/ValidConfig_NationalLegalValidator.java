package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexLegalValidationService;
import wp4.testenvironment.NationalDummyImplementations.National_LegalValidationService;


// SUB_CONF_09
public class ValidConfig_NationalLegalValidator {
    // SUB_CONF_09 Variant 1
    public static ECodexLegalValidationService get_NationalLegalValidator_FullData() {
        National_LegalValidationService workingService = new National_LegalValidationService();

        workingService.setValid_AllDataPresent();

        return workingService;
    }

    // SUB_CONF_09 Variant 2
    public static ECodexLegalValidationService get_NationalLegalValidator_NoDisclaimer() {
        National_LegalValidationService workingService = new National_LegalValidationService();

        workingService.setValid_NoDisclaimer();

        return workingService;
    }
}
