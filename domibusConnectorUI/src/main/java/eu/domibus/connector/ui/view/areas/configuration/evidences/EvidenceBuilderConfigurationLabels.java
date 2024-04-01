package eu.domibus.connector.ui.view.areas.configuration.evidences;


import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationLabel;


public class EvidenceBuilderConfigurationLabels {
    public static final ConfigurationLabel evidenceTimoutActiveLabels = new ConfigurationLabel(
            "Use the evidences timout job",
            "connector.controller.evidence.timeoutActive",
            "Used to turn on/off the check if evidences for outgoing messages have been received ",
            "within configurated time periods. Please see also the next few configuration settings."
    );

    public static final ConfigurationLabel checkTimoutIntervalLabels = new ConfigurationLabel(
            "Time period of timeout check to run",
            "connector.controller.evidence.checkTimeout",
            "Defines how often the timeout of evidences should be checked. ",
            "The default is every hour.",
            "Example: 1h"
    );

    public static final ConfigurationLabel relayTimoutLabels = new ConfigurationLabel(
            "Timeout for non-received RelayREMMD evidences",
            "connector.controller.evidence.relayREMMDTimeout",
            "After this period, if no RelayREMMD evidence was received for a successfully sent outgoing message, ",
            "a RelayREMMDRejection will be created for this message and sent back to the backend.",
            "Example: 24h"
    );

    public static final ConfigurationLabel deliveryTimoutLabels = new ConfigurationLabel(
            "Timeout for non-received Delivery/Non-Delivery evidences",
            "connector.controller.evidence.deliveryTimeout",
            "After this period, if no Delivery/Non-Delivery evidence was received for a successfully sent outgoing " +
                    "message, ",
            "a Non-Delivery evidence will be created for this message and sent back to the backend.",
            "Example: 24h"
    );

    public static final ConfigurationLabel retrievalTimoutLabels = new ConfigurationLabel(
            "Timeout for non-received Retrieval/Non-Retrieval evidences",
            "connector.controller.evidence.retrievalTimeout",
            "After this period, if no Retrieval/Non-Retrieval evidence was received for a successfully sent outgoing" +
                    " " +
                    "message, ",
            "a Non-Retrieval evidence will be created for this message and sent back to the backend.",
            "Example: 24h"
    );

    public static final ConfigurationLabel endpointAddressLabels = new ConfigurationLabel(
            "Endpoint address of the home gateway",
            "gateway.endpoint.address",
            "This value will be put into generated evidences. It tells the address of the sending gateway. ",
            "This value should match the endpoint address that is contained within the PModes. ",
            "When importing the PModes the address of the home gateway is tried to be resolved and set here."
    );

    public static final ConfigurationLabel gatewayNameLabels = new ConfigurationLabel(
            "Name of the home gateway",
            "gateway.name",
            "This value will be put into generated evidences. It tells the name of the sending gateway. ",
            "This value should match the name that is contained within the PModes. ",
            "When importing the PModes the name of the home gateway is tried to be resolved and set here."
    );

    public static final ConfigurationLabel postalAddressStreetLabels = new ConfigurationLabel(
            "Postal address - Street/Number",
            "postal.address.street",
            "This value will be put into generated evidences. It is part of the  postal address mentioned in " +
                    "evidences. "
    );

    public static final ConfigurationLabel postalAddressLocalityLabels = new ConfigurationLabel(
            "Postal address - City/Municipality",
            "postal.address.locality",
            "This value will be put into generated evidences. It is part of the  postal address mentioned in " +
                    "evidences. "
    );

    public static final ConfigurationLabel postalAddressPostalCodeLabels = new ConfigurationLabel(
            "Postal address - postal code / zip code",
            "postal.address.zip-code",
            "This value will be put into generated evidences. It is part of the  postal address mentioned in " +
                    "evidences. "
    );

    public static final ConfigurationLabel postalAddressCountryLabels = new ConfigurationLabel(
            "Postal address - Country",
            "postal.address.country",
            "This value will be put into generated evidences. It is part of the  postal address mentioned in " +
                    "evidences. "
    );

    public static final ConfigurationLabel evidencesKeyStorePathLabels = new ConfigurationLabel(
            "Evidences Keystore",
            "connector.evidences.key-store.path",
            "The File-Path to the keystore holding the certificate with which generated evidences are signed. ",
            "The path ideally should be absolute and with a \"file:\" prefix. Also \"\\\" should be replaced by " +
                    "\"/\"" +
                    " " +
                    "or \"\\\\\"",
            "Example: file:C:/<anyPath>/connector.jks"
    );

    public static final ConfigurationLabel evidencesKeyStorePasswordLabels = new ConfigurationLabel(
            "Evidences Keystore Password",
            "connector.evidences.key-store.password",
            "The Password of the keystore holding the certificate with which generated evidences are signed. "
    );

    public static final ConfigurationLabel evidencesKeyAliasLabels = new ConfigurationLabel(
            "Evidences Key Alias",
            "connector.evidences.private-key.alias",
            "The alias of the private key with which generated evidences are signed. "
    );

    public static final ConfigurationLabel evidencesKeyPasswordLabels = new ConfigurationLabel(
            "Evidences Key Password",
            "connector.evidences.private-key.password",
            "The Password of the private key with which generated evidences are signed. "
    );
}
