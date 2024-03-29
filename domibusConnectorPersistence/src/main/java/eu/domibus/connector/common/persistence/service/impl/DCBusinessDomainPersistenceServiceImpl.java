package eu.domibus.connector.common.persistence.service.impl;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageLane;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
public class DCBusinessDomainPersistenceServiceImpl implements DCBusinessDomainPersistenceService {
    private final DomibusConnectorBusinessDomainDao businessDomainDao;

    public DCBusinessDomainPersistenceServiceImpl(DomibusConnectorBusinessDomainDao businessDomainDao) {
        this.businessDomainDao = businessDomainDao;
    }

    @Override
    public Optional<DomibusConnectorBusinessDomain> findById(DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        Optional<PDomibusConnectorMessageLane> byName = businessDomainDao.findByName(businessDomainId);
        return byName.map(this::mapToDomain);
    }

    @Override
    public List<DomibusConnectorBusinessDomain> findAll() {
        return businessDomainDao.findAll().stream().map(this::mapToDomain).collect(Collectors.toList());
    }

    @Override
    public DomibusConnectorBusinessDomain update(DomibusConnectorBusinessDomain domibusConnectorBusinessDomain) {
        if (domibusConnectorBusinessDomain == null) {
            throw new IllegalArgumentException("domibusConnectorBusinessDomain is not allowed to be null!");
        }
        Optional<PDomibusConnectorMessageLane> lane =
                businessDomainDao.findByName(domibusConnectorBusinessDomain.getId());
        if (lane.isPresent()) {
            PDomibusConnectorMessageLane dbBusinessDomain = this.mapToDb(domibusConnectorBusinessDomain, lane.get());
            PDomibusConnectorMessageLane save = businessDomainDao.save(dbBusinessDomain);
            return this.mapToDomain(save);
        } else {
            throw new IllegalArgumentException(String.format(
                    "No BusinessDomain configured with id [%s]",
                    domibusConnectorBusinessDomain.getId()
            ));
        }
    }

    @Override
    public DomibusConnectorBusinessDomain create(DomibusConnectorBusinessDomain businessDomain) {
        if (businessDomain == null) {
            throw new IllegalArgumentException("Null is not allowed for businessDomain");
        }
        if (businessDomain.getId() == null) {
            throw new IllegalArgumentException("Null is not allowed for businessDomainId!");
        }
        PDomibusConnectorMessageLane dbBusinessDomain =
                this.mapToDb(businessDomain, new PDomibusConnectorMessageLane());
        dbBusinessDomain.setName(businessDomain.getId());
        PDomibusConnectorMessageLane save = businessDomainDao.save(dbBusinessDomain);
        return this.mapToDomain(save);
    }

    private PDomibusConnectorMessageLane mapToDb(
            DomibusConnectorBusinessDomain businessDomain,
            PDomibusConnectorMessageLane dbDomain) {
        Map<String, String> map = new HashMap<>(businessDomain.getMessageLaneProperties());
        dbDomain.setProperties(map);
        dbDomain.setDescription(businessDomain.getDescription());
        return dbDomain;
    }

    private DomibusConnectorBusinessDomain mapToDomain(PDomibusConnectorMessageLane pDomibusConnectorMessageLane) {
        DomibusConnectorBusinessDomain lane = new DomibusConnectorBusinessDomain();
        lane.setDescription(pDomibusConnectorMessageLane.getDescription());
        lane.setId(pDomibusConnectorMessageLane.getName());
        lane.setConfigurationSource(ConfigurationSource.DB);
        lane.setEnabled(true);
        Map<String, String> p = new HashMap<>(pDomibusConnectorMessageLane.getProperties());
        lane.setMessageLaneProperties(p);
        return lane;
    }
}
