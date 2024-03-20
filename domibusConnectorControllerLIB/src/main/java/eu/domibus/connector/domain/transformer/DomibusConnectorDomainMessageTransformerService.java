package eu.domibus.connector.domain.transformer;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.domain.transition.*;
import eu.domibus.connector.domain.transition.tools.ConversionTools;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

import javax.activation.DataHandler;

import javax.validation.constraints.NotNull;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transforms the TransitionObjects to the internal domainModel
 */
@Service
public class DomibusConnectorDomainMessageTransformerService {

    private final static Logger LOGGER = LoggerFactory.getLogger(DomibusConnectorDomainMessageTransformerService.class);
    private final LargeFilePersistenceService largeFilePersistenceService;

    public List<DomibusConnectorMessageError> transformTransitionToDomain(List<DomibusConnectorMessageErrorType> messageErrors) {
        return messageErrors.stream()
                .map(this::transformMessageErrorTransitionToDomain)
                .collect(Collectors.toList());
    }


    /**
     * This exception is thrown when the provided
     * domain model object does not fullfill the requirements of the
     * transition model in a way that there is no mapping possibly
     */
    public static class CannotBeMappedToTransitionException extends RuntimeException {

        private CannotBeMappedToTransitionException(String text) {
            super(text);
        }

        private CannotBeMappedToTransitionException(String text, Throwable thr) {
            super(text, thr);
        }

    }

    @Autowired
    public DomibusConnectorDomainMessageTransformerService(LargeFilePersistenceService largeFilePersistenceService) {
        this.largeFilePersistenceService = largeFilePersistenceService;
    }

    /**
     * Holds the current domibus connector message id / message processing id
     * is package private so can be access by Test
     */
    ThreadLocal<DomibusConnectorMessageId> messageIdThreadLocal = new ThreadLocal<>();


    /**
     * transforms a message from domain model to
     * transition model
     * <p>
     * this method does not check if the message makes sense
     * <p>
     * <p>
     * DomibusConnectorMessage#getMessageDetails must be not null!
     *
     * @param domainMessage - the message (domain model)
     * @return - the transformed message (transition model)
     * @throws CannotBeMappedToTransitionException if a
     */
    public  @NotNull
    DomibusConnectorMessageType transformDomainToTransition(final @NotNull DomibusConnectorMessage domainMessage) throws CannotBeMappedToTransitionException {
        LOGGER.debug("transformDomainToTransition: with domainMessage [{}]", domainMessage);
        if (domainMessage == null) {
            throw new CannotBeMappedToTransitionException("domainMessage is not allowed to be null!");
        }
        if (domainMessage.getMessageDetails() == null) {
            throw new CannotBeMappedToTransitionException("DomibusConnectorMessage.getMessageDetails() is not allowed to be null!");
        }
        DomibusConnectorMessageType TOMessageType = new DomibusConnectorMessageType();

        //map messageDetails
        TOMessageType.setMessageDetails(transformMessageDetailsDomainToTransition(domainMessage));
        //map messageContent
        TOMessageType.setMessageContent(transformMessageContentDomainToTransition(domainMessage.getMessageContent()));
        //map message confirmations
        List<DomibusConnectorMessageConfirmation> messageConfirmations = domainMessage.getTransportedMessageConfirmations();
        LOGGER.trace("#transformDomainToTransition: transform messageConfirmations [{}] to transition", messageConfirmations);
        for (DomibusConnectorMessageConfirmation msgConfirm : messageConfirmations) {
            TOMessageType.getMessageConfirmations()
                    .add(transformMessageConfirmationDomainToTransition(msgConfirm));
        }
        //map message attachments
        LOGGER.trace("#transformDomainToTransition: transform messageAttachments [{}] to transition", domainMessage.getMessageAttachments());
        for (DomibusConnectorMessageAttachment msgAttach : domainMessage.getMessageAttachments()) {
            TOMessageType.getMessageAttachments()
                    .add(transformMessageAttachmentDomainToTransition(msgAttach));
        }
        //map message errors
        LOGGER.trace("#transformDomainToTransition: transform messageErrors [{}] to transition", domainMessage.getMessageProcessErrors());
        for (DomibusConnectorMessageError msgError : domainMessage.getMessageProcessErrors()) {
            TOMessageType.getMessageErrors()
                    .add(transformMessageErrorDomainToTransition(msgError));
        }

        return TOMessageType;
    }

