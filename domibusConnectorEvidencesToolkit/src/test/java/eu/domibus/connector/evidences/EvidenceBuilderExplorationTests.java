package eu.domibus.connector.evidences;

import eu.domibus.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.ecodex.evidences.ECodexEvidenceBuilder;
import eu.ecodex.evidences.exception.ECodexEvidenceBuilderException;
import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import org.assertj.core.api.Assertions;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


class EvidenceBuilderExplorationTests {
    @Test
    void testEvidenceBuilderWithoutValidKeyStore() throws ECodexEvidenceBuilderException,
            DomibusConnectorEvidencesToolkitException {
        ECodexEvidenceBuilder eCodexEvidenceBuilder =
                new ECodexEvidenceBuilder(new ClassPathResource(""), "JKS", "", "", "");

        byte[] submissionAcceptanceRejection = eCodexEvidenceBuilder.createSubmissionAcceptanceRejection(
                false,
                REMErrorEvent.OTHER,
                buildEDeliveryDetails(),
                buildMessageDetails()
        );
        // if there is an error the generated evidence gets null!
        Assertions.assertThat(submissionAcceptanceRejection).isNull();
    }

    @Test
    void testEvidenceBuilderWithtValidKeyStore() throws ECodexEvidenceBuilderException,
            DomibusConnectorEvidencesToolkitException {
        ECodexEvidenceBuilder eCodexEvidenceBuilder =
                new ECodexEvidenceBuilder(
                        new ClassPathResource("/keystore/evidence_test.jks"),
                        "JKS",
                        "12345",
                        "test",
                        "12345"
                );

        byte[] submissionAcceptanceRejection = eCodexEvidenceBuilder.createSubmissionAcceptanceRejection(
                false,
                REMErrorEvent.OTHER,
                buildEDeliveryDetails(),
                buildMessageDetails()
        );

        Assertions.assertThat(submissionAcceptanceRejection).isNotNull();
    }

    private EDeliveryDetails buildEDeliveryDetails() {
        EDeliveryDetail detail = new EDeliveryDetail();

        EDeliveryDetail.Server server = new EDeliveryDetail.Server();
        server.setGatewayName("GWName");
        server.setGatewayAddress("GWAddress");
        detail.setServer(server);

        EDeliveryDetail.PostalAdress postalAddress = new EDeliveryDetail.PostalAdress();
        postalAddress.setStreetAddress("postStreet");
        postalAddress.setLocality("postLocality");
        postalAddress.setPostalCode("postZipCode");
        postalAddress.setCountry("postCountry");
        detail.setPostalAdress(postalAddress);

        EDeliveryDetails evidenceIssuerDetails = new EDeliveryDetails(detail);
        return evidenceIssuerDetails;
    }

    private ECodexMessageDetails buildMessageDetails() throws DomibusConnectorEvidencesToolkitException {

        String nationalMessageId = "nat1";
        String senderAddress = "sender";
        String recipientAddress = "recipientAddress";
        String hash = "87213521ac44d4fe";

        ECodexMessageDetails messageDetails = new ECodexMessageDetails();

        messageDetails.setHashAlgorithm("MD5");
        if (hash != null) messageDetails.setHashValue(Hex.decode(hash));

        if (nationalMessageId == null || nationalMessageId.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                    "the nationalMessageId may not be null for building a submission evidence!");
        }
        if (recipientAddress == null || recipientAddress.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                    "the recipientAddress may not be null for building a submission evidence!");
        }
        if (senderAddress == null || senderAddress.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                    "the senderAddress may not be null for building a submission evidence!");
        }
        messageDetails.setNationalMessageId(nationalMessageId);
        messageDetails.setRecipientAddress(recipientAddress);
        messageDetails.setSenderAddress(senderAddress);
        return messageDetails;
    }
}
