/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.checkbox.Checkbox;
import lombok.NoArgsConstructor;

/**
 * The LumoCheckbox class is a custom checkbox component that extends the Checkbox component
 * provided by Vaadin Flow. It provides additional styling and functionality specific to the Lumo
 * theme.
 */
@NoArgsConstructor
public class LumoCheckbox extends Checkbox {
    {
        super.getStyle().set(
            "font-family",
            "-apple-system, BlinkMacSystemFont, \"Roboto\", \"Segoe UI\", Helvetica, Arial, "
                + "sans-serif, \"Apple Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
        );
    }

    public LumoCheckbox(String labelText) {
        super(labelText);
    }

    public LumoCheckbox(boolean initialValue) {
        super(initialValue);
    }

    public LumoCheckbox(String labelText, boolean initialValue) {
        super(labelText, initialValue);
    }

    public LumoCheckbox(
        String label, ValueChangeListener<ComponentValueChangeEvent<Checkbox, Boolean>> listener) {
        super(label, listener);
    }
}