    /**
     * converts a messageConfirmation from domain model to
     * transition model
     * the getEvidence byteArray must be not null!
     *
     * @param messageConfirmation the messageConfirmation
     * @return the messageConfirmation in transition model
     * @throws IllegalArgumentException is thrown if DomibusConnectorMessageConfirmation#getEvidence returns null or
     *                                  DomibusConnectorMessageConfirmation#getEvidenceType returns null
     */
    @NotNull
    DomibusConnectorMessageConfirmationType transformMessageConfirmationDomainToTransition(final @NotNull DomibusConnectorMessageConfirmation messageConfirmation) {
        DomibusConnectorMessageConfirmationType confirmationTO = new DomibusConnectorMessageConfirmationType();
        if (messageConfirmation.getEvidence() == null) {
            throw new CannotBeMappedToTransitionException("byte array getEvidence() is not allowed to be null!");
        }
        if (messageConfirmation.getEvidenceType() == null) {
            throw new CannotBeMappedToTransitionException("evidenceType is not allowed to be null!");
        }

        StreamSource streamSource = new StreamSource(new ByteArrayInputStream(
                //byte[] is copied because domain model is not immutable
                Arrays.copyOf(messageConfirmation.getEvidence(), messageConfirmation.getEvidence().length)));
        confirmationTO.setConfirmation(streamSource);

        //confirmationTO.setConfirmation(Arrays.copyOf(messageConfirmation.getEvidence(), messageConfirmation.getEvidence().length));        


        confirmationTO.setConfirmationType(DomibusConnectorConfirmationType.valueOf(messageConfirmation.getEvidenceType().name()));

        return confirmationTO;

    }

    /**
     * Translates messageError from domain model to transition model
     *
     * @param messageError - the (domain model) messageError
     * @return the translated (transition model) messageError
     */
    @NotNull
    DomibusConnectorMessageErrorType transformMessageErrorDomainToTransition(final @NotNull DomibusConnectorMessageError messageError) {
        DomibusConnectorMessageErrorType errorTO = new DomibusConnectorMessageErrorType();
        BeanUtils.copyProperties(messageError, errorTO);
        return errorTO;
    }


    /**
     * Translates messageAttachment from domain model to transition model
     * the attachment and identifier property must not be null!
     *
     * @param messageAttachment - the (domain model) messageAttachment
     * @return the translated (transition model) messageAttachment
     */
    @NotNull
    DomibusConnectorMessageAttachmentType transformMessageAttachmentDomainToTransition(final @NotNull DomibusConnectorMessageAttachment messageAttachment) {
        DomibusConnectorMessageAttachmentType attachmentTO = new DomibusConnectorMessageAttachmentType();
        if (messageAttachment.getAttachment() == null) {
            throw new CannotBeMappedToTransitionException("attachment is not allowed to be null!");
        }
        if (messageAttachment.getIdentifier() == null) {
            throw new CannotBeMappedToTransitionException("identifier is not allowed to be null!");
        }
        BeanUtils.copyProperties(messageAttachment, attachmentTO);


        attachmentTO.setAttachment(convertBigDataReferenceToDataHandler(messageAttachment.getAttachment(), messageAttachment.getMimeType()));
        return attachmentTO;
    }

