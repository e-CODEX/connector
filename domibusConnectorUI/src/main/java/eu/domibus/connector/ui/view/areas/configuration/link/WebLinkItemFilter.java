/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.link;

import eu.domibus.connector.domain.enums.LinkType;
import lombok.Data;

/**
 * The WebLinkItemFilter class represents a filter for WebLinkItem objects based on their link
 * type.
 */
@Data
public class WebLinkItemFilter {
    private LinkType linkType;
}
