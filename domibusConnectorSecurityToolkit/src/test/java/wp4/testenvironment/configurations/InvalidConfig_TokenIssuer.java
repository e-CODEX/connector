package wp4.testenvironment.configurations;

import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.TokenIssuer;


// SUB_CONF_16
public class InvalidConfig_TokenIssuer {
    private static TokenIssuer issuer;

    // SUB_CONF_16 Variant 1
    // Authentication Based & Signature Based - No AdvancedElectronicSystem
    public static TokenIssuer get_NoAdvancedElectronicSystem() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(null);
        issuer.setCountry("DE");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    // SUB_CONF_16 Variant 2
    // Authentication Based	- No Country
    public static TokenIssuer get_AuthBased_NoCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry(null);
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    // SUB_CONF_16 Variant 3
    // Authentication Based	- No Service Provider
    public static TokenIssuer get_AuthBased_NoServiceProvider() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider(null);

        return issuer;
    }

    // SUB_CONF_16 Variant 4
    // Authentication Based	- Not-ISO-3166 Country
    public static TokenIssuer get_AuthBased_NoISOCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.AUTHENTICATION_BASED);
        issuer.setCountry("Deutschland");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    // SUB_CONF_16 Variant 5
    // Signature Based - No Country
    public static TokenIssuer get_SigBased_NoCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry(null);
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }

    // SUB_CONF_16 Variant 6
    // Signature Based	- No Service Provider
    public static TokenIssuer get_SigBased_NoServiceProvider() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry("DE");
        issuer.setServiceProvider(null);

        return issuer;
    }

    // SUB_CONF_16 Variant 7
    // Signature Based	- Not-ISO-3166 Country
    public static TokenIssuer get_SigBased_NoISOCountry() {
        issuer = new TokenIssuer();
        issuer.setAdvancedElectronicSystem(AdvancedSystemType.SIGNATURE_BASED);
        issuer.setCountry("Deutschland");
        issuer.setServiceProvider("Test Inc.");

        return issuer;
    }
}
