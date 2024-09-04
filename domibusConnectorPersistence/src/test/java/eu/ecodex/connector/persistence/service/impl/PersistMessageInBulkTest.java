/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.service.impl;

import eu.ecodex.connector.domain.enums.DomibusConnectorMessageDirection;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import eu.ecodex.connector.persistence.dao.CommonPersistenceTest;
import eu.ecodex.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.ecodex.connector.persistence.service.TransportStepPersistenceService;
import java.util.stream.IntStream;
import javax.sql.DataSource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Test the persistence of multiple messages in parallel.
 */
@CommonPersistenceTest
class PersistMessageInBulkTest {
    @Autowired
    DataSource ds;
    @Autowired
    TransportStepPersistenceService transportStepPersistenceService;
    @Autowired
    DCMessagePersistenceServiceImpl messagePersistenceService;
    @Autowired
    DomibusConnectorMessageDao msgDao;

    @Test
    @Disabled("is broken and does test a deprecated function")
    void testBulkMessage() {
        DomibusConnectorMessage message = DomainEntityCreator.createMessage();
        message.setConnectorMessageId(new DomibusConnectorMessageId("msg2"));
        messagePersistenceService.persistMessageIntoDatabase(
            message, DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);

        IntStream.range(0, 30).parallel().forEach(i -> {
            DomibusConnectorMessage message2 = DomainEntityCreator.createMessage();
            message2.setConnectorMessageId(new DomibusConnectorMessageId("msg2" + i));
            messagePersistenceService.persistMessageIntoDatabase(
                message2, DomibusConnectorMessageDirection.BACKEND_TO_GATEWAY);
        });
    }
}
