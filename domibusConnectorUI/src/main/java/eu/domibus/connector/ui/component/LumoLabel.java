/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.html.Label;
import lombok.NoArgsConstructor;

/**
 * The LumoLabel class extends the Label component and represents a customized label with specific
 * styling. It sets the font family to "-apple-system, BlinkMacSystemFont, 'Roboto', 'Segoe UI',
 * Helvetica, Arial, sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol'".
 */
@NoArgsConstructor
public class LumoLabel extends Label {
    {
        super.getStyle().set(
            "font-family",
            "-apple-system, BlinkMacSystemFont, \"Roboto\", \"Segoe UI\", Helvetica, Arial, "
                + "sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
        );
    }

    public LumoLabel(String text) {
        super(text);
    }
}
