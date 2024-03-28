package eu.domibus.connector.evidences;

import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.common.service.DCBusinessDomainManager;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.DomibusConnectorRejectionReason;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.transformer.util.LargeFileReferenceMemoryBacked;
import eu.domibus.connector.evidences.exception.DomibusConnectorEvidencesToolkitException;
import eu.domibus.connector.evidences.spring.EvidencesToolkitConfigurationProperties;
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

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;


@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
class DomibusConnectorEvidencesToolkitTest {
    private static Logger LOG = LoggerFactory.getLogger(DomibusConnectorEvidencesToolkitTest.class);

    @MockBean
    DCBusinessDomainManager dcBusinessDomainManager;
    @Autowired
    private DomibusConnectorEvidencesToolkit evidencesToolkit;
    @Autowired
    private EvidencesToolkitConfigurationProperties evidencesToolkitConfigurationProperties;

    @BeforeEach
    public void beforeEach() {
        CurrentBusinessDomain.setCurrentBusinessDomain(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
        Mockito
                .when(dcBusinessDomainManager.getBusinessDomain(eq(DomibusConnectorBusinessDomain.getDefaultMessageLaneId())))
                .thenReturn(Optional.of(DomibusConnectorBusinessDomain.getDefaultMessageLane()));
    }

    @Test
    void testCreateSubmissionAcceptance() throws DomibusConnectorEvidencesToolkitException,
            TransformerException {
        LOG.info("Started testCreateSubmissionAcceptance");

        DomibusConnectorMessage message = buildTestMessage();

        assertThat(evidencesToolkit).as("evidences toolkit must be init!").isNotNull();
        assertThat(message).as("message must not be null!").isNotNull();

        DomibusConnectorMessageConfirmation confirmation =
                evidencesToolkit.createEvidence(
                        DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        message,
                        null,
                        null
                );
        Assertions.assertNotNull(confirmation);
        String evidencePretty = prettyPrint(confirmation.getEvidence());
        LOG.info(evidencePretty);

        LOG.info("Finished testCreateSubmissionAcceptance");
    }

    @Test
    void testCreateSubmissionAcceptance_businessDocIsNull() throws DomibusConnectorEvidencesToolkitException,
            TransformerException {
        LOG.info("Started testCreateSubmissionAcceptance");

        DomibusConnectorMessage message = buildTestMessage_businessDocIsNull();

        assertThat(evidencesToolkit).as("evidences toolkit must be init!").isNotNull();
        assertThat(message).as("message must not be null!").isNotNull();

        DomibusConnectorMessageConfirmation confirmation =
                evidencesToolkit.createEvidence(
                        DomibusConnectorEvidenceType.SUBMISSION_ACCEPTANCE,
                        message,
                        null,
                        null
                );
        Assertions.assertNotNull(confirmation);
        String evidencePretty = prettyPrint(confirmation.getEvidence());
        LOG.info(evidencePretty);

        LOG.info("Finished testCreateSubmissionAcceptance");
    }

    @Test
    void testCreateSubmissionRejection() {
        LOG.info("Started testCreateSubmissionRejection");

        DomibusConnectorMessage message = buildTestMessage();

        try {
            DomibusConnectorMessageConfirmation confirmation = evidencesToolkit.createEvidence(
                    DomibusConnectorEvidenceType.SUBMISSION_REJECTION,
                    message,
                    DomibusConnectorRejectionReason.OTHER,
                    null
            );
            Assertions.assertNotNull(confirmation);
            String evidencePretty = prettyPrint(confirmation.getEvidence());
            LOG.info(evidencePretty);
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
        LOG.info("Finished testCreateSubmissionRejection");
    }

    private DomibusConnectorMessage buildTestMessage() {
        DomibusConnectorMessageDetails details = new DomibusConnectorMessageDetails();
        details.setBackendMessageId("nationalMessageId1");
        details.setOriginalSender("someSenderAddress");
        details.setFinalRecipient("someRecipientAddress");

        DomibusConnectorMessageContent content = new DomibusConnectorMessageContent();

        LargeFileReferenceMemoryBacked ref = new LargeFileReferenceMemoryBacked("originalMessage".getBytes());

        DomibusConnectorMessageDocument document = new DomibusConnectorMessageDocument(ref, "documentName", null);

        content.setXmlContent("originalMessage".getBytes());
        content.setDocument(document);

        DomibusConnectorMessage message = new DomibusConnectorMessage(details, content);

        return message;
    }

    private DomibusConnectorMessage buildTestMessage_businessDocIsNull() {
        DomibusConnectorMessageDetails details = new DomibusConnectorMessageDetails();
        details.setBackendMessageId("nationalMessageId1");
        details.setOriginalSender("someSenderAddress");
        details.setFinalRecipient("someRecipientAddress");

        DomibusConnectorMessageContent content = new DomibusConnectorMessageContent();
        // LargeFileReferenceMemoryBacked ref = new LargeFileReferenceMemoryBacked("originalMessage".getBytes());
        DomibusConnectorMessageDocument document = new DomibusConnectorMessageDocument(null, "documentName", null);

        content.setXmlContent("originalMessage".getBytes());
        content.setDocument(document);

        DomibusConnectorMessage message = new DomibusConnectorMessage(details, content);

        return message;
    }

    private String prettyPrint(byte[] input) throws TransformerFactoryConfigurationError, TransformerException {
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

    @SpringBootApplication(
            scanBasePackages = {"eu.domibus.connector.evidences", "eu.domibus.connector.common", "eu.domibus" +
                    ".connector.utils", "eu.domibus.connector.lib"}
    )
    public static class TestContext {
    }
}
