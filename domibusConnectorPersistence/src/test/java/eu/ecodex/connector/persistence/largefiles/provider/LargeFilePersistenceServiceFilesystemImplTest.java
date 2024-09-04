/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.largefiles.provider;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.model.LargeFileReference;
import eu.ecodex.connector.persistence.largefiles.provider.LargeFilePersistenceServiceFilesystemImpl.FileBasedLargeFileReference;
import eu.ecodex.connector.persistence.spring.DomibusConnectorFilesystemPersistenceProperties;
import eu.ecodex.connector.testutil.assertj.DomibusByteArrayAssert;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.util.Base64Utils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

class LargeFilePersistenceServiceFilesystemImplTest {
    private LargeFilePersistenceServiceFilesystemImpl filesystemImpl;
    private static final byte[] input1 = "Hallo Welt, ich bin ein Testtext".getBytes();
    private File testStorageLocation;

    @BeforeEach
    public void setUp(TestInfo testInfo) throws IOException {

        String methodName = testInfo.getTestMethod().get().getName();

        testStorageLocation = new File(
            "./target/tests/"
                + LargeFilePersistenceServiceFilesystemImplTest.class.getSimpleName()
                + "/"
                + methodName
                + "/fsstorage/");

        FileSystemUtils.deleteRecursively(testStorageLocation);
        testStorageLocation.mkdirs();

        File src = new File("./target/test-classes/testdata/fsstorage/");

        // copy testdata to testfolder
        FileSystemUtils.copyRecursively(src, testStorageLocation);

        DomibusConnectorFilesystemPersistenceProperties fsProps =
            new DomibusConnectorFilesystemPersistenceProperties();
        fsProps.setStoragePath(Paths.get(testStorageLocation.getAbsolutePath()));
        fsProps.setEncryptionActive(true);

        filesystemImpl = new LargeFilePersistenceServiceFilesystemImpl();
        filesystemImpl.setFilesystemPersistenceProperties(fsProps);
    }

    @Test
    void getReadableDataSource() throws IOException {
        LargeFilePersistenceServiceFilesystemImpl.FileBasedLargeFileReference fsRef =
            new LargeFilePersistenceServiceFilesystemImpl.FileBasedLargeFileReference(
                filesystemImpl);
        fsRef.setStorageIdReference("testmsg1" + File.separator + "file1");
        fsRef.setName("file1");
        fsRef.setMimetype("text");
        // fsRef.setEncryptionKey();FileBasedDomibusConnectorBigDataReference

        LargeFileReference readableDataSource = filesystemImpl.getReadableDataSource(fsRef);

        assertThat(readableDataSource).isNotNull();
        assertThat(readableDataSource.getInputStream()).isNotNull();

        byte[] bytes = StreamUtils.copyToByteArray(readableDataSource.getInputStream());
        DomibusByteArrayAssert.assertThat(bytes).containsUTF8String("Hallo Welt!");
    }

    @SuppressWarnings("squid:S1135")
    @Test
    void createDomibusConnectorBigDataReference() {
        LargeFileReference largeFileReference =
            filesystemImpl.createDomibusConnectorBigDataReference(
                new ByteArrayInputStream(input1),
                "msg1",
                "file1",
                "text/utf-8"
            );
        assertThat(largeFileReference).isNotNull();

        String storageIdReference = largeFileReference.getStorageIdReference();
        assertThat(storageIdReference).isNotNull();

        // TODO: assert file exists in FS
        File f = new File(testStorageLocation + File.separator + storageIdReference);
        assertThat(f).as(String.format("A file <%s> should exist", f.getAbsolutePath()))
                              .exists();

        FileBasedLargeFileReference fileReference =
            (FileBasedLargeFileReference) largeFileReference;
        System.out.println(
            "key: " + fileReference.getEncryptionKey() + " iv: " + fileReference.getInitVector()
                + " cipher-suite: " + fileReference.getCipherSuite());
    }

    @Test
    void readEncryptedBigDataReference() throws IOException {
        FileBasedLargeFileReference largeFileReference =
            new FileBasedLargeFileReference(filesystemImpl);
        largeFileReference.setEncryptionKey("AES#@#kC6lanKld+xuiVfarsZNLQ==");
        largeFileReference.setInitVector("cO+U0ufVjzCheGnXYkfvXg==");
        largeFileReference.setCipherSuite("AES/CBC/PKCS5Padding");
        largeFileReference.setStorageIdReference("testmsg2/2de3f474-3f1a-42d7-ab7c-2151590c77f1");

        LargeFileReference readableDataSource =
            filesystemImpl.getReadableDataSource(largeFileReference);

        InputStream is = readableDataSource.getInputStream();
        byte[] bytes = StreamUtils.copyToByteArray(is);

        assertThat(bytes).isEqualTo(input1);
    }

    @Test
    void deleteDomibusConnectorBigDataReference() {
        String msgId = "testmsg2";
        FileBasedLargeFileReference fsRef =
            new LargeFilePersistenceServiceFilesystemImpl.FileBasedLargeFileReference(
                filesystemImpl);
        String storageRef = msgId + File.separator + "file1";
        fsRef.setStorageIdReference(storageRef);

        filesystemImpl.deleteDomibusConnectorBigDataReference(fsRef);

        String filePath =
            testStorageLocation.getAbsolutePath() + File.separator + msgId + File.separator
                + "file1";
        File f = new File(filePath);
        assertThat(f).as("file " + filePath + " should be deleted").doesNotExist();
    }

    @Test
    void testConvertSecretKeyToString() throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        KeyGenerator kg = KeyGenerator.getInstance("AES");
        kg.init(random);

        var secretKeySpec = new SecretKeySpec(
            Base64Utils.decodeFromString("sJ0kZ3pVBcG75ar4ADWwgg=="),
            "AES"
        );

        String secretKeyToString = filesystemImpl.convertSecretKeyToString(secretKeySpec);

        assertThat(secretKeyToString).isEqualTo("AES#@#sJ0kZ3pVBcG75ar4ADWwgg==");
    }

    @Test
    void testGetAllAvailableReferences() {
        Map<DomibusConnectorMessageId, List<LargeFileReference>> allAvailableReferences =
            filesystemImpl.getAllAvailableReferences();

        assertThat(allAvailableReferences).hasSize(2);
        assertThat(allAvailableReferences.get(new DomibusConnectorMessageId("testmsg2"))).hasSize(
            2);
    }
}
