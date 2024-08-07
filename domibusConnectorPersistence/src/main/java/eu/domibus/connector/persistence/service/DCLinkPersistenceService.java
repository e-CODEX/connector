/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.service;

import eu.domibus.connector.domain.enums.ConfigurationSource;
import eu.domibus.connector.domain.enums.LinkMode;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.persistence.dao.DomibusConnectorLinkConfigurationDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorLinkPartnerDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorLinkConfiguration;
import eu.domibus.connector.persistence.model.PDomibusConnectorLinkPartner;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * The DCLinkPersistenceService class is responsible for handling the persistence of Domibus
 * Connector links and configurations.
 */
@Service
public class DCLinkPersistenceService {
    private static final Logger LOGGER = LogManager.getLogger(DCLinkPersistenceService.class);
    private static final String CONFIG_PROPERTY_PREFIX = "prop.";
    private static final String PULL_INTERVAL_PROPERTY = "pull-interval";
    private static final String SEND_LINK_MODE_PROPERTY = "send-link-mode";
    private static final String RCV_LINK_MODE_PROPERTY = "rcv-link-mode";
    private final DomibusConnectorLinkPartnerDao linkPartnerDao;
    private final DomibusConnectorLinkConfigurationDao linkConfigurationDao;

    public DCLinkPersistenceService(
        DomibusConnectorLinkPartnerDao linkPartnerDao,
        DomibusConnectorLinkConfigurationDao linkConfigurationDao) {
        this.linkPartnerDao = linkPartnerDao;
        this.linkConfigurationDao = linkConfigurationDao;
    }

    /**
     * Retrieves a list of all enabled DomibusConnectorLinkPartner objects.
     *
     * @return List of all enabled DomibusConnectorLinkPartner objects.
     */
    public List<DomibusConnectorLinkPartner> getAllEnabledLinks() {
        return linkPartnerDao
            .findAllByEnabledIsTrue()
            .stream()
            .map(this::mapToLinkPartner)
            .toList();
    }

    /**
     * Retrieves a list of all DomibusConnectorLinkConfigurations.
     *
     * @return List of all DomibusConnectorLinkConfigurations.
     */
    public List<DomibusConnectorLinkConfiguration> getAllLinkConfigurations() {
        return linkConfigurationDao.findAll()
                                   .stream()
                                   .map(this::mapToLinkConfiguration)
                                   .toList();
    }

    private DomibusConnectorLinkPartner mapToLinkPartner(PDomibusConnectorLinkPartner dbLinkInfo) {
        var linkPartner = new DomibusConnectorLinkPartner();
        BeanUtils.copyProperties(dbLinkInfo, linkPartner);
        PDomibusConnectorLinkConfiguration linkConfiguration = dbLinkInfo.getLinkConfiguration();

        linkPartner.setLinkPartnerName(
            new DomibusConnectorLinkPartner.LinkPartnerName(dbLinkInfo.getLinkName()));
        linkPartner.setLinkConfiguration(mapToLinkConfiguration(linkConfiguration));

        Map<String, String> dbProperties = dbLinkInfo.getProperties();
        String pullInterval = dbProperties.get(PULL_INTERVAL_PROPERTY);
        if (StringUtils.hasLength(pullInterval)) {
            linkPartner.setPullInterval(Duration.parse(pullInterval));
        }
        linkPartner.setSendLinkMode(mapOrDefault(dbProperties.get(SEND_LINK_MODE_PROPERTY)));
        linkPartner.setRcvLinkMode(mapOrDefault(dbProperties.get(RCV_LINK_MODE_PROPERTY)));
        linkPartner.setProperties(mapToLinkPartnerProperties(dbProperties));
        linkPartner.setConfigurationSource(ConfigurationSource.DB);

        return linkPartner;
    }

    private LinkMode mapOrDefault(String s) {
        return LinkMode.ofDbName(s).orElse(null);
    }

    private DomibusConnectorLinkConfiguration mapToLinkConfiguration(
        PDomibusConnectorLinkConfiguration dbLinkConfig) {
        if (dbLinkConfig == null) {
            return null;
        }
        var configuration = new DomibusConnectorLinkConfiguration();

        HashMap<String, String> p = new HashMap<>();
        p.putAll(dbLinkConfig.getProperties());
        configuration.setProperties(p);
        configuration.setConfigName(
            new DomibusConnectorLinkConfiguration.LinkConfigName(dbLinkConfig.getConfigName()));
        configuration.setLinkImpl(dbLinkConfig.getLinkImpl());
        configuration.setConfigurationSource(ConfigurationSource.DB);
        return configuration;
    }

