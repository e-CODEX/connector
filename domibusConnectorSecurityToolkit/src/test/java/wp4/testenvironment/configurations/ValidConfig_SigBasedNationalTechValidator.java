package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import wp4.testenvironment.NationalDummyImplementations.National_Signature_TechnicalValidationService;


// SUB_CONF_04
public class ValidConfig_SigBasedNationalTechValidator {
    // SUB_CONF_04 Variant 1
    public static ECodexTechnicalValidationService get_SigBasedNationalTechValidator() {
        National_Signature_TechnicalValidationService workingService =
                new National_Signature_TechnicalValidationService();

        workingService.setValid_AllDataPresent();

        return workingService;
    }
}
