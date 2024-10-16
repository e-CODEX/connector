/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import lombok.experimental.UtilityClass;

/**
 * The FormsUtil class provides utility methods for creating and customizing form fields.
 */
@UtilityClass
public class FormsUtil {
    public static final String WIDTH_600_PX = "600px";

    /**
     * Returns a formatted TextField component that is read-only.
     *
     * @return a read-only formatted TextField component
     */
    public static TextField getFormattedTextFieldReadOnly() {
        var loc = new TextField();
        loc.setReadOnly(true);
        loc.getStyle().set("fontSize", "13px");
        loc.setWidth(WIDTH_600_PX);
        return loc;
    }

    /**
     * Retrieves a formatted TextField component.
     *
     * @return a formatted TextField component
     */
    public static TextField getFormattedTextField() {
        var loc = new TextField();
        loc.getStyle().set("fontSize", "13px");
        loc.setWidth(WIDTH_600_PX);
        return loc;
    }

    /**
     * Retrieves a formatted required TextField component.
     *
     * @return a formatted TextField component that is set to be required
     */
    public static TextField getFormattedRequiredTextField() {
        var loc = getFormattedTextField();
        loc.setRequired(true);
        return loc;
    }

    /**
     * Returns a formatted TextArea component with default styling.
     *
     * @return a TextArea component with formatted style
     */
    public static TextArea getFormattedTextArea() {
        var loc = new TextArea();
        loc.getStyle().set("fontSize", "13px");
        loc.setWidth(WIDTH_600_PX);
        return loc;
    }

    /**
     * Returns a required ComboBox component with default styling.
     *
     * @return a required ComboBox component
     */
    public static ComboBox<?> getRequiredCombobox() {
        ComboBox<?> box = new ComboBox<>();
        box.getStyle().set("fontSize", "13px");
        box.setWidth(WIDTH_600_PX);
        box.setRequired(true);
        return box;
    }
}
