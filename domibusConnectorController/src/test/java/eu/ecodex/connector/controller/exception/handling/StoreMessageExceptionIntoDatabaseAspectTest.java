/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.controller.exception.handling;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import eu.ecodex.connector.controller.exception.DomibusConnectorMessageException;
import eu.ecodex.connector.controller.exception.DomibusConnectorMessageExceptionBuilder;
import eu.ecodex.connector.controller.processor.DomibusConnectorMessageProcessor;
import eu.ecodex.connector.controller.test.util.ConnectorControllerTestDomainCreator;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageError;
import eu.ecodex.connector.persistence.service.DomibusConnectorMessageErrorPersistenceService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {
    StoreMessageExceptionIntoDatabaseAspectTest.TestContextConfiguration.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class StoreMessageExceptionIntoDatabaseAspectTest {
    @Configuration
    @EnableAspectJAutoProxy
    @ComponentScan(basePackages = "eu.ecodex.connector.controller.exception.handling")
    public static class TestContextConfiguration {
        @Bean
        public DomibusConnectorMessageErrorPersistenceService myMockedPersistenceService() {
            return Mockito.mock(DomibusConnectorMessageErrorPersistenceService.class);
        }

        @Bean("nopassException")
        public DomibusConnectorMessageProcessor messageProcessor() {
            return new NoPassExceptionMessageProcessor();
        }

        @Bean("passException")
        public DomibusConnectorMessageProcessor passExceptionMessageProcessor() {
            return new PassExceptionMessageProcessor();
        }
    }

    @Resource(name = "passException")
    DomibusConnectorMessageProcessor passExceptionProcessor;
    @Resource(name = "nopassException")
    DomibusConnectorMessageProcessor noPassExceptionProcessor;
    @Autowired
    DomibusConnectorMessageErrorPersistenceService persistenceService;

    public StoreMessageExceptionIntoDatabaseAspectTest() {
        // TODO implement this test if applicable
    }

    @Test
    void testIfExceptionIsPersisted() {
        DomibusConnectorMessage message = ConnectorControllerTestDomainCreator.createMessage();
        message.setConnectorMessageId("testid");
        try {
            passExceptionProcessor.processMessage(message);
        } catch (DomibusConnectorMessageException ex) {
            // do nothing...
        }

        // test if persistence service was called
        Mockito.verify(persistenceService, Mockito.times(1))
            .persistMessageError(eq("testid"), any(DomibusConnectorMessageError.class));
    }

    @Test
    void testPassException() {
        Assertions.assertThrows(DomibusConnectorMessageException.class, () -> {
            DomibusConnectorMessage message = ConnectorControllerTestDomainCreator.createMessage();
            message.setConnectorMessageId("testid");
            passExceptionProcessor.processMessage(message);
        });
    }

    @Test
    void testAspectNotPassException() {
        DomibusConnectorMessage message = ConnectorControllerTestDomainCreator.createMessage();
        message.setConnectorMessageId("testid");
        noPassExceptionProcessor.processMessage(message);

        // test if persistence service was called
        Mockito.verify(persistenceService, Mockito.times(1))
            .persistMessageError(eq("testid"), any(DomibusConnectorMessageError.class));
    }

    private static class NoPassExceptionMessageProcessor
        implements DomibusConnectorMessageProcessor {
        @Override
        @StoreMessageExceptionIntoDatabase
        public void processMessage(DomibusConnectorMessage message) {
            throw DomibusConnectorMessageExceptionBuilder.createBuilder()
                .setText("i am an exception!")
                .setSourceObject(this)
                .setMessage(message)
                .build();
        }
    }

    private static class PassExceptionMessageProcessor implements DomibusConnectorMessageProcessor {
        @Override
        @StoreMessageExceptionIntoDatabase(passException = true)
        public void processMessage(DomibusConnectorMessage message) {
            throw DomibusConnectorMessageExceptionBuilder.createBuilder()
                .setText("i am an exception!")
                .setSourceObject(this)
                .setMessage(message)
                .build();
        }
    }
}
