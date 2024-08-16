/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.link.api;

/**
 * The PluginFeature enum represents the features supported by a LinkPlugin.
 */
public enum PluginFeature {
    SUPPORTS_MULTIPLE_PARTNERS, // if the plugin supports multiple link partners within one
    // link-configuration
    // (eg. multiple backends communicating over the same Webservice interface)
    RCV_PULL_MODE, // pulling messages from remote
    RCV_PASSIVE_MODE, // getting queried/pulled
    SEND_PASSIVE_MODE, // getting queried/pulled
    SEND_PUSH_MODE, // pushing messages to remote
    SUPPORTS_LINK_PARTNER_SHUTDOWN, // link partner can be shutdown/deactivated during runtime
    SUPPORTS_LINK_SHUTDOWN, // link can be shutdown during runtime
    CAN_RETRY, // the link plugin can handle a retry
    GATEWAY_PLUGIN, // the plugin can handle GW connections
    BACKEND_PLUGIN // the plugin can handle backend connections
}
