package eu.domibus.connector.evidences;

import static org.assertj.core.api.Assertions.assertThat;

import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.common.service.DCBusinessDomainManager;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.transformer.util.LargeFileReferenceMemoryBacked;
import eu.domibus.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Optional;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class DomibusConnectorEvidencesToolkitTest {
    @SpringBootApplication(
        scanBasePackages = {"eu.domibus.connector.evidences", "eu.domibus.connector.common",
            "eu.domibus.connector.utils", "eu.domibus.connector.lib"}
    )
    public static class TestContext {}

    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorEvidencesToolkitTest.class);
    @MockBean
    DCBusinessDomainManager dcBusinessDomainManager;
    @Autowired
    private DomibusConnectorEvidencesToolkit evidencesToolkit;
    @Autowired
    private EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties;

    @BeforeEach
    public void beforeEach() {
        CurrentBusinessDomain.setCurrentBusinessDomain(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        Mockito.when(dcBusinessDomainManager.getBusinessDomain(
                   DomibusConnectorBusinessDomain.getDefaultMessageLaneId()))
               .thenReturn(Optional.of(DomibusConnectorBusinessDomain.getDefaultMessageLane()));
    }

    @Test
    void testCreateSubmissionAcceptance()
        throws DomibusConnectorEvidencesToolkitException, TransformerException {
        LOGGER.info("Started testCreateSubmissionAcceptance");

        DomibusConnectorMessage message = buildTestMessage();

        assertThat(evidencesToolkit).as("evidences toolkit must be init!").isNotNull();
        assertThat(message).as("message must not be null!").isNotNull();

        DomibusConnectorMessageConfirmation confirmation =
            evidencesToolkit.createEvidence(
                DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE, message, null, null
            );
        Assertions.assertNotNull(confirmation);
        String evidencePretty = prettyPrint(confirmation.getEvidence());
        LOGGER.info(evidencePretty);

        LOGGER.info("Finished testCreateSubmissionAcceptance");
    }

    @Test
    void testCreateSubmissionAcceptance_businessDocIsNull()
        throws DomibusConnectorEvidencesToolkitException, TransformerException {
        LOGGER.info("Started testCreateSubmissionAcceptance");

        DomibusConnectorMessage message = buildTestMessage_businessDocIsNull();

        assertThat(evidencesToolkit).as("evidences toolkit must be init!").isNotNull();
        assertThat(message).as("message must not be null!").isNotNull();

        DomibusConnectorMessageConfirmation confirmation =
            evidencesToolkit.createEvidence(DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                                            message, null, null
            );
        Assertions.assertNotNull(confirmation);
        String evidencePretty = prettyPrint(confirmation.getEvidence());
        LOGGER.info(evidencePretty);

        LOGGER.info("Finished testCreateSubmissionAcceptance");
    }

    @Test
    void testCreateSubmissionRejection() {
        LOGGER.info("Started testCreateSubmissionRejection");

        DomibusConnectorMessage message = buildTestMessage();

        try {
            DomibusConnectorMessageConfirmation confirmation = evidencesToolkit.createEvidence(
                DomibusConnectorEvidenceType.SUBMISSION_REJECTION,
                message,
                DomibusConnectorRejectionReason.OTHER, null
            );
            Assertions.assertNotNull(confirmation);
            String evidencePretty = prettyPrint(confirmation.getEvidence());
            LOGGER.info(evidencePretty);
        } catch (DomibusConnectorEvidencesToolkitException e) {
            e.printStackTrace();
            Assertions.fail("");
        } catch (TransformerFactoryConfigurationError e) {
            e.printStackTrace();
            Assertions.fail("");
        } catch (TransformerException e) {
            e.printStackTrace();
            Assertions.fail("");
        }
        LOGGER.info("Finished testCreateSubmissionRejection");
    }

    private DomibusConnectorMessage buildTestMessage() {
        DomibusConnectorMessageDetails details = new DomibusConnectorMessageDetails();
        details.setBackendMessageId("nationalMessageId1");
        details.setOriginalSender("someSenderAddress");
        details.setFinalRecipient("someRecipientAddress");

        DomibusConnectorMessageContent content = new DomibusConnectorMessageContent();

        LargeFileReferenceMemoryBacked ref =
            new LargeFileReferenceMemoryBacked("originalMessage".getBytes());

        DomibusConnectorMessageDocument document =
            new DomibusConnectorMessageDocument(ref, "documentName", null);

        content.setXmlContent("originalMessage".getBytes());
        content.setDocument(document);

        return new DomibusConnectorMessage(details, content);
    }

    private DomibusConnectorMessage buildTestMessage_businessDocIsNull() {
        DomibusConnectorMessageDetails details = new DomibusConnectorMessageDetails();
        details.setBackendMessageId("nationalMessageId1");
        details.setOriginalSender("someSenderAddress");
        details.setFinalRecipient("someRecipientAddress");

        DomibusConnectorMessageContent content = new DomibusConnectorMessageContent();

        var document = new DomibusConnectorMessageDocument(null, "documentName", null);

        content.setXmlContent("originalMessage".getBytes());
        content.setDocument(document);

        return new DomibusConnectorMessage(details, content);
    }

    private String prettyPrint(byte[] input)
        throws TransformerFactoryConfigurationError, TransformerException {
        // Instantiate transformer input
        Source xmlInput = new StreamSource(new ByteArrayInputStream(input));
        StreamResult xmlOutput = new StreamResult(new StringWriter());

        // Configure transformer
        Transformer transformer = TransformerFactory.newInstance().newTransformer(); // An
        // identity
        // transformer
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "testing.dtd");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(xmlInput, xmlOutput);

        return xmlOutput.getWriter().toString();
    }
}
