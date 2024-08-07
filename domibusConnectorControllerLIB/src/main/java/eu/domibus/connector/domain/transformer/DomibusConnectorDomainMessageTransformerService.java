/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.domain.transformer;

import eu.domibus.connector.domain.enums.AdvancedElectronicSystemType;
import eu.domibus.connector.domain.enums.DomibusConnectorEvidenceType;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.domain.model.DetachedSignature;
import eu.domibus.connector.domain.model.DomibusConnectorAction;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageAttachment;
import eu.domibus.connector.domain.model.DomibusConnectorMessageConfirmation;
import eu.domibus.connector.domain.model.DomibusConnectorMessageContent;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDetails;
import eu.domibus.connector.domain.model.DomibusConnectorMessageDocument;
import eu.domibus.connector.domain.model.DomibusConnectorMessageError;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.DomibusConnectorParty;
import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageErrorBuilder;
import eu.domibus.connector.domain.model.helper.DomainModelHelper;
import eu.domibus.connector.domain.transition.DomibusConnectorActionType;
import eu.domibus.connector.domain.transition.DomibusConnectorConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorDetachedSignatureType;
import eu.domibus.connector.domain.transition.DomibusConnectorDocumentAESType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageAttachmentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageConfirmationType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageContentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDetailsType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageDocumentType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageErrorType;
import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import eu.domibus.connector.domain.transition.DomibusConnectorPartyType;
import eu.domibus.connector.domain.transition.DomibusConnectorServiceType;
import eu.domibus.connector.domain.transition.tools.ConversionTools;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import jakarta.activation.DataHandler;
import jakarta.validation.constraints.NotNull;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;

/**
 * Transforms the TransitionObjects to the internal domainModel.
 */
@Service
public class DomibusConnectorDomainMessageTransformerService {
    private static final Logger LOGGER =
        LoggerFactory.getLogger(DomibusConnectorDomainMessageTransformerService.class);
    private final LargeFilePersistenceService largeFilePersistenceService;

    /**
     * Transforms a list of {@link DomibusConnectorMessageErrorType} objects to a list of
     * {@link DomibusConnectorMessageError} objects. Each {@link DomibusConnectorMessageErrorType}
     * object is transformed using the
     * {@link #transformMessageErrorTransitionToDomain(DomibusConnectorMessageErrorType)} method.
     *
     * @param messageErrors - the list of {@link DomibusConnectorMessageErrorType} objects
     * @return - the list of transformed {@link DomibusConnectorMessageError} objects
     */
    public List<DomibusConnectorMessageError> transformTransitionToDomain(
        List<DomibusConnectorMessageErrorType> messageErrors) {
        return messageErrors.stream()
                            .map(this::transformMessageErrorTransitionToDomain)
                            .collect(Collectors.toList());
    }

