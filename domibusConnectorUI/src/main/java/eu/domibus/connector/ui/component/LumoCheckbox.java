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
