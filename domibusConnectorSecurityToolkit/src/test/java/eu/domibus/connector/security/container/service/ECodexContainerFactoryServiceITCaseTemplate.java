package eu.domibus.connector.security.container.service;

import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.domain.model.DCMessageProcessSettings;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.service.ECodexContainerService;
import eu.ecodex.dss.service.ECodexException;
import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;

import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

import static org.assertj.core.api.Assertions.assertThat;


public abstract class ECodexContainerFactoryServiceITCaseTemplate {


    private final static Logger LOGGER = LoggerFactory.getLogger(ECodexContainerFactoryServiceITCaseTemplate.class);
    public static final String TEST_FILE_RESULTS_DIR_PROPERTY_NAME = "test.file.results";
    private static File TEST_RESULTS_FOLDER;

    @Autowired
    ECodexContainerFactoryService eCodexContainerFactoryService;

    ECodexContainerFactoryService getECodexContainerFactoryService() {
        return eCodexContainerFactoryService;
    }

    @BeforeAll
    public static void initClass(TestInfo testInfo) {
        String dir = System.getenv().getOrDefault(TEST_FILE_RESULTS_DIR_PROPERTY_NAME, "./target/testfileresults/");
        dir = dir + "/" + testInfo.getTestClass().get().getSimpleName();
        TEST_RESULTS_FOLDER = new File(dir);
        TEST_RESULTS_FOLDER.mkdirs();
    }

    @BeforeEach
    public void beforeEach() {
        CurrentBusinessDomain.setCurrentBusinessDomain(DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
    }

    @AfterEach
    public void afterEach() {
        CurrentBusinessDomain.setCurrentBusinessDomain(null);
    }

    @Test
    @DisplayName("Build ASIC-S container with XML as business doc")
    public void createContainerFromXML(TestInfo testInfo) throws ECodexException, IOException {

        ECodexContainerService eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());

        BusinessContent businessContent = new BusinessContent();

        //bytes name mimetype
        DSSDocument businessDoc = loadDocumentFromResource("examples/ExampleXmlSigned.xml", "ExampleXmlSigned.xml", MimeTypeEnum.XML);
        businessContent.setDocument(businessDoc);

        ECodexContainer eCodexContainer = eCodexContainerService.create(businessContent);

        DSSDocument asicDocument = eCodexContainer.getAsicDocument();
        assertThat(asicDocument).isNotNull();
//
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        DSSDocument tokenXML = eCodexContainer.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        //check if produced container and token can also be resolved again
        byte[] asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        byte[] tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());
        eCodexContainer = eCodexContainerService.receive(new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml));

    }

    @Test
    @DisplayName("Build ASIC-S container with XML authentication based")
    public void createContainerFromXMLWithAuthenticationBased(TestInfo testInfo) throws ECodexException, IOException, ParserConfigurationException, SAXException, JAXBException {


        DCMessageProcessSettings settings = new DCMessageProcessSettings();
        settings.setValidationServiceName(AdvancedElectronicSystemType.AUTHENTICATION_BASED);
        DomibusConnectorMessage msg = DomibusConnectorMessageBuilder.createBuilder()
                .setMessageDetails(DomibusConnectorMessageDetailsBuilder.create()
                        .withOriginalSender("originalSender")
                        .build())
                .build();
        msg.setDcMessageProcessSettings(settings);

        ECodexContainerService eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(msg);

        BusinessContent businessContent = new BusinessContent();

        //bytes name mimetype
        DSSDocument businessDoc = loadDocumentFromResource("examples/ExampleXmlSigned.xml", "ExampleXmlSigned.xml", MimeTypeEnum.XML);
        businessContent.setDocument(businessDoc);

        ECodexContainer eCodexContainer = eCodexContainerService.create(businessContent);

        DSSDocument asicDocument = eCodexContainer.getAsicDocument();
        assertThat(asicDocument).isNotNull();
//
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        DSSDocument tokenXML = eCodexContainer.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        //check if produced container and token can also be resolved again
        byte[] asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        byte[] tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());



        eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());
        eCodexContainer = eCodexContainerService.receive(new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml));

        JAXBContext jaxbContext = JAXBContext.newInstance(Token.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        JAXBElement<Token> jaxbToken = unmarshaller.unmarshal(new StreamSource(new ByteArrayInputStream(tokenXml)), Token.class);

        Token token = jaxbToken.getValue();

        assertThat(token.getAdvancedElectronicSystem())
                .as("The system type has been passed with the message and should be AUTHENTICATION BASED!")
                .isEqualTo(AdvancedSystemType.AUTHENTICATION_BASED);

    }


    @Test
    @DisplayName("Build ASIC-S container with ASIC-S as business doc")
    public void createContainerFromAsicS(TestInfo testInfo) throws ECodexException, IOException {

        ECodexContainerService eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());

        BusinessContent businessContent = new BusinessContent();

        //bytes name mimetype
        DSSDocument businessDoc = loadDocumentFromResource("examples/ExampleAsics.asics", "ExampleAsics.asics", MimeTypeEnum.ASICS);
        businessContent.setDocument(businessDoc);

        ECodexContainer eCodexContainer = eCodexContainerService.create(businessContent);

        DSSDocument asicDocument = eCodexContainer.getAsicDocument();
        assertThat(asicDocument).isNotNull();
