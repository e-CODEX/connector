/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.test.util;

import static org.assertj.core.api.Assertions.assertThat;

import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.testutil.DomainEntityCreator;
import java.io.File;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

class LoadStoreMessageFromPathTest {
    @Test
    void testLoadMsg() throws IOException {
        Resource r = new ClassPathResource("testmessages/msg2/");
        DomibusConnectorMessage message = LoadStoreMessageFromPath.loadMessageFrom(r);

        assertThat(message).isNotNull();
        assertThat(message.getMessageAttachments()).hasSize(2);
        assertThat(message.getTransportedMessageConfirmations()).hasSize(1);
    }

    @Test
    void testStoreMsg() throws IOException {
        File file = new File("./target/testmsg/msg1/");
        FileSystemUtils.deleteRecursively(file);
        file.mkdirs();
        Resource r = new FileSystemResource("./target/testmsg/msg1/");
        DomibusConnectorMessage message = DomainEntityCreator.createEpoMessage();
        LoadStoreMessageFromPath.storeMessageTo(r, message);
    }
}
