/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.tools;

import lombok.experimental.UtilityClass;

/**
 * This class contains MDC logging property names.
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
@UtilityClass
public class LoggingMDCPropertyNames {
    public static final String MDC_JMS_ID = "jmsid";
    public static final String MDC_DC_MESSAGE_PROCESSOR_PROPERTY_NAME = "message_processor";
    public static final String MDC_DC_QUEUE_LISTENER_PROPERTY_NAME = "queue_listener";
    public static final String MDC_DC_STEP_PROCESSOR_PROPERTY_NAME = "step";
    public static final String MDC_LINK_CONFIG_NAME = "linkconfigname";
    public static final String MDC_LINK_PARTNER_NAME = "linkpartnername";
    public static final String MDC_REMOTE_MSG_ID = "remote_message_id";
    public static final String MDC_DOMIBUS_CONNECTOR_MESSAGE_ID_PROPERTY_NAME = "messageid";
    public static final String MDC_BACKEND_MESSAGE_ID_PROPERTY_NAME = "backendMessageId";
    public static final String MDC_EBMS_MESSAGE_ID_PROPERTY_NAME = "ebmsMessageId";
}
