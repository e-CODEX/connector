/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.evidences;

import eu.ecodex.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.ecodex.evidences.ECodexEvidenceBuilder;
import eu.ecodex.evidences.types.ECodexMessageDetails;
import eu.spocseu.edeliverygw.REMErrorEvent;
import eu.spocseu.edeliverygw.configuration.EDeliveryDetails;
import eu.spocseu.edeliverygw.configuration.xsd.EDeliveryDetail;
import org.assertj.core.api.Assertions;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

@SuppressWarnings("checkstyle:LocalVariableName")
class EvidenceBuilderExplorationTests {
    @Test
    void testEvidenceBuilderWithoutValidKeyStore()
        throws DomibusConnectorEvidencesToolkitException {
        ECodexEvidenceBuilder eCodexEvidenceBuilder = new ECodexEvidenceBuilder(
            new ClassPathResource(""), "JKS", "", "", ""
        );

        byte[] submissionAcceptanceRejection =
            eCodexEvidenceBuilder.createSubmissionAcceptanceRejection(false, REMErrorEvent.OTHER,
                                                                      buildEDeliveryDetails(),
                                                                      buildMessageDetails()
            );
        // if there is an error the generated evidence gets null!
        Assertions.assertThat(submissionAcceptanceRejection).isNull();
    }

    @Test
    void testEvidenceBuilderWithValidKeyStore()
        throws DomibusConnectorEvidencesToolkitException {
        var eCodexEvidenceBuilder = new ECodexEvidenceBuilder(
            new ClassPathResource("/keystore/evidence_test.jks"), "JKS", "12345", "test", "12345"
        );

        byte[] submissionAcceptanceRejection =
            eCodexEvidenceBuilder.createSubmissionAcceptanceRejection(false, REMErrorEvent.OTHER,
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

        return new EDeliveryDetails(detail);
    }

    private ECodexMessageDetails buildMessageDetails()
        throws DomibusConnectorEvidencesToolkitException {

        var nationalMessageId = "nat1";
        var hash = "87213521ac44d4fe";

        ECodexMessageDetails messageDetails = new ECodexMessageDetails();

        messageDetails.setHashAlgorithm("MD5");
        if (hash != null) {
            messageDetails.setHashValue(Hex.decode(hash));
        }

        if (nationalMessageId == null || nationalMessageId.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                "the nationalMessageId may not be null for building a submission evidence!");
        }
        var recipientAddress = "recipientAddress";
        if (recipientAddress == null || recipientAddress.isEmpty()) {
            throw new DomibusConnectorEvidencesToolkitException(
                "the recipientAddress may not be null for building a submission evidence!");
        }
        var senderAddress = "sender";
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
