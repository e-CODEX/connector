/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.api;

import eu.domibus.connector.domain.model.DomibusConnectorLinkConfiguration;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import javax.annotation.CheckForNull;
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
    @CheckForNull
    private ConfigurableApplicationContext childContext;
    @CheckForNull
    private SubmitToLinkPartner submitToLink;
}