    /**
     * Adds a new link partner to the Domibus Connector.
     *
     * @param linkPartner The link partner to be added.
     * @throws IllegalArgumentException if the link partner does not have a link configuration.
     */
    //    @Transactional
    public void addLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        if (linkPartner.getLinkConfiguration() == null) {
            throw new IllegalArgumentException(
                "Cannot add a LinkPartner without an LinkConfiguration!");
        }
        PDomibusConnectorLinkPartner dbLinkPartner = mapToDbLinkPartner(linkPartner);

        PDomibusConnectorLinkConfiguration dbLinkConfiguration =
            mapToDbLinkConfiguration(linkPartner.getLinkConfiguration());
        dbLinkConfiguration = linkConfigurationDao.save(dbLinkConfiguration);
        dbLinkPartner.setLinkConfiguration(dbLinkConfiguration);

        linkPartnerDao.save(dbLinkPartner);
        LOGGER.debug("Saving [{}] to database", dbLinkPartner);

        // check for only one gw link config...
        var gatewayExample = new PDomibusConnectorLinkPartner();
        gatewayExample.setLinkType(LinkType.GATEWAY);
        gatewayExample.setEnabled(true);

        List<PDomibusConnectorLinkPartner> all = linkPartnerDao.findAll(Example.of(gatewayExample));
        if (all.size() > 1) {
            LOGGER.warn(
                "Only one active gateway configuration at once is allowed - new link will be "
                    + "inactive!"
            );
            dbLinkPartner.setEnabled(false);
            linkPartnerDao.save(dbLinkPartner);
        }

