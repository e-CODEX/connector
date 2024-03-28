package test.eu.domibus.connector.link;

import eu.domibus.connector.common.configuration.ConnectorConfigurationProperties;
import eu.domibus.connector.common.persistence.dao.DomibusConnectorBusinessDomainDao;
import eu.domibus.connector.common.service.ConfigurationPropertyLoaderServiceImpl;
import eu.domibus.connector.common.service.DCBusinessDomainManagerImpl;
import eu.domibus.connector.common.service.DCKeyStoreService;
import eu.domibus.connector.controller.exception.DomibusConnectorSubmitToLinkException;
import eu.domibus.connector.controller.routing.DCRoutingRulesManager;
import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;
import eu.domibus.connector.controller.service.SubmitToConnector;
import eu.domibus.connector.controller.service.TransportStateService;
import eu.domibus.connector.domain.enums.LinkType;
import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.domain.model.DomibusConnectorMessage;
import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import eu.domibus.connector.domain.transformer.DomibusConnectorDomainMessageTransformerService;
import eu.domibus.connector.lib.spring.configuration.validation.HelperMethods;
import eu.domibus.connector.link.common.MerlinPropertiesFactory;
import eu.domibus.connector.link.service.DCLinkPluginConfiguration;
import eu.domibus.connector.persistence.dao.DomibusConnectorLinkConfigurationDao;
import eu.domibus.connector.persistence.dao.DomibusConnectorLinkPartnerDao;
import eu.domibus.connector.persistence.service.DCLinkPersistenceService;
import eu.domibus.connector.persistence.service.DCMessagePersistenceService;
import eu.domibus.connector.persistence.service.LargeFilePersistenceService;
import eu.domibus.connector.persistence.service.testutil.LargeFilePersistenceServicePassthroughImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.*;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;


@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,})
@Import(
        {LinkTestContext.LinkServiceContext.class,
                HelperMethods.class,
                DCKeyStoreService.class,
                ConfigurationPropertyLoaderServiceImpl.class,
                DCBusinessDomainManagerImpl.class,
                ConnectorConfigurationProperties.class
        }
)
@ComponentScan(basePackages = {"eu.domibus.connector.common", "eu.domibus.connector.utils"})
public class LinkTestContext {
    public static final String SUBMIT_TO_CONNECTOR_QUEUE = "submitToConnector";
    private static final Logger LOGGER = LogManager.getLogger(LinkTestContext.class);

    @MockBean
    DomibusConnectorLinkPartnerDao dao;
    @MockBean
    DomibusConnectorBusinessDomainDao domainDao;
    @MockBean
    DCRoutingRulesManager rulesManager;

    @Bean
    public MerlinPropertiesFactory merlinPropertiesFactory() {
        return new MerlinPropertiesFactory();
    }

    @Bean
    @ConditionalOnMissingBean
    DCLinkPersistenceService dcLinkPersistenceService(
            DomibusConnectorLinkPartnerDao linkPartnerDao,
            DomibusConnectorLinkConfigurationDao linkConfigurationDao) {
        return new DCLinkPersistenceService(linkPartnerDao, linkConfigurationDao);
    }

    @Bean
    @ConditionalOnMissingBean
    DomibusConnectorMessageIdGenerator DomibusConnectorMessageIdGenerator() {
        return () -> new DomibusConnectorMessageId("testcon_" + UUID.randomUUID().toString());
    }

    @Bean
    @ConditionalOnMissingBean
    public DCMessagePersistenceService domibusConnectorMessagePersistenceService() {
        return Mockito.mock(DCMessagePersistenceService.class);
    }

    //    @Bean
    //    @ConditionalOnMissingBean
    //    public LargeFileProviderMemoryImpl largeFilePersistenceServiceMemoryImpl() {
    //        return new LargeFileProviderMemoryImpl();
    //    }

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

    @Bean
    @ConditionalOnMissingBean
    public TransportStateService transportStatusService() {
        TransportStateService mock = Mockito.mock(TransportStateService.class);

        return mock;
    }

    @Bean
    @ConditionalOnMissingBean
    public DomibusConnectorLinkConfigurationDao linkConfigurationDao() {
        return Mockito.mock(DomibusConnectorLinkConfigurationDao.class);
    }

    @Bean
    @Primary
    public SubmitToConnector submitToConnector() {
        return new SubmitToConnectorQueuImpl();
    }

    @Bean
    @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
    public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages() {
        return new LinkedBlockingDeque<>(90);
    }

    @Configuration
    @ComponentScan(basePackageClasses = DCLinkPluginConfiguration.class)
    public static class LinkServiceContext {

    }

    public static class SubmitToConnectorQueuImpl implements SubmitToConnector {
        @Autowired
        @Qualifier(SUBMIT_TO_CONNECTOR_QUEUE)
        public BlockingQueue<DomibusConnectorMessage> toConnectorSubmittedMessages;

        @Override
        public void submitToConnector(
                DomibusConnectorMessage message,
                DomibusConnectorLinkPartner.LinkPartnerName linkPartner,
                LinkType linkType) throws DomibusConnectorSubmitToLinkException {

            LOGGER.info("Adding message [{}] to submitToConnector [{}] Queue", message, toConnectorSubmittedMessages);
            try {
                toConnectorSubmittedMessages.put(message);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
