/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.container.service;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.common.service.CurrentBusinessDomain;
import eu.ecodex.connector.domain.enums.AdvancedElectronicSystemType;
import eu.ecodex.connector.domain.model.DCMessageProcessSettings;
import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.ecodex.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.service.ECodexException;
import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.transform.stream.StreamSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * This class is an abstract template for integration tests of ECodexContainerFactoryService. It
 * provides common functionality and setup for the test cases.
 */
public abstract class ECodexContainerFactoryServiceITCaseTemplate {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(ECodexContainerFactoryServiceITCaseTemplate.class);
    public static final String TEST_FILE_RESULTS_DIR_PROPERTY_NAME = "test.file.results";
    private static File TEST_RESULTS_FOLDER;
    @Autowired
    ECodexContainerFactoryService containerFactoryService;

    ECodexContainerFactoryService getECodexContainerFactoryService() {
        return containerFactoryService;
    }

    /**
     * Initializes the class by creating the necessary test results folder.
     *
     * @param testInfo the information about the test being executed
     */
    @BeforeAll
    public static void initClass(TestInfo testInfo) {
        var dir = System.getenv().getOrDefault(
            TEST_FILE_RESULTS_DIR_PROPERTY_NAME,
            "./target/testfileresults/"
        );
        dir = dir + "/" + testInfo.getTestClass().get().getSimpleName();
        TEST_RESULTS_FOLDER = new File(dir);
        TEST_RESULTS_FOLDER.mkdirs();
    }

    @BeforeEach
    public void beforeEach() {
        CurrentBusinessDomain.setCurrentBusinessDomain(
            DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
    }

    @AfterEach
    public void afterEach() {
        CurrentBusinessDomain.setCurrentBusinessDomain(null);
    }

    @Test
    @DisplayName("Build ASIC-S container with XML as business doc")
    public void createContainerFromXML(TestInfo testInfo) throws ECodexException, IOException {
        var containerService = getECodexContainerFactoryService().createECodexContainerService(
            DomainEntityCreator.createEpoMessage()
        );

        var businessContent = new BusinessContent();

        // bytes name mimetype
        var businessDoc = loadDocumentFromResource(
            "examples/ExampleXmlSigned.xml",
            "ExampleXmlSigned.xml",
            MimeTypeEnum.XML
        );
        businessContent.setDocument(businessDoc);

        var container = containerService.create(businessContent);

        var asicDocument = container.getAsicDocument();
        assertThat(asicDocument).isNotNull();
        //
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        var tokenXML = container.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        // check if produced container and token can also be resolved again
        var asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        var tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        containerService = getECodexContainerFactoryService().createECodexContainerService(
            DomainEntityCreator.createEpoMessage()
        );
        containerService.receive(
            new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml)
        );
    }

    @Test
    @DisplayName("Build ASIC-S container with XML authentication based")
    public void createContainerFromXMLWithAuthenticationBased(TestInfo testInfo)
        throws ECodexException, IOException, JAXBException {
        var settings = new DCMessageProcessSettings();
        settings.setValidationServiceName(AdvancedElectronicSystemType.AUTHENTICATION_BASED);
        var msg = DomibusConnectorMessageBuilder
            .createBuilder()
            .setMessageDetails(DomibusConnectorMessageDetailsBuilder
                                   .create()
                                   .withOriginalSender("originalSender")
                                   .build()
            ).build();
        msg.setDcMessageProcessSettings(settings);

        var containerService = getECodexContainerFactoryService().createECodexContainerService(msg);
        var businessContent = new BusinessContent();
        // bytes name mimetype
        var businessDoc = loadDocumentFromResource(
            "examples/ExampleXmlSigned.xml",
            "ExampleXmlSigned.xml",
            MimeTypeEnum.XML
        );
        businessContent.setDocument(businessDoc);

        var codexContainer = containerService.create(businessContent);

        var asicDocument = codexContainer.getAsicDocument();
        assertThat(asicDocument).isNotNull();
        //
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        var tokenXML = codexContainer.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        // check if produced container and token can also be resolved again
        var asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        var tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        containerService = getECodexContainerFactoryService().createECodexContainerService(
            DomainEntityCreator.createEpoMessage()
        );
        containerService.receive(
            new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml)
        );

        var jaxbContext = JAXBContext.newInstance(Token.class);
        var unmarshaller = jaxbContext.createUnmarshaller();
        var jaxbToken = unmarshaller.unmarshal(
            new StreamSource(new ByteArrayInputStream(tokenXml)),
            Token.class
        );

        var token = jaxbToken.getValue();

