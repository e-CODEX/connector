/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
