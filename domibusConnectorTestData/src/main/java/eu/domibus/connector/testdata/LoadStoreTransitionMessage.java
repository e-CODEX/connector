/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.testdata;

import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureMimeType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import eu.domibus.connector.domain.transition.tools.ConversionTools;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.annotation.Nullable;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

/**
 * Load and store a Transition Message to the FileSystem Very Simple - used for testing.
 */
@SuppressWarnings("squid:S1135")
public class LoadStoreTransitionMessage {
    // TODO: refactor this class to reduce file size
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
    public static final String MESSAGE_DOCUMENT_SIGNATURE_FILE_PROP_NAME =
        "message.content.document.signature.file";
    public static final String MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME =
        "message.content.document.signature.type";
    public static final String MESSAGE_DOCUMENT_SIGNATURE_NAME_PROP_NAME =
        "message.content.document.signature.name";
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
    public static final String MESSAGE_CONFIRMATIONS_PREFIX = "message.confirmation";
    public static final String MESSAGE_ATTACHMENT_PREFIX = "message.attachment";
    private Path basicFolder;
    private Properties messageProperties;

    /**
     * Loads a DomibusConnectorMessageType object from a given Resource.
     *
     * @param resource The Resource from which to load the DomibusConnectorMessageType object.
     * @return The loaded DomibusConnectorMessageType object.
     * @throws RuntimeException if an error occurs while loading the message.
     */
    public static DomibusConnectorMessageType loadMessageFrom(Resource resource) {
        LOGGER.info("Load message from path [{}]", resource);
        try {
            var path = Paths.get(resource.getURI());
            var load = new LoadStoreTransitionMessage(path);
            return load.loadMessage();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Loads a DomibusConnectorMessageType object from the specified path.
     *
     * @param path The path from which to load the DomibusConnectorMessageType object.
     * @return The loaded DomibusConnectorMessageType object.
     * @throws RuntimeException if an error occurs while loading the message.
     */
    public static DomibusConnectorMessageType loadMessageFrom(Path path) {
        LOGGER.info("Load message from path [{}]", path);
        try {
            var load = new LoadStoreTransitionMessage(path);
            return load.loadMessage();
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Stores a DomibusConnectorMessageType object to the specified path.
     *
     * @param path      The path to which the DomibusConnectorMessageType object will be stored.
     * @param message   The DomibusConnectorMessageType object to be stored.
     * @param overwrite If set to true, the existing file will be overwritten. If set to false and a
     *                  file already exists at the specified path, an exception will be thrown.
     * @throws RuntimeException if an error occurs while storing the message.
     */
    public static void storeMessageTo(
        Path path, DomibusConnectorMessageType message, boolean overwrite) {
        LOGGER.debug("storeMessageTo [{}]", path);
        try {
            var store = new LoadStoreTransitionMessage(path);
            store.storeMessageTo(message, overwrite);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Stores a DomibusConnectorMessageType object to the specified path.
     *
     * @param resourcePath The path to which the DomibusConnectorMessageType object will be stored.
     * @param message      The DomibusConnectorMessageType object to be stored.
     * @param overwrite    If set to true, the existing file will be overwritten. If set to false
     *                     and a file already exists at the specified path, an exception will be
     *                     thrown.
     * @throws RuntimeException if an error occurs while storing the message.
     */
    public static void storeMessageTo(
        Resource resourcePath, DomibusConnectorMessageType message, boolean overwrite) {
        LOGGER.debug("storeMessageTo [{}]", resourcePath);
        try {
            var nioPath = Paths.get(resourcePath.getURI());
            var store = new LoadStoreTransitionMessage(nioPath);
            store.storeMessageTo(message, overwrite);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Stores a DomibusConnectorMessageType object to the appropriate location on disk.
     *
     * @param message   The DomibusConnectorMessageType object to be stored.
     * @param overwrite If set to true, the existing file will be overwritten. If set to false and a
     *                  file already exists, an exception will be thrown.
     * @throws IOException if an error occurs while storing the message.
     */
    // TODO: java locking
    private void storeMessageTo(DomibusConnectorMessageType message, boolean overwrite)
        throws IOException {
        LOGGER.debug("storeMessage to [{}] with overwrite [{}]", basicFolder, overwrite);

        if (Files.exists(basicFolder) && !overwrite) {
            throw new RuntimeException(
                String.format(
                    "Overwrite is false, cannot overwrite message in folder %s",
                    basicFolder
                ));
        }

        if (!Files.exists(basicFolder)) {
            Files.createDirectory(basicFolder);
        }

        Path propertiesResource = basicFolder.resolve(MESSAGE_PROPERTIES_PROPERTY_FILE_NAME);
        var messagePropertiesFile = propertiesResource.toFile();

        storeMessageContent(message.getMessageContent());
        storeMessageConfirmations(message.getMessageConfirmations());
        storeMessageAttachments(message.getMessageAttachments());
        storeMessageDetails(message.getMessageDetails());

        try (var fileOutputStream = new FileOutputStream(messagePropertiesFile)) {
            LOGGER.debug("writing properties file [{}]", messagePropertiesFile);
            messageProperties.store(fileOutputStream, "");
        } catch (IOException ioe) {
            // TODO see why this is empty
        }
    }

    /**
     * Stores the details of a DomibusConnectorMessage in the system.
     *
     * @param messageDetails The DomibusConnectorMessageDetailsType object containing the message
     *                       details.
     */
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
            putIfNotNull(
                FROM_PARTY_ID_TYPE_PROP_NAME, messageDetails.getFromParty().getPartyIdType());
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

    /**
     * Puts the specified value into the message properties map with the specified key, if the value
     * is not null.
     *
     * @param key   the key with which the specified value is to be associated
     * @param value the value to be put into the message properties map
     */
    private void putIfNotNull(String key, Object value) {
        if (value != null) {
            messageProperties.put(key, value);
        } else {
            LOGGER.trace("Not putting [{}] into message properties, because it's null!", key);
        }
    }

    private void storeMessageAttachments(
        List<DomibusConnectorMessageAttachmentType> messageAttachments) {
        for (var i = 0; i < messageAttachments.size(); i++) {
            DomibusConnectorMessageAttachmentType a = messageAttachments.get(i);

            var attachmentPropertyFile = String.format(
                "%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX,
                i,
                "file"
            );

            String fileName = a.getName();
            if (fileName == null) {
                fileName = a.getIdentifier();
            }
            Path attachmentOutputResource = basicFolder.resolve(fileName);
            writeBigDataReferenceToResource(attachmentOutputResource, a.getAttachment());
            messageProperties.put(attachmentPropertyFile, fileName);

            var attachmentPropertyName = String.format(
                "%s.%s.%s",
                LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX,
                i,
                "identifier"
            );
            messageProperties.put(attachmentPropertyName, a.getIdentifier());
        }
    }

    private void storeMessageConfirmations(
        List<DomibusConnectorMessageConfirmationType> messageConfirmations) {
        for (var i = 0; i < messageConfirmations.size(); i++) {
            try {
                var confirmation = messageConfirmations.get(i);
                var evidenceFilePropertyName = String.format(
                    "%s.%s.%s",
                    LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX,
                    i,
                    "file"
                );
                var evidenceTypePropertyName = String.format(
                    "%s.%s.%s",
                    LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX,
                    i,
                    "type"
                );

                messageProperties.put(
                    evidenceTypePropertyName, confirmation.getConfirmationType().name()
                );

                var fileName = confirmation.getConfirmationType().name() + ".xml";
                messageProperties.put(
                    evidenceFilePropertyName, confirmation.getConfirmationType().name() + ".xml"
                );

                Path r = basicFolder.resolve(fileName);
                writeXmlSourceToResource(r, confirmation.getConfirmation());
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private void storeMessageContent(DomibusConnectorMessageContentType content)
        throws IOException {
        if (content == null) {
            return;
        }
        var xmlContent = content.getXmlContent();
        var defaultCOntentPath = basicFolder.resolve(DEFAULT_CONTENT_XML_FILE_NAME);
        messageProperties.put(
            LoadStoreTransitionMessage.MESSAGE_CONTENT_XML_PROP_NAME,
            LoadStoreTransitionMessage.DEFAULT_CONTENT_XML_FILE_NAME
        );

        writeXmlSourceToResource(defaultCOntentPath, xmlContent);

        // store message document
        var messageDocument = content.getDocument();
        if (messageDocument != null) {
            messageDocument.getDocument();
            var fileName = messageDocument.getDocumentName() == null
                ? "document.pdf"
                : messageDocument.getDocumentName();
            var messageDocumentPath = basicFolder.resolve(fileName);
            writeBigDataReferenceToResource(messageDocumentPath, messageDocument.getDocument());

            messageProperties.put(
                LoadStoreTransitionMessage.MESSAGE_DOCUMENT_FILE_PROP_NAME, fileName
            );

            if (messageDocument.getDocumentName() != null) {
                messageProperties.put(
                    LoadStoreTransitionMessage.MESSAGE_DOCUMENT_NAME_PROP_NAME,
                    messageDocument.getDocumentName()
                );
            }

            var detachedSignature = messageDocument.getDetachedSignature();
            if (detachedSignature != null) {
                var detachedSignatureBytes = detachedSignature.getDetachedSignature();
                var detachedSignatureMimeType = detachedSignature.getMimeType();
                var detachedSignatureName = detachedSignature.getDetachedSignatureName() == null
                    ? "detachedSignature"
                    : detachedSignature.getDetachedSignatureName();

                var appendix = detachedSignatureMimeType.name().toLowerCase();
                var detachedResourceFilename = detachedSignatureName + "." + appendix;
                var res = basicFolder.resolve(detachedResourceFilename);

                writeByteArrayToFile(res, detachedSignatureBytes);

                messageProperties.put(
                    LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_FILE_PROP_NAME,
                    detachedResourceFilename
                );
                messageProperties.put(
                    LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME,
                    detachedSignatureMimeType.value()
                );
                if (detachedSignatureName != null) {
                    messageProperties.put(
                        LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_NAME_PROP_NAME,
                        detachedSignatureName
                    );
                }
            }
        }
    }

    private void writeByteArrayToFile(Path res, byte[] bytes) {
        try (var fileOutputStream = new FileOutputStream(res.toFile())) {
            StreamUtils.copy(bytes, fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeBigDataReferenceToResource(Path d, DataHandler document) {
        try (var fileOutputStream = new FileOutputStream(d.toFile())) {
            StreamUtils.copy(document.getInputStream(), fileOutputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void writeXmlSourceToResource(Path r, Source xmlContent) throws IOException {
        var fileOutputStream = new FileOutputStream(r.toFile());
        fileOutputStream.write(ConversionTools.convertXMLSourceToByteArray(xmlContent));
        fileOutputStream.flush();
        fileOutputStream.close();
        // try (FileOutputStream fout = new FileOutputStream(r.toFile())) {
        //     Transformer transformer = TransformerFactory.newInstance().newTransformer();
        //     transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        //     transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        //     StreamResult xmlOutput = new StreamResult(new OutputStreamWriter(fout));
        //     transformer.transform(xmlContent, xmlOutput);
        // } catch (TransformerConfigurationException e) {
        //     LOGGER.error("Exception occured", e);
        //     throw new RuntimeException(e);
        // } catch (TransformerException e) {
        //     throw new RuntimeException(e);
        // }
    }

    private LoadStoreTransitionMessage(Path basicFolder) {
        messageProperties = new Properties();
        this.basicFolder = basicFolder;
    }

    // TODO: java locking
    private DomibusConnectorMessageType loadMessage() throws IOException {
        var message = new DomibusConnectorMessageType();

        var propertiesPath = basicFolder.resolve(MESSAGE_PROPERTIES_PROPERTY_FILE_NAME);
        if (!Files.exists(propertiesPath)) {
            throw new IOException("properties " + propertiesPath + " does not exist!");
        }

        messageProperties.load(new FileInputStream(propertiesPath.toFile()));

        message.setMessageDetails(loadMessageDetails());

        var contentResource = createRelativeResource(
            messageProperties.getProperty(MESSAGE_CONTENT_XML_PROP_NAME)
        );
        if (contentResource != null && contentResource.exists()) {
            var content = new DomibusConnectorMessageContentType();
            content.setXmlContent(loadResourceAsSource(contentResource));

            message.setMessageContent(content);
            // load document
            var docFileName = messageProperties.getProperty(MESSAGE_DOCUMENT_FILE_PROP_NAME);
            if (docFileName != null) {
                var messageDoc = new DomibusConnectorMessageDocumentType();
                Path r = basicFolder.resolve(docFileName);

                messageDoc.setDocument(
                    loadResourceAsDataHandler(new FileSystemResource(r.toFile()))
                );
                var docName = messageProperties.getProperty(MESSAGE_DOCUMENT_NAME_PROP_NAME);
                messageDoc.setDocumentName(docName);

                // TODO: load signature
                var signatureFileName =
                    messageProperties.getProperty(MESSAGE_DOCUMENT_SIGNATURE_FILE_PROP_NAME);
                if (signatureFileName != null) {
                    var detachedSignature = new DomibusConnectorDetachedSignatureType();

                    var mimeTypeString = messageProperties.getProperty(
                        MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME
                    );
                    var detachedSignatureMimeType =
                        DomibusConnectorDetachedSignatureMimeType.fromValue(mimeTypeString);

                    String name =
                        messageProperties.getProperty(MESSAGE_DOCUMENT_SIGNATURE_NAME_PROP_NAME);
                    var signatureFilePath = basicFolder.resolve(signatureFileName);
                    var signatureBytes = loadResourceAsByteArray(
                        new FileSystemResource(signatureFilePath.toFile())
                    );

                    detachedSignature.setDetachedSignature(signatureBytes);
                    detachedSignature.setDetachedSignatureName(name);
                    detachedSignature.setMimeType(detachedSignatureMimeType);

                    messageDoc.setDetachedSignature(detachedSignature);
                }

                content.setDocument(messageDoc);
            }
        }

        // load attachments
        message.getMessageAttachments().addAll(loadMessageAttachments());

        // load confirmations
        message.getMessageConfirmations().addAll(loadMessageConfirmations());

        return message;
    }

    private List<? extends DomibusConnectorMessageConfirmationType> loadMessageConfirmations() {
        return messageProperties
            .stringPropertyNames()
            .stream()
            .sorted()
            .filter(k -> k.startsWith(LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX))
            .map(k -> k.split("\\.")[2])
            .distinct()
            .map(k -> {
                var evidenceFilePropertyName = String.format(
                    "%s.%s.%s",
                    LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX,
                    k, "file"
                );
                var evidenceTypePropertyName = String.format(
                    "%s.%s.%s",
                    LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX,
                    k, "type"
                );

                var resEvidenceFile = createRelativeResource(
                    messageProperties.getProperty(evidenceFilePropertyName));

                // TODO: determine evidence type!
                //  DomibusConnectorMessageC domibusConnectorEvidenceType =
                //  DomibusConnectorEvidenceType.valueOf(messageProperties
                //  .getProperty(evidenceTypePropertyName));
                var domibusConnectorConfirmationType = DomibusConnectorConfirmationType.fromValue(
                    messageProperties.getProperty(evidenceTypePropertyName)
                );

                var confirmation = new DomibusConnectorMessageConfirmationType();
                confirmation.setConfirmation(loadResourceAsSource(resEvidenceFile));
                confirmation.setConfirmationType(domibusConnectorConfirmationType);
                // confirmation.setEvidenceType(domibusConnectorEvidenceType);
                // confirmation.setConfirmationType(DomibusConnectorConfirmationType.valueOf());

                return confirmation;
            })
            .toList();
    }

    private Source loadResourceAsSource(Resource resEvidenceFile) {
        try {
            return new StreamSource(resEvidenceFile.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DomibusConnectorMessageAttachmentType> loadMessageAttachments() {
        return messageProperties
            .stringPropertyNames()
            .stream()
            .sorted()
            .filter(k -> k.startsWith(
                LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX))
            .map(k -> k.split("\\.")[2])
            .distinct()
            .map(k -> {
                var attachment = new DomibusConnectorMessageAttachmentType();
                var filePropertyName = String.format(
                    "%s.%s.%s",
                    LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX,
                    k,
                    "file"
                );
                var identifierPropertyName = String.format(
                    "%s.%s.%s",
                    LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX,
                    k,
                    "identifier"
                );
                Path res = basicFolder.resolve(
                    messageProperties.getProperty(filePropertyName));
                attachment.setAttachment(loadResourceAsDataHandler(
                    new FileSystemResource(res.toFile())));
                attachment.setIdentifier(
                    messageProperties.getProperty(identifierPropertyName));
                return attachment;
            })
            .toList();
    }

    private DataHandler loadResourceAsDataHandler(Resource res) {
        try {
            return new DataHandler(new InputStreamDataSource(res.getInputStream()));
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
            var inputStream = res.getInputStream();
            return StreamUtils.copyToByteArray(inputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private DomibusConnectorMessageDetailsType loadMessageDetails() {
        var messageDetails = new DomibusConnectorMessageDetailsType();

        var domibusConnectorActionType = new DomibusConnectorActionType();
        domibusConnectorActionType.setAction(messageProperties.getProperty(ACTION_PROP_NAME));
        messageDetails.setAction(domibusConnectorActionType);

        var fromParty = new DomibusConnectorPartyType();
        fromParty.setPartyId(messageProperties.getProperty(FROM_PARTY_ID_PROP_NAME));
        fromParty.setPartyIdType(messageProperties.getProperty(FROM_PARTY_ID_TYPE_PROP_NAME));
        fromParty.setRole(messageProperties.getProperty(FROM_PARTY_ROLE_PROP_NAME));
        messageDetails.setFromParty(fromParty);

        var toParty = new DomibusConnectorPartyType();
        toParty.setPartyId(messageProperties.getProperty(TO_PARTY_ID_PROP_NAME));
        toParty.setPartyIdType(messageProperties.getProperty(TO_PARTY_ID_TYPE_PROP_NAME));
        toParty.setRole(messageProperties.getProperty(TO_PARTY_ROLE_PROP_NAME));
        messageDetails.setToParty(toParty);

        var service = new DomibusConnectorServiceType();
        service.setService(messageProperties.getProperty(SERVICE_NAME_PROP_NAME));
        service.setServiceType(messageProperties.getProperty(SERVICE_TYPE_PROP_NAME));
        messageDetails.setService(service);

        messageDetails.setConversationId(messageProperties.getProperty(CONVERSATION_ID_PROP_NAME));

        messageDetails.setEbmsMessageId(messageProperties.getProperty(EBMS_ID_PROP_NAME));
        messageDetails.setRefToMessageId(
            messageProperties.getProperty(REF_TO_MESSAGE_ID_PROP_NAME));

        messageDetails.setBackendMessageId(messageProperties.getProperty(NATIONAL_ID_PROP_NAME));

        messageDetails.setOriginalSender(
            messageProperties.getProperty(ORIGINAL_SENDER_NAME_PROP_NAME));
        messageDetails.setFinalRecipient(messageProperties.getProperty(FINAL_RECIPIENT_PROP_NAME));

        return messageDetails;
    }

    /**
     * An implementation of the {@link DataSource} interface that wraps an {@link InputStream}. It
     * reads the content of the input stream and stores it in memory using a
     * {@link ByteArrayOutputStream}. The content can be retrieved as an {@link InputStream} or as a
     * byte array. This class is read-only, and attempting to get an output stream will throw an
     * {@link IOException}.
     */
    public static class InputStreamDataSource implements DataSource {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        /**
         * An implementation of the {@link DataSource} interface that wraps an {@link InputStream}.
         * It reads the content of the input stream and stores it in memory using a
         * {@link ByteArrayOutputStream}. The content can be retrieved as an {@link InputStream} or
         * as a byte array. This class is read-only, and attempting to get an output stream will
         * throw an {@link IOException}.
         *
         * @param inputStream The input stream to be wrapped.
         */
        public InputStreamDataSource(InputStream inputStream) {
            try {
                StreamUtils.copy(inputStream, buffer);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getContentType() {
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