    /**
     * Translates messageContent from domain model to transition model
     *
     * @param messageContent - the (domain model) messageContent
     * @return the translated (transition model) messageContent or null if null provided
     */
    @Nullable
    DomibusConnectorMessageContentType transformMessageContentDomainToTransition(final @Nullable DomibusConnectorMessageContent messageContent) {
        if (messageContent == null) {
            return null;
        }
        DomibusConnectorMessageContentType messageContentTO = new DomibusConnectorMessageContentType();
        if (messageContent.getXmlContent() == null) {
            throw new CannotBeMappedToTransitionException("xmlContent of content must be not null!");
        }

        if(LOGGER.isTraceEnabled()) {
        	LOGGER.trace("Business content XML before transformed to stream: {}", new String(messageContent.getXmlContent()));
        }
        StreamSource streamSource = new StreamSource(new ByteArrayInputStream(
                //byte[] is copied because domain model is not immutable
//                Arrays.copyOf(messageContent.getXmlContent(), messageContent.getXmlContent().length)
        		messageContent.getXmlContent()
                ));
        messageContentTO.setXmlContent(streamSource);

        //maps Document of messageContent
        DomibusConnectorMessageDocument document = messageContent.getDocument();
        DomibusConnectorMessageDocumentType documentTO = new DomibusConnectorMessageDocumentType();

        if (document != null) {
            LargeFileReference docDataRef = document.getDocument();
            documentTO.setDocument(convertBigDataReferenceToDataHandler(docDataRef, null));
            documentTO.setDocumentName(document.getDocumentName());
            messageContentTO.setDocument(documentTO);

            //map signature type of document
            DetachedSignature detachedSignature = document.getDetachedSignature();
            if (detachedSignature != null) {
                DomibusConnectorDetachedSignatureType detachedSignatureTypeTO = new DomibusConnectorDetachedSignatureType();
                detachedSignatureTypeTO.setDetachedSignature(
                        Arrays.copyOf(detachedSignature.getDetachedSignature(), detachedSignature.getDetachedSignature().length));
                detachedSignatureTypeTO.setDetachedSignatureName(detachedSignature.getDetachedSignatureName());
                detachedSignatureTypeTO.setMimeType(
                        DomibusConnectorDomainDetachedSignatureEnumTransformer
                                .transformDetachedSignatureMimeTypeDomainToTransition(detachedSignature.getMimeType()));
                documentTO.setDetachedSignature(detachedSignatureTypeTO);
            } else {
                LOGGER.debug("#transformMessageContentDomainToTransition: no detached signature to map!");
            }
        } else {
            LOGGER.debug("#transformMessageContentDomainToTransition: document contains no document data");
        }


        return messageContentTO;
    }


    @NotNull
    DataHandler convertBigDataReferenceToDataHandler(@NotNull LargeFileReference largeFileReference, @Nullable String mimeType) {
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        LargeFileReference readableDataSource = this.largeFilePersistenceService.getReadableDataSource(largeFileReference);
        DataHandler dataHandler = new DataHandler(readableDataSource);
        return dataHandler;
    }



    @NotNull
    DomibusConnectorMessageDetailsType transformMessageDetailsDomainToTransition(final @NotNull DomibusConnectorMessage message) {
        DomibusConnectorMessageDetails messageDetails = message.getMessageDetails();
        LOGGER.debug("transformMessageDetailsDomaintToTransition: messageDetails are [{}]", messageDetails);
        if (messageDetails == null) {
            throw new CannotBeMappedToTransitionException("messageDetails are not allowed to be null!");
        }
        DomibusConnectorMessageDetailsType TODetailsType = new DomibusConnectorMessageDetailsType();

        if (messageDetails.getFinalRecipient() == null) {
            throw new CannotBeMappedToTransitionException("final recipient is mandatory!");
        }
        if (messageDetails.getOriginalSender() == null) {
            throw new CannotBeMappedToTransitionException("original sender is mandatory!");
        }
        //map all properties with same name and type: backendMessageId, conversationId, finalRecipient, originalSender, refToMessageId
        BeanUtils.copyProperties(messageDetails, TODetailsType);

        //map action        
        if (messageDetails.getAction() == null) {
            throw new CannotBeMappedToTransitionException("action is mandatory in messageDetails!");
        }
        DomibusConnectorActionType actionTO = new DomibusConnectorActionType();
        BeanUtils.copyProperties(messageDetails.getAction(), actionTO);
        TODetailsType.setAction(actionTO);

        //map fromParty
        if (messageDetails.getFromParty() == null) {
            throw new CannotBeMappedToTransitionException("fromParty is mandatory in messageDetails");
        }
        DomibusConnectorPartyType fromPartyTO = new DomibusConnectorPartyType();
        BeanUtils.copyProperties(messageDetails.getFromParty(), fromPartyTO);
        TODetailsType.setFromParty(fromPartyTO);

        //map toParty
        if (messageDetails.getToParty() == null) {
            throw new CannotBeMappedToTransitionException("toParty is mandatory in messageDetails");
        }
        DomibusConnectorPartyType toPartyTO = new DomibusConnectorPartyType();
        BeanUtils.copyProperties(messageDetails.getToParty(), toPartyTO);
        TODetailsType.setToParty(toPartyTO);

        //map service
        if (messageDetails.getService() == null) {
            throw new CannotBeMappedToTransitionException("service is mandatory in messageDetails");
        }
        DomibusConnectorServiceType serviceTO = new DomibusConnectorServiceType();
        BeanUtils.copyProperties(messageDetails.getService(), serviceTO);
        TODetailsType.setService(serviceTO);

        //map ref to message id
        TODetailsType.setRefToMessageId(message.getMessageDetails().getRefToMessageId());
        //map backendMessageId
        if (DomainModelHelper.isEvidenceMessage(message)) {
            LOGGER.debug("Message is an evidence message, setting backendMessageId to [{}] (from refToBackendMessageId)!", messageDetails.getRefToBackendMessageId());
            TODetailsType.setBackendMessageId(messageDetails.getRefToBackendMessageId());
            //only use refToBackendMessageId if is going to backend and it is not empty
            if (message.getMessageDetails().getDirection().getTarget() == MessageTargetSource.BACKEND &&
                    !StringUtils.isEmpty(messageDetails.getRefToBackendMessageId())) {
                TODetailsType.setRefToMessageId(messageDetails.getRefToBackendMessageId());
            }
        }

        return TODetailsType;
    }



