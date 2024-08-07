/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.persistence.dao.PackageDomibusConnectorRepositories;
import eu.domibus.connector.persistence.model.PDomibusConnectorPersistenceModel;
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
    "classpath:/eu/domibus/connector/persistence/config/default-persistence-config.properties"
)
public class DomibusConnectorPersistenceContext {
}