        assertThat(token.getAdvancedElectronicSystem())
            .as("The system type has been passed with the message and should be "
                    + "AUTHENTICATION BASED!"
            )
            .isEqualTo(AdvancedSystemType.AUTHENTICATION_BASED);
    }

    @Test
    @DisplayName("Build ASIC-S container with ASIC-S as business doc")
    public void createContainerFromAsicS(TestInfo testInfo) throws ECodexException, IOException {
        var containerService = getECodexContainerFactoryService().createECodexContainerService(
            DomainEntityCreator.createEpoMessage()
        );
        var businessContent = new BusinessContent();

        // bytes name mimetype
        var businessDoc = loadDocumentFromResource(
            "examples/ExampleAsics.asics",
            "ExampleAsics.asics",
            MimeTypeEnum.ASICS
        );
        businessContent.setDocument(businessDoc);

        var container = containerService.create(businessContent);

        var asicDocument = container.getAsicDocument();
        assertThat(asicDocument).isNotNull();
        //
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        var tokenXML = container.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        // check if produced container and token can also be resolved again
        var asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        var tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        containerService = getECodexContainerFactoryService().createECodexContainerService(
            DomainEntityCreator.createEpoMessage()
        );
        container = containerService.receive(
            new ByteArrayInputStream(asics),
            new ByteArrayInputStream(tokenXml)
        );

        // there must be a token XML
        assertThat(container.getTokenXML()).isNotNull();

        // there must be a pdf token!
        assertThat(container.getTokenPDF()).isNotNull();

        // there must also be a business document!
        assertThat(container.getBusinessContent().getDocument()).isNotNull();
    }

    @Test
    @DisplayName("Build ASIC-S container with unsigned doc.txt")
    public void createContainerFromTextDocument(TestInfo testInfo)
        throws ECodexException, IOException {
        var containerService = getECodexContainerFactoryService()
            .createECodexContainerService(DomainEntityCreator.createEpoMessage());

        var businessContent = new BusinessContent();

        // bytes name mimetype
        var businessDoc = loadDocumentFromResource(
            "examples/text.txt",
            "text.txt",
            MimeTypeEnum.TEXT
        );
        businessContent.setDocument(businessDoc);

        var container = containerService.create(businessContent);

        var asicDocument = container.getAsicDocument();
        assertThat(asicDocument).isNotNull();
        //
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        var tokenXML = container.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        // check if produced container and token can also be resolved again
        var asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        var tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        containerService = getECodexContainerFactoryService().createECodexContainerService(
            DomainEntityCreator.createEpoMessage());
        containerService.receive(
            new ByteArrayInputStream(asics),
            new ByteArrayInputStream(tokenXml)
        );
    }

    @Test
    public void simpleTestCreateContainerServiceAndBuildAsicContainer(TestInfo testInfo)
        throws ECodexException, IOException {
        var businessContent = new BusinessContent();

        // bytes name mimetype
        var businessDoc = loadDocumentFromResource(
            "examples/Form_A.pdf",
            "Form_A.pdf",
            MimeTypeEnum.PDF
        );
        businessContent.setDocument(businessDoc);

        var dssDocument = loadDocumentFromResource(
            "examples/Form_A.pdf",
            "Addition.pdf",
            MimeTypeEnum.PDF
        );
        businessContent.addAttachment(dssDocument);

        var containerService = getECodexContainerFactoryService()
            .createECodexContainerService(DomainEntityCreator.createEpoMessage());
        var container = containerService.create(businessContent);

        var asicDocument = container.getAsicDocument();
        assertThat(asicDocument).isNotNull();
        //
        asicDocument.setName("asic-s.asics");
        writeDssDocToDisk(testInfo, asicDocument);

        var tokenXML = container.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        // check if produced container and token can also be resolved again
        var asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        var tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        containerService = getECodexContainerFactoryService().createECodexContainerService(
            DomainEntityCreator.createEpoMessage());
        containerService.receive(
            new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml)
        );
    }

    @Test
    public void testCreateContainerServiceAndResolveAsicContainer() throws ECodexException {
        var asicContainer = getClass().getResourceAsStream("/examples/asics1/asic-s.asics");
        var xmlToken = getClass().getResourceAsStream("/examples/asics1/asic-s_trustoktoken.xml");

        assertThat(asicContainer).isNotNull();
        assertThat(xmlToken).isNotNull();

        var containerService = getECodexContainerFactoryService()
            .createECodexContainerService(DomainEntityCreator.createEpoMessage());
        var container = containerService.receive(asicContainer, xmlToken);
        var check = containerService.check(container);
        assertThat(check.isSuccessful()).isTrue();
        var dssDocument = container.getBusinessAttachments().getFirst();
        assertThat(dssDocument.getName()).isEqualTo("Addition.pdf");
    }

    private void writeDssDocToDisk(TestInfo testInfo, DSSDocument document) throws IOException {
        var testMethodName = testInfo.getTestMethod().get().getName();

        var file = new File(TEST_RESULTS_FOLDER + File.separator + testMethodName);
        file.mkdirs();

        var docName = document.getName();
        var docPath = new File(file.getAbsolutePath() + File.separator + docName);

        LOGGER.info("Writing document [{}] to [{}]", document, docPath);

        var fileOutputStream = new FileOutputStream(docPath);
        document.writeTo(fileOutputStream);
    }

    private InMemoryDocument loadDocumentFromResource(
        String classpathResource, String name, MimeType mimeType) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(classpathResource);
        var inputStream = classPathResource.getInputStream();
        var content = StreamUtils.copyToByteArray(inputStream);
        return new InMemoryDocument(content, name, mimeType);
    }

    private byte[] loadByteArrayFromResource(Resource resource) {
        try {
            var inputStream = resource.getInputStream();
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
