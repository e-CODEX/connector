/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.spring;

import eu.ecodex.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.ecodex.connector.persistence.dao.PackageDomibusConnectorRepositories;
import eu.ecodex.connector.persistence.model.PDomibusConnectorPersistenceModel;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * The DomibusConnectorPersistenceContext class represents the persistence configuration for Domibus
 * Connector. It is responsible for configuring the entity scanning, JPA repositories, transaction
 * management, and property source.
 *
 * <p>The configuration includes the following:
 * <ul>
 *     <li>The base package classes for entity scanning
 *     <li>The base package classes for JPA repositories
 *     <li>The base package class for the business domain DAO
 *     <li>Enablement of transaction management
 *     <li>Property source for default persistence configuration properties
 * </ul>
 *
 * <p>This class also contains a logger instance for logging purposes.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 * @since 1.0
 */
@Configuration
@EntityScan(basePackageClasses = {PDomibusConnectorPersistenceModel.class})
@EnableJpaRepositories(
    basePackageClasses = {PackageDomibusConnectorRepositories.class,
        DomibusConnectorBusinessDomainDao.class}
)
@EnableTransactionManagement
@PropertySource(
    "classpath:/eu/ecodex/connector/persistence/config/default-persistence-config.properties"
)
public class DomibusConnectorPersistenceContext {
}
