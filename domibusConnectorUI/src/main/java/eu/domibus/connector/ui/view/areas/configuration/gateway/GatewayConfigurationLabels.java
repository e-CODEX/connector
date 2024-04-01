package eu.domibus.connector.ui.view.areas.configuration.gateway;


import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationLabel;


public class GatewayConfigurationLabels {
    public static final ConfigurationLabel gatewaySubmissionLinkLabels = new ConfigurationLabel(
            "Gateway submission webservice address",
            "connector.gatewaylink.ws.submission-endpoint-address",
            "The connector is using this webservice address to submit messages to the gateway.",
            "Example: http://127.0.0.1:8080/domibus/services/domibusConnectorSubmissionWebservice"
    );

    public static final ConfigurationLabel gatewayKeyStorePathLabels = new ConfigurationLabel(
            "Signing Keystore",
            "connector.gatewaylink.ws.key-store.path",
            "The File-Path to the keystore holding the certificate with which the connector signs and decrypts " +
                    "messages from/to the gateway. ",
            "The path ideally should be absolute and with a \"file:\" prefix. Also \"\\\" should be replaced by \"/\"" +
                    " " +
                    "or \"\\\\\"",
            "Example: file:C:/<anyPath>/connector.jks"
    );

    public static final ConfigurationLabel gatewayKeyStorePasswordLabels = new ConfigurationLabel(
            "Signing Keystore Password",
            "connector.gatewaylink.ws.key-store.password",
            "The Password of the keystore. "
    );

    public static final ConfigurationLabel gatewayKeyAliasLabels = new ConfigurationLabel(
            "Signing Key Alias",
            "connector.gatewaylink.ws.private-key.alias",
            "The alias of the private key with which the connector signs and decrypts messages from/to the gateway. "
    );

    public static final ConfigurationLabel gatewayKeyPasswordLabels = new ConfigurationLabel(
            "Signing Key Password",
            "connector.gatewaylink.ws.private-key.password",
            "The Password of the private key. "
    );

    public static final ConfigurationLabel gatewayTrustStorePathLabels = new ConfigurationLabel(
            "Truststore",
            "connector.gatewaylink.ws.trust-store.path",
            "The File-Path to the truststore holding the public certificate of the gateway's backend. When receiving " +
                    "a" +
                    " message from the gateway, ",
            "the connector validates the signature of it. Also, when sending a message to the gateway, the connector" +
                    " " +
                    "encrypts the message with ",
            "a public key of the gateway.",
            "The path ideally should be absolute and with a \"file:\" prefix. Also \"\\\" should be replaced by \"/\"" +
                    " " +
                    "or \"\\\\\"",
            "Example: file:C:/<anyPath>/connector.jks"
    );

    public static final ConfigurationLabel gatewayTrustStorePasswordLabels = new ConfigurationLabel(
            "Truststore Password",
            "connector.gatewaylink.ws.trust-store.password",
            "The Password of the truststore. "
    );

    public static final ConfigurationLabel gatewayEncryptAliasLabels = new ConfigurationLabel(
            "The alias to encrypt messages",
            "connector.gatewaylink.ws.encrypt-alias",
            "This is the alias of the public key with which messages sent to the gateway are encrypted. The " +
                    "corresponding private key  ",
            "must be within the keystore of the gateway which the domibus-connector-plugin references."
    );
}
