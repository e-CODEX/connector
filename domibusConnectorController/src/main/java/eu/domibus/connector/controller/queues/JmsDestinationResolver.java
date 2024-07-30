/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.queues;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * This class is an implementation of the DestinationResolver interface from
 * the JMS (Java Message Service) API.
 * It resolves the destination name by looking up the corresponding bean in the ApplicationContext.
 */
@Component
public class JmsDestinationResolver implements DestinationResolver {
    private final ApplicationContext ctx;

    public JmsDestinationResolver(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Destination resolveDestinationName(
        @Nullable Session session, String destinationName, boolean pubSubDomain)
        throws JMSException {
        return ctx.getBean(destinationName, Destination.class);
    }
}
