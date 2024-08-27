/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.common.persistence.service.impl;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessageLane;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The DCBusinessDomainPersistenceServiceImpl class is an implementation of the
 * DCBusinessDomainPersistenceService interface. It provides methods for accessing and manipulating
 * business domain data in Domibus.
 */
@Service
@Transactional
public class DCBusinessDomainPersistenceServiceImpl implements DCBusinessDomainPersistenceService {
    private final DomibusConnectorBusinessDomainDao businessDomainDao;

    public DCBusinessDomainPersistenceServiceImpl(
        DomibusConnectorBusinessDomainDao businessDomainDao) {
        this.businessDomainDao = businessDomainDao;
    }

    @Override
    public Optional<DomibusConnectorBusinessDomain> findById(
        DomibusConnectorBusinessDomain.BusinessDomainId businessDomainId) {
        Optional<PDomibusConnectorMessageLane> byName =
            businessDomainDao.findByName(businessDomainId);
        return byName.map(this::mapToDomain);
    }

    @Override
    public List<DomibusConnectorBusinessDomain> findAll() {
        return businessDomainDao.findAll()
                                .stream()
                                .map(this::mapToDomain)
                                .toList();
    }

    @Override
    public DomibusConnectorBusinessDomain update(
        DomibusConnectorBusinessDomain domibusConnectorBusinessDomain) {
        if (domibusConnectorBusinessDomain == null) {
            throw new IllegalArgumentException(
                "domibusConnectorBusinessDomain is not allowed to be null!");
        }
        Optional<PDomibusConnectorMessageLane> lane =
            businessDomainDao.findByName(domibusConnectorBusinessDomain.getId());
        if (lane.isPresent()) {
            PDomibusConnectorMessageLane dbBusinessDomain =
                this.mapToDb(domibusConnectorBusinessDomain, lane.get());
            PDomibusConnectorMessageLane save = businessDomainDao.save(dbBusinessDomain);
            return this.mapToDomain(save);
        } else {
            throw new IllegalArgumentException(
                String.format(
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
        DomibusConnectorBusinessDomain businessDomain, PDomibusConnectorMessageLane dbDomain) {
        Map<String, String> map = new HashMap<>(businessDomain.getMessageLaneProperties());
        dbDomain.setProperties(map);
        dbDomain.setDescription(businessDomain.getDescription());
        return dbDomain;
    }

    private DomibusConnectorBusinessDomain mapToDomain(
        PDomibusConnectorMessageLane connectorMessageLane) {
        var lane = new DomibusConnectorBusinessDomain();
        lane.setDescription(connectorMessageLane.getDescription());
        lane.setId(connectorMessageLane.getName());
        lane.setConfigurationSource(ConfigurationSource.DB);
        lane.setEnabled(true);
        Map<String, String> p = new HashMap<>(connectorMessageLane.getProperties());
        lane.setMessageLaneProperties(p);
        return lane;
    }
}
