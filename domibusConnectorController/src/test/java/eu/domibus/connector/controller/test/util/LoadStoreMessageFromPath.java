package eu.domibus.connector.controller.test.util;



//TODO: NOT FINIHSED YET

import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.model.builder.*;
import eu.domibus.connector.domain.testutil.LargeFileReferenceGetSetBased;
import eu.domibus.connector.testdata.LoadStoreTransitionMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Should load test messages from directory
 * store messages to directory
 *
 */
public class LoadStoreMessageFromPath {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadStoreMessageFromPath.class);

    private final Resource basicFolder;

    private Properties messageProperties;



    public static void storeMessageTo(Resource resource, DomibusConnectorMessage message) throws IOException {
        LoadStoreMessageFromPath loadStoreMessageFromPath = new LoadStoreMessageFromPath(resource);
        loadStoreMessageFromPath.storeMessage(message);
    }

    private LoadStoreMessageFromPath(Resource basicFolder) {
        this.basicFolder = basicFolder;
        this.messageProperties = new Properties();
    }




    private void storeMessage(DomibusConnectorMessage message) throws IOException {

        if (!basicFolder.exists()) {
            System.out.println("basic folder is: " + basicFolder.getFile().getAbsolutePath());
            basicFolder.getFile().mkdirs();
        }

        Resource propertiesResource = basicFolder.createRelative("/message.properties");
        if (propertiesResource.exists()) {
            throw new RuntimeException("message already exists cannot overwrite it!");
        }


        storeMessageDetails(message.getMessageDetails());

        //store content
        DomibusConnectorMessageContent messageContent = message.getMessageContent();
        if (messageContent != null) {
            byte[] xmlContent = messageContent.getXmlContent();
            Resource r = basicFolder.createRelative(LoadStoreTransitionMessage.DEFAULT_CONTENT_XML_FILE_NAME);
            messageProperties.put(LoadStoreTransitionMessage.MESSAGE_CONTENT_XML_PROP_NAME, LoadStoreTransitionMessage.DEFAULT_CONTENT_XML_FILE_NAME);
            writeByteArrayToResource(r, xmlContent);

            //store message document
            DomibusConnectorMessageDocument messageDocument = messageContent.getDocument();
            if (messageDocument != null) {
                messageDocument.getDocument();
                String fileName = messageDocument.getDocumentName() == null ? "document.pdf" : messageDocument.getDocumentName();
                Resource d = basicFolder.createRelative(fileName);
                writeBigDataReferenceToResource(d, messageDocument.getDocument());

                messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_FILE_PROP_NAME, fileName);

                if (messageDocument.getDocumentName() != null ) {
                    messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_NAME_PROP_NAME, messageDocument.getDocumentName());
                }
                if (messageDocument.getHashValue() != null) {
                    messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_HASH_PROP_NAME, messageDocument.getHashValue());
                }

                DetachedSignature detachedSignature = messageDocument.getDetachedSignature();
                if (detachedSignature != null) {
                    byte[] detachedSignatureBytes = detachedSignature.getDetachedSignature();
                    DetachedSignatureMimeType detachedSignatureMimeType = detachedSignature.getMimeType();
                    String detachedSignatureName = detachedSignature.getDetachedSignatureName() == null ? "detachedSignature" : detachedSignature.getDetachedSignatureName();

                    String appendix = detachedSignatureMimeType.name().toLowerCase();
                    String detachedResourceFilename = detachedSignatureName + "." + appendix;
                    Resource res = basicFolder.createRelative(detachedResourceFilename);

                    writeByteArrayToResource(res, detachedSignatureBytes);

                    messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_FILE_PROP_NAME, detachedResourceFilename);
                    messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_TYPE_PROP_NAME, detachedSignatureMimeType.name());
                    if (detachedSignatureName != null) {
                        messageProperties.put(LoadStoreTransitionMessage.MESSAGE_DOCUMENT_SIGNATURE_NAME_PROP_NAME, detachedSignatureName);
                    }

                }

            }

        }

        //store attachments
        storeMessageAttachments(message.getMessageAttachments());


        //store confirmations
        storeMessageConfirmations(message.getTransportedMessageConfirmations());

        File file = propertiesResource.getFile();
        System.out.println(file.getAbsolutePath());
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        messageProperties.store(fileOutputStream, null);

    }

    private void storeMessageConfirmations(List<DomibusConnectorMessageConfirmation> messageConfirmations) {
        for (int i = 0; i < messageConfirmations.size(); i++) {
            try {
                DomibusConnectorMessageConfirmation confirmation = messageConfirmations.get(i);

                String evidenceFilePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, i, "file");
                String evidenceTypePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, i, "type");

                messageProperties.put(evidenceTypePropertyName, confirmation.getEvidenceType().name());

                String fileName = confirmation.getEvidenceType().name() + ".xml";
                messageProperties.put(evidenceFilePropertyName, confirmation.getEvidenceType().name() + ".xml");

                Resource r = basicFolder.createRelative(fileName);
                writeByteArrayToResource(r, confirmation.getEvidence());

            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }

        }
    }


    private void storeMessageAttachments(List<DomibusConnectorMessageAttachment> attachments) {
        for (int i = 0; i < attachments.size(); i++) {
            try {
                DomibusConnectorMessageAttachment a = attachments.get(i);

                String attachmentPropertyFile = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, i, "file");


                String fileName = a.getName();
                if (fileName == null) {
                    fileName = a.getIdentifier();
                }
                Resource attachmentOutputResource = basicFolder.createRelative("/" + fileName);
                writeBigDataReferenceToResource(attachmentOutputResource, a.getAttachment());
                messageProperties.put(attachmentPropertyFile, fileName);

                String attachmentPropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, i, "identifier");
                messageProperties.put(attachmentPropertyName, a.getIdentifier());
            } catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }
    }

    private void writeBigDataReferenceToResource(Resource res, LargeFileReference bigDataReference) {
        try {
            File attachmentOutputFile = res.getFile();
            InputStream inputStream = null;

            inputStream = bigDataReference.getInputStream();

            FileOutputStream fileOutputStream = new FileOutputStream(attachmentOutputFile);
            StreamUtils.copy(inputStream, fileOutputStream);
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }


    public static DomibusConnectorMessage loadMessageFrom(Resource resource) throws IOException {
        LoadStoreMessageFromPath loadStoreMessageFromPath = new LoadStoreMessageFromPath(resource);
        DomibusConnectorMessage message = loadStoreMessageFromPath.loadMessage();

        return message;
    }

    private DomibusConnectorMessage loadMessage() throws IOException {
        DomibusConnectorMessageBuilder messageBuilder = DomibusConnectorMessageBuilder.createBuilder();



        Resource propertiesResource = basicFolder.createRelative("message.properties");
        if (!propertiesResource.exists()) {
            throw new IOException("properties " + propertiesResource + " does not exist!");
        }

        messageProperties.load(propertiesResource.getInputStream());

        messageBuilder.setMessageDetails(loadMessageDetailsFromProperties());

        Resource contentResource = createRelativeResource(messageProperties.getProperty(LoadStoreTransitionMessage.MESSAGE_CONTENT_XML_PROP_NAME));
        if (contentResource != null && contentResource.exists()) {
            DomibusConnectorMessageContent content = new DomibusConnectorMessageContent();
            content.setXmlContent(StreamUtils.copyToByteArray(contentResource.getInputStream()));

            messageBuilder.setMessageContent(content);
            //load document
            String docFileName = messageProperties.getProperty("message.content.document.file");
            if (docFileName != null) {
                DomibusConnectorMessageDocumentBuilder documentBuilder = DomibusConnectorMessageDocumentBuilder.createBuilder();
                Resource r = basicFolder.createRelative(docFileName);
                LargeFileReference bigDataReferenceDocument = loadResourceAsBigDataRef(r);
                documentBuilder.setContent(bigDataReferenceDocument);
                String docName = messageProperties.getProperty("message.content.document.name");
                documentBuilder.setName(docName);

                //load signature
                String signatureFileName = messageProperties.getProperty("message.content.document.signature.file");
                if (signatureFileName != null) {

                    DetachedSignatureMimeType mimeType = DetachedSignatureMimeType.valueOf(messageProperties.getProperty("message.content.document.signature.type"));
                    String name = messageProperties.getProperty("message.content.document.signature.name");
                    Resource fResource = basicFolder.createRelative(signatureFileName);
                    byte[] signatureBytes = loadResourceAsByteArray(fResource);


                    DetachedSignature detachedSignature = DetachedSignatureBuilder.createBuilder()
                            .setMimeType(mimeType)
                            .setName(name)
                            .setSignature(signatureBytes)
                            .build();
                    documentBuilder.withDetachedSignature(detachedSignature);
                }


                DomibusConnectorMessageDocument doc = documentBuilder.build();
                content.setDocument(doc);

            }

        }

        messageBuilder.addAttachments(loadAttachments());
        messageBuilder.addTransportedConfirmations(loadConfirmations());

        String connid = messageProperties.getProperty("message.connector-id", null);
        if (connid != null) {
            messageBuilder.setConnectorMessageId(connid);
        }

        return messageBuilder.build();

    }




    private List<DomibusConnectorMessageConfirmation> loadConfirmations() {
        return messageProperties.stringPropertyNames()
                .stream()
                .sorted()
                .filter( (k) -> k.startsWith(LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX))
                .map( (k) -> k.split("\\.")[2])
                .distinct()
                .map( (k) -> {


                        String evidenceFilePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, k, "file");
                        String evidenceTypePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_CONFIRMATIONS_PREFIX, k, "type");

                        Resource resEvidenceFile = createRelativeResource(messageProperties.getProperty(evidenceFilePropertyName));
                        DomibusConnectorEvidenceType domibusConnectorEvidenceType = DomibusConnectorEvidenceType.valueOf(messageProperties.getProperty(evidenceTypePropertyName));

                        DomibusConnectorMessageConfirmationBuilder builder = DomibusConnectorMessageConfirmationBuilder
                                .createBuilder()
                                .setEvidence(loadResourceAsByteArray(resEvidenceFile))
                                .setEvidenceType(domibusConnectorEvidenceType);

                        return builder.build();

                })
                .collect(Collectors.toList());
    }



    private List<DomibusConnectorMessageAttachment> loadAttachments() {

        return messageProperties.stringPropertyNames()
                .stream()
                .sorted()
                .filter( (k) -> k.startsWith(LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX) )
                .map( (k) -> k.split("\\.")[2])
                .distinct()
                .map( (k) -> {
                    try {
                        DomibusConnectorMessageAttachmentBuilder builder = DomibusConnectorMessageAttachmentBuilder.createBuilder();
                        String filePropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, k, "file");
                        String identifierPropertyName = String.format("%s.%s.%s", LoadStoreTransitionMessage.MESSAGE_ATTACHMENT_PREFIX, k, "identifier");
                        Resource res = basicFolder.createRelative(messageProperties.getProperty(filePropertyName));
                        builder.setAttachment(loadResourceAsBigDataRef(res));
                        builder.setIdentifier(messageProperties.getProperty(identifierPropertyName));

                        return builder.build();

                    } catch (IOException ioe) {
                        throw new RuntimeException(ioe);
                    }
                })
                .collect(Collectors.toList());
    }

    private @Nullable Resource createRelativeResource(@Nullable String relativePath) {
        if (relativePath == null) {
            return null;
        }
        try {
            Resource contentResource = basicFolder.createRelative(relativePath);
            return contentResource;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }



    public static LargeFileReference loadResourceAsBigDataRef(Resource resource) {
        try {
            InputStream inputStream = resource.getInputStream();

            LargeFileReferenceGetSetBased inMemory = new LargeFileReferenceGetSetBased();
            inMemory.setBytes(StreamUtils.copyToByteArray(inputStream));
            inMemory.setReadable(true);
            inMemory.setStorageIdReference(UUID.randomUUID().toString());
            return inMemory;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private byte[] loadResourceAsByteArray(Resource res) {
        try {
            InputStream inputStream = res.getInputStream();
            return StreamUtils.copyToByteArray(inputStream);
        } catch(IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    private void writeByteArrayToResource(Resource res, byte[] bytes) {
        try {
            File f = res.getFile();
            FileOutputStream fileOutputStream = new FileOutputStream(f);

            org.springframework.util.StreamUtils.copy(bytes, fileOutputStream);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void storeMessageDetails(DomibusConnectorMessageDetails details) {

        if (details.getAction() != null && details.getAction().getAction() != null) {
            messageProperties.put(LoadStoreTransitionMessage.ACTION_PROP_NAME, details.getAction().getAction());
        }

        if (details.getFromParty() != null && details.getFromParty().getPartyId() != null && details.getFromParty().getRole() != null) {
            messageProperties.put(LoadStoreTransitionMessage.FROM_PARTY_ID_PROP_NAME, details.getFromParty().getPartyId());
            messageProperties.put(LoadStoreTransitionMessage.FROM_PARTY_ROLE_PROP_NAME, details.getFromParty().getRole());
        }
        if (details.getToParty() != null && details.getToParty().getPartyId() != null && details.getToParty().getRole() != null) {
            messageProperties.put(LoadStoreTransitionMessage.TO_PARTY_ID_PROP_NAME, details.getToParty().getPartyId());
            messageProperties.put(LoadStoreTransitionMessage.TO_PARTY_ROLE_PROP_NAME, details.getToParty().getRole());
        }

        if (details.getService() != null && details.getService() != null) {
            messageProperties.put(LoadStoreTransitionMessage.SERVICE_NAME_PROP_NAME, details.getService().getService());
        }

        if (details.getEbmsMessageId() != null) {
            messageProperties.put(LoadStoreTransitionMessage.EBMS_ID_PROP_NAME, details.getEbmsMessageId());
        }
        if (details.getConversationId() != null) {
            messageProperties.put(LoadStoreTransitionMessage.CONVERSATION_ID_PROP_NAME, details.getConversationId());
        }
        if (details.getConnectorBackendClientName() != null) {
            messageProperties.put(LoadStoreTransitionMessage.BACKEND_CLIENT_NAME_PROP_NAME, details.getConnectorBackendClientName());
        }
        if (details.getBackendMessageId() != null) {
            messageProperties.put(LoadStoreTransitionMessage.NATIONAL_ID_PROP_NAME, details.getBackendMessageId());
        }


    }




    private DomibusConnectorMessageDetails loadMessageDetailsFromProperties() {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();

        messageDetails.setAction(DomibusConnectorActionBuilder.createBuilder()
                .setAction(messageProperties.getProperty("action"))
//                .withDocumentRequired(false)
                .build()
        );


        messageDetails.setFromParty(DomibusConnectorPartyBuilder.createBuilder()
                .setPartyId(messageProperties.getProperty(LoadStoreTransitionMessage.FROM_PARTY_ID_PROP_NAME))
                .setPartyIdType(messageProperties.getProperty(LoadStoreTransitionMessage.FROM_PARTY_ID_TYPE_PROP_NAME))
                .setRole(messageProperties.getProperty(LoadStoreTransitionMessage.FROM_PARTY_ROLE_PROP_NAME))
                .build()
        );

        messageDetails.setToParty(DomibusConnectorPartyBuilder.createBuilder()
                .setPartyId(messageProperties.getProperty(LoadStoreTransitionMessage.TO_PARTY_ID_PROP_NAME))
                .setPartyIdType(messageProperties.getProperty(LoadStoreTransitionMessage.TO_PARTY_ID_TYPE_PROP_NAME))
                .setRole(messageProperties.getProperty(LoadStoreTransitionMessage.TO_PARTY_ROLE_PROP_NAME))
                .build()
        );

        messageDetails.setService(DomibusConnectorServiceBuilder.createBuilder()
                .setService(messageProperties.getProperty(LoadStoreTransitionMessage.SERVICE_NAME_PROP_NAME))
                .build()
        );

        messageDetails.setConversationId(messageProperties.getProperty(LoadStoreTransitionMessage.CONVERSATION_ID_PROP_NAME));

        messageDetails.setEbmsMessageId(messageProperties.getProperty(LoadStoreTransitionMessage.EBMS_ID_PROP_NAME));

        messageDetails.setBackendMessageId(messageProperties.getProperty(LoadStoreTransitionMessage.NATIONAL_ID_PROP_NAME));

        return messageDetails;
    }





}