//
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        DSSDocument tokenXML = eCodexContainer.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        //check if produced container and token can also be resolved again
        byte[] asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        byte[] tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());
        eCodexContainer = eCodexContainerService.receive(new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml));

        //there must be a token XML
        assertThat(eCodexContainer.getTokenXML()).isNotNull();

        //there must be a pdf token!
        assertThat(eCodexContainer.getTokenPDF()).isNotNull();

        //there must also be a business document!
        assertThat(eCodexContainer.getBusinessContent().getDocument()).isNotNull();
    }


    @Test
    @DisplayName("Build ASIC-S container with unsigned doc.txt")
    public void createContainerFromTextDocument(TestInfo testInfo) throws ECodexException, IOException {

        ECodexContainerService eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());

        BusinessContent businessContent = new BusinessContent();

        //bytes name mimetype
        DSSDocument businessDoc = loadDocumentFromResource("examples/text.txt", "text.txt", MimeTypeEnum.TEXT);
        businessContent.setDocument(businessDoc);

        ECodexContainer eCodexContainer = eCodexContainerService.create(businessContent);

        DSSDocument asicDocument = eCodexContainer.getAsicDocument();
        assertThat(asicDocument).isNotNull();
//
        asicDocument.setName("asic-s.asics.zip");
        writeDssDocToDisk(testInfo, asicDocument);

        DSSDocument tokenXML = eCodexContainer.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        //check if produced container and token can also be resolved again
        byte[] asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        byte[] tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());
        eCodexContainer = eCodexContainerService.receive(new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml));

    }


    @Test
    public void simpleTestCreateContainerServiceAndBuildAsicContainer(TestInfo testInfo) throws ECodexException, IOException {


        ECodexContainerService eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());

        BusinessContent businessContent = new BusinessContent();

        //bytes name mimetype
        DSSDocument businessDoc = loadDocumentFromResource("examples/Form_A.pdf", "Form_A.pdf", MimeTypeEnum.PDF);
        businessContent.setDocument(businessDoc);

        DSSDocument dssDocument = loadDocumentFromResource("examples/Form_A.pdf", "Addition.pdf", MimeTypeEnum.PDF);
        businessContent.addAttachment(dssDocument);

        ECodexContainer eCodexContainer = eCodexContainerService.create(businessContent);

        DSSDocument asicDocument = eCodexContainer.getAsicDocument();
        assertThat(asicDocument).isNotNull();
//
        asicDocument.setName("asic-s.asics");
        writeDssDocToDisk(testInfo, asicDocument);

        DSSDocument tokenXML = eCodexContainer.getTokenXML();
        assertThat(tokenXML).isNotNull();
        tokenXML.setName("asic-s_trustoktoken.xml");
        writeDssDocToDisk(testInfo, tokenXML);

        //check if produced container and token can also be resolved again
        byte[] asics = StreamUtils.copyToByteArray(asicDocument.openStream());
        byte[] tokenXml = StreamUtils.copyToByteArray(tokenXML.openStream());

        eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());
        eCodexContainer = eCodexContainerService.receive(new ByteArrayInputStream(asics), new ByteArrayInputStream(tokenXml));

    }


    @Test
    public void testCreateContainerServiceAndResolveAsicContainer() throws IOException, ECodexException {

        ECodexContainerService eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(DomainEntityCreator.createEpoMessage());


        InputStream asicContainer = getClass().getResourceAsStream("/examples/asics1/asic-s.asics");
        InputStream xmlToken = getClass().getResourceAsStream("/examples/asics1/asic-s_trustoktoken.xml");

        assertThat(asicContainer).isNotNull();
        assertThat(xmlToken).isNotNull();


        ECodexContainer eCodexContainer =
                eCodexContainerService.receive(asicContainer, xmlToken);

        CheckResult check = eCodexContainerService.check(eCodexContainer);

        assertThat(check.isSuccessful()).isTrue();

        DSSDocument dssDocument = eCodexContainer.getBusinessAttachments().get(0);
        assertThat(dssDocument.getName()).isEqualTo("Addition.pdf");

    }

//    @Test
//    @Ignore("not finished yet!")
//    public void simpleTestCreateContainerServiceAndBuildAsicContainer_withStreams() throws IOException, ECodexException {
//
//        //securityContainerService.create()
//
//        ECodexContainerService eCodexContainerService = getECodexContainerFactoryService().createECodexContainerService(null);
//
//        BusinessContent businessContent = new BusinessContent();
//
//        //bytes name mimetype
//        DSSDocument businessDoc = loadDocumentFromResource("examples/Form_A.pdf", "Form_A.pdf", MimeType.PDF);
//        businessContent.setDocument(businessDoc);
//
//        DSSDocument dssDocument = loadDocumentFromResource("examples/Form_A.pdf", "Addition.pdf", MimeType.PDF);
//        businessContent.addAttachment(dssDocument);
//
//        ECodexContainer eCodexContainer = eCodexContainerService.create(businessContent, tokenIssuerFactory.getTokenIssuer(null));
//
//    }


    private void writeDssDocToDisk(TestInfo testInfo, DSSDocument document) throws IOException {

        String testMethodName = testInfo.getTestMethod().get().getName();

        File f = new File(TEST_RESULTS_FOLDER + File.separator + testMethodName);
        f.mkdirs();

        String docName = document.getName();
        File docPath = new File(f.getAbsolutePath() + File.separator + docName);

        LOGGER.info("Writing document [{}] to [{}]", document, docPath );


        FileOutputStream fileOutputStream = new FileOutputStream(docPath);
        document.writeTo(fileOutputStream);

    }

    private InMemoryDocument loadDocumentFromResource(String classpathResource, String name, MimeType mimeType) throws IOException {
        ClassPathResource classPathResource = new ClassPathResource(classpathResource);
        InputStream inputStream = classPathResource.getInputStream();
        byte[] content = StreamUtils.copyToByteArray(inputStream);
        InMemoryDocument doc = new InMemoryDocument(content, name, mimeType);

        return doc;
    }

    private byte[] loadByteArrayFromResource(Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }



}
