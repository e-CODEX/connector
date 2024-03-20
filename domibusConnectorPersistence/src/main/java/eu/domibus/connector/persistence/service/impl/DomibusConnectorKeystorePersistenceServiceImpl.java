package eu.domibus.connector.persistence.service.impl;

import java.sql.Blob;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eu.domibus.connector.domain.model.DomibusConnectorKeystore;
import eu.domibus.connector.persistence.dao.DomibusConnectorKeystoreDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorKeystore;
import eu.domibus.connector.persistence.service.DomibusConnectorKeystorePersistenceService;

@Service
public class DomibusConnectorKeystorePersistenceServiceImpl implements DomibusConnectorKeystorePersistenceService {

	
	@Autowired
    DomibusConnectorKeystoreDao keystoreDao;

	@Override
	@Transactional
	public DomibusConnectorKeystore persistNewKeystore(DomibusConnectorKeystore pKeystore) {
		PDomibusConnectorKeystore dbKeystore = new PDomibusConnectorKeystore();
		
		String uuid = pKeystore.getUuid();

		if(StringUtils.isEmpty(uuid)) {
			uuid = String.format("%s@%s", UUID.randomUUID(), "dc.keystore.eu");
		}

		dbKeystore.setUuid(uuid);
		dbKeystore.setKeystore(pKeystore.getKeystoreBytes());

		dbKeystore.setPassword(pKeystore.getPasswordPlain());		
		dbKeystore.setDescription(pKeystore.getDescription());
		dbKeystore.setType(pKeystore.getType());
		
		dbKeystore = keystoreDao.save(dbKeystore);
		
		pKeystore.setUuid(dbKeystore.getUuid());
		pKeystore.setUploaded(dbKeystore.getUploaded());
		
		return pKeystore;
	}
	
	@Override
	@Transactional
	public void updateKeystorePassword(DomibusConnectorKeystore pKeystore, String newKeystorePassword) {
		if (StringUtils.isEmpty(pKeystore.getUuid())) {
            throw new IllegalArgumentException("UUID of keystore must not be null!");
        }
		
		Optional<PDomibusConnectorKeystore> dbKeystore = keystoreDao.findByUuid(pKeystore.getUuid());
		if(dbKeystore.isPresent()) {
			dbKeystore.get().setPassword(newKeystorePassword);
			keystoreDao.save(dbKeystore.get());
		}else {
			throw new NoResultException(String.format("No keystore with UUID [%s] found in database!", pKeystore.getUuid()));
		}
		
	}

	@Override
	public DomibusConnectorKeystore getKeystoreByUUID(String uuid) {
		throw new RuntimeException("Not implemented yet!?");
	}



}
