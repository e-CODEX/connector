package wp4.testenvironment.configurations;

import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.service.impl.dss.DSSECodexLegalValidationService;


// SUB_CONF_10
public class ValidConfig_BasicLegalValidator {
    // SUB_CONF_10 Variant 1
    public static ECodexLegalValidationService get_LegalValidator() {
        return new DSSECodexLegalValidationService();
    }
}
