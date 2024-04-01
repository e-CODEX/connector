package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexLegalValidationService;
import wp4.testenvironment.NationalDummyImplementations.National_LegalValidationService;


// SUB-CONF-11
public class InvalidConfig_NationalLegalValidator {
    // SUB-CONF-11 Variant 1
    public static ECodexLegalValidationService get_NationalLegalValidator_NullResult() {
        National_LegalValidationService workingService = new National_LegalValidationService();

        workingService.setInvalid_NullResult();

        return workingService;
    }

    // SUB-CONF-11 Variant 2
    public static ECodexLegalValidationService get_NationalLegalValidator_EmptyResult() {
        National_LegalValidationService workingService = new National_LegalValidationService();

        workingService.setInvalid_EmptyResult();

        return workingService;
    }

    // SUB-CONF-11 Variant 3
    public static ECodexLegalValidationService get_NationalLegalValidator_MissingTrustLevel() {
        National_LegalValidationService workingService = new National_LegalValidationService();

        workingService.setInvalid_MissingTrustLevel();

        return workingService;
    }
}