    /**
     * Transforms a transition message object to a domain message object.
     *
     * @param transitionMessage - the transition message object to transform
     * @param messageId         - the message ID associated with the transition message
     * @return - the transformed domain message object
     * @throws IllegalArgumentException if the provided transition model cannot be mapped
     */
    public @NotNull
    DomibusConnectorMessage transformTransitionToDomain(
        final @NotNull DomibusConnectorMessageType transitionMessage,
        final @NotNull DomibusConnectorMessageId messageId) {
        messageIdThreadLocal.set(messageId);
        try {
            LOGGER.trace(
                "#transformTransitionToDomain: transforming transition message object [{}] "
                    + "to domain message object",
                transitionMessage
            );
            DomibusConnectorMessageDetailsType messageDetailsTO =
                transitionMessage.getMessageDetails();
            DomibusConnectorMessageDetails messageDetails =
                transformMessageDetailsTransitionToDomain(messageDetailsTO);
            // DomibusConnectorMessage
            DomibusConnectorMessage domibusConnectorMessage = null;

            // map confirmations
            LOGGER.trace(
                "#transformTransitionToDomain: transitionMessage has [{}] confirmations",
                transitionMessage.getMessageConfirmations().size()
            );
            List<DomibusConnectorMessageConfirmation> confirmations =
                transitionMessage.getMessageConfirmations().stream()
                                 .map(this::transformMessageConfirmationTransitionToDomain)
                                 .collect(Collectors.toList());

            if (transitionMessage.getMessageContent() == null && !confirmations.isEmpty()) {
                LOGGER.trace(
                    "#transformTransitionToDomain: transforming message is a confirmation message");
                DomibusConnectorMessageConfirmation confirmation = confirmations.removeFirst();
                domibusConnectorMessage = new DomibusConnectorMessage(messageDetails, confirmation);
                LOGGER.trace(
                    "#transformTransitionToDomain: added [{}] additional confirmations to "
                        + "confirmation message",
                    confirmations
                );
                for (DomibusConnectorMessageConfirmation c : confirmations) {
                    domibusConnectorMessage.addTransportedMessageConfirmation(c);
                }
            } else if (transitionMessage.getMessageContent() != null) {
                LOGGER.trace(
                    "#transformTransitionToDomain: transforming message is a business message");
                // DomibusConnectorMessageContentType messageContent =
                // transitionMessage.getMessageContent();
                DomibusConnectorMessageContent messageContent =
                    transformMessageContentTransitionToDomain(
                        transitionMessage.getMessageContent());
                domibusConnectorMessage =
                    new DomibusConnectorMessage(messageDetails, messageContent);
                LOGGER.trace(
                    "#transformTransitionToDomain: added [{}] confirmations to message",
                    confirmations
                );
                for (DomibusConnectorMessageConfirmation c : confirmations) {
                    domibusConnectorMessage.addTransportedMessageConfirmation(c);
                }
            } else {
                // should not end up here!
                throw new IllegalArgumentException("cannot map provided transition model!");
            }

            // map message errors
            for (DomibusConnectorMessageErrorType error : transitionMessage.getMessageErrors()) {
                domibusConnectorMessage.addError(
                    transformMessageErrorTransitionToDomain(error));
            }

            // map message attachments
            LOGGER.trace(
                "#transformTransitionToDomain: transform messageAttachments [{}]",
                transitionMessage.getMessageAttachments()
            );
            for (DomibusConnectorMessageAttachmentType attachment :
                transitionMessage.getMessageAttachments()) {
                domibusConnectorMessage.addAttachment(
                    transformMessageAttachmentTransitionToDomain(attachment));
            }
            LOGGER.trace(
                "#transformTransitionToDomain: Sucessfully transformed [{}] message attachments",
                domibusConnectorMessage.getMessageAttachments()
            );

            LOGGER.trace(
                "#transformTransitionToDomain: Sucessfully transformed message to [{}]",
                domibusConnectorMessage
            );

            domibusConnectorMessage.setConnectorMessageId(messageIdThreadLocal.get());

            setMessageProcessProperties(domibusConnectorMessage, transitionMessage);

            return domibusConnectorMessage;
        } finally {
            messageIdThreadLocal.remove();
        }
    }

    /**
     * This exception is thrown when the provided domain model object does not fulfill the
     * requirements of the transition model in a way that there is no mapping possibly.
     */
    public static class CannotBeMappedToTransitionException extends RuntimeException {
        private CannotBeMappedToTransitionException(String text) {
            super(text);
        }

        private CannotBeMappedToTransitionException(String text, Throwable thr) {
            super(text, thr);
        }
    }

    public DomibusConnectorDomainMessageTransformerService(
        LargeFilePersistenceService largeFilePersistenceService) {
        this.largeFilePersistenceService = largeFilePersistenceService;
    }

    /**
     * Holds the current domibus connector message id / message processing id is package private so
     * can be access by Test.
     */
    ThreadLocal<DomibusConnectorMessageId> messageIdThreadLocal = new ThreadLocal<>();

