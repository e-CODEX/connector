package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexTechnicalValidationService;
import wp4.testenvironment.NationalDummyImplementations.National_Signature_TechnicalValidationService;


// SUB_CONF_07
public class InvalidConfig_SigBasedNationalTechValidator {
    // SUB_CONF_07 Variant 1
    public static ECodexTechnicalValidationService get_SigBasedNationalTechValidator_NullResult() {
        National_Signature_TechnicalValidationService workingService =
                new National_Signature_TechnicalValidationService();

        workingService.setInvalid_NoResult();

        return workingService;
    }

    // SUB_CONF_07 Variant 2
    public static ECodexTechnicalValidationService get_SigBasedNationalTechValidator_EmptyResult() {
        National_Signature_TechnicalValidationService workingService =
                new National_Signature_TechnicalValidationService();

        workingService.setInvalid_EmptyResult();

        return workingService;
    }

    // SUB_CONF_07 Variant 3
    public static ECodexTechnicalValidationService get_SigBasedNationalTechValidator_InvalidResult() {
        National_Signature_TechnicalValidationService workingService =
                new National_Signature_TechnicalValidationService();

        workingService.setInvalid_InvalidResult();

        return workingService;
    }
}
