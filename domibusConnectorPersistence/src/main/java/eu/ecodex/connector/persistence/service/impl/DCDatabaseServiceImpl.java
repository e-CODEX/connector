/*
 * Copyright 2025 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import eu.ecodex.connector.persistence.dao.DomibusConnectorBigDataDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorEvidenceDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageErrorDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageInfoDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMsgContDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorTransportStepDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorTransportStepStatusDao;
import eu.ecodex.connector.persistence.service.DCDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link DCDatabaseService} interface.
 *
 * <p>Provides database service.
 */
@Service
public class DCDatabaseServiceImpl implements DCDatabaseService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DCDatabaseServiceImpl.class);

    private final DomibusConnectorTransportStepDao transportStepDao;
    private final DomibusConnectorMessageDao connectorMessageDao;
    private final DomibusConnectorTransportStepStatusDao transportStepStatusDao;
    private final DomibusConnectorMessageInfoDao messageInfoDao;
    private final DomibusConnectorMsgContDao connectorMsgContDao;
    private final DomibusConnectorEvidenceDao  evidenceDao;
    private final DomibusConnectorBigDataDao  bigDataDao;
    private final DomibusConnectorMessageErrorDao messageErrorDao;

    /**
     * Constructor.
     */
    public DCDatabaseServiceImpl(
            DomibusConnectorTransportStepDao transportStepDao,
            DomibusConnectorMessageDao connectorMessageDao,
            DomibusConnectorTransportStepStatusDao transportStepStatusDao,
            DomibusConnectorMessageInfoDao messageInfoDao,
            DomibusConnectorMsgContDao connectorMsgContDao,
            DomibusConnectorEvidenceDao evidenceDao,
            DomibusConnectorBigDataDao bigDataDao,
            DomibusConnectorMessageErrorDao messageErrorDao) {
        this.transportStepDao = transportStepDao;
        this.connectorMessageDao = connectorMessageDao;
        this.transportStepStatusDao = transportStepStatusDao;
        this.messageInfoDao = messageInfoDao;
        this.connectorMsgContDao = connectorMsgContDao;
        this.evidenceDao = evidenceDao;
        this.bigDataDao = bigDataDao;
        this.messageErrorDao = messageErrorDao;
    }

    @Override
    @Transactional
    @Scheduled(cron = "${connector.database.cleaning.cron:0 0 0 * * 7}")
    public void clean() {
        LOGGER.info("Start the message cleanup process in the database");
        var completedTransportStepIds = this.transportStepStatusDao.getCompletedTransportStepIds();
        LOGGER.info(
                "{} message(s) is/are about to be cleaned from the database",
                completedTransportStepIds.size()
        );
        this.transportStepStatusDao.clean(completedTransportStepIds);

        var messageIds = this.transportStepDao.getCompletedMessageIds(completedTransportStepIds);
        this.transportStepDao.clean(completedTransportStepIds);

        var messageIntegerIds = this.connectorMessageDao.getMessageIntegerIds(messageIds);

        this.messageInfoDao.deleteAllByMessageIdIn(messageIntegerIds);
        this.connectorMsgContDao.deleteAllByMessageIdIn(messageIntegerIds);
        this.evidenceDao.deleteAllByBusinessMessageIdIn(messageIntegerIds);
        this.messageInfoDao.deleteAllByMessageIdIn(messageIntegerIds);
        this.bigDataDao.deleteAllByConnectorMessageIdIn(messageIds);
        this.messageErrorDao.deleteAllByMessageIdIn(messageIntegerIds);
        this.connectorMessageDao.deleteAllByIdIn(messageIntegerIds);
        LOGGER.info("End the message cleanup process in the database");
    }
}