    /**
     * @param transitionMessage - the TransitionMessage
     * @return the domainModel message
     */
    public  @NotNull
    DomibusConnectorMessage transformTransitionToDomain(final @NotNull DomibusConnectorMessageType transitionMessage, final @NotNull DomibusConnectorMessageId messageId) {
        messageIdThreadLocal.set(messageId);
        try {
            LOGGER.trace("#transformTransitionToDomain: transforming transition message object [{}] to domain message object", transitionMessage);
            DomibusConnectorMessageDetailsType messageDetailsTO = transitionMessage.getMessageDetails();
            DomibusConnectorMessageDetails messageDetails = transformMessageDetailsTransitionToDomain(messageDetailsTO);
            //DomibusConnectorMessage
            DomibusConnectorMessage domibusConnectorMessage = null;

            //map confirmations
            LOGGER.trace("#transformTransitionToDomain: transitionMessage has [{}] confirmations", transitionMessage.getMessageConfirmations().size());
            List<DomibusConnectorMessageConfirmation> confirmations =
                    transitionMessage.getMessageConfirmations().stream().map(c -> transformMessageConfirmationTransitionToDomain(c)).collect(Collectors.toList());

            if (transitionMessage.getMessageContent() == null && confirmations.size() > 0) {
                LOGGER.trace("#transformTransitionToDomain: transforming message is a confirmation message");
                DomibusConnectorMessageConfirmation confirmation = confirmations.remove(0);
                domibusConnectorMessage = new DomibusConnectorMessage(messageDetails, confirmation);
                LOGGER.trace("#transformTransitionToDomain: added [{}] additional confirmations to confirmation message", confirmations);
                for (DomibusConnectorMessageConfirmation c : confirmations) {
                    domibusConnectorMessage.addTransportedMessageConfirmation(c);
                }


            } else if (transitionMessage.getMessageContent() != null) {
                LOGGER.trace("#transformTransitionToDomain: transforming message is a business message");
                //DomibusConnectorMessageContentType messageContent = transitionMessage.getMessageContent();
                DomibusConnectorMessageContent messageContent = transformMessageContentTransitionToDomain(transitionMessage.getMessageContent());
                domibusConnectorMessage = new DomibusConnectorMessage(messageDetails, messageContent);
                LOGGER.trace("#transformTransitionToDomain: added [{}] confirmations to message", confirmations);
                for (DomibusConnectorMessageConfirmation c : confirmations) {
                    domibusConnectorMessage.addTransportedMessageConfirmation(c);
                }

            } else {
                //should not end up here!
                throw new IllegalArgumentException("cannot map provided transition model!");
            }

            //map message errors
            for (DomibusConnectorMessageErrorType error : transitionMessage.getMessageErrors()) {
                domibusConnectorMessage.addError(
                        transformMessageErrorTransitionToDomain(error));
            }

            //map message attachments
            LOGGER.trace("#transformTransitionToDomain: transform messageAttachments [{}]", transitionMessage.getMessageAttachments());
            for (DomibusConnectorMessageAttachmentType attachment : transitionMessage.getMessageAttachments()) {
                domibusConnectorMessage.addAttachment(
                        transformMessageAttachmentTransitionToDomain(attachment));
            }
            LOGGER.trace("#transformTransitionToDomain: Sucessfully transformed [{}] message attachments", domibusConnectorMessage.getMessageAttachments());

            LOGGER.trace("#transformTransitionToDomain: Sucessfully transformed message to [{}]", domibusConnectorMessage);

            domibusConnectorMessage.setConnectorMessageId(messageIdThreadLocal.get());

            setMessageProcessProperties(domibusConnectorMessage, transitionMessage);

            return domibusConnectorMessage;

        } finally {
            messageIdThreadLocal.remove();
        }
    }

