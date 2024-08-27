/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.persistence.largefiles.provider;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.model.LargeFileReference;
import eu.domibus.connector.persistence.service.exceptions.LargeFileDeletionException;
import eu.domibus.connector.persistence.service.exceptions.LargeFileException;
import eu.domibus.connector.persistence.service.exceptions.PersistenceException;
import eu.domibus.connector.persistence.spring.DomibusConnectorFilesystemPersistenceProperties;
import eu.domibus.connector.persistence.spring.DomibusConnectorPersistenceProperties;
import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.util.StreamUtils;

/**
 * Implementation of the LargeFilePersistenceProvider interface for storing and retrieving large
 * file references using the filesystem as the storage system.
 */
@ConditionalOnProperty(
    prefix = DomibusConnectorPersistenceProperties.PREFIX,
    value = "provider-" + LargeFilePersistenceServiceFilesystemImpl.PROVIDER_NAME,
    havingValue = "true", matchIfMissing = true
)
@SuppressWarnings("squid:S1135")
@Service
@Setter
@Getter
public class LargeFilePersistenceServiceFilesystemImpl implements LargeFilePersistenceProvider {
    public static final String PROVIDER_NAME = "filesystem";
    private static final Logger LOGGER =
        LoggerFactory.getLogger(LargeFilePersistenceServiceFilesystemImpl.class);
    @Autowired
    DomibusConnectorFilesystemPersistenceProperties filesystemPersistenceProperties;

    @Override
    public String getProviderName() {
        return PROVIDER_NAME;
    }

