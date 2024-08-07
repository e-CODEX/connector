/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.persistence.dao.DomibusConnectorKeystoreDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import eu.domibus.connector.persistence.service.DomibusConnectorKeystorePersistenceService;
import java.util.Optional;
import java.util.UUID;
import javax.persistence.NoResultException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides methods for persisting and retrieving DomibusConnectorKeystore objects from
 * the database.
 */
@Service
public class DomibusConnectorKeystorePersistenceServiceImpl
    implements DomibusConnectorKeystorePersistenceService {
    @Autowired
    DomibusConnectorKeystoreDao keystoreDao;

    @Override
    @Transactional
    public DomibusConnectorKeystore persistNewKeystore(DomibusConnectorKeystore connectorKeystore) {
        var dbKeystore = new PDomibusConnectorKeystore();

        String uuid = connectorKeystore.getUuid();

        if (StringUtils.isEmpty(uuid)) {
            uuid = String.format("%s@%s", UUID.randomUUID(), "dc.keystore.eu");
        }

        dbKeystore.setUuid(uuid);
        dbKeystore.setKeystore(connectorKeystore.getKeystoreBytes());

        dbKeystore.setPassword(connectorKeystore.getPasswordPlain());
        dbKeystore.setDescription(connectorKeystore.getDescription());
        dbKeystore.setType(connectorKeystore.getType());

        dbKeystore = keystoreDao.save(dbKeystore);

        connectorKeystore.setUuid(dbKeystore.getUuid());
        connectorKeystore.setUploaded(dbKeystore.getUploaded());

        return connectorKeystore;
    }

    @Override
    @Transactional
    public void updateKeystorePassword(
        DomibusConnectorKeystore connectorKeystore, String newKeystorePassword) {
        if (StringUtils.isEmpty(connectorKeystore.getUuid())) {
            throw new IllegalArgumentException("UUID of keystore must not be null!");
        }

        Optional<PDomibusConnectorKeystore> dbKeystore =
            keystoreDao.findByUuid(connectorKeystore.getUuid());
        if (dbKeystore.isPresent()) {
            dbKeystore.get().setPassword(newKeystorePassword);
            keystoreDao.save(dbKeystore.get());
        } else {
            throw new NoResultException(
                String.format(
                    "No keystore with UUID [%s] found in database!",
                    connectorKeystore.getUuid()
                ));
        }
    }

    @Override
    public DomibusConnectorKeystore getKeystoreByUUID(String uuid) {
        throw new RuntimeException("Not implemented yet!?");
    }
}
