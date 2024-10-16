/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.link.api;

import eu.ecodex.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.ecodex.connector.link.service.SubmitToLinkPartner;
import jakarta.annotation.Nullable;
import lombok.Data;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The ActiveLink class represents an active link configuration in Domibus. It contains information
 * about the link plugin, link configuration, child context, and submit to link partner.
 */
@Data
public class ActiveLink {
    private LinkPlugin linkPlugin;
    private DomibusConnectorLinkConfiguration linkConfiguration;
    @Nullable
    private ConfigurableApplicationContext childContext;
    @Nullable
    private SubmitToLinkPartner submitToLink;
}
