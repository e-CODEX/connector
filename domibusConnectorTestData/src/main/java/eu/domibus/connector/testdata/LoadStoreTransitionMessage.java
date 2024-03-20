package eu.domibus.connector.testdata;


import eu.domibus.connector.domain.transition.*;
import eu.domibus.connector.domain.transition.tools.ConversionTools;

import org.apache.cxf.helpers.FileUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.*;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Load and store a Transition Message to the FileSystem
 * Very Simple - used for testing
 * <p>
 * TODO: refactor this class to reduce file size
 */
public class LoadStoreTransitionMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadStoreTransitionMessage.class);

    public static final String READ_LOCK_FILE_NAME = "edit.lock";

    public static final String MESSAGE_PROPERTIES_PROPERTY_FILE_NAME = "message.properties";
    public static final String SERVICE_NAME_PROP_NAME = "service";
    public static final String SERVICE_TYPE_PROP_NAME = "service.type";
    public static final String NATIONAL_ID_PROP_NAME = "message.national-id";
    public static final String EBMS_ID_PROP_NAME = "message.ebms-id";
    public static final String BACKEND_CLIENT_NAME_PROP_NAME = "message.backend-client-name";
    public static final String MESSAGE_CONTENT_XML_PROP_NAME = "message.content.xml";
    public static final String DEFAULT_CONTENT_XML_FILE_NAME = "content.xml";
    public static final String MESSAGE_DOCUMENT_FILE_PROP_NAME = "message.content.document.file";
    public static final String MESSAGE_DOCUMENT_NAME_PROP_NAME = "message.content.document.name";
    public static final String MESSAGE_DOCUMENT_HASH_PROP_NAME = "message.content.document.hash";
    public static final String MESSAGE_DOCUMENT_SIGNATURE_FILE_PROP_NAME = "message.content.document.signature.file";
    public static final String MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME = "message.content.document.signature.type";
    public static final String MESSAGE_DOCUMENT_SIGNATURE_NAME_PROP_NAME = "message.content.document.signature.name";
    public static final String FROM_PARTY_ID_PROP_NAME = "from.party.id";
    public static final String FROM_PARTY_ID_TYPE_PROP_NAME = "from.party.id.type";
    public static final String FROM_PARTY_ROLE_PROP_NAME = "from.party.role";
    public static final String TO_PARTY_ID_PROP_NAME = "to.party.id";
    public static final String TO_PARTY_ID_TYPE_PROP_NAME = "to.party.id.type";
    public static final String TO_PARTY_ROLE_PROP_NAME = "to.party.role";
    public static final String ACTION_PROP_NAME = "action";
    public static final String CONVERSATION_ID_PROP_NAME = "message.conversation-id";
    public static final String FINAL_RECIPIENT_PROP_NAME = "message.final-recipient";
    public static final String ORIGINAL_SENDER_NAME_PROP_NAME = "message.original-sender";
    public static final String REF_TO_MESSAGE_ID_PROP_NAME = "message.ref-to-msg.id";

    public static String MESSAGE_CONFIRMATIONS_PREFIX = "message.confirmation";
    public static String MESSAGE_ATTACHMENT_PREFIX = "message.attachment";

    private Path basicFolder;

    private Properties messageProperties;

    public static DomibusConnectorMessageType loadMessageFrom(Resource resource) {
        LOGGER.info("Load message from path [{}]", resource);
        try {
            Path path = Paths.get(resource.getURI());
            LoadStoreTransitionMessage load = new LoadStoreTransitionMessage(path);
            return load.loadMessage();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static DomibusConnectorMessageType loadMessageFrom(Path path) {
        LOGGER.info("Load message from path [{}]", path);
        try {
            LoadStoreTransitionMessage load = new LoadStoreTransitionMessage(path);
            return load.loadMessage();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public static void storeMessageTo(Path path, DomibusConnectorMessageType message, boolean overwrite) {
        LOGGER.debug("storeMessageTo [{}]", path);
        try {
            LoadStoreTransitionMessage store = new LoadStoreTransitionMessage(path);
            store.storeMessageTo(message, overwrite);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    public static void storeMessageTo(Resource resourcePath, DomibusConnectorMessageType message, boolean overwrite) {
        LOGGER.debug("storeMessageTo [{}]", resourcePath);
        try {
            Path nioPath = Paths.get(resourcePath.getURI());
            LoadStoreTransitionMessage store = new LoadStoreTransitionMessage(nioPath);
            store.storeMessageTo(message, overwrite);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    //TODO: java locking
    private void storeMessageTo(DomibusConnectorMessageType message, boolean overwrite) throws IOException {
        LOGGER.debug("storeMessage to [{}] with overwrite [{}]", basicFolder, overwrite);

        if (Files.exists(basicFolder) && !overwrite) {
            throw new RuntimeException(String.format("Overwrite is false, cannot overwrite message in folder %s", basicFolder));
        }

        if (!Files.exists(basicFolder)) {
            Files.createDirectory(basicFolder);
        }


        Path propertiesResource = basicFolder.resolve(MESSAGE_PROPERTIES_PROPERTY_FILE_NAME);
        File f = propertiesResource.toFile();

        storeMessageContent(message.getMessageContent());

        storeMessageConfirmations(message.getMessageConfirmations());

        storeMessageAttachments(message.getMessageAttachments());

        storeMessageDetails(message.getMessageDetails());

        try (FileOutputStream fout = new FileOutputStream(f)) {
            LOGGER.debug("writing properties file [{}]", f);
            messageProperties.store(fout, "");
        } catch (IOException ioe) {

        }


    }

    private void storeMessageDetails(DomibusConnectorMessageDetailsType messageDetails) {
        putIfNotNull(NATIONAL_ID_PROP_NAME, messageDetails.getBackendMessageId());
        putIfNotNull(EBMS_ID_PROP_NAME, messageDetails.getEbmsMessageId());
        putIfNotNull(CONVERSATION_ID_PROP_NAME, messageDetails.getConversationId());
        putIfNotNull(FINAL_RECIPIENT_PROP_NAME, messageDetails.getFinalRecipient());
        putIfNotNull(ORIGINAL_SENDER_NAME_PROP_NAME, messageDetails.getOriginalSender());
        putIfNotNull(REF_TO_MESSAGE_ID_PROP_NAME, messageDetails.getRefToMessageId());

        if (messageDetails.getService() != null) {
            putIfNotNull(SERVICE_NAME_PROP_NAME, messageDetails.getService().getService());
            putIfNotNull(SERVICE_TYPE_PROP_NAME, messageDetails.getService().getServiceType());
        }
        if (messageDetails.getFromParty() != null) {
            putIfNotNull(FROM_PARTY_ID_PROP_NAME, messageDetails.getFromParty().getPartyId());
            putIfNotNull(FROM_PARTY_ROLE_PROP_NAME, messageDetails.getFromParty().getRole());
            putIfNotNull(FROM_PARTY_ID_TYPE_PROP_NAME, messageDetails.getFromParty().getPartyIdType());
        }
        if (messageDetails.getToParty() != null) {
            putIfNotNull(TO_PARTY_ID_PROP_NAME, messageDetails.getToParty().getPartyId());
            putIfNotNull(TO_PARTY_ROLE_PROP_NAME, messageDetails.getToParty().getRole());
            putIfNotNull(TO_PARTY_ID_TYPE_PROP_NAME, messageDetails.getToParty().getPartyIdType());
        }

        if (messageDetails.getAction() != null) {
            putIfNotNull(ACTION_PROP_NAME, messageDetails.getAction().getAction());
        }

    }

    private void putIfNotNull(String key, Object value) {
        if (value != null) {
            messageProperties.put(key, value);
        } else {
            LOGGER.trace("Not putting [{}] into message properties, because it's null!", key);
        }
    }

    private void storeMessageAttachments(List<DomibusConnectorMessageAttachmentType> messageAttachments) {
        for (int i = 0; i < messageAttachments.size(); i++) {
//            try {
            DomibusConnectorMessageAttachmentType a = messageAttachments.get(i);

            String attachmentPropertyFile = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, i, "file");

            String fileName = a.getName();
            if (fileName == null) {
                fileName = a.getIdentifier();
            }
            Path attachmentOutputResource = basicFolder.resolve(fileName);
            writeBigDataReferenceToResource(attachmentOutputResource, a.getAttachment());
            messageProperties.put(attachmentPropertyFile, fileName);

            String attachmentPropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, i, "identifier");
            messageProperties.put(attachmentPropertyName, a.getIdentifier());
//            } catch (IOException ioe) {
//                throw new RuntimeException(ioe);
//            }
        }
    }

    private void storeMessageConfirmations(List<DomibusConnectorMessageConfirmationType> messageConfirmations) {
        for (int i = 0; i < messageConfirmations.size(); i++) {
            try {
                DomibusConnectorMessageConfirmationType confirmation = messageConfirmations.get(i);

                String evidenceFilePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, i, "file");
                String evidenceTypePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, i, "type");

                messageProperties.put(evidenceTypePropertyName, confirmation.getConfirmationType().name());

                String fileName = confirmation.getConfirmationType().name() + ".xml";
                messageProperties.put(evidenceFilePropertyName, confirmation.getConfirmationType().name() + ".xml");

                Path r = basicFolder.resolve(fileName);
                writeXmlSourceToResource(r, confirmation.getConfirmation());

            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private void storeMessageContent(DomibusConnectorMessageContentType content) throws IOException {
        if (content == null) {
            return;
        }
        Source xmlContent = content.getXmlContent();
        Path r = basicFolder.resolve(DEFAULT_CONTENT_XML_FILE_NAME);
        messageProperties.put(LoadStoreTransitionMessage.MESSAGE_CONTENT_XML_PROP_NAME, LoadStoreTransitionMessage.DEFAULT_CONTENT_XML_FILE_NAME);

        writeXmlSourceToResource(r, xmlContent);

        //store message document
        DomibusConnectorMessageDocumentType messageDocument = content.getDocument();
        if (messageDocument != null) {
            messageDocument.getDocument();
            String fileName = messageDocument.getDocumentName() == null ? "document.pdf" : messageDocument.getDocumentName();
            Path d = basicFolder.resolve(fileName);
            writeBigDataReferenceToResource(d, messageDocument.getDocument());

            messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_FILE_PROP_NAME, fileName);

            if (messageDocument.getDocumentName() != null) {
                messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_NAME_PROP_NAME, messageDocument.getDocumentName());
            }

            DomibusConnectorDetachedSignatureType detachedSignature = messageDocument.getDetachedSignature();
            if (detachedSignature != null) {
                byte[] detachedSignatureBytes = detachedSignature.getDetachedSignature();
                DomibusConnectorDetachedSignatureMimeType detachedSignatureMimeType = detachedSignature.getMimeType();
                String detachedSignatureName = detachedSignature.getDetachedSignatureName() == null ? "detachedSignature" : detachedSignature.getDetachedSignatureName();

                String appendix = detachedSignatureMimeType.name().toLowerCase();
                String detachedResourceFilename = detachedSignatureName + "." + appendix;
                Path res = basicFolder.resolve(detachedResourceFilename);

                writeByteArrayToFile(res, detachedSignatureBytes);

                messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_FILE_PROP_NAME, detachedResourceFilename);
                messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME, detachedSignatureMimeType.value());
                if (detachedSignatureName != null) {
                    messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_NAME_PROP_NAME, detachedSignatureName);
                }
            }
        }
    }

    private void writeByteArrayToFile(Path res, byte[] bytes) {
        try (FileOutputStream fout = new FileOutputStream(res.toFile())) {
            StreamUtils.copy(bytes, fout);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeBigDataReferenceToResource(Path d, DataHandler document) {
        try (FileOutputStream fout = new FileOutputStream(d.toFile())) {
            StreamUtils.copy(document.getInputStream(), fout);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeXmlSourceToResource(Path r, Source xmlContent) throws IOException {
//        File f = r.getFile();
    	FileOutputStream out = new FileOutputStream(r.toFile());
    	out.write(ConversionTools.convertXmlSourceToByteArray(xmlContent));
    	out.flush();
    	out.close();
//        try (FileOutputStream fout = new FileOutputStream(r.toFile())) {
//            Transformer transformer = TransformerFactory.newInstance().newTransformer();
//            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
//            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//            StreamResult xmlOutput = new StreamResult(new OutputStreamWriter(fout));
//            transformer.transform(xmlContent, xmlOutput);
//        } catch (TransformerConfigurationException e) {
//            LOGGER.error("Exception occured", e);
//            throw new RuntimeException(e);
//        } catch (TransformerException e) {
//            throw new RuntimeException(e);
//        }
    }


    private LoadStoreTransitionMessage() {
    }

    private LoadStoreTransitionMessage(Path basicFolder) {
        messageProperties = new Properties();
        this.basicFolder = basicFolder;
    }


    //TODO: java locking
    private DomibusConnectorMessageType loadMessage() throws IOException {
        DomibusConnectorMessageType message = new DomibusConnectorMessageType();

        Path propertiesPath = basicFolder.resolve(MESSAGE_PROPERTIES_PROPERTY_FILE_NAME);
        if (!Files.exists(propertiesPath)) {
            throw new IOException("properties " + propertiesPath + " does not exist!");
        }

        messageProperties.load(new FileInputStream(propertiesPath.toFile()));

        message.setMessageDetails(loadMessageDetails());

        Resource contentResource = createRelativeResource(messageProperties.getProperty(MESSAGE_CONTENT_XML_PROP_NAME));
        if (contentResource != null && contentResource.exists()) {
            DomibusConnectorMessageContentType content = new DomibusConnectorMessageContentType();
            content.setXmlContent(loadResourceAsSource(contentResource));

            message.setMessageContent(content);
            //load document
            String docFileName = messageProperties.getProperty(MESSAGE_DOCUMENT_FILE_PROP_NAME);
            if (docFileName != null) {
                DomibusConnectorMessageDocumentType messageDoc = new DomibusConnectorMessageDocumentType();
                Path r = basicFolder.resolve(docFileName);

                messageDoc.setDocument(loadResourceAsDataHandler(new FileSystemResource(r.toFile())));
                String docName = messageProperties.getProperty(MESSAGE_DOCUMENT_NAME_PROP_NAME);
                messageDoc.setDocumentName(docName);

                //TODO: load signature
                String signatureFileName = messageProperties.getProperty(MESSAGE_DOCUMENT_SIGNATURE_FILE_PROP_NAME);
                if (signatureFileName != null) {
                    DomibusConnectorDetachedSignatureType detachedSignature = new DomibusConnectorDetachedSignatureType();


                    String mimeTypeString = messageProperties.getProperty(MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME);
                    DomibusConnectorDetachedSignatureMimeType detachedSignatureMimeType =
                            DomibusConnectorDetachedSignatureMimeType.fromValue(mimeTypeString);


//                    DetachedSignatureMimeType mimeType = DetachedSignatureMimeType.valueOf(messageProperties.getProperty("MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME"));
                    String name = messageProperties.getProperty(MESSAGE_DOCUMENT_SIGNATURE_NAME_PROP_NAME);
                    Path fResource = basicFolder.resolve(signatureFileName);
                    byte[] signatureBytes = loadResourceAsByteArray(new FileSystemResource(fResource.toFile()));

                    detachedSignature.setDetachedSignature(signatureBytes);
                    detachedSignature.setDetachedSignatureName(name);
                    detachedSignature.setMimeType(detachedSignatureMimeType);

                    messageDoc.setDetachedSignature(detachedSignature);
                }

                content.setDocument(messageDoc);

            }

        }


        //load attachments
        message.getMessageAttachments().addAll(loadMessageAttachments());

        //load confirmations
        message.getMessageConfirmations().addAll(loadMessageConfirmations());


        return message;
    }

    private List<? extends DomibusConnectorMessageConfirmationType> loadMessageConfirmations() {
        return messageProperties.stringPropertyNames()
                .stream()
                .sorted()
                .filter((k) -> k.startsWith(LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX))
                .map((k) -> k.split("\\.")[2])
                .distinct()
                .map((k) -> {


                    String evidenceFilePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, k, "file");
                    String evidenceTypePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, k, "type");

                    Resource resEvidenceFile = createRelativeResource(messageProperties.getProperty(evidenceFilePropertyName));

                    //TODO: determine evidence type!
                    //DomibusConnectorMessageC domibusConnectorEvidenceType = DomibusConnectorEvidenceType.valueOf(messageProperties.getProperty(evidenceTypePropertyName));

                    DomibusConnectorConfirmationType domibusConnectorConfirmationType = DomibusConnectorConfirmationType.fromValue(messageProperties.getProperty(evidenceTypePropertyName));

                    DomibusConnectorMessageConfirmationType confirmation = new DomibusConnectorMessageConfirmationType();
                    confirmation.setConfirmation(loadResourceAsSource(resEvidenceFile));
                    confirmation.setConfirmationType(domibusConnectorConfirmationType);
                    //confirmation.setEvidenceType(domibusConnectorEvidenceType);
                    //confirmation.setConfirmationType(DomibusConnectorConfirmationType.valueOf());

                    return confirmation;

                })
                .collect(Collectors.toList());
    }

    private Source loadResourceAsSource(Resource resEvidenceFile) {
        try {
            return new StreamSource(resEvidenceFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DomibusConnectorMessageAttachmentType> loadMessageAttachments() {


        return messageProperties.stringPropertyNames()
                .stream()
                .sorted()
                .filter((k) -> k.startsWith(LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX))
                .map((k) -> k.split("\\.")[2])
                .distinct()
                .map((k) -> {
//                    try {
                    DomibusConnectorMessageAttachmentType attachment = new DomibusConnectorMessageAttachmentType();
                    String filePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, k, "file");
                    String identifierPropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, k, "identifier");
                    Path res = basicFolder.resolve(messageProperties.getProperty(filePropertyName));
                    attachment.setAttachment(loadResourceAsDataHandler(new FileSystemResource(res.toFile())));
                    attachment.setIdentifier(messageProperties.getProperty(identifierPropertyName));
                    return attachment;

//                    } catch (IOException ioe) {
//                        throw new RuntimeException(ioe);
//                    }
                })
                .collect(Collectors.toList());

    }

    private DataHandler loadResourceAsDataHandler(Resource res) {
        try {
            DataHandler dh = new DataHandler(new InputStreamDataSource(res.getInputStream()));
            return dh;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private @Nullable
    Resource createRelativeResource(@Nullable String relativePath) {
        if (relativePath == null) {
            return null;
        }
        Path contentResource = basicFolder.resolve(relativePath);
        return new FileSystemResource(contentResource.toFile());
    }

    private byte[] loadResourceAsByteArray(Resource res) {
        try {
            InputStream inputStream = res.getInputStream();
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }




    private DomibusConnectorMessageDetailsType loadMessageDetails() {
        DomibusConnectorMessageDetailsType messageDetails = new DomibusConnectorMessageDetailsType();

        DomibusConnectorActionType domibusConnectorActionType = new DomibusConnectorActionType();
        domibusConnectorActionType.setAction(messageProperties.getProperty(ACTION_PROP_NAME));
        messageDetails.setAction(domibusConnectorActionType);

        DomibusConnectorPartyType fromParty = new DomibusConnectorPartyType();
        fromParty.setPartyId(messageProperties.getProperty(FROM_PARTY_ID_PROP_NAME));
        fromParty.setPartyIdType(messageProperties.getProperty(FROM_PARTY_ID_TYPE_PROP_NAME));
        fromParty.setRole(messageProperties.getProperty(FROM_PARTY_ROLE_PROP_NAME));
        messageDetails.setFromParty(fromParty);

        DomibusConnectorPartyType toParty = new DomibusConnectorPartyType();
        toParty.setPartyId(messageProperties.getProperty(TO_PARTY_ID_PROP_NAME));
        toParty.setPartyIdType(messageProperties.getProperty(TO_PARTY_ID_TYPE_PROP_NAME));
        toParty.setRole(messageProperties.getProperty(TO_PARTY_ROLE_PROP_NAME));
        messageDetails.setToParty(toParty);

        DomibusConnectorServiceType service = new DomibusConnectorServiceType();
        service.setService(messageProperties.getProperty(SERVICE_NAME_PROP_NAME));
        service.setServiceType(messageProperties.getProperty(SERVICE_TYPE_PROP_NAME));
        messageDetails.setService(service);

        messageDetails.setConversationId(messageProperties.getProperty(CONVERSATION_ID_PROP_NAME));

        messageDetails.setEbmsMessageId(messageProperties.getProperty(EBMS_ID_PROP_NAME));
        messageDetails.setRefToMessageId(messageProperties.getProperty(REF_TO_MESSAGE_ID_PROP_NAME));

        messageDetails.setBackendMessageId(messageProperties.getProperty(NATIONAL_ID_PROP_NAME));

        messageDetails.setOriginalSender(messageProperties.getProperty(ORIGINAL_SENDER_NAME_PROP_NAME));
        messageDetails.setFinalRecipient(messageProperties.getProperty(FINAL_RECIPIENT_PROP_NAME));

        return messageDetails;
    }


    public static class InputStreamDataSource implements DataSource {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        public InputStreamDataSource(InputStream inputStream) {
            try {
                int nRead;
                StreamUtils.copy(inputStream, buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getContentType() {
            //return new MimetypesFileTypeMap().getContentType(name);
            return "application/octet-stream";
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(buffer.toByteArray());
        }

        @Override
        public String getName() {
            return "";
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Read-only data");
        }

    }


}
