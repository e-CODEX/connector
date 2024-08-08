/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.persistence.model.enums;

/**
 * Enumeration representing the direction of a message.
 * The direction can be either NAT_TO_GW (from NAT to Gateway) or GW_TO_NAT (from Gateway to NAT).
 */
public enum PMessageDirection {
    NAT_TO_GW,
    GW_TO_NAT;
}
