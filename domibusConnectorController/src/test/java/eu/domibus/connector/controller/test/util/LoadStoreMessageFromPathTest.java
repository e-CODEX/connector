package eu.domibus.connector.controller.test.util;

import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.testutil.DomainEntityCreator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.FileSystemUtils;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


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
