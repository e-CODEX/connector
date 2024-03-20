package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.largefiles.provider.LargeFilePersistenceProvider;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import eu.domibus.connector.persistence.service.exceptions.LargeFileDeletionException;
import eu.domibus.connector.persistence.spring.DomibusConnectorPersistenceProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LargeFilePersistenceServiceImpl implements LargeFilePersistenceService {

    private static final Logger LOGGER = LogManager.getLogger(LargeFilePersistenceServiceImpl.class);

    @Autowired
    DomibusConnectorPersistenceProperties domibusConnectorPersistenceProperties;

    LargeFilePersistenceProvider defaultLargeFilePersistenceProvider;

    @Autowired(required = false)
    List<LargeFilePersistenceProvider> availableLargeFilePersistenceProvider = new ArrayList<>();

    @PostConstruct
    public void init() {
        final Class<? extends LargeFilePersistenceProvider> defaultLargeFileProviderClass = domibusConnectorPersistenceProperties.getDefaultLargeFileProviderClass();
        final String defaultProviderName = domibusConnectorPersistenceProperties.getDefaultLargeFileProviderName();
        LargeFilePersistenceProvider p = availableLargeFilePersistenceProvider
                .stream()
                //lookup for provider name first, and then for class name
                .filter(largeFilePersistenceProvider ->
                                (!StringUtils.isEmpty(defaultProviderName) && defaultProviderName.equals(largeFilePersistenceProvider.getProviderName()) ||
                                (StringUtils.isEmpty(defaultProviderName) && defaultLargeFileProviderClass != null && defaultLargeFileProviderClass.isAssignableFrom(largeFilePersistenceProvider.getClass()))))
                .findFirst()
                .orElse(null);
        if (p == null) {
            throw new RuntimeException(String.format("No LargeFilePersistenceProvider provider with Class [%s] or Name [%s] is registered as spring bean!\n" +
                    "The following LargeFilePersistenceProvider are available:\n[%s]\nPlease consider updating the configuration property [%s]",
                    defaultLargeFileProviderClass,
                    defaultProviderName,
                    getAvailableStorageProviderAsStringWithNewLine(),
                    DomibusConnectorPersistenceProperties.PREFIX + "default-large-file-provider-name"
            ));
        } else {
            defaultLargeFilePersistenceProvider = p;
            LOGGER.info("Setting LargeFilePersistenceProvider [{}] as default provider", defaultLargeFilePersistenceProvider);
        }
    }


    @Override
    public LargeFileReference getReadableDataSource(LargeFileReference bigDataReference) {
        return getProviderByLargeFileReference(bigDataReference)
                .getReadableDataSource(bigDataReference);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(InputStream input, String connectorMessageId, String documentName, String documentContentType) {
        return defaultLargeFilePersistenceProvider.createDomibusConnectorBigDataReference(input, connectorMessageId, documentName, documentContentType);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(DomibusConnectorMessageId connectorMessageId, String documentName, String documentContentType) {
        return defaultLargeFilePersistenceProvider.createDomibusConnectorBigDataReference(connectorMessageId, documentName, documentContentType);
    }

    @Override
    public void deleteDomibusConnectorBigDataReference(LargeFileReference bigDataReference) throws LargeFileDeletionException {
        try {
            getProviderByLargeFileReference(bigDataReference)
                    .deleteDomibusConnectorBigDataReference(bigDataReference);
        } catch (Exception e) {
            throw new LargeFileDeletionException("Error during delete", e);
        }
    }

    @Override
    public Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences() {
        Map<DomibusConnectorMessageId, List<LargeFileReference>> collect = availableLargeFilePersistenceProvider
                .stream()
                .map(provider -> {
                    try {
                        return provider.getAllAvailableReferences();
                    } catch (Exception e) {
                        //ignore..
                    }
                    return new HashMap<DomibusConnectorMessageId, List<LargeFileReference>> ();
                })
                .flatMap(refmap -> refmap.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return collect;
    }

    @Override
    public boolean isStorageProviderAvailable(LargeFileReference toCopy) {
        return getProviderByName(toCopy.getStorageProviderName()).isPresent();
    }

    @Override
    public LargeFilePersistenceProvider getDefaultProvider() {
        return defaultLargeFilePersistenceProvider;
    }

    private LargeFilePersistenceProvider getProviderByLargeFileReference(LargeFileReference bigDataReference) {
        String storageProviderName = bigDataReference.getStorageProviderName();
        LOGGER.debug("Looking up Storage provider for largeFileReference [{}] with name [{}]",
                bigDataReference.getStorageIdReference(),
                storageProviderName);
        LargeFilePersistenceProvider largeFilePersistenceProvider = this.getProviderByName(storageProviderName)
                .orElseThrow(() -> new RuntimeException(String.format("No  LargeFilePersistenceProvider with name %s is available.\n" +
                                "The following LargeFilePersistenceProvider are available:\n[%s]",
                        storageProviderName,
                        getAvailableStorageProviderAsStringWithNewLine()
                )));
        return largeFilePersistenceProvider;
    }

    private Optional<LargeFilePersistenceProvider> getProviderByName(String providerName) {
        if (StringUtils.isEmpty(providerName)) {
            throw new IllegalArgumentException("largeFilePersistenceProviderName is not allowed to be empty!");
        }
        return availableLargeFilePersistenceProvider
                .stream()
                .filter(largeFilePersistenceProvider -> providerName.equals(largeFilePersistenceProvider.getProviderName()))
                .findFirst();
    }

    private String getAvailableStorageProviderAsStringWithNewLine() {
        return availableLargeFilePersistenceProvider
                .stream()
                .map(l -> l.getProviderName()  + ":" + l.getClass())
                .collect(Collectors.joining("\n"));
    }

}
