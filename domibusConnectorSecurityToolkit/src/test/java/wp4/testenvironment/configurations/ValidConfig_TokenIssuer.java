package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;


// SUB_CONF_15
public class ValidConfig_TokenIssuer {
    private static TokenIssuer issuer;

    // SUB_CONF_15 Variant 1
    public static TokenIssuer get_FullAuthenticationBased() {
        issuer = new TokenIssuer();

        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    // SUB_CONF_15 Variant 2
    public static TokenIssuer get_FullSignatureBased() {
        issuer = new TokenIssuer();

        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }
}
