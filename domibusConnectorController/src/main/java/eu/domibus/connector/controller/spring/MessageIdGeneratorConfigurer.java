package eu.domibus.connector.controller.spring;

import java.util.UUID;

import eu.domibus.connector.domain.model.DomibusConnectorMessageId;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.domibus.connector.controller.service.DomibusConnectorMessageIdGenerator;

/**
 *  Default connectorMessage ID generator
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@Configuration
public class MessageIdGeneratorConfigurer {


    @ConditionalOnMissingBean(DomibusConnectorMessageIdGenerator.class)
    @Bean
    public DomibusConnectorMessageIdGenerator domibusConnectorMessageIdGenerator() {
        return () -> new DomibusConnectorMessageId(String.format("%s@%s", UUID.randomUUID(), "domibus.connector.eu"));
    }
    
    
}
