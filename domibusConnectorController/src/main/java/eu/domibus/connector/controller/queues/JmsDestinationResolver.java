/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.controller.queues;

import jakarta.jms.Destination;
import jakarta.jms.JMSException;
import jakarta.jms.Session;
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