    /**
     * Transforms a message from domain model to transition model.
     *
     * <p>this method does not check if the message makes sense
     *
     * <p>DomibusConnectorMessage#getMessageDetails must be not null!
     *
     * @param domainMessage - the message (domain model)
     * @return - the transformed message (transition model)
     * @throws CannotBeMappedToTransitionException if a
     */
    public @NotNull
    DomibusConnectorMessageType transformDomainToTransition(
        final @NotNull DomibusConnectorMessage domainMessage)
        throws CannotBeMappedToTransitionException {
        LOGGER.debug("transformDomainToTransition: with domainMessage [{}]", domainMessage);
        if (domainMessage == null) {
            throw new CannotBeMappedToTransitionException(
                "domainMessage is not allowed to be null!");
        }
        if (domainMessage.getMessageDetails() == null) {
            throw new CannotBeMappedToTransitionException(
                "DomibusConnectorMessage.getMessageDetails() is not allowed to be null!");
        }
        var toMessageType = new DomibusConnectorMessageType();

        // map messageDetails
        toMessageType.setMessageDetails(transformMessageDetailsDomainToTransition(domainMessage));
        // map messageContent
        toMessageType.setMessageContent(
            transformMessageContentDomainToTransition(domainMessage.getMessageContent()));
        // map message confirmations
        List<DomibusConnectorMessageConfirmation> messageConfirmations =
            domainMessage.getTransportedMessageConfirmations();
        LOGGER.trace(
            "#transformDomainToTransition: transform messageConfirmations [{}] to transition",
            messageConfirmations
        );
        for (DomibusConnectorMessageConfirmation msgConfirm : messageConfirmations) {
            toMessageType.getMessageConfirmations()
                         .add(transformMessageConfirmationDomainToTransition(msgConfirm));
        }
        // map message attachments
        LOGGER.trace(
            "#transformDomainToTransition: transform messageAttachments [{}] to transition",
            domainMessage.getMessageAttachments()
        );
        for (DomibusConnectorMessageAttachment msgAttach : domainMessage.getMessageAttachments()) {
            toMessageType.getMessageAttachments()
                         .add(transformMessageAttachmentDomainToTransition(msgAttach));
        }
        // map message errors
        LOGGER.trace(
            "#transformDomainToTransition: transform messageErrors [{}] to transition",
            domainMessage.getMessageProcessErrors()
        );
        for (DomibusConnectorMessageError msgError : domainMessage.getMessageProcessErrors()) {
            toMessageType.getMessageErrors()
                         .add(transformMessageErrorDomainToTransition(msgError));
        }

        return toMessageType;
    }

    /**
     * Converts a messageConfirmation from domain model to transition model the getEvidence
     * byteArray must be not null.
     *
     * @param messageConfirmation the messageConfirmation
     * @return the messageConfirmation in transition model
     * @throws IllegalArgumentException is thrown if DomibusConnectorMessageConfirmation#getEvidence
     *                                  returns null or
     *                                  DomibusConnectorMessageConfirmation#getEvidenceType returns
     *                                  null
     */
    @NotNull
    DomibusConnectorMessageConfirmationType transformMessageConfirmationDomainToTransition(
        final @NotNull DomibusConnectorMessageConfirmation messageConfirmation) {
        var confirmationTO = new DomibusConnectorMessageConfirmationType();
        if (messageConfirmation.getEvidence() == null) {
            throw new CannotBeMappedToTransitionException(
                "byte array getEvidence() is not allowed to be null!");
        }
        if (messageConfirmation.getEvidenceType() == null) {
            throw new CannotBeMappedToTransitionException(
                "evidenceType is not allowed to be null!");
        }

        var streamSource = new StreamSource(new ByteArrayInputStream(
            // byte[] is copied because domain model is not immutable
            Arrays.copyOf(
                messageConfirmation.getEvidence(), messageConfirmation.getEvidence().length)));
        confirmationTO.setConfirmation(streamSource);

        // confirmationTO.setConfirmation(Arrays.copyOf(messageConfirmation.getEvidence(),
        // messageConfirmation.getEvidence().length));

        confirmationTO.setConfirmationType(
            DomibusConnectorConfirmationType.valueOf(messageConfirmation.getEvidenceType().name()));

        return confirmationTO;
    }

    /**
     * Translates messageError from domain model to transition model.
     *
     * @param messageError - the (domain model) messageError
     * @return the translated (transition model) messageError
     */
    @NotNull
    DomibusConnectorMessageErrorType transformMessageErrorDomainToTransition(
        final @NotNull DomibusConnectorMessageError messageError) {
        var errorTO = new DomibusConnectorMessageErrorType();
        BeanUtils.copyProperties(messageError, errorTO);
        return errorTO;
    }

    /**
     * Translates messageAttachment from domain model to transition model the attachment and
     * identifier property must not be null.
     *
     * @param messageAttachment - the (domain model) messageAttachment
     * @return the translated (transition model) messageAttachment
     */
    @NotNull
    DomibusConnectorMessageAttachmentType transformMessageAttachmentDomainToTransition(
        final @NotNull DomibusConnectorMessageAttachment messageAttachment) {
        var attachmentTO = new DomibusConnectorMessageAttachmentType();
        if (messageAttachment.getAttachment() == null) {
            throw new CannotBeMappedToTransitionException("attachment is not allowed to be null!");
        }
        if (messageAttachment.getIdentifier() == null) {
            throw new CannotBeMappedToTransitionException("identifier is not allowed to be null!");
        }
        BeanUtils.copyProperties(messageAttachment, attachmentTO);

        attachmentTO.setAttachment(
            convertBigDataReferenceToDataHandler(
                messageAttachment.getAttachment(),
                messageAttachment.getMimeType()
            ));
        return attachmentTO;
    }

