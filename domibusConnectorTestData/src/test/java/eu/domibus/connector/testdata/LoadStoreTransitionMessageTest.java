package eu.domibus.connector.testdata;

import eu.domibus.connector.domain.transition.DomibusConnectorMessageType;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;


@Log4j2
public class LoadStoreTransitionMessageTest {
    public static final String TEST_DIR = "./target/teststore/";

    @BeforeAll
    public static void beforeAll() throws IOException {
        Path p = Paths.get(TEST_DIR);
        FileSystemUtils.deleteRecursively(p);
        Files.createDirectory(p);
    }

    @Test
    void loadMessageFrom() throws Exception {
        DomibusConnectorMessageType msg1 = LoadStoreTransitionMessage.loadMessageFrom(new ClassPathResource(
                "endtoendtest/messages/epo_forma_backend_to_gw/"));

        assertThat(msg1).isNotNull();

        assertThat(msg1.getMessageAttachments()).hasSize(0);
        assertThat(msg1.getMessageContent().getXmlContent()).isNotNull();
        assertThat(msg1.getMessageContent().getDocument().getDocument()).isNotNull();
    }

    @Test
    void testStoreMessage() throws Exception {
        Path p = Paths.get(TEST_DIR).resolve("testmsg1").normalize();

        FileSystemResource resource = new FileSystemResource(p.toFile());
        DomibusConnectorMessageType testmessage = TransitionCreator.createMessage();

        LoadStoreTransitionMessage.storeMessageTo(resource, testmessage, true);
    }

    @Test
    void testStoreThanLoad() throws Exception {
        DomibusConnectorMessageType testmessage = TransitionCreator.createMessage();

        Path p = Paths.get(TEST_DIR).resolve("testmsg2").normalize();
        LoadStoreTransitionMessage.storeMessageTo(p, testmessage, true);

        DomibusConnectorMessageType testmsg2 = LoadStoreTransitionMessage.loadMessageFrom(p);

        assertThat(testmsg2.getMessageDetails()).isEqualToComparingFieldByFieldRecursively(testmessage.getMessageDetails());
        assertThat(testmsg2.getMessageContent().getXmlContent()).isNotNull();

        Path p2 = Paths.get(TEST_DIR).resolve("testmsg3");
        LoadStoreTransitionMessage.storeMessageTo(p2, testmsg2, true);
    }

    //    @Test
    //    public void testStoreThenLoadChangeThenStore() throws Exception {
    //        DomibusConnectorMessageType testmessage = TransitionCreator.createMessage();
    //
    //        Path p = Paths.get(TEST_DIR).resolve("testmsg4");
    //        LoadStoreTransitionMessage.storeMessageTo(p, testmessage, true);
    //
    //        DomibusConnectorMessageType testmsg2 = LoadStoreTransitionMessage.loadMessageFrom(p);
    //
    //        assertThat(testmsg2.getMessageDetails()).isEqualToComparingFieldByFieldRecursively(testmessage
    //        .getMessageDetails());
    //        assertThat(testmsg2.getMessageContent().getXmlContent()).isNotNull();
    //
    //        testmsg2.getMessageConfirmations().add(TransitionCreator.createMessageConfirmationType_NON_DELIVERY());
    //
    //        LoadStoreTransitionMessage.storeMessageTo(p, testmsg2, true);
    //    }
}
