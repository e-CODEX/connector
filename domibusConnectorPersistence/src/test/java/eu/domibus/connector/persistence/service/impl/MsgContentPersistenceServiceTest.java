package eu.domibus.connector.persistence.service.impl;

import eu.domibus.connector.domain.model.*;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDocumentBuilder;
import eu.domibus.connector.domain.test.util.DomainEntityCreatorForPersistenceTests;
import eu.domibus.connector.domain.transformer.util.LargeFileReferenceMemoryBacked;
import eu.domibus.connector.persistence.dao.DomibusConnectorMessageDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorMsgContDao;
import eu.domibus.connector.persistence.model.PDomibusConnectorMessage;
import eu.domibus.connector.persistence.model.PDomibusConnectorMsgCont;
import eu.domibus.connector.persistence.model.test.util.PersistenceEntityCreator;
import eu.domibus.connector.persistence.service.impl.helper.StoreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;


/**
 *
 */
@Disabled("repair test")
@ExtendWith(SpringExtension.class)
class MsgContentPersistenceServiceTest {
    @InjectMocks
    MsgContentPersistenceService msgContService;
    @Mock
    private DomibusConnectorMsgContDao msgContDao;
    @Mock
    private DomibusConnectorMessageDao msgDao;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @Disabled("todo repair")
    void testLoadMsgContent() {
        List<PDomibusConnectorMsgCont> list = new ArrayList<>();
        list.add(createTestMsgContentWithMessageContent());
        list.add(createTestMsgContWithMessageAttachment());
        list.add(createTestMsgContWithMessageAttachment());
        list.add(createTestMsgContWithMessageConfirmation());
        //        Mockito.when(msgContDao.findByMessage(any(PDomibusConnectorMessage.class))).thenReturn(list);

        PDomibusConnectorMessage dbMessage = PersistenceEntityCreator.createSimpleDomibusConnectorMessage();

        DomibusConnectorMessageBuilder messageBuilder = DomibusConnectorMessageBuilder.createBuilder();
        messageBuilder.setConnectorMessageId("id1");
        messageBuilder.setMessageDetails(new DomibusConnectorMessageDetails());
        msgContService.loadMessagePayloads(messageBuilder, dbMessage);

        DomibusConnectorMessage message = messageBuilder.build();
        DomibusConnectorMessageContent messageContent = message.getMessageContent();

        assertThat(messageContent).as("testdata has an message content").isNotNull();

        assertThat(message.getMessageAttachments()).as("appended 2 attachments in test data").hasSize(2);

        assertThat(message.getTransportedMessageConfirmations()).as("appended 1 delivery confirmation").hasSize(1);
    }

