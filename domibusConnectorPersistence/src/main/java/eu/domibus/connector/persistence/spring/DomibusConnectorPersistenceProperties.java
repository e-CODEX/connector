/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.spring;

import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceServiceFilesystemImpl;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceServiceJpaImpl;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Contains all properties related to the persistence module except DataSource which is configured
 * via spring boot datasource.
 */
@Component
@ConfigurationProperties(prefix = DomibusConnectorPersistenceProperties.PREFIX)
@PropertySource(
    "classpath:eu/domibus/connector/persistence/config/default-persistence-config.properties"
)
@Getter
@Setter
public class DomibusConnectorPersistenceProperties {
    public static final String PREFIX = "connector.persistence";
    /**
     * Which big LargeFileImpl should be used by default part of the connector are.
     * <ul>
     *     <li>FileBased ({@link LargeFilePersistenceServiceFilesystemImpl#getClass()}
     *     <li>DatabaseBased ({@link LargeFilePersistenceServiceJpaImpl#getClass()}
     * </ul>
     */
    public Class<? extends LargeFilePersistenceProvider> defaultLargeFileProviderClass =
        LargeFilePersistenceServiceFilesystemImpl.class;
    /**
     * Which big LargeFileImpl should be used by default part of the connector are.
     * <ul>
     *     <li>FileBased ({@link LargeFilePersistenceServiceFilesystemImpl#getProviderName()}</li>
     *     <li>DatabaseBased ({@link LargeFilePersistenceServiceJpaImpl#getProviderName()}</li>
     * </ul>
     */
    public String defaultLargeFileProviderName;
}
