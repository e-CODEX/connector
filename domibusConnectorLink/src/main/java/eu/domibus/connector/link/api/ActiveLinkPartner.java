/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.link.api;

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import java.util.Optional;
import javax.annotation.CheckForNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The ActiveLinkPartner class represents an active link partner in the Domibus Connector. It
 * contains information about the link partner, parent link, child context, and submit to link
 * partner.
 */
@Getter
@Setter
public class ActiveLinkPartner {
    private DomibusConnectorLinkPartner linkPartner;
    private ActiveLink parentLink;
    @CheckForNull
    private ConfigurableApplicationContext childContext;
    @CheckForNull
    private SubmitToLinkPartner submitToLink;

    public Optional<ConfigurableApplicationContext> getChildContext() {
        return Optional.ofNullable(childContext);
    }

    public Optional<SubmitToLinkPartner> getSubmitToLink() {
        return Optional.ofNullable(submitToLink);
    }
}
