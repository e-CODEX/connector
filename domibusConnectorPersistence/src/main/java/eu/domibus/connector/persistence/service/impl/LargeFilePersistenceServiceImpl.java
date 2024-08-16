/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import eu.domibus.connector.persistence.service.exceptions.LargeFileDeletionException;
import eu.domibus.connector.persistence.spring.DomibusConnectorPersistenceProperties;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * This class is an implementation of the {@link LargeFilePersistenceService} interface. It provides
 * methods for storing and retrieving large files.
 */
@Service
public class LargeFilePersistenceServiceImpl implements LargeFilePersistenceService {
    private static final Logger LOGGER =
        LogManager.getLogger(LargeFilePersistenceServiceImpl.class);
    @Autowired
    DomibusConnectorPersistenceProperties domibusConnectorPersistenceProperties;
    LargeFilePersistenceProvider defaultLargeFilePersistenceProvider;
    @Autowired(required = false)
    List<LargeFilePersistenceProvider> availableLargeFilePersistenceProvider = new ArrayList<>();

    /**
     * Initializes the LargeFilePersistenceService by setting the default large file provider.
     * If no provider is found matching the default provider class or name, an exception is thrown.
     */
    @PostConstruct
    public void init() {
        final Class<? extends LargeFilePersistenceProvider> defaultLargeFileProviderClass =
            domibusConnectorPersistenceProperties.getDefaultLargeFileProviderClass();
        final String defaultProviderName =
            domibusConnectorPersistenceProperties.getDefaultLargeFileProviderName();
        LargeFilePersistenceProvider p = availableLargeFilePersistenceProvider
            .stream()
            // lookup for provider name first, and then for class name
            .filter(largeFilePersistenceProvider ->
                        (StringUtils.hasLength(defaultProviderName) && defaultProviderName.equals(
                            largeFilePersistenceProvider.getProviderName())
                            || (!StringUtils.hasLength(defaultProviderName)
                            && defaultLargeFileProviderClass != null
                            && defaultLargeFileProviderClass.isAssignableFrom(
                            largeFilePersistenceProvider.getClass()
                        )
                        )
                        ))
            .findFirst()
            .orElse(null);
        if (p == null) {
            throw new RuntimeException(String.format(
                """
                    No LargeFilePersistenceProvider provider with Class [%s] or Name [%s] is 
                    registered as spring bean!
                    The following LargeFilePersistenceProvider are available:
                    [%s]
                    Please consider updating the configuration property [%s]""",
                defaultLargeFileProviderClass,
                defaultProviderName,
                getAvailableStorageProviderAsStringWithNewLine(),
                DomibusConnectorPersistenceProperties.PREFIX + "default-large-file-provider-name"
            ));
        } else {
            defaultLargeFilePersistenceProvider = p;
            LOGGER.info(
                "Setting LargeFilePersistenceProvider [{}] as default provider",
                defaultLargeFilePersistenceProvider
            );
        }
    }

    @Override
    public LargeFileReference getReadableDataSource(LargeFileReference bigDataReference) {
        return getProviderByLargeFileReference(bigDataReference)
            .getReadableDataSource(bigDataReference);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        InputStream input, String connectorMessageId, String documentName,
        String documentContentType) {
        return defaultLargeFilePersistenceProvider.createDomibusConnectorBigDataReference(
            input, connectorMessageId, documentName, documentContentType);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        DomibusConnectorMessageId connectorMessageId, String documentName,
        String documentContentType) {
        return defaultLargeFilePersistenceProvider.createDomibusConnectorBigDataReference(
            connectorMessageId, documentName, documentContentType);
    }

    @Override
    public void deleteDomibusConnectorBigDataReference(LargeFileReference bigDataReference)
        throws LargeFileDeletionException {
        try {
            getProviderByLargeFileReference(bigDataReference)
                .deleteDomibusConnectorBigDataReference(bigDataReference);
        } catch (Exception e) {
            throw new LargeFileDeletionException("Error during delete", e);
        }
    }

    @Override
    public Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences() {
        return availableLargeFilePersistenceProvider
            .stream()
            .map(provider -> {
                try {
                    return provider.getAllAvailableReferences();
                } catch (Exception e) {
                    // ignore..
                }
                return new HashMap<DomibusConnectorMessageId, List<LargeFileReference>>();
            })
            .flatMap(refmap -> refmap.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public boolean isStorageProviderAvailable(LargeFileReference toCopy) {
        return getProviderByName(toCopy.getStorageProviderName()).isPresent();
    }

    @Override
    public LargeFilePersistenceProvider getDefaultProvider() {
        return defaultLargeFilePersistenceProvider;
    }

    private LargeFilePersistenceProvider getProviderByLargeFileReference(
        LargeFileReference bigDataReference) {
        String storageProviderName = bigDataReference.getStorageProviderName();
        LOGGER.debug(
            "Looking up Storage provider for largeFileReference [{}] with name [{}]",
            bigDataReference.getStorageIdReference(),
            storageProviderName
        );
        return this.getProviderByName(storageProviderName)
                   .orElseThrow(() -> new RuntimeException(String.format(
                       """
                           No  LargeFilePersistenceProvider with name %s is available.
                           The following LargeFilePersistenceProvider are available:
                           [%s]""",
                       storageProviderName,
                       getAvailableStorageProviderAsStringWithNewLine()
                   )));
    }

    private Optional<LargeFilePersistenceProvider> getProviderByName(String providerName) {
        if (!StringUtils.hasLength(providerName)) {
            throw new IllegalArgumentException(
                "largeFilePersistenceProviderName is not allowed to be empty!");
        }
        return availableLargeFilePersistenceProvider
            .stream()
            .filter(largeFilePersistenceProvider -> providerName.equals(
                largeFilePersistenceProvider.getProviderName()))
            .findFirst();
    }

    private String getAvailableStorageProviderAsStringWithNewLine() {
        return availableLargeFilePersistenceProvider
            .stream()
            .map(l -> l.getProviderName() + ":" + l.getClass())
            .collect(Collectors.joining("\n"));
    }
}
