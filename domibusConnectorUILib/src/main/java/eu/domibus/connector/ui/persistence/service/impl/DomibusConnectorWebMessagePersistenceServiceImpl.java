package eu.domibus.connector.ui.persistence.service.impl;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.model.*;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.dto.WebMessageDetail;
import eu.domibus.connector.ui.dto.WebMessageEvidence;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


@org.springframework.stereotype.Service("webMessagePersistenceService")
public class DomibusConnectorWebMessagePersistenceServiceImpl implements DomibusConnectorWebMessagePersistenceService {
    private static final Logger LOGGER =
            LoggerFactory.getLogger(DomibusConnectorWebMessagePersistenceServiceImpl.class);

    private DomibusConnectorMessageDao messageDao;

    /*
     * DAO SETTER
     */
    @Autowired
    public void setMessageDao(DomibusConnectorMessageDao messageDao) {
        this.messageDao = messageDao;
    }

    @Override
    public LinkedList<WebMessage> getAllMessages() {
        Iterable<PDomibusConnectorMessage> allMessages = messageDao.findAllByOrderByCreatedDesc();
        return mapDbMessagesToWebMessages(allMessages);
    }

    @Override
    public Optional<WebMessage> getMessageByConnectorId(String connectorMessageId) {
        Optional<PDomibusConnectorMessage> dbMessage = messageDao.findOneByConnectorMessageId(connectorMessageId);
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    public LinkedList<WebMessage> getMessagesWithinPeriod(Date from, Date to) {
        Iterable<PDomibusConnectorMessage> allMessages = messageDao.findByPeriod(from, to);
        return mapDbMessagesToWebMessages(allMessages);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WebMessage> findMessageByNationalId(
            String nationalMessageId,
            DomibusConnectorMessageDirection direction) {
        Optional<PDomibusConnectorMessage> dbMessage =
                messageDao.findOneByBackendMessageIdAndDirectionTarget(nationalMessageId, direction.getTarget());
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WebMessage> findMessageByEbmsId(String ebmsMessageId, DomibusConnectorMessageDirection direction) {
        Optional<PDomibusConnectorMessage> dbMessage =
                messageDao.findOneByEbmsMessageIdAndDirectionTarget(ebmsMessageId, direction.getTarget());
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public LinkedList<WebMessage> findMessagesByConversationId(String conversationId) {
        List<PDomibusConnectorMessage> dbMessages = messageDao.findByConversationId(conversationId);
        return mapDbMessagesToWebMessages(dbMessages);
    }

    @Override
    public Page<WebMessage> findAll(Example<WebMessage> example, Pageable pageable) {
        Example<PDomibusConnectorMessage> exampleDbMsg = getpDomibusConnectorMessageExample(example);
        Page<PDomibusConnectorMessage> all = messageDao.findAll(exampleDbMsg, pageable);
        LOGGER.debug("Returned {} results.", all.getSize());
        return all.map(c -> new DBMessageToWebMessageConverter().convert(c));
    }

    @Override
    public long count(Example<WebMessage> example) {
        Example<PDomibusConnectorMessage> pDomibusConnectorMessageExample =
                getpDomibusConnectorMessageExample(example);
        return messageDao.count(pDomibusConnectorMessageExample);
    }

    @Override
    public Optional<WebMessage> getOutgoingMessageByBackendMessageId(String backendMessageId) {
        Optional<PDomibusConnectorMessage> dbMessage =
                messageDao.findOneByBackendMessageIdAndDirectionTarget(backendMessageId, MessageTargetSource.GATEWAY);
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    public Optional<WebMessage> getIncomingMessageByEbmsMessageId(String ebmsMessageId) {
        Optional<PDomibusConnectorMessage> dbMessage =
                messageDao.findOneByEbmsMessageIdAndDirectionTarget(ebmsMessageId, MessageTargetSource.BACKEND);
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    public LinkedList<WebMessage> findConnectorTestMessages(String connectorTestBackendName) {
        List<PDomibusConnectorMessage> dbMessages = messageDao.findByBackendName(connectorTestBackendName);
        return mapDbMessagesToWebMessages(dbMessages);
    }

    private Example<PDomibusConnectorMessage> getpDomibusConnectorMessageExample(Example<WebMessage> example) {
        if (example == null) {
            throw new IllegalArgumentException("Example cannot be null!");
        }

        PDomibusConnectorMessage dbMsg = new PDomibusConnectorMessage();

        BeanUtils.copyProperties(example.getProbe(), dbMsg);
        // TODO: map properties which have not the same name...
        dbMsg.setMessageInfo(new PDomibusConnectorMessageInfo());
        dbMsg.getMessageInfo().setAction(new PDomibusConnectorAction());
        dbMsg.getMessageInfo().setService(new PDomibusConnectorService());
        dbMsg.getMessageInfo().setFrom(new PDomibusConnectorParty());
        dbMsg.getMessageInfo().setTo(new PDomibusConnectorParty());
        if (example.getProbe().getMessageInfo() != null) {
            BeanUtils.copyProperties(example.getProbe().getMessageInfo(), dbMsg.getMessageInfo());
            if (example.getProbe().getMessageInfo().getAction() != null) {
                BeanUtils.copyProperties(
                        example.getProbe().getMessageInfo().getAction(),
                        dbMsg.getMessageInfo().getAction()
                );
            }
            if (example.getProbe().getMessageInfo().getService() != null) {
                BeanUtils.copyProperties(
                        example.getProbe().getMessageInfo().getService(),
                        dbMsg.getMessageInfo().getService()
                );
            }
            if (example.getProbe().getMessageInfo().getFrom() != null) {
                BeanUtils.copyProperties(
                        example.getProbe().getMessageInfo().getFrom(),
                        dbMsg.getMessageInfo().getFrom()
                );
            }
            if (example.getProbe().getMessageInfo().getTo() != null) {
                BeanUtils.copyProperties(example.getProbe().getMessageInfo().getTo(), dbMsg.getMessageInfo().getTo());
            }
        }
        Example<PDomibusConnectorMessage> exampleDbMsg = Example.of(
                dbMsg,
                example.getMatcher().withIgnoreNullValues()
        );
        return exampleDbMsg;
    }

    private LinkedList<WebMessage> mapDbMessagesToWebMessages(Iterable<PDomibusConnectorMessage> messages) {
        LinkedList<WebMessage> webMessages = new LinkedList<WebMessage>();
        Iterator<PDomibusConnectorMessage> msgIt = messages.iterator();
        while (msgIt.hasNext()) {
            PDomibusConnectorMessage pMessage = msgIt.next();

            WebMessage message = new DBMessageToWebMessageConverter().convert(pMessage);
            webMessages.addLast(message);
        }

        return webMessages;
    }

    private Optional<WebMessage> mapDbMessageToWebMessage(Optional<PDomibusConnectorMessage> pMessage) {
        return pMessage.map(m -> new DBMessageToWebMessageConverter().convert(m));
    }

    private static class DBMessageToWebMessageConverter implements Converter<PDomibusConnectorMessage, WebMessage> {
        @Nullable
        @Override
        public WebMessage convert(PDomibusConnectorMessage pMessage) {
            WebMessage message = new WebMessage();

            message.setConnectorMessageId(pMessage.getConnectorMessageId());
            message.setEbmsMessageId(pMessage.getEbmsMessageId());
            message.setBackendMessageId(pMessage.getBackendMessageId());
            message.setConversationId(pMessage.getConversationId());
            message.setBackendName(pMessage.getBackendName());
            message.setDirectionSource(pMessage.getDirectionSource() != null ? pMessage.getDirectionSource()
                                                                                       .getDbName() : null);
            message.setDirectionTarget(pMessage.getDirectionTarget() != null ? pMessage.getDirectionTarget()
                                                                                       .getDbName() : null);
            message.setDeliveredToNationalSystem(pMessage.getDeliveredToNationalSystem() != null ?
                                                         ZonedDateTime.ofInstant(
                                                                 pMessage.getDeliveredToNationalSystem().toInstant(),
                                                                 ZoneId.systemDefault()
                                                         ) : null);
            message.setDeliveredToGateway(pMessage.getDeliveredToGateway() != null ? ZonedDateTime.ofInstant(
                    pMessage.getDeliveredToGateway().toInstant(),
                    ZoneId.systemDefault()
            ) : null);
            message.setCreated(pMessage.getCreated() != null ? ZonedDateTime.ofInstant(
                    pMessage.getCreated().toInstant(),
                    ZoneId.systemDefault()
            ) : null);
            message.setConfirmed(pMessage.getConfirmed() != null ? ZonedDateTime.ofInstant(
                    pMessage.getConfirmed().toInstant(),
                    ZoneId.systemDefault()
            ) : null);
            message.setRejected(pMessage.getRejected() != null ? ZonedDateTime.ofInstant(
                    pMessage.getRejected().toInstant(),
                    ZoneId.systemDefault()
            ) : null);

            PDomibusConnectorMessageInfo pMessageInfo = pMessage.getMessageInfo();
            if (pMessageInfo != null) {

                message.setMessageInfo(mapDbMessageInfoToWebMessageDetail(pMessageInfo));
            }

            if (!CollectionUtils.isEmpty(pMessage.getRelatedEvidences())) {
                for (PDomibusConnectorEvidence dbEvidence : pMessage.getRelatedEvidences()) {
                    WebMessageEvidence evidence = new WebMessageEvidence();
                    evidence.setEvidenceType(dbEvidence.getType().name());
                    evidence.setDeliveredToGateway(dbEvidence.getDeliveredToGateway());
                    evidence.setDeliveredToBackend(dbEvidence.getDeliveredToBackend());
                    message.getEvidences().add(evidence);
                }
            }
            return message;
        }

        private WebMessageDetail mapDbMessageInfoToWebMessageDetail(PDomibusConnectorMessageInfo pMessageInfo) {
            WebMessageDetail messageDetail = new WebMessageDetail();

            messageDetail.setOriginalSender(pMessageInfo.getOriginalSender());
            messageDetail.setFinalRecipient(pMessageInfo.getFinalRecipient());

            if (pMessageInfo.getAction() != null) {
                messageDetail.setAction(new WebMessageDetail.Action(pMessageInfo.getAction().getAction()));
            }

            if (pMessageInfo.getService() != null) {
                messageDetail.setService(new WebMessageDetail.Service(
                        pMessageInfo.getService().getService(),
                        pMessageInfo.getService().getServiceType()
                ));
            }

            if (pMessageInfo.getFrom() != null) {
                messageDetail.setFrom(new WebMessageDetail.Party(
                        pMessageInfo.getFrom().getPartyId(),
                        pMessageInfo.getFrom().getPartyIdType(),
                        pMessageInfo.getFrom().getRole()
                ));
            }

            if (pMessageInfo.getTo() != null) {
                messageDetail.setTo(new WebMessageDetail.Party(
                        pMessageInfo.getTo().getPartyId(),
                        pMessageInfo.getTo().getPartyIdType(),
                        pMessageInfo.getTo().getRole()
                ));
            }

            return messageDetail;
        }
    }
}
