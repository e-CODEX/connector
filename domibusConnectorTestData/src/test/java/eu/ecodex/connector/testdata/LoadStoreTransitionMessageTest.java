/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.testdata;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.transition.DomibusConnectorMessageType;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.FileSystemUtils;

/**
 * The LoadStoreTransitionMessageTest class is a unit test class for the LoadStoreTransitionMessage
 * class. It contains test methods for loading and storing DomibusConnectorMessageType objects to
 * and from files.
 *
 * <p>This class uses JUnit 5 and log4j2 for logging. Before running the tests, it creates a test
 * directory and deletes its contents if it already exists.
 */
@SuppressWarnings("squid:S1135")
@Log4j2
public class LoadStoreTransitionMessageTest {
    public static final String TEST_DIR = "./target/teststore/";

    /**
     * Executes the specified code block before all test methods in the test suite.
     */
    @BeforeAll
    public static void beforeAll() throws IOException {
        Path p = Paths.get(TEST_DIR);
        FileSystemUtils.deleteRecursively(p);
        Files.createDirectory(p);
    }

    @Test
    void loadMessageFrom() {
        DomibusConnectorMessageType msg1 = LoadStoreTransitionMessage.loadMessageFrom(
            new ClassPathResource("endtoendtest/messages/epo_forma_backend_to_gw/"));

        assertThat(msg1).isNotNull();

        assertThat(msg1.getMessageAttachments()).isEmpty();
        assertThat(msg1.getMessageContent().getXmlContent()).isNotNull();
        assertThat(msg1.getMessageContent().getDocument().getDocument()).isNotNull();
    }

    @Test
    void testStoreMessage() {
        Path p = Paths.get(TEST_DIR).resolve("testmsg1").normalize();

        FileSystemResource resource = new FileSystemResource(p.toFile());
        DomibusConnectorMessageType testMessage = TransitionCreator.createMessage();

        LoadStoreTransitionMessage.storeMessageTo(resource, testMessage, true);
    }

    @Test
    void testStoreThanLoad() {
        DomibusConnectorMessageType testMessage = TransitionCreator.createMessage();

        Path p = Paths.get(TEST_DIR).resolve("testmsg2").normalize();
        LoadStoreTransitionMessage.storeMessageTo(p, testMessage, true);

        DomibusConnectorMessageType testmsg2 = LoadStoreTransitionMessage.loadMessageFrom(p);

        assertThat(testmsg2.getMessageDetails()).isEqualToComparingFieldByFieldRecursively(
            testMessage.getMessageDetails());
        assertThat(testmsg2.getMessageContent().getXmlContent()).isNotNull();

        Path p2 = Paths.get(TEST_DIR).resolve("testmsg3");
        LoadStoreTransitionMessage.storeMessageTo(p2, testmsg2, true);
    }

    // TODO see why this test is commented
    // @Test
    // public void testStoreThenLoadChangeThenStore() throws Exception {
    //     DomibusConnectorMessageType testMessage = TransitionCreator.createMessage();
    //
    //     Path p = Paths.get(TEST_DIR).resolve("testmsg4");
    //     LoadStoreTransitionMessage.storeMessageTo(p, testMessage, true);
    //
    //     DomibusConnectorMessageType testmsg2 = LoadStoreTransitionMessage.loadMessageFrom(p);
    //
    //     assertThat(testmsg2.getMessageDetails()).isEqualToComparingFieldByFieldRecursively(
    //         testMessage.getMessageDetails());
    //     assertThat(testmsg2.getMessageContent().getXmlContent()).isNotNull();
    //
    //     testmsg2.getMessageConfirmations()
    //             .add(TransitionCreator.createMessageConfirmationType_NON_DELIVERY());
    //
    //     LoadStoreTransitionMessage.storeMessageTo(p, testmsg2, true);
    // }
}
