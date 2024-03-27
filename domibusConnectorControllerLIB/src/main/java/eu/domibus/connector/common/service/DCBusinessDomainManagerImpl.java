package eu.domibus.connector.common.service;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.persistence.service.DCBusinessDomainPersistenceService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class DCBusinessDomainManagerImpl implements DCBusinessDomainManager {
    private static final Logger LOGGER = LogManager.getLogger(DCBusinessDomainManagerImpl.class);

    private final ConnectorConfigurationProperties businessDomainConfigurationProperties;
    private final DCBusinessDomainPersistenceService businessDomainPersistenceService;

    public DCBusinessDomainManagerImpl(
            ConnectorConfigurationProperties businessDomainConfigurationProperties,
            DCBusinessDomainPersistenceService businessDomainPersistenceService) {
        this.businessDomainConfigurationProperties = businessDomainConfigurationProperties;
        this.businessDomainPersistenceService = businessDomainPersistenceService;
    }

    @Override
    public List<DomibusConnectorBusinessDomain.BusinessDomainId> getActiveBusinessDomainIds() {
        Set<DomibusConnectorBusinessDomain.BusinessDomainId> collect = new HashSet<>();
        if (businessDomainConfigurationProperties.isLoadBusinessDomainsFromDb()) {
            businessDomainPersistenceService
                    .findAll()
                    .stream()
                    .filter(DomibusConnectorBusinessDomain::isEnabled)
                    .map(DomibusConnectorBusinessDomain::getId)
                    .forEach(b -> collect.add(b));
        }

        businessDomainConfigurationProperties.getBusinessDomain()
                                             .entrySet()
                                             .stream()
                                             .map(this::mapBusinessConfigToBusinessDomain)
                                             .map(DomibusConnectorBusinessDomain::getId)
                                             .forEach(b -> {
                                                 if (!collect.add(b)) {
                                                     LOGGER.warn(
                                                             "Database has already provided a business domain with id" +
                                                                     " [{}]. The domain will not be added from " +
                                                                     "environment. DB takes precedence!",
                                                             b
                                                     );
                                                 }
                                             });

        return new ArrayList<>(collect);
    }

    @Override
    public Optional<DomibusConnectorBusinessDomain> getBusinessDomain(DomibusConnectorBusinessDomain.BusinessDomainId id) {
        Optional<DomibusConnectorBusinessDomain> db = Optional.empty();
        if (businessDomainConfigurationProperties.isLoadBusinessDomainsFromDb()) {
            db = businessDomainPersistenceService.findById(id);
        }
        if (!db.isPresent()) {
            db = businessDomainConfigurationProperties
                    .getBusinessDomain()
                    .entrySet().stream().map(this::mapBusinessConfigToBusinessDomain)
                    .filter(b -> b.getId().equals(id))
                    .findAny();
        }
        return db;
    }

    @Override
    public void updateConfig(DomibusConnectorBusinessDomain.BusinessDomainId id, Map<String, String> properties) {
        Optional<DomibusConnectorBusinessDomain> byId = businessDomainPersistenceService.findById(id);
        if (byId.isPresent()) {
            DomibusConnectorBusinessDomain domibusConnectorBusinessDomain = byId.get();
            if (domibusConnectorBusinessDomain.getConfigurationSource() != ConfigurationSource.DB) {
                LOGGER.warn("Cannot update other than DB source!");
                return;
            }

            Map<String, String> updatedProperties =
                    updateChangedProperties(domibusConnectorBusinessDomain.getMessageLaneProperties(), properties);
            domibusConnectorBusinessDomain.setMessageLaneProperties(updatedProperties);

            businessDomainPersistenceService.update(domibusConnectorBusinessDomain);
        } else {
            throw new RuntimeException("no business domain found for update config!");
        }
    }

    @Override
    public void createBusinessDomain(DomibusConnectorBusinessDomain businessDomain) {
        businessDomainPersistenceService.create(businessDomain);
    }

    Map<String, String> updateChangedProperties(Map<String, String> currentProperties, Map<String, String> properties) {
        currentProperties.putAll(properties);
        return currentProperties
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private DomibusConnectorBusinessDomain mapBusinessConfigToBusinessDomain(Map.Entry<DomibusConnectorBusinessDomain.BusinessDomainId, ConnectorConfigurationProperties.BusinessDomainConfig> messageLaneIdBusinessDomainConfigEntry) {
        DomibusConnectorBusinessDomain lane = new DomibusConnectorBusinessDomain();
        lane.setDescription(messageLaneIdBusinessDomainConfigEntry.getValue().getDescription());
        lane.setId(messageLaneIdBusinessDomainConfigEntry.getKey());
        lane.setConfigurationSource(ConfigurationSource.ENV);
        Map<String, String> p = new HashMap<>(messageLaneIdBusinessDomainConfigEntry.getValue().getProperties());
        lane.setMessageLaneProperties(p);
        return lane;
    }
}