    @Override
    public LargeFileReference getReadableDataSource(LargeFileReference ref) {
        if (ref instanceof FileBasedLargeFileReference largeFileReference
            && largeFileReference.inputStream != null) {
            try {
                ((FileBasedLargeFileReference) ref).inputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
        var fileBasedReference = new FileBasedLargeFileReference(this, ref);
        fileBasedReference.setReadable(true);
        return fileBasedReference;
    }

    private InputStream getInputStream(FileBasedLargeFileReference ref) {
        var storageIdReference = ref.getStorageIdReference();
        var filePath = getStoragePath().resolve(storageIdReference);
        try {
            var fis = new FileInputStream(filePath.toFile());
            if (ref.getEncryptionKey() != null) {
                return generateDecryptedInputStream(ref, fis);
            } else {
                return fis;
            }
        } catch (FileNotFoundException e) {
            throw new PersistenceException(
                String.format("Could not found the required file [%s]!", filePath), e);
        }
    }

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        String connectorMessageId, String documentName, String documentContentType) {
        return createDomibusConnectorBigDataReference(
            null, connectorMessageId, documentName, documentContentType);
    }

    @Override
    public LargeFileReference createDomibusConnectorBigDataReference(
        InputStream input, String connectorMessageId, String documentName,
        String documentContentType) {
        var bigDataReference = new FileBasedLargeFileReference(this);
        bigDataReference.setName(documentName);
        bigDataReference.setMimetype(documentContentType);
        bigDataReference.setStorageProviderName(this.getProviderName());

        var messageFolderName = connectorMessageId;
        Path messageFolder = getStoragePath().resolve(messageFolderName);
        try {
            LOGGER.debug("Creating message folder [{}]", messageFolder);
            Files.createDirectory(messageFolder);
        } catch (java.nio.file.FileAlreadyExistsException alreadyExists) {
            if (!Files.isDirectory(messageFolder)) {
                throw new RuntimeException(
                    String.format("Cannot use directory path [%s] because it is a file!"));
            }
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Cannot create directory [%s]", messageFolder), e);
        }

        String storageFileName = simpleDateFormat.format(new Date()) + UUID.randomUUID();
        String storageFileRelativePathName =
            createReferenceName(messageFolderName, storageFileName);
        bigDataReference.setStorageIdReference(storageFileRelativePathName);

        Path storageFile = messageFolder.resolve(storageFileName);
        LOGGER.debug("Storage file path is [{}]", storageFile.toAbsolutePath());

        try {
            Files.createFile(storageFile);
        } catch (FileAlreadyExistsException alreadyExistsException) {
            throw new PersistenceException(String.format(
                "Error while creating file [%s], looks like the file has already written! "
                    + "You can only write once to a bigDataReference OutputStream!",
                storageFile
            ), alreadyExistsException);
        } catch (IOException e) {
            throw new PersistenceException(
                String.format("Error while creating file [%s]", storageFile), e);
        }

        if (input != null) {
            try (var os = getOutputStream(bigDataReference)) {
                StreamUtils.copy(input, os);
                bigDataReference.setOutputStream(os);
            } catch (FileNotFoundException e) {
                throw new PersistenceException(
                    String.format(
                        "Error while creating FileOutputStream for file [%s]",
                        storageFile
                    ), e);
            } catch (IOException e) {
                throw new PersistenceException(
                    String.format("Error while writing to file [%s]", storageFile), e);
            }
        } else {
            try {
                bigDataReference.setOutputStream(getOutputStream(bigDataReference));
            } catch (FileNotFoundException e) {
                throw new PersistenceException(
                    String.format(
                        "Error while creating FileOutputStream for file [%s]",
                        storageFile
                    ), e);
            }
        }

        return bigDataReference;
    }

    OutputStream getOutputStream(FileBasedLargeFileReference dataReference)
        throws FileNotFoundException {
        Path storageFile = getStoragePath().resolve(dataReference.getStorageIdReference());
        LOGGER.debug("Storage file path is [{}]", storageFile.toAbsolutePath());

        if (!Files.exists(storageFile)) {
            throw new PersistenceException(String.format(
                "The requested file [%s] does not exist yet! Looks like this method is not called "
                    + "correctly!",
                storageFile
            ));
        }

        var fos = new FileOutputStream(storageFile.toFile());
        OutputStream outputStream;
        if (filesystemPersistenceProperties.isEncryptionActive()) {
            LOGGER.debug("Encryption is activated creating encrypted output stream");
            outputStream = generateEncryptedOutputStream(dataReference, fos);
            return outputStream;
        }
        return fos;
    }

    @Override
    public void deleteDomibusConnectorBigDataReference(LargeFileReference ref) {
        FileBasedLargeFileReference reference;
        LOGGER.trace("#deleteDomibusConnectorBigDataReference:: called with reference [{}]", ref);
        Path storageFile = getStoragePath().resolve(ref.getStorageIdReference());
        if ((ref instanceof FileBasedLargeFileReference largeFileReference)) {
            reference = largeFileReference;
        } else {
            reference = new FileBasedLargeFileReference(this, ref);
        }
        if (reference.inputStream != null) {
            try {
                reference.inputStream.close();
            } catch (IOException e) {
                // ignore
            }
        }
        try {
            Files.delete(storageFile);
        } catch (IOException e) {
            var largeFileDeletionException = new LargeFileDeletionException(
                String.format("Unable to delete file [%s] due exception:", storageFile), e);
            largeFileDeletionException.setReferenceFailedToDelete(reference);
            throw largeFileDeletionException;
        } finally {
            storageFile = null;
        }
        deleteFolderIfEmpty(reference); // check if this runs on nfs share!
    }

    private void deleteFolderIfEmpty(FileBasedLargeFileReference reference) {
        String folderName = getFolderNameFromReferenceName(reference.getStorageIdReference());
        var messagePath = getStoragePath().resolve(folderName);
        try {
            Files.delete(messagePath);
            LOGGER.debug("#deleteFolderIfEmpty:: Directory [{}] deleted", messagePath);
        } catch (DirectoryNotEmptyException notEmpty) {
            LOGGER.debug(
                "#deleteFolderIfEmpty:: Directory [{}] is not empty - will no be deleted!",
                messagePath
            );
        } catch (IOException e) {
            LOGGER.warn(
                "#deleteFolderIfEmpty:: An IOException occurred while trying to delete directory ["
                    + messagePath + "]", e);
        }
    }

    @Override
    public Map<DomibusConnectorMessageId, List<LargeFileReference>> getAllAvailableReferences() {
        var storagePath = getStoragePath();
        try (Stream<Path> files = Files.list(storagePath)) {
            return files.filter(p -> !p.startsWith(".")) // exclude hidden files on Unix
                        .collect(Collectors.toMap(
                            path -> new DomibusConnectorMessageId(path.getFileName().toString()),
                            this::listReferences
                        ));
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Error while ls files in directory [%s]", storagePath));
        }
    }

    private List<LargeFileReference> listReferences(Path messageFolder) {
        var messageFolderName = messageFolder.getFileName().toString();
        try (Stream<Path> files = Files.list(messageFolder)) {
            return files
                .filter(p -> !p.startsWith(".")) // filter hidden unix files
                .map(p -> p.getFileName().toString())
                .map(s -> mapMessageFolderAndFileNameToReference(messageFolderName, s))
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(
                String.format("Error while listing all files in messageFolder [%s]", messageFolder),
                e
            );
        }
    }

    private LargeFileReference mapMessageFolderAndFileNameToReference(
        String messageFolderName, String fileName) {
        String storageIdRef = createReferenceName(messageFolderName, fileName);
        var ref = new FileBasedLargeFileReference(this);
        var filePath = getStoragePath().resolve(storageIdRef);
        try {
            ref.setCreationDate(
                Files.getLastModifiedTime(filePath).toInstant().atZone(ZoneId.systemDefault()));
        } catch (IOException e) {
            throw new LargeFileException(
                String.format("Unable to read file reference [%s]", storageIdRef), e);
        }
        ref.setStorageIdReference(storageIdRef);
        return ref;
    }

    private String createReferenceName(String messageFolderName, String fileName) {
        return messageFolderName + File.separator + fileName;
    }

    private String getFolderNameFromReferenceName(String referenceName) {
        int separatorPos = referenceName.indexOf(File.separator, 0);
        return referenceName.substring(0, separatorPos);
    }

    private Path getStoragePath() {
        return filesystemPersistenceProperties.getStoragePath();
    }

    /**
     * Initializes the storage path for the file system persistence provider. It checks if the path
     * is writable and creates the directory if it does not exist. If the directory creation is
     * disabled and the path does not exist, it throws an IllegalArgumentException.
     */
    @PostConstruct
    public void init() {
        // TODO: check: path writable?
        var storagePath = filesystemPersistenceProperties.getStoragePath();
        var file = storagePath.toFile();
        if (!file.exists() && filesystemPersistenceProperties.isCreateDir()) {
            LOGGER.info("Creating missing directory path [{}]", storagePath);
            file.mkdirs();
        } else if (!file.exists()) {
            throw new IllegalArgumentException(String.format(
                "The by configuration (%s) provided file path [%s] does not exist an file path "
                    + "creation (%s) is false!",
                "connector.persistence.filesystem.storage-path",
                // TODO: call property service for correct property name
                storagePath,
                "connector.persistence.filesystem.create-dir"
            )); // TODO: call property service for correct property name
        }
    }

    InputStream generateDecryptedInputStream(
        FileBasedLargeFileReference bigDataReference, InputStream encryptedInputStream) {
        var ivspec =
            new IvParameterSpec(Base64Utils.decodeFromString(bigDataReference.getInitVector()));
        var secretKey = loadFromKeyString(bigDataReference.getEncryptionKey());

        Cipher cipher;
        try {
            cipher = Cipher.getInstance(bigDataReference.getCipherSuite());
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return new CipherInputStream(encryptedInputStream, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    OutputStream generateEncryptedOutputStream(
        FileBasedLargeFileReference bigDataReference, OutputStream outputStream) {
        var random = new SecureRandom();
        var iv = new byte[128 / 8];
        random.nextBytes(iv);
        var ivspec = new IvParameterSpec(iv);

        var initVector = Base64Utils.encodeToString(ivspec.getIV());
        bigDataReference.setInitVector(initVector);

        SecretKey secretKey;
        try {
            var kg = KeyGenerator.getInstance("AES"); // TODO: load from properties
            kg.init(random);
            secretKey = kg.generateKey();
            // TODO: also put configurable part of key there - to avoid having the whole key stored
            //  into the database!
            bigDataReference.setEncryptionKey(
                convertSecretKeyToString(
                    secretKey
                )
            );
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Cannot initialize Key Generator!");
        }

        Cipher cipher;
        try {
            var cipherSuite = "AES/CBC/PKCS5Padding"; // TODO: load from properties
            cipher = Cipher.getInstance(cipherSuite);
            bigDataReference.setCipherSuite(cipherSuite);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return new CipherOutputStream(outputStream, cipher);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException
                 | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convert a SecretKey object to a string representation.
     *
     * @param key The SecretKey object to convert.
     * @return A string representation of the SecretKey, in the format
     *      "algorithm#@#base64EncodedKey".
     */
    public String convertSecretKeyToString(SecretKey key) {
        String alg = key.getAlgorithm();
        var base64KeyString = Base64Utils.encodeToString(key.getEncoded());
        return alg + "#@#" + base64KeyString;
    }

    SecretKey loadFromKeyString(String str) {
        String[] split = str.split("#@#");
        if (split.length != 2) {
            throw new IllegalArgumentException(String.format(
                "The provided string [%s] does not match the format! Maybe the data is corrupted!",
                str
            ));
        }
        String keyAlgorithm = split[0];
        byte[] keyBinary = Base64Utils.decodeFromString(split[1]);
        return new SecretKeySpec(keyBinary, keyAlgorithm);
    }

    /**
     * BigDataPersistenceServiceFilesystemImpl implementation of BigDataReference this class is
     * internal api do not use this class outside the BigDataPersistenceServiceFilesystemImpl.
     */
    @Getter
    @Setter
    static final class FileBasedLargeFileReference extends LargeFileReference {
        Charset charset = StandardCharsets.UTF_8;
        private static final long serialVersionUID = 1;
        transient LargeFilePersistenceServiceFilesystemImpl fsService;
        transient InputStream inputStream;
        transient OutputStream outputStream;
        boolean readable;
        boolean writeable;
        private String encryptionKey;
        private String initVector;
        private String cipherSuite;

        public FileBasedLargeFileReference(LargeFilePersistenceServiceFilesystemImpl fsService) {
            this.fsService = fsService;
        }

        public FileBasedLargeFileReference(
            LargeFilePersistenceServiceFilesystemImpl fsService, LargeFileReference ref) {
            super(ref);
            this.fsService = fsService;
            if (!StringUtils.isEmpty(ref.getText())) {
                String[] s = ref.getText().split("__");
                encryptionKey = new String(Base64Utils.decodeFromString(s[0]), charset);
                initVector = new String(Base64Utils.decodeFromString(s[1]), charset);
                cipherSuite = new String(Base64Utils.decodeFromString(s[2]), charset);
            }
        }

        @Override
        public String getStorageProviderName() {
            return PROVIDER_NAME;
        }

        @Override
        public synchronized InputStream getInputStream() throws IOException {
            if (this.inputStream != null) {
                return this.inputStream;
            }
            if (isReadable()) {
                this.setReadable(false);
                this.inputStream = fsService.getInputStream(this);
                return this.inputStream;
            } else {
                throw new IOException("Input Stream already consumed");
            }
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
        public String getText() {
            if (encryptionKey == null) {
                return "";
            }
            return Base64Utils.encodeToString(encryptionKey.getBytes(charset))
                + "__"
                + Base64Utils.encodeToString(initVector.getBytes(charset))
                + "__"
                + Base64Utils.encodeToString(cipherSuite.getBytes(charset));
        }
    }
}