    /**
     * Translates messageContent from domain model to transition model.
     *
     * @param messageContent - the (domain model) messageContent
     * @return the translated (transition model) messageContent or null if null provided
     */
    @Nullable
    DomibusConnectorMessageContentType transformMessageContentDomainToTransition(
        final @Nullable DomibusConnectorMessageContent messageContent) {
        if (messageContent == null) {
            return null;
        }
        var messageContentTO = new DomibusConnectorMessageContentType();
        if (messageContent.getXmlContent() == null) {
            throw new CannotBeMappedToTransitionException(
                "xmlContent of content must be not null!");
        }

        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(
                "Business content XML before transformed to stream: {}",
                new String(messageContent.getXmlContent())
            );
        }
        var streamSource = new StreamSource(new ByteArrayInputStream(
            // byte[] is copied because domain model is not immutable
            // Arrays.copyOf(messageContent.getXmlContent(), messageContent.getXmlContent().length)
            messageContent.getXmlContent()
        ));
        messageContentTO.setXmlContent(streamSource);

        // maps Document of messageContent
        DomibusConnectorMessageDocument document = messageContent.getDocument();
        var documentTO = new DomibusConnectorMessageDocumentType();

        if (document != null) {
            LargeFileReference docDataRef = document.getDocument();
            documentTO.setDocument(convertBigDataReferenceToDataHandler(docDataRef, null));
            documentTO.setDocumentName(document.getDocumentName());
            messageContentTO.setDocument(documentTO);

            // map signature type of document
            var detachedSignature = document.getDetachedSignature();
            if (detachedSignature != null) {
                var detachedSignatureTypeTO = new DomibusConnectorDetachedSignatureType();
                detachedSignatureTypeTO.setDetachedSignature(
                    Arrays.copyOf(
                        detachedSignature.getDetachedSignature(),
                        detachedSignature.getDetachedSignature().length
                    ));
                detachedSignatureTypeTO.setDetachedSignatureName(
                    detachedSignature.getDetachedSignatureName());
                detachedSignatureTypeTO.setMimeType(
                    DomibusConnectorDomainDetachedSignatureEnumTransformer
                        .transformDetachedSignatureMimeTypeDomainToTransition(
                            detachedSignature.getMimeType()));
                documentTO.setDetachedSignature(detachedSignatureTypeTO);
            } else {
                LOGGER.debug(
                    "#transformMessageContentDomainToTransition: no detached signature to map!");
            }
        } else {
            LOGGER.debug(
                "#transformMessageContentDomainToTransition: document contains no document data");
        }

