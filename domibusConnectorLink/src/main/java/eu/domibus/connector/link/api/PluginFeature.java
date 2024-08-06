/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
