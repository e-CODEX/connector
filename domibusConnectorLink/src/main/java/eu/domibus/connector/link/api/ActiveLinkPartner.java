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

import eu.domibus.connector.domain.model.DomibusConnectorLinkPartner;
import eu.domibus.connector.link.service.SubmitToLinkPartner;
import jakarta.annotation.Nullable;
import java.util.Optional;
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
    @Nullable
    private ConfigurableApplicationContext childContext;
    @Nullable
    private SubmitToLinkPartner submitToLink;

    public Optional<ConfigurableApplicationContext> getChildContext() {
        return Optional.ofNullable(childContext);
    }

    public Optional<SubmitToLinkPartner> getSubmitToLink() {
        return Optional.ofNullable(submitToLink);
    }
}
