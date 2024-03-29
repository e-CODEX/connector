package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.dao.DomibusConnectorBigDataDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorBigData;
import eu.domibus.connector.persistence.spring.DomibusConnectorPersistenceProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StreamUtils;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;


/**
 * this service uses jdbc to create an input / output stream with writing it
 * to an byte array (ByteArrayInputStream / ByteArrayOutputStream)
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@ConditionalOnProperty(
        prefix = DomibusConnectorPersistenceProperties.PREFIX,
        value = "provider-" + LargeFilePersistenceServiceJpaImpl.PROVIDER_NAME, havingValue = "true",
        matchIfMissing = true
)
@Service
@Transactional
public class LargeFilePersistenceServiceJpaImpl implements LargeFilePersistenceProvider {
    public static final String PROVIDER_NAME = "jpa";
    private static final Logger LOGGER = LoggerFactory.getLogger(LargeFilePersistenceServiceJpaImpl.class);
    // Entity manager is required to access LobCreator
    @PersistenceContext
    EntityManager entityManager;
    @Autowired
    private DomibusConnectorBigDataDao bigDataDao;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    @Transactional(readOnly = false)
    public LargeFileReference getReadableDataSource(LargeFileReference bigDataReference) {
        if (bigDataReference.getStorageIdReference() == null) {
            throw new IllegalArgumentException(
                    "storageIdReference must be not null!\n The reference must exist in database!"
            );
        }
        String storageReference = bigDataReference.getStorageIdReference();

        try {
            long storageRef = Long.parseLong(storageReference);
            PDomibusConnectorBigData bigData = null;
            LOGGER.debug("Loading big data with storage ref [{}] from database", storageRef);
            Optional<PDomibusConnectorBigData> bigDataOptional = bigDataDao.findById(storageRef);

            if (bigDataOptional.isPresent()) {
                bigData = bigDataOptional.get();
            } else {
                throw new RuntimeException(String.format(
                        "No data found in DB for JPA based LargeFileReference with id [%s]\n" + "Check Table %s",
                        storageRef,
                        PDomibusConnectorBigData.TABLE_NAME
                ));
            }

            JpaBasedLargeFileReference jpaBasedDomibusConnectorBigDataReference = new JpaBasedLargeFileReference(this);
            jpaBasedDomibusConnectorBigDataReference.setStorageProviderName(this.getProviderName());

            // TODO: use stream from db!
            byte[] content = bigData.getContent();
            if (content != null) {
                // InputStream dbStream = content.getBinaryStream();
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(content);
                jpaBasedDomibusConnectorBigDataReference.setInputStream(byteArrayInputStream);
            } else {
                String error = String.format("Blob Content of bigDataStorage with reference [%d] is null!", storageRef);
                throw new IllegalStateException(error);
            }

            jpaBasedDomibusConnectorBigDataReference.setStorageIdReference(storageReference);
            jpaBasedDomibusConnectorBigDataReference.setReadable(true);
            jpaBasedDomibusConnectorBigDataReference.setWriteable(false);
            jpaBasedDomibusConnectorBigDataReference.setMimetype(bigData.getMimeType());
            jpaBasedDomibusConnectorBigDataReference.setName(bigData.getName());

            return jpaBasedDomibusConnectorBigDataReference;
        } catch (NumberFormatException nfe) {
            String error = String.format(
                    "Cannot load big data with storage reference [%s]\nThe actual implementation expects a Long as " +
                            "storage ref key!",
                    storageReference
            );
            throw new RuntimeException(error, nfe);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public LargeFileReference createDomibusConnectorBigDataReference(
            InputStream in, String connectorMessageId, String documentName, String documentContentType) {

        LOGGER.trace(
                "#createDomibusConnectorBigDataReference: called for message {} and document {}",
                connectorMessageId,
                documentName
        );

        JpaBasedLargeFileReference reference = new JpaBasedLargeFileReference(this);
        reference.setReadable(false);
        reference.setWriteable(false);

        PDomibusConnectorBigData bigData = new PDomibusConnectorBigData();

        bigData.setConnectorMessageId(connectorMessageId);
        bigData.setName(documentName);
        bigData.setMimeType(documentContentType);
        bigData.setLastAccess(new Date());

        DbBackedOutputStream outputStream = new DbBackedOutputStream(bigData);
        try {
            StreamUtils.copy(in, outputStream);
        } catch (IOException e1) {
            LOGGER.error("Exception copy streams for big data to database!", e1);
        }
        byte[] toByteArray = outputStream.toByteArray();

        // Session hibernateSession = entityManager.unwrap(Session.class);
        // Blob blob = Hibernate.getLobCreator(hibernateSession).createBlob(toByteArray);
        bigData.setContent(toByteArray);
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(toByteArray);
        bigData.setChecksum(md5DigestAsHex);

        try {
            bigData = bigDataDao.save(bigData);
        } catch (Exception e) {
            LOGGER.error("Exception saving big data to database!", e);
        }

        reference.setStorageIdReference(Long.toString(bigData.getId()));
        return reference;
    }

    @Override
    @Transactional
    public LargeFileReference createDomibusConnectorBigDataReference(
            String connectorMessageId, String documentName, String documentContentType) {
        LOGGER.trace(
                "#createDomibusConnectorBigDataReference: called for message {} and document {}",
                connectorMessageId,
                documentName
        );

        JpaBasedLargeFileReference reference = new JpaBasedLargeFileReference(this);
        reference.setStorageProviderName(this.getProviderName());
        reference.setReadable(false);
        reference.setWriteable(true);

        PDomibusConnectorBigData bigData = new PDomibusConnectorBigData();

        bigData.setConnectorMessageId(connectorMessageId);

        bigData.setName(documentName);
        bigData.setMimeType(documentContentType);
        bigData.setLastAccess(new Date());

        DbBackedOutputStream outputStream = new DbBackedOutputStream(bigData);
        byte[] toByteArray = outputStream.toByteArray();

        // Session hibernateSession = entityManager.unwrap(Session.class);
        // Blob blob = Hibernate.getLobCreator(hibernateSession).createBlob(toByteArray);
        bigData.setContent(toByteArray);
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(toByteArray);
        bigData.setChecksum(md5DigestAsHex);

        try {
            bigData = bigDataDao.save(bigData);
        } catch (Exception e) {
            LOGGER.error("Exception saving big data to database!", e);
        }

        reference.setOutputStream(outputStream);

        reference.setStorageIdReference(Long.toString(bigData.getId()));
        return reference;
    }

    @Override
    @Transactional(readOnly = false)
    public void deleteDomibusConnectorBigDataReference(LargeFileReference bigDataReference) {
        LOGGER.trace("deleteDomibusConnectorBigDataReference: called to delete all data {}", bigDataReference);
        JpaBasedLargeFileReference ref = new JpaBasedLargeFileReference(this, bigDataReference);
        long dataId = convertStorageIdReferenceToDbId(ref.getStorageIdReference());
        LOGGER.debug("Deleting big data entry with db id: [{}]", dataId);
        bigDataDao.deleteById(dataId);
    }

    @Override
    public Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences() {
        Map<DomibusConnectorMessageId, List<LargeFileReference>> map = new HashMap<>();
        Iterable<PDomibusConnectorBigData> all = bigDataDao.findAll();
        all.forEach(bigData -> {
            String messageId = bigData.getConnectorMessageId();
            DomibusConnectorMessageId connectorMessageId = new DomibusConnectorMessageId(messageId);

            if (!map.containsKey(connectorMessageId)) {
                map.put(connectorMessageId, new ArrayList<>());
            }

            List<LargeFileReference> dataRefList = map.get(connectorMessageId);
            JpaBasedLargeFileReference reference = new JpaBasedLargeFileReference(this);
            reference.setStorageProviderName(this.getProviderName());
            reference.setReadable(false);
            reference.setWriteable(false);
            reference.setStorageIdReference(Long.toString(bigData.getId()));
            reference.setCreationDate(ZonedDateTime.ofInstant(
                    bigData.getCreated().toInstant(),
                    ZoneId.systemDefault()
            ));
            dataRefList.add(reference);
        });
        return map;
    }

    private long convertConnectorMessageIdToLong(String connectorMessageId) {
        return connectorMessageId.hashCode();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected void saveOnClose(DbBackedOutputStream dbBackedOutputStream) {
        PDomibusConnectorBigData bigData = bigDataDao.findById(dbBackedOutputStream.storageReference.getId()).get();
        byte[] toByteArray = dbBackedOutputStream.toByteArray();
        // Session hibernateSession = entityManager.unwrap(Session.class);
        // Blob blob = Hibernate.getLobCreator(hibernateSession).createBlob(toByteArray);
        bigData.setContent(toByteArray);
        String md5DigestAsHex = DigestUtils.md5DigestAsHex(toByteArray);
        bigData.setChecksum(md5DigestAsHex);

        bigData.setLastAccess(new Date());

        bigDataDao.save(bigData);
    }

    private long convertStorageIdReferenceToDbId(String storageRef) {
        try {
            return Long.parseLong(storageRef);
        } catch (NumberFormatException nfe) {
            String error = String.format(
                    "The provided storage reference [%s] cannot be converted to a big data database reference",
                    storageRef
            );
            throw new IllegalStateException(error, nfe);
        }
    }

    private static class JpaBasedLargeFileReference extends LargeFileReference {
        /**
         *
         */
        private static final long serialVersionUID = -4587251476768140113L;
        private final LargeFilePersistenceServiceJpaImpl persistenceServiceJpa;
        transient InputStream inputStream;
        transient OutputStream outputStream;
        boolean readable;
        boolean writeable;

        public JpaBasedLargeFileReference(LargeFilePersistenceServiceJpaImpl persistenceServiceJpa) {
            this.persistenceServiceJpa = persistenceServiceJpa;
        }

        public JpaBasedLargeFileReference(
                LargeFilePersistenceServiceJpaImpl persistenceServiceJpa, LargeFileReference bigDataReference) {
            super(bigDataReference);
            this.persistenceServiceJpa = persistenceServiceJpa;
        }
        @Override
        public InputStream getInputStream() throws IOException {
            return this.inputStream;
        }
        @Override
        public OutputStream getOutputStream() throws IOException {
            return this.outputStream;
        }
        @Override
        public boolean isReadable() {
            return readable;
        }
        @Override
        public boolean isWriteable() {
            return writeable;
        }
        @Override
        public String getStorageProviderName() {
            return PROVIDER_NAME;
        }
        public void setWriteable(boolean writeable) {
            this.writeable = writeable;
        }
        public void setReadable(boolean readable) {
            this.readable = readable;
        }
        public void setOutputStream(OutputStream outputStream) {
            this.outputStream = outputStream;
        }
        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }
    }

    private class DbBackedOutputStream extends ByteArrayOutputStream {
        private final PDomibusConnectorBigData storageReference;
        public DbBackedOutputStream(PDomibusConnectorBigData bigData) {
            this.storageReference = bigData;
        }

        @Override
        public void flush() throws IOException {
            LOGGER.debug("called flush on DbBackedOutputStream [{}] - flush is not implemented doing nothing!", this);
            super.flush();
        }
        public String toString() {
            long byteCount = this.toByteArray() == null ? -1 : this.toByteArray().length;
            return String.format(
                    "DbBackedOutputStream with bytes [%d] and with storageRef: [%s]",
                    byteCount,
                    storageReference
            );
        }
        @Override
        public void close() throws IOException {
            LOGGER.debug("called close on DbBackedOutputStream [{}]", this);
            super.close();
            LargeFilePersistenceServiceJpaImpl.this.saveOnClose(this);
        }
    }
}
