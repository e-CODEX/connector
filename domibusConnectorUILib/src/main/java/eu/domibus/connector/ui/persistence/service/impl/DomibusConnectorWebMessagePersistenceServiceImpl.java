/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.persistence.service.impl;

import eu.domibus.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.domibus.connector.domain.enums.MessageTargetSource;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorAction;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageInfo;
import eu.domibus.connector.persistence.model.PDomibusConnectorParty;
import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import eu.domibus.connector.ui.dto.WebMessage;
import eu.domibus.connector.ui.dto.WebMessageDetail;
import eu.domibus.connector.ui.dto.WebMessageEvidence;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebMessagePersistenceService;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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

/**
 * This class implements the DomibusConnectorWebMessagePersistenceService interface to provide
 * persistence operations related to web messages in Domibus.
 */
@SuppressWarnings("squid:S1135")
@org.springframework.stereotype.Service("webMessagePersistenceService")
public class DomibusConnectorWebMessagePersistenceServiceImpl
    implements DomibusConnectorWebMessagePersistenceService {
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
    public LinkedList<WebMessage> getMessagesWithinPeriod(Date from, Date to) {
        Iterable<PDomibusConnectorMessage> allMessages = messageDao.findByPeriod(from, to);
        return mapDbMessagesToWebMessages(allMessages);
    }

    @Override
    public Optional<WebMessage> getMessageByConnectorId(String connectorMessageId) {
        Optional<PDomibusConnectorMessage> dbMessage =
            messageDao.findOneByConnectorMessageId(connectorMessageId);
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    public Optional<WebMessage> getOutgoingMessageByBackendMessageId(String backendMessageId) {
        Optional<PDomibusConnectorMessage> dbMessage =
            messageDao.findOneByBackendMessageIdAndDirectionTarget(
                backendMessageId,
                MessageTargetSource.GATEWAY
            );
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    public Optional<WebMessage> getIncomingMessageByEbmsMessageId(String ebmsMessageId) {
        Optional<PDomibusConnectorMessage> dbMessage =
            messageDao.findOneByEbmsMessageIdAndDirectionTarget(
                ebmsMessageId,
                MessageTargetSource.BACKEND
            );
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WebMessage> findMessageByNationalId(
        String nationalMessageId, DomibusConnectorMessageDirection direction) {
        Optional<PDomibusConnectorMessage> dbMessage =
            messageDao.findOneByBackendMessageIdAndDirectionTarget(
                nationalMessageId,
                direction.getTarget()
            );
        return mapDbMessageToWebMessage(dbMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WebMessage> findMessageByEbmsId(
        String ebmsMessageId, DomibusConnectorMessageDirection direction) {
        Optional<PDomibusConnectorMessage> dbMessage =
            messageDao.findOneByEbmsMessageIdAndDirectionTarget(
                ebmsMessageId,
                direction.getTarget()
            );
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
        Example<PDomibusConnectorMessage> exampleDbMsg =
            getpDomibusConnectorMessageExample(example);

        var connectorMessages = messageDao.findAll(exampleDbMsg, pageable);

        LOGGER.debug("Returned {} results.", connectorMessages.getSize());

        return connectorMessages.map(c -> new DBMessageToWebMessageConverter().convert(c));
    }

    @Override
    public LinkedList<WebMessage> findConnectorTestMessages(String connectorTestBackendName) {
        List<PDomibusConnectorMessage> dbMessages =
            messageDao.findByBackendName(connectorTestBackendName);
        return mapDbMessagesToWebMessages(dbMessages);
    }

    private Example<PDomibusConnectorMessage> getpDomibusConnectorMessageExample(
        Example<WebMessage> example) {
        if (example == null) {
            throw new IllegalArgumentException("Example cannot be null!");
        }

        var connectorMessage = new PDomibusConnectorMessage();

        BeanUtils.copyProperties(example.getProbe(), connectorMessage);
        // TODO: map properties which have not the same name...
        connectorMessage.setMessageInfo(new PDomibusConnectorMessageInfo());
        connectorMessage.getMessageInfo().setAction(new PDomibusConnectorAction());
        connectorMessage.getMessageInfo().setService(new PDomibusConnectorService());
        connectorMessage.getMessageInfo().setFrom(new PDomibusConnectorParty());
        connectorMessage.getMessageInfo().setTo(new PDomibusConnectorParty());
        if (example.getProbe().getMessageInfo() != null) {
            BeanUtils.copyProperties(
                example.getProbe().getMessageInfo(), connectorMessage.getMessageInfo());
            if (example.getProbe().getMessageInfo().getAction() != null) {
                BeanUtils.copyProperties(
                    example.getProbe().getMessageInfo().getAction(),
                    connectorMessage.getMessageInfo().getAction()
                );
            }
            if (example.getProbe().getMessageInfo().getService() != null) {
                BeanUtils.copyProperties(
                    example.getProbe().getMessageInfo().getService(),
                    connectorMessage.getMessageInfo().getService()
                );
            }
            if (example.getProbe().getMessageInfo().getFrom() != null) {
                BeanUtils.copyProperties(
                    example.getProbe().getMessageInfo().getFrom(),
                    connectorMessage.getMessageInfo().getFrom()
                );
            }
            if (example.getProbe().getMessageInfo().getTo() != null) {
                BeanUtils.copyProperties(
                    example.getProbe().getMessageInfo().getTo(),
                    connectorMessage.getMessageInfo().getTo()
                );
            }
        }
        return Example.of(connectorMessage, example.getMatcher().withIgnoreNullValues());
    }

    private static class DBMessageToWebMessageConverter
        implements Converter<PDomibusConnectorMessage, WebMessage> {
        @Nullable
        @Override
        public WebMessage convert(PDomibusConnectorMessage connectorMessage) {
            var webMessage = new WebMessage();

            webMessage.setConnectorMessageId(connectorMessage.getConnectorMessageId());
            webMessage.setEbmsMessageId(connectorMessage.getEbmsMessageId());
            webMessage.setBackendMessageId(connectorMessage.getBackendMessageId());
            webMessage.setConversationId(connectorMessage.getConversationId());
            webMessage.setBackendName(connectorMessage.getBackendName());
            webMessage.setDirectionSource(
                connectorMessage.getDirectionSource() != null
                    ? connectorMessage.getDirectionSource().getDbName()
                    : null
            );
            webMessage.setDirectionTarget(
                connectorMessage.getDirectionTarget() != null
                    ? connectorMessage.getDirectionTarget().getDbName()
                    : null
            );
            webMessage.setDeliveredToNationalSystem(
                connectorMessage.getDeliveredToNationalSystem() != null
                    ? ZonedDateTime.ofInstant(
                    connectorMessage.getDeliveredToNationalSystem().toInstant(),
                    ZoneId.systemDefault()
                )
                    : null
            );
            webMessage.setDeliveredToGateway(
                connectorMessage.getDeliveredToGateway() != null
                    ? ZonedDateTime.ofInstant(
                    connectorMessage.getDeliveredToGateway().toInstant(),
                    ZoneId.systemDefault()
                )
                    : null
            );
            webMessage.setCreated(
                connectorMessage.getCreated() != null
                    ? ZonedDateTime.ofInstant(
                    connectorMessage.getCreated().toInstant(),
                    ZoneId.systemDefault()
                )
                    : null
            );
            webMessage.setConfirmed(
                connectorMessage.getConfirmed() != null
                    ? ZonedDateTime.ofInstant(
                    connectorMessage.getConfirmed().toInstant(),
                    ZoneId.systemDefault()
                )
                    : null);
            webMessage.setRejected(
                connectorMessage.getRejected() != null
                    ? ZonedDateTime.ofInstant(
                    connectorMessage.getRejected().toInstant(),
                    ZoneId.systemDefault()
                )
                    : null);

            PDomibusConnectorMessageInfo messageMessageInfo = connectorMessage.getMessageInfo();
            if (messageMessageInfo != null) {

                webMessage.setMessageInfo(mapDbMessageInfoToWebMessageDetail(messageMessageInfo));
            }

            if (!CollectionUtils.isEmpty(connectorMessage.getRelatedEvidences())) {
                for (var dbEvidence : connectorMessage.getRelatedEvidences()) {
                    var evidence = new WebMessageEvidence();
                    evidence.setEvidenceType(dbEvidence.getType().name());
                    evidence.setDeliveredToGateway(dbEvidence.getDeliveredToGateway());
                    evidence.setDeliveredToBackend(dbEvidence.getDeliveredToBackend());
                    webMessage.getEvidences().add(evidence);
                }
            }
            return webMessage;
        }

        private WebMessageDetail mapDbMessageInfoToWebMessageDetail(
            PDomibusConnectorMessageInfo connectorMessageInfo) {
            var webMessageDetail = new WebMessageDetail();

            webMessageDetail.setOriginalSender(connectorMessageInfo.getOriginalSender());
            webMessageDetail.setFinalRecipient(connectorMessageInfo.getFinalRecipient());

            if (connectorMessageInfo.getAction() != null) {
                webMessageDetail.setAction(
                    new WebMessageDetail.Action(connectorMessageInfo.getAction().getAction()));
            }

            if (connectorMessageInfo.getService() != null) {
                webMessageDetail.setService(
                    new WebMessageDetail.Service(
                        connectorMessageInfo.getService().getService(),
                        connectorMessageInfo.getService().getServiceType()
                    ));
            }

            if (connectorMessageInfo.getFrom() != null) {
                webMessageDetail.setFrom(
                    new WebMessageDetail.Party(
                        connectorMessageInfo.getFrom().getPartyId(),
                        connectorMessageInfo.getFrom().getPartyIdType(),
                        connectorMessageInfo.getFrom().getRole()
                    ));
            }

            if (connectorMessageInfo.getTo() != null) {
                webMessageDetail.setTo(new WebMessageDetail.Party(
                    connectorMessageInfo.getTo().getPartyId(),
                    connectorMessageInfo.getTo().getPartyIdType(),
                    connectorMessageInfo.getTo().getRole()
                ));
            }

            return webMessageDetail;
        }
    }

    @Override
    public long count(Example<WebMessage> example) {
        var connectorMessageExample = getpDomibusConnectorMessageExample(example);
        return messageDao.count(connectorMessageExample);
    }

    private LinkedList<WebMessage> mapDbMessagesToWebMessages(
        Iterable<PDomibusConnectorMessage> messages) {
        LinkedList<WebMessage> webMessages = new LinkedList<>();
        for (PDomibusConnectorMessage connectorMessage : messages) {
            WebMessage message = new DBMessageToWebMessageConverter().convert(connectorMessage);
            webMessages.addLast(message);
        }

        return webMessages;
    }

    private Optional<WebMessage> mapDbMessageToWebMessage(
        Optional<PDomibusConnectorMessage> connectorMessage) {
        return connectorMessage.map(m -> new DBMessageToWebMessageConverter().convert(m));
    }
}