    private void setMessageProcessProperties(DomibusConnectorMessage domibusConnectorMessage, DomibusConnectorMessageType transitionMessage) {
        if (transitionMessage.getMessageContent() != null &&
                transitionMessage.getMessageContent().getDocument() != null &&
                transitionMessage.getMessageContent()
                        .getDocument()
                        .getAesType() != null
        ) {
            DomibusConnectorDocumentAESType aesType = transitionMessage.getMessageContent()
                    .getDocument()
                    .getAesType();

            AdvancedElectronicSystemType advancedElectronicSystemType = AdvancedElectronicSystemType.valueOf(aesType.name());

            domibusConnectorMessage.getDcMessageProcessSettings()
                .setValidationServiceName(advancedElectronicSystemType);
            LOGGER.trace("#transformTransitionToDomain: setting AES type to ", advancedElectronicSystemType);
        }
    }


    @NotNull
    DomibusConnectorMessageAttachment transformMessageAttachmentTransitionToDomain(final @NotNull DomibusConnectorMessageAttachmentType messageAttachmentTO) {

        DomibusConnectorMessageAttachment messageAttachment = new DomibusConnectorMessageAttachment(
                convertDataHandlerToBigFileReference(messageAttachmentTO.getAttachment()),
                messageAttachmentTO.getIdentifier()
        );
        BeanUtils.copyProperties(messageAttachmentTO, messageAttachment);

        return messageAttachment;
    }


    @NotNull
    DomibusConnectorMessageError transformMessageErrorTransitionToDomain(final @NotNull DomibusConnectorMessageErrorType errorTypeTO) {
        return DomibusConnectorMessageErrorBuilder.createBuilder()
                .setText(errorTypeTO.getErrorMessage())
                .setDetails(errorTypeTO.getErrorDetails())
                .setSource(errorTypeTO.getErrorSource())
                .build();
    }

    DomibusConnectorMessageConfirmation transformMessageConfirmationTransitionToDomain(final @NotNull DomibusConnectorMessageConfirmationType messageConfirmationTO) {
        DomibusConnectorMessageConfirmation confirmation = new DomibusConnectorMessageConfirmation();

        Source evidence = messageConfirmationTO.getConfirmation();
        if (evidence != null) {
            confirmation.setEvidence(ConversionTools.convertXmlSourceToByteArray(evidence));
        }
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.valueOf(messageConfirmationTO.getConfirmationType().name()));

