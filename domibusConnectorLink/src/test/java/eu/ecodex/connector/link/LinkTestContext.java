/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link;

import eu.ecodex.connector.common.configuration.ConnectorConfigurationProperties;
import eu.ecodex.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.ecodex.connector.common.service.ConfigurationPropertyLoaderServiceImpl;
import eu.ecodex.connector.common.service.DCBusinessDomainManagerImpl;
import eu.ecodex.connector.common.service.DCKeyStoreService;
import eu.ecodex.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.ecodex.connector.controller.routing.DCRoutingRulesManager;
import eu.ecodex.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.ecodex.connector.controller.service.SubmitToConnector;
import eu.ecodex.connector.controller.service.TransportStateService;
import eu.ecodex.connector.domain.enums.LinkType;
import eu.ecodex.connector.domain.model.DomibusConnectorLinkPartner;
import eu.ecodex.connector.domain.model.DomibusConnectorMessage;
import eu.ecodex.connector.domain.model.DomibusConnectorMessageId;
import eu.ecodex.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.ecodex.connector.lib.spring.configuration.validation.HelperMethods;
import eu.ecodex.connector.link.common.MerlinPropertiesFactory;
import eu.ecodex.connector.link.service.DCLinkPluginConfiguration;
import eu.ecodex.connector.link.wsbackendplugin.TestConfigurationCXFWsBug;
import eu.ecodex.connector.persistence.dao.DomibusConnectorLinkConfigurationDao;
import eu.ecodex.connector.persistence.dao.DomibusConnectorLinkPartnerDao;
import eu.ecodex.connector.persistence.service.DCLinkPersistenceService;
import eu.ecodex.connector.persistence.service.DCMessagePersistenceService;
import eu.ecodex.connector.persistence.service.LargeFilePersistenceService;
import eu.ecodex.connector.persistence.service.testutil.LargeFilePersistenceServicePassthroughImpl;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;

/**
 * The LinkTestContext class is used for configuring the test context for LinkModule tests.
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@Import(
    {LinkTestContext.LinkServiceContext.class,
        HelperMethods.class,
        DCKeyStoreService.class,
        ConfigurationPropertyLoaderServiceImpl.class,
        DCBusinessDomainManagerImpl.class,
        ConnectorConfigurationProperties.class,
        TestConfigurationCXFWsBug.class
    }
)
@ComponentScan(basePackages = {"eu.ecodex.connector.common", "eu.ecodex.connector.utils"})
public class LinkTestContext {
    private static final Logger LOGGER = LogManager.getLogger(LinkTestContext.class);

    @Bean
    public MerlinPropertiesFactory merlinPropertiesFactory() {
        return new MerlinPropertiesFactory();
    }

    @MockBean
    DomibusConnectorLinkPartnerDao dao;
    @MockBean
    DomibusConnectorBusinessDomainDao domainDao;
    @MockBean
    DCRoutingRulesManager rulesManager;

    /**
     * LinkServiceContext is a configuration class that defines the beans for the LinkService. It is
     * used to configure and create instances of the LinkService.
     *
     * <p>The LinkServiceContext class is annotated with the @Configuration annotation, indicating
     * that it is a configuration class. It also uses the @ComponentScan annotation to specify the
     * base package classes to scan for component scanning. In this case, it scans the classes in
     * the DCLinkPluginConfiguration class.
     */
    @Configuration
    @ComponentScan(basePackageClasses = DCLinkPluginConfiguration.class)
    public static class LinkServiceContext {
    }

    @Bean
    @ConditionalOnMissingBean
    DCLinkPersistenceService dcLinkPersistenceService(
        DomibusConnectorLinkPartnerDao linkPartnerDao,
        DomibusConnectorLinkConfigurationDao linkConfigurationDao) {
        return new DCLinkPersistenceService(linkPartnerDao, linkConfigurationDao);
    }

    /**
     * This method generates a unique Message ID for a received message in the Domibus connector
     * module. The generated id must be unique across multiple instances.
     *
     * @return The generated message id as a DomibusConnectorMessageId object.
     */
    @SuppressWarnings("checkstyle:MethodName")
    @Bean
    @ConditionalOnMissingBean
    DomibusConnectorMessageIdGenerator DomibusConnectorMessageIdGenerator() {
        return () -> new DomibusConnectorMessageId("testcon_" + UUID.randomUUID());
    }

    @Bean
    @ConditionalOnMissingBean
    public DCMessagePersistenceService domibusConnectorMessagePersistenceService() {
        return Mockito.mock(DCMessagePersistenceService.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public LargeFilePersistenceService largeFilePersistenceServicePassthroughImpl() {
        return new LargeFilePersistenceServicePassthroughImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public DomibusConnectorDomainMessageTransformerService transformerService(
        LargeFilePersistenceService largeFilePersistenceService) {
        return new DomibusConnectorDomainMessageTransformerService(largeFilePersistenceService);
    }

    /**
     * This method creates and returns a mock instance of the {@link TransportStateService}
     * interface. If no other beans of type {@link TransportStateService} are available, this mock
     */
    @Bean
    @ConditionalOnMissingBean
    public TransportStateService transportStatusService() {
        return Mockito.mock(TransportStateService.class);
    }

    @Bean
    @ConditionalOnMissingBean
    public DomibusConnectorLinkConfigurationDao linkConfigurationDao() {
        return Mockito.mock(DomibusConnectorLinkConfigurationDao.class);
    }

    @Bean
    @Primary
    public SubmitToConnector submitToConnector() {
        return new SubmitToConnectorQueueImpl();
    }

    public static final String SUBMIT_TO_CONNECTOR_QUEUE = "submitToConnector";

    @Bean
    @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
    public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages() {
        return new LinkedBlockingDeque<>(90);
    }

    /**
     * The SubmitToConnectorQueueImpl class is an implementation of the SubmitToConnector interface.
     * It provides a method to submit a message to the connector's queue.
     */
    public static class SubmitToConnectorQueueImpl implements SubmitToConnector {
        @Autowired
        @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
        public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages;

        @Override
        public void submitToConnector(
            DomibusConnectorMessage message,
            DomibusConnectorLinkPartner.LinkPartnerName linkPartner, LinkType linkType)
            throws DomibusConnectorSubmitToLinkException {

            LOGGER.info(
                "Adding message [{}] to submitToConnector [{}] Queue", message,
                toConnectorSubmittedMessages
            );
            try {
                toConnectorSubmittedMessages.put(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
