/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.common;

import eu.ecodex.connector.domain.transition.DomibusConnectorConfirmationType;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

/**
 * This class contains constants used in Domibus Gateway.
 */
@UtilityClass
public class DomibusGwConstants {
    public static final Set<String> EVIDENCE_TYPE_NAMES =
        (Arrays.stream(DomibusConnectorConfirmationType.values()).map(
                   DomibusConnectorConfirmationType::value)
               .collect(Collectors.toSet())
        );
    public static final String ASIC_S_NAME = "ASIC-S";
    public static final String ASIC_S_DESCRIPTION_NAME = "ASIC-S";
    public static final String TOKEN_XML_NAME = "tokenXML";
    public static final String TOKEN_XML_DESCRIPTION_NAME = "tokenXML";
    public static final String MESSAGE_CONTENT_DESCRIPTION_NAME = "messageContent";
    public static final String ASIC_S_MIMETYPE = "application/vnd.etsi.asic-s+zip";
}