        return messageContentTO;
    }

    @NotNull
    DataHandler convertBigDataReferenceToDataHandler(
        @NotNull LargeFileReference largeFileReference, @Nullable String mimeType) {
        if (mimeType == null) {
            mimeType = "application/octet-stream";
        }
        LargeFileReference readableDataSource =
            this.largeFilePersistenceService.getReadableDataSource(largeFileReference);
        return new DataHandler(readableDataSource);
    }

    @NotNull
    DomibusConnectorMessageDetailsType transformMessageDetailsDomainToTransition(
        final @NotNull DomibusConnectorMessage message) {
        DomibusConnectorMessageDetails messageDetails = message.getMessageDetails();
        LOGGER.debug(
            "transformMessageDetailsDomaintToTransition: messageDetails are [{}]", messageDetails);
        if (messageDetails == null) {
            throw new CannotBeMappedToTransitionException(
                "messageDetails are not allowed to be null!");
        }
        var toDetailsType = new DomibusConnectorMessageDetailsType();

        if (messageDetails.getFinalRecipient() == null) {
            throw new CannotBeMappedToTransitionException("final recipient is mandatory!");
        }
        if (messageDetails.getOriginalSender() == null) {
            throw new CannotBeMappedToTransitionException("original sender is mandatory!");
        }
        // map all properties with same name and type: backendMessageId, conversationId,
        // finalRecipient, originalSender, refToMessageId
        BeanUtils.copyProperties(messageDetails, toDetailsType);

        // map action
        if (messageDetails.getAction() == null) {
            throw new CannotBeMappedToTransitionException("action is mandatory in messageDetails!");
        }
        var actionTO = new DomibusConnectorActionType();
        BeanUtils.copyProperties(messageDetails.getAction(), actionTO);
        toDetailsType.setAction(actionTO);

        // map fromParty
        if (messageDetails.getFromParty() == null) {
            throw new CannotBeMappedToTransitionException(
                "fromParty is mandatory in messageDetails");
        }
        var fromPartyTO = new DomibusConnectorPartyType();
        BeanUtils.copyProperties(messageDetails.getFromParty(), fromPartyTO);
        toDetailsType.setFromParty(fromPartyTO);

        // map toParty
        if (messageDetails.getToParty() == null) {
            throw new CannotBeMappedToTransitionException("toParty is mandatory in messageDetails");
        }
        var toPartyTO = new DomibusConnectorPartyType();
        BeanUtils.copyProperties(messageDetails.getToParty(), toPartyTO);
        toDetailsType.setToParty(toPartyTO);

        // map service
        if (messageDetails.getService() == null) {
            throw new CannotBeMappedToTransitionException("service is mandatory in messageDetails");
        }
        var serviceTO = new DomibusConnectorServiceType();
        BeanUtils.copyProperties(messageDetails.getService(), serviceTO);
        toDetailsType.setService(serviceTO);

        // map ref to message id
        toDetailsType.setRefToMessageId(message.getMessageDetails().getRefToMessageId());
        // map backendMessageId
        if (DomainModelHelper.isEvidenceMessage(message)) {
            LOGGER.debug(
                "Message is an evidence message, setting backendMessageId to [{}] "
                    + "(from refToBackendMessageId)!",
                messageDetails.getRefToBackendMessageId()
            );
            toDetailsType.setBackendMessageId(messageDetails.getRefToBackendMessageId());
            // only use refToBackendMessageId if going to backend and it is not empty
            if (message.getMessageDetails().getDirection().getTarget()
                == MessageTargetSource.BACKEND
                && StringUtils.hasLength(messageDetails.getRefToBackendMessageId())) {
                toDetailsType.setRefToMessageId(messageDetails.getRefToBackendMessageId());
            }
        }

        return toDetailsType;
    }

    private void setMessageProcessProperties(
        DomibusConnectorMessage domibusConnectorMessage,
        DomibusConnectorMessageType transitionMessage) {
        if (transitionMessage.getMessageContent() != null
            && transitionMessage.getMessageContent().getDocument() != null
            && transitionMessage.getMessageContent()
                                .getDocument()
                                .getAesType() != null
        ) {
            DomibusConnectorDocumentAESType aesType = transitionMessage
                .getMessageContent()
                .getDocument()
                .getAesType();

            var advancedElectronicSystemType = AdvancedElectronicSystemType.valueOf(aesType.name());

            domibusConnectorMessage.getDcMessageProcessSettings()
                                   .setValidationServiceName(advancedElectronicSystemType);
            LOGGER.trace(
                "#transformTransitionToDomain: setting AES type to [{}]",
                advancedElectronicSystemType
            );
        }
    }

    @NotNull
    DomibusConnectorMessageAttachment transformMessageAttachmentTransitionToDomain(
        final @NotNull DomibusConnectorMessageAttachmentType messageAttachmentTO) {

        var messageAttachment = new DomibusConnectorMessageAttachment(
            convertDataHandlerToBigFileReference(messageAttachmentTO.getAttachment()),
            messageAttachmentTO.getIdentifier()
        );
        BeanUtils.copyProperties(messageAttachmentTO, messageAttachment);

        return messageAttachment;
    }

    @NotNull
    DomibusConnectorMessageError transformMessageErrorTransitionToDomain(
        final @NotNull DomibusConnectorMessageErrorType errorTypeTO) {
        return DomibusConnectorMessageErrorBuilder.createBuilder()
                                                  .setText(errorTypeTO.getErrorMessage())
                                                  .setDetails(errorTypeTO.getErrorDetails())
                                                  .setSource(errorTypeTO.getErrorSource())
                                                  .build();
    }

    DomibusConnectorMessageConfirmation transformMessageConfirmationTransitionToDomain(
        final @NotNull DomibusConnectorMessageConfirmationType messageConfirmationTO) {
        var confirmation = new DomibusConnectorMessageConfirmation();

        Source evidence = messageConfirmationTO.getConfirmation();
        if (evidence != null) {
            confirmation.setEvidence(ConversionTools.convertXMLSourceToByteArray(evidence));
        }
        confirmation.setEvidenceType(DomibusConnectorEvidenceType.valueOf(
            messageConfirmationTO.getConfirmationType().name()));

        return confirmation;
    }

    @NotNull
    DomibusConnectorMessageContent transformMessageContentTransitionToDomain(
        final @NotNull DomibusConnectorMessageContentType messageContentTO) {
        var messageContent = new DomibusConnectorMessageContent();

        byte[] result = ConversionTools.convertXMLSourceToByteArray(
            messageContentTO.getXmlContent()
        );

        messageContent.setXmlContent(result);
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace(
                "Business content XML after transformed from stream: {}", new String(result));
        }

        // maps Document of messageContent
        DomibusConnectorMessageDocumentType documentTO = messageContentTO.getDocument();

        if (documentTO != null) {
            var documentBuilder = DomibusConnectorMessageDocumentBuilder.createBuilder();
            // maps signature of document
            DomibusConnectorDetachedSignatureType detachedSignatureTO =
                documentTO.getDetachedSignature();

            if (detachedSignatureTO != null) {
                var detachedSignature = new DetachedSignature(
                    Arrays.copyOf(
                        detachedSignatureTO.getDetachedSignature(),
                        detachedSignatureTO.getDetachedSignature().length
                    ),
                    detachedSignatureTO.getDetachedSignatureName(),
                    // eu.domibus.connector.domain.model.DetachedSignatureMimeType
                    // .valueOf(detachedSignatureTO.getMimeType().name())
                    DomibusConnectorDomainDetachedSignatureEnumTransformer
                        .transformDetachedSignatureMimeTypeTransitionToDomain(
                            detachedSignatureTO.getMimeType())
                );
                documentBuilder.withDetachedSignature(detachedSignature);
            }
            documentBuilder.setContent(
                convertDataHandlerToBigFileReference(documentTO.getDocument()));
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
        var domibusConnectorMessageId = messageIdThreadLocal.get();
        LargeFileReference domibusConnectorBigDataReference =
            largeFilePersistenceService.createDomibusConnectorBigDataReference(
                domibusConnectorMessageId, dataHandler.getName(), dataHandler.getContentType());
        try (var is = dataHandler.getInputStream();
             var os = domibusConnectorBigDataReference.getOutputStream()) {
            StreamUtils.copy(is, os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return domibusConnectorBigDataReference;
    }

    @NotNull
    DomibusConnectorMessageDetails transformMessageDetailsTransitionToDomain(
        final @NotNull DomibusConnectorMessageDetailsType messageDetailsTO) {
        var messageDetails = new DomibusConnectorMessageDetails();

        // map all properties with same name and type: backendMessageId, conversationId,
        // finalRecipient, originalSender, refToMessageId
        BeanUtils.copyProperties(messageDetailsTO, messageDetails);

        // map action
        DomibusConnectorActionType actionTO = messageDetailsTO.getAction();

        var action = new DomibusConnectorAction(actionTO.getAction());
        // default mapping is assumed true!
        // new DomibusConnectorAction(actionTO.getAction(), true);
        messageDetails.setAction(action);

        // map service
        DomibusConnectorServiceType serviceTO = messageDetailsTO.getService();
        var service =
            new DomibusConnectorService(serviceTO.getService(), serviceTO.getServiceType());
        messageDetails.setService(service);

        // map partyTO
        DomibusConnectorPartyType toPartyTO = messageDetailsTO.getToParty();
        if (toPartyTO == null) {
            throw new IllegalArgumentException(
                "toParty in messageDetails is not allowed to be null!");
        }
        var toParty = new DomibusConnectorParty(
            toPartyTO.getPartyId(), toPartyTO.getPartyIdType(), toPartyTO.getRole());
        messageDetails.setToParty(toParty);

        // map partyFrom
        DomibusConnectorPartyType fromPartyTO = messageDetailsTO.getFromParty();
        if (fromPartyTO == null) {
            throw new IllegalArgumentException(
                "fromParty in messageDetails is not allowed to be null!");
        }
        var fromParty = new DomibusConnectorParty(
            fromPartyTO.getPartyId(), fromPartyTO.getPartyIdType(), fromPartyTO.getRole());
        messageDetails.setFromParty(fromParty);

        return messageDetails;
    }
}