    @Test
    @Disabled("todo repair")
    void testStoreMsgContent() {
        DomibusConnectorMessage message = DomainEntityCreatorForPersistenceTests.createMessage("msgid");
        final List<PDomibusConnectorMsgCont> savedMsgCont = new ArrayList<>();

        PDomibusConnectorMessage dbMessage = PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
        Mockito.when(msgDao.findOneByConnectorMessageId(eq("msgid"))).thenReturn(Optional.of(dbMessage));

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocationOnMock) throws Throwable {
                Iterable iter = invocationOnMock.getArgument(0);
                iter.forEach(c -> savedMsgCont.add((PDomibusConnectorMsgCont) c));
                return null;
            }
        }).when(this.msgContDao).saveAll(any());

        //        this.msgContService.saveMessagePayloads(message);

        //        Mockito.verify(msgContDao, times(1)).save(any(PDomibusConnectorMsgCont.class));

        assertThat(savedMsgCont.stream()
                               .filter(c -> StoreType.MESSAGE_CONTENT.equals(c.getContentType()))
                               .count()).as("There should be one MessageContent").isEqualTo(1);

        //        Mockito.verify(msgContDao, times(1)).deleteByMessage(eq(dbMessage));
    }

    @Test
    @Disabled("todo repair")
    void testStoreMsgContent_noDocument() {
        DomibusConnectorMessage message = DomainEntityCreatorForPersistenceTests.createMessage("msgid");
        message.getMessageContent().setDocument(null); // there is no main document!

        final List<PDomibusConnectorMsgCont> savedMsgCont = new ArrayList<>();

        PDomibusConnectorMessage dbMessage = PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
        Mockito.when(msgDao.findOneByConnectorMessageId(eq("msgid"))).thenReturn(Optional.of(dbMessage));

        Mockito.doAnswer(new Answer<Void>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Iterable iter = invocation.getArgument(0);
                iter.forEach(c -> savedMsgCont.add((PDomibusConnectorMsgCont) c));
                return null;
            }
        }).when(this.msgContDao).saveAll(any());

        //        this.msgContService.saveMessagePayloads(message);

        assertThat(savedMsgCont.stream()
                               .filter(c -> StoreType.MESSAGE_CONTENT.equals(c.getContentType()))
                               .count()).as("There should be one MessageContent").isEqualTo(1);

        //        Mockito.verify(msgContDao, times(1)).deleteByMessage(eq(dbMessage));
    }

    //    @Test
    //    public void testMapToMsgCont_withMessageContent() {
    //        PDomibusConnectorMessage dbMessage = PersistenceEntityCreator.createSimpleDomibusConnectorMessage();
    //        PDomibusConnectorMsgCont mapToMsgCont = this.msgContService.storeObjectIntoMsgCont(dbMessage, StoreType
    //        .MESSAGE_CONTENT, createTestMessageContent());
    //
    //        assertThat(mapToMsgCont.getContent()).isNotNull();
    //        assertThat(mapToMsgCont.getContentType()).isEqualTo(StoreType.MESSAGE_CONTENT);
    //        assertThat(mapToMsgCont.getChecksum()).isNotNull();
    //        assertThat(mapToMsgCont.getMessage()).isEqualTo(dbMessage);
    //
    //    }

    private PDomibusConnectorMsgCont createTestMsgContentWithMessageContent() {
        PDomibusConnectorMsgCont cont = new PDomibusConnectorMsgCont();
        cont.setContent(writeToByte(createTestMessageContent()));
        cont.setContentType(StoreType.MESSAGE_CONTENT);
        cont.setChecksum("");
        return cont;
    }

    private PDomibusConnectorMsgCont createTestMsgContWithMessageAttachment() {
        PDomibusConnectorMsgCont cont = new PDomibusConnectorMsgCont();
        DomibusConnectorMessageAttachment attachment =
                DomainEntityCreatorForPersistenceTests.createSimpleMessageAttachment();
        cont.setContent(writeToByte(attachment));
        cont.setContentType(StoreType.MESSAGE_ATTACHMENT);
        cont.setChecksum("");
        return cont;
    }

    private PDomibusConnectorMsgCont createTestMsgContWithMessageConfirmation() {
        PDomibusConnectorMsgCont cont = new PDomibusConnectorMsgCont();
        DomibusConnectorMessageConfirmation confirmation =
                DomainEntityCreatorForPersistenceTests.createMessageDeliveryConfirmation();
        cont.setContent(writeToByte(confirmation));
        cont.setContentType(StoreType.MESSAGE_CONFIRMATION);
        cont.setChecksum("");
        return cont;
    }

    private DomibusConnectorMessageContent createTestMessageContent() {
        DomibusConnectorMessageContent messageContent = new DomibusConnectorMessageContent();
        messageContent.setXmlContent("<xmlContent></xmlContent>".getBytes());

        DomibusConnectorMessageDocumentBuilder documentBuilder = DomibusConnectorMessageDocumentBuilder.createBuilder();
        documentBuilder.setContent(new LargeFileReferenceMemoryBacked("documentContent".getBytes()));
        documentBuilder.setName("docname");
        messageContent.setDocument(documentBuilder.build());
        return messageContent;
    }

    private byte[] writeToByte(Object obj) {
        ObjectOutputStream out = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            out = new ObjectOutputStream(byteOut);
            out.writeObject(obj);
            byte[] toByteArray = byteOut.toByteArray();
            return toByteArray;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    //    @Test
    //    public void test_mapFromMsgCont_withMessageContent() {
    //        PDomibusConnectorMsgCont cont = createTestMsgContentWithMessageContent();
    //
    //        DomibusConnectorMessageContent mapFromMsgCont = this.msgContService.mapFromMsgCont(cont,
    //        DomibusConnectorMessageContent.class);
    //
    //        assertThat(mapFromMsgCont.getXmlContent()).isEqualTo("<xmlContent></xmlContent>".getBytes());
    //        //assertThat(mapFromMsgCont.getDocument().getDocument()).isEqualTo("documentContent".getBytes());
    //    }

    //    @Test
    //    public void testMapContentMessageAttachment_withNotSerializeableDataReference(){
    //        DomibusConnectorMessageId messageId = new DomibusConnectorMessageId("abc21");
    //
    //        NotSerializableBigDataReference dataRef = new NotSerializableBigDataReference();
    //        dataRef.setInputStream(new ByteArrayInputStream("documentContent".getBytes()));
    //        dataRef.setStorageIdReference(UUID.randomUUID().toString());
    //
    //        DomibusConnectorMessageAttachment attachment = DomibusConnectorMessageAttachmentBuilder.createBuilder()
    //                .setAttachment(dataRef)
    //                .setIdentifier("ASIC-S")
    //                .build();
    //
    //        this.msgContService.mapAttachment(messageId, attachment);
    //    }

    private static class NotSerializableBigDataReference extends LargeFileReference {
        InputStream inputStream;
        OutputStream outputStram;

        @Override
        public InputStream getInputStream() {
            return inputStream;
        }

        public void setInputStream(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public OutputStream getOutputStream() {
            return outputStram;
        }

        public void setOutputStream(OutputStream outputStram) {
            this.outputStram = outputStram;
        }
    }
}
