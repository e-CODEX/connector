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

import eu.ecodex.connector.domain.model.DomibusConnectorService;
import eu.ecodex.connector.persistence.dao.DomibusConnectorServiceDao;
import eu.ecodex.connector.persistence.model.PDomibusConnectorService;
import eu.ecodex.connector.persistence.service.DomibusConnectorServicePersistenceService;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The DomibusConnectorServicePersistenceImpl class implements the
 * DomibusConnectorServicePersistenceService interface to provide methods for interacting with the
 * persistence layer for managing DomibusConnectorService objects.
 */
@Service
public class DomibusConnectorServicePersistenceImpl
    implements DomibusConnectorServicePersistenceService {
    DomibusConnectorServiceDao serviceDao;

    @Autowired
    public void setServiceDao(DomibusConnectorServiceDao serviceDao) {
        this.serviceDao = serviceDao;
    }

    @Override
    public DomibusConnectorService persistNewService(DomibusConnectorService newService) {
        PDomibusConnectorService dbService = ServiceMapper.mapServiceToPersistence(newService);
        dbService = this.serviceDao.save(dbService);
        return ServiceMapper.mapServiceToDomain(dbService);
    }

    @Override
    public List<DomibusConnectorService> getServiceList() {
        List<DomibusConnectorService> services = new ArrayList<>();
        for (PDomibusConnectorService dbService : this.serviceDao.findAll()) {
            DomibusConnectorService srv = ServiceMapper.mapServiceToDomain(dbService);
            services.add(srv);
        }
        return services;
    }

    @Override
    public List<String> getServiceListString() {
        List<String> services = new ArrayList<>();
        for (PDomibusConnectorService dbService : this.serviceDao.findAll()) {
            services.add(dbService.getService());
        }
        return services;
    }

    @Override
    public DomibusConnectorService updateService(
        DomibusConnectorService oldService, DomibusConnectorService newService) {
        PDomibusConnectorService dbService = ServiceMapper.mapServiceToPersistence(newService);
        dbService = this.serviceDao.save(dbService);
        return ServiceMapper.mapServiceToDomain(dbService);
    }

    @Override
    public void deleteService(DomibusConnectorService service) {
        PDomibusConnectorService dbService = ServiceMapper.mapServiceToPersistence(service);
        this.serviceDao.delete(dbService);
    }

    @Override
    public DomibusConnectorService getService(String service) {
        // PDomibusConnectorService srv = serviceDao.findById(service).get();
        // return ServiceMapper.mapServiceToDomain(srv);
        return null;
    }
}