        LOGGER.debug("Successfully saved [{}] to database", dbLinkPartner);
    }

    private PDomibusConnectorLinkPartner mapToDbLinkPartner(
        DomibusConnectorLinkPartner linkPartner) {
        if (linkPartner == null) {
            return null;
        }
        String linkName = linkPartner.getLinkPartnerName() == null ? null :
            linkPartner.getLinkPartnerName().getLinkName();

        Optional<PDomibusConnectorLinkPartner> oneByLinkName =
            linkPartnerDao.findOneByLinkName(linkName);
        PDomibusConnectorLinkPartner dbLinkPartner =
            oneByLinkName.orElse(new PDomibusConnectorLinkPartner());

        BeanUtils.copyProperties(linkPartner, dbLinkPartner);

        dbLinkPartner.setLinkType(linkPartner.getLinkType());
        dbLinkPartner.setDescription(linkPartner.getDescription());
        dbLinkPartner.setLinkName(linkName);
        dbLinkPartner.setEnabled(linkPartner.isEnabled());
        dbLinkPartner.setLinkConfiguration(
            this.mapToDbLinkConfiguration(linkPartner.getLinkConfiguration()));

        Map<String, String> dbProperties = mapToDbProperties(linkPartner.getProperties());
        dbProperties.put(PULL_INTERVAL_PROPERTY, linkPartner.getPullInterval().toString());
        dbProperties.put(SEND_LINK_MODE_PROPERTY, linkPartner.getSendLinkMode().getDbName());
        dbProperties.put(RCV_LINK_MODE_PROPERTY, linkPartner.getRcvLinkMode().getDbName());
        dbLinkPartner.setProperties(dbProperties);

        return dbLinkPartner;
    }

    private PDomibusConnectorLinkConfiguration mapToDbLinkConfiguration(
        DomibusConnectorLinkConfiguration linkConfiguration) {
        if (linkConfiguration == null) {
            return null;
        }
        String configName = linkConfiguration.getConfigName() == null ? null :
            linkConfiguration.getConfigName().getConfigName();

        Optional<PDomibusConnectorLinkConfiguration> oneByConfigName =
            linkConfigurationDao.getOneByConfigName(configName);

        PDomibusConnectorLinkConfiguration dbLinkConfig =
            oneByConfigName.orElse(new PDomibusConnectorLinkConfiguration());
        dbLinkConfig.setConfigName(configName);
        dbLinkConfig.setLinkImpl(linkConfiguration.getLinkImpl());

        Map<String, String> collect = linkConfiguration.getProperties().entrySet()
                                                       .stream().filter(
                e -> StringUtils.hasText(e.getValue()))
                                                       .collect(Collectors.toMap(
                                                           Map.Entry::getKey,
                                                           Map.Entry::getValue
                                                       ));

        dbLinkConfig.setProperties(collect);

        return dbLinkConfig;
    }

    private Map<String, String> mapToDbProperties(Map<String, String> properties) {
        if (properties == null) {
            return new HashMap<>();
        }
        return properties.entrySet()
                         .stream()
                         .filter(e -> StringUtils.hasText(e.getValue()))
                         .collect(Collectors.toMap(
                             e -> CONFIG_PROPERTY_PREFIX + e.getKey(),
                             Map.Entry::getValue
                         ));
    }

    private Map<String, String> mapToLinkPartnerProperties(Map<String, String> properties) {
        return properties.entrySet()
                         .stream()
                         .filter(
                             e -> e.getKey().startsWith(CONFIG_PROPERTY_PREFIX))
                         .collect(Collectors.toMap(
                             e -> e.getKey().substring(
                                 CONFIG_PROPERTY_PREFIX.length()),
                             Map.Entry::getValue
                         ));
    }

    /**
     * Retrieves the DomibusConnectorLinkConfiguration for the given configName.
     *
     * @param configName The name of the link configuration to retrieve.
     * @return Optional containing DomibusConnectorLinkConfiguration if found, otherwise
     *      Optional.empty().
     */
    public Optional<DomibusConnectorLinkConfiguration> getLinkConfiguration(
        DomibusConnectorLinkConfiguration.LinkConfigName configName) {
        Optional<PDomibusConnectorLinkConfiguration> oneByConfigName =
            linkConfigurationDao.getOneByConfigName(configName.getConfigName());
        return Optional.ofNullable(this.mapToLinkConfiguration(oneByConfigName.orElse(null)));
    }

    /**
     * Retrieves the link partner with the given link name.
     *
     * @param linkPartnerName The name of the link partner to retrieve.
     * @return An Optional containing the link partner if found, otherwise Optional.empty().
     */
    public Optional<DomibusConnectorLinkPartner> getLinkPartner(
        DomibusConnectorLinkPartner.LinkPartnerName linkPartnerName) {
        Optional<PDomibusConnectorLinkPartner> linkPartner =
            linkPartnerDao.findOneByLinkName(linkPartnerName.getLinkName());
        return Optional.ofNullable(this.mapToLinkPartner(linkPartner.orElse(null)));
    }

    /**
     * Deletes the given link partner from the Domibus Connector. This method performs the following
     * steps:
     * 1. Retrieves the link partner with the given link name using the findOneByLinkName()
     * method of linkPartnerDao.
     * 2. If the link partner is found, it is deleted using the delete()
     * method of linkPartnerDao.
     *
     * @param linkPartner The link partner to be deleted.
     */
    public void deleteLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        var linkPartnerName = linkPartner.getLinkPartnerName();
        Optional<PDomibusConnectorLinkPartner> dbEntity =
            linkPartnerDao.findOneByLinkName(linkPartnerName.getLinkName());
        dbEntity.ifPresent(linkPartnerDao::delete);
    }

    /**
     * Retrieves a list of all link partners in the Domibus Connector.
     *
     * @return List of all link partners as DomibusConnectorLinkPartner objects.
     */
    public List<DomibusConnectorLinkPartner> getAllLinks() {
        return linkPartnerDao.findAll()
                             .stream()
                             .map(this::mapToLinkPartner)
                             .toList();
    }

    public void updateLinkPartner(DomibusConnectorLinkPartner linkPartner) {
        PDomibusConnectorLinkPartner dbLinkPartner = mapToDbLinkPartner(linkPartner);
        linkPartnerDao.save(dbLinkPartner);
    }

    @Transactional
    public void updateLinkConfig(DomibusConnectorLinkConfiguration linkConfig) {
        PDomibusConnectorLinkConfiguration dbLinkConfig = mapToDbLinkConfiguration(linkConfig);
        linkConfigurationDao.save(dbLinkConfig);
    }

    /**
     * Adds a new link configuration to the Domibus Connector.
     *
     * @param linkConfiguration The link configuration to be added.
     */
    public void addLinkConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        PDomibusConnectorLinkConfiguration dbLinkConfiguration =
            mapToDbLinkConfiguration(linkConfiguration);
        linkConfigurationDao.save(dbLinkConfiguration);
    }

    /**
     * Deletes the given link configuration from the Domibus Connector. This method performs the
     * following steps:
     * 1. Find all link partners that have the same link configuration as the given link
     * configuration.
     * 2. For each link partner found, delete it using the deleteLinkPartner method.
     * 3. Map the given link configuration to a database link configuration using the
     * mapToDbLinkConfiguration method.
     * 4. Delete the database link configuration using the linkConfigurationDao's delete method.
     *
     * @param linkConfiguration The link configuration to be deleted.
     */
    public void deleteLinkConfiguration(DomibusConnectorLinkConfiguration linkConfiguration) {
        getAllLinks()
            .stream()
            .filter(l -> Objects.equals(l.getLinkConfiguration(), linkConfiguration))
            .forEach(this::deleteLinkPartner);
        var mapToDbLinkConfiguration = mapToDbLinkConfiguration(linkConfiguration);
        linkConfigurationDao.delete(mapToDbLinkConfiguration);
    }
}
