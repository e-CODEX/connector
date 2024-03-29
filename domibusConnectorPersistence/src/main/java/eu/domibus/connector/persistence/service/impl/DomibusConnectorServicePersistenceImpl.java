package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorService;
import eu.domibus.connector.persistence.dao.DomibusConnectorServiceDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorService;
import eu.domibus.connector.persistence.service.DomibusConnectorServicePersistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class DomibusConnectorServicePersistenceImpl implements DomibusConnectorServicePersistenceService {
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
    public eu.domibus.connector.domain.model.DomibusConnectorService getService(String service) {
        // PDomibusConnectorService srv = serviceDao.findById(service).get();
        // return ServiceMapper.mapServiceToDomain(srv);
        return null;
    }

    @Override
    public List<String> getServiceListString() {
        List<String> services = new ArrayList<>();
        for (PDomibusConnectorService dbService : this.serviceDao.findAll()) {
            services.add(dbService.getService());
        }
        return services;
    }
}
