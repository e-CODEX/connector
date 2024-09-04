/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.persistence.model.enums;

/**
 * Enumeration representing the direction of a message.
 * The direction can be either NAT_TO_GW (from NAT to Gateway) or GW_TO_NAT (from Gateway to NAT).
 */
public enum PMessageDirection {
    NAT_TO_GW,
    GW_TO_NAT;
}
