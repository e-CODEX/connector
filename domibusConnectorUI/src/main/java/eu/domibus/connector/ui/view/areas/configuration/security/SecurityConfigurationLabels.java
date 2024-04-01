package eu.domibus.connector.ui.view.areas.configuration.security;


import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationLabel;


public class SecurityConfigurationLabels {
    public static final ConfigurationLabel tokenIssuerCountryLabels = new ConfigurationLabel(
            "Token issuer - Country",
            "token.issuer.country",
            "The ISO 2 Countrycode of the token issuer. "
    );

    public static final ConfigurationLabel tokenIssuerIdentityProvider = new ConfigurationLabel(
            "Token issuer - Identity Provider",
            "token.issuer.identity-provider",
            "Who has authenticated the sending system, only needed if AUTHENTICATION_BASED is enabled"
    );

    public static final ConfigurationLabel tokenIssuerServiceProviderLabels = new ConfigurationLabel(
            "Token issuer - Serviceprovider",
            "token.issuer.service-provider",
            "The service provider name of the token issuer. "
    );

    public static final ConfigurationLabel tokenIssuerAESValueLabels = new ConfigurationLabel(
            "Token issuer - Advanced Electronic System",
            "token.issuer.advanced-electronic-system-type",
            "This value indicates whether the main documents used in messages are already signed by the backend or " +
                    "not" +
                    ". ",
            "Values:",
            "SIGNATURE_BASED: The main document is signed. The signature is validated by the connector using the " +
                    "certificate which has to be in the truststore. ",
            "AUTHENTICATION_BASED: The main document is NOT signed and comes from an advanced system that is in the " +
                    "same closed network as the connector is."
    );

    public static final ConfigurationLabel lotlSchemeURILabels = new ConfigurationLabel(
            "List of trusted lists - Scheme URI",
            "security.lotl.scheme.uri",
            "The URI where the connector can load the scheme of the list of trusted lists. "
    );

    public static final ConfigurationLabel lotlURLLabels = new ConfigurationLabel(
            "List of trusted lists - URL",
            "security.lotl.url",
            "The internet address where the connector can load the list of trusted lists. "
    );

    public static final ConfigurationLabel ojURLLabels = new ConfigurationLabel(
            "Trusted lists -  oj URL",
            "security.oj.url",
            "The internet address where the connector can load the oj for the trusted lists. "
    );

    public static final ConfigurationLabel securityKeyStorePathLabels = new ConfigurationLabel(
            "Signing Keystore",
            "connector.security.key-store.path",
            "The File-Path to the keystore holding the certificate with which the built secure container (ASIC-S) is " +
                    "signed. ",
            "The path ideally should be absolute and with a \"file:\" prefix. Also \"\\\" should be replaced by \"/\"" +
                    " " +
                    "or \"\\\\\"",
            "Example: file:C:/<anyPath>/connector.jks"
    );

    public static final ConfigurationLabel securityKeyStorePasswordLabels = new ConfigurationLabel(
            "Signing Keystore Password",
            "connector.security.key-store.password",
            "The Password of the keystore holding the certificate with which the built secure container (ASIC-S) is " +
                    "signed. "
    );

    public static final ConfigurationLabel securityKeyAliasLabels = new ConfigurationLabel(
            "Signing Key Alias",
            "connector.security.private-key.alias",
            "The alias of the private key with which the built secure container (ASIC-S) is signed. "
    );

    public static final ConfigurationLabel securityKeyPasswordLabels = new ConfigurationLabel(
            "Signing Key Password",
            "connector.security.private-key.password",
            "The Password of the private key with which the built secure container (ASIC-S) is signed. "
    );

    public static final ConfigurationLabel securityTrustStorePathLabels = new ConfigurationLabel(
            "Truststore",
            "connector.security.trust-store.path",
            "The File-Path to the truststore holding all public certificates of partners. When receiving a message " +
                    "the" +
                    " connector validates the ",
            "signature of the secure container (ASIC-S) by using the certificates in this store.",
            "The path ideally should be absolute and with a \"file:\" prefix. Also \"\\\" should be replaced by \"/\"" +
                    " " +
                    "or \"\\\\\"",
            "Example: file:C:/<anyPath>/connector.jks"
    );

    public static final ConfigurationLabel securityTrustStorePasswordLabels = new ConfigurationLabel(
            "Truststore Password",
            "connector.security.trust-store.password",
            "The Password of the truststore holding the certificates with which the built secure container's " +
                    "signature" +
                    " can be validated. "
    );
}