        return confirmation;
    }


    @NotNull
    DomibusConnectorMessageContent transformMessageContentTransitionToDomain(final @NotNull DomibusConnectorMessageContentType messageContentTO) {
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();

        byte[] result = ConversionTools.convertXmlSourceToByteArray(messageContentTO.getXmlContent());

        messageContent.setXmlContent(result);
        if(LOGGER.isTraceEnabled()) {
        	LOGGER.trace("Business content XML after transformed from stream: {}", new String(result));
        }

        //maps Document of messageContent
        DomibusConnectorMessageDocumentType documentTO = messageContentTO.getDocument();

        if (documentTO != null) {
            DomibusConnectorMessageDocumentBuilder documentBuilder = DomibusConnectorMessageDocumentBuilder.createBuilder();
            //maps signature of document     
            DomibusConnectorDetachedSignatureType detachedSignatureTO = documentTO.getDetachedSignature();

            if (detachedSignatureTO != null) {
                DetachedSignature detachedSignature = new DetachedSignature(
                        Arrays.copyOf(detachedSignatureTO.getDetachedSignature(), detachedSignatureTO.getDetachedSignature().length),
                        detachedSignatureTO.getDetachedSignatureName(),
                        //                eu.domibus.connector.domain.model.DetachedSignatureMimeType.valueOf(detachedSignatureTO.getMimeType().name())
                        DomibusConnectorDomainDetachedSignatureEnumTransformer
                                .transformDetachedSignatureMimeTypeTransitionToDomain(detachedSignatureTO.getMimeType())
                );
                documentBuilder.withDetachedSignature(detachedSignature);
            }
            documentBuilder.setContent(convertDataHandlerToBigFileReference(documentTO.getDocument()));
            documentBuilder.setName(documentTO.getDocumentName());

            messageContent.setDocument(documentBuilder.build());
        }

        return messageContent;
    }


    @NotNull
    LargeFileReference convertDataHandlerToBigFileReference(DataHandler dataHandler) {
//        LargeFileHandlerBacked bigDataReference = new LargeFileHandlerBacked();
//        bigDataReference.setDataHandler(dataHandler);
//        return bigDataReference;
        DomibusConnectorMessageId domibusConnectorMessageId = messageIdThreadLocal.get();
        LargeFileReference domibusConnectorBigDataReference = largeFilePersistenceService.createDomibusConnectorBigDataReference(domibusConnectorMessageId, dataHandler.getName(), dataHandler.getContentType());
        try (InputStream is = dataHandler.getInputStream();
            OutputStream os = domibusConnectorBigDataReference.getOutputStream() ) {
            StreamUtils.copy(is, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return domibusConnectorBigDataReference;

    }

    @NotNull
    DomibusConnectorMessageDetails transformMessageDetailsTransitionToDomain(final @NotNull DomibusConnectorMessageDetailsType messageDetailsTO) {
        DomibusConnectorMessageDetails messageDetails = new DomibusConnectorMessageDetails();

        //map all properties with same name and type: backendMessageId, conversationId, finalRecipient, originalSender, refToMessageId
        BeanUtils.copyProperties(messageDetailsTO, messageDetails);

        //map action
        DomibusConnectorActionType actionTO = messageDetailsTO.getAction();

        DomibusConnectorAction action =
//                new DomibusConnectorAction(actionTO.getAction(), true); //default mapping is assumed true!
        		new DomibusConnectorAction(actionTO.getAction());
        messageDetails.setAction(action);

        //map service
        DomibusConnectorServiceType serviceTO = messageDetailsTO.getService();
        DomibusConnectorService service =
                new DomibusConnectorService(serviceTO.getService(), serviceTO.getServiceType());
        messageDetails.setService(service);

        //map partyTO
        DomibusConnectorPartyType toPartyTO = messageDetailsTO.getToParty();
        if (toPartyTO == null) {
            throw new IllegalArgumentException("toParty in messageDetails is not allowed to be null!");
        }
        DomibusConnectorParty toParty =
                new DomibusConnectorParty(toPartyTO.getPartyId(), toPartyTO.getPartyIdType(), toPartyTO.getRole());
        messageDetails.setToParty(toParty);

        //map partyFrom
        DomibusConnectorPartyType fromPartyTO = messageDetailsTO.getFromParty();
        if (fromPartyTO == null) {
            throw new IllegalArgumentException("fromParty in messageDetails is not allowed to be null!");
        }
        DomibusConnectorParty fromParty =
                new DomibusConnectorParty(fromPartyTO.getPartyId(), fromPartyTO.getPartyIdType(), fromPartyTO.getRole());
        messageDetails.setFromParty(fromParty);


        return messageDetails;
    }

}