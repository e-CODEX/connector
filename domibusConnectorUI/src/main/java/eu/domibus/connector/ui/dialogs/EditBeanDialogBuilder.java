/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.Command;

/**
 * EditBeanDialogBuilder is a utility class that provides a convenient way to create and display a
 * dialog for editing a bean object.
 *
 * @param <T> the type of the bean object being edited
 */
public class EditBeanDialogBuilder<T> {
    private Command onConfirmCallback;
    private Command onCancelCallback;
    private ValueCallback<T> onValueCallback;
    private T value;
    private CustomField<T> field;
    private boolean closeOnEscape;

    /**
     * The ValueCallback interface defines a callback method that can be used to handle a value of
     * type T.
     *
     * @param <T> the type of value to be handled by the callback
     */
    public interface ValueCallback<T> {
        boolean onValue(T o);
    }

    private EditBeanDialogBuilder() {
    }

    public static <T> EditBeanDialogBuilder<T> getBuilder() {
        return new EditBeanDialogBuilder<>();
    }

    public EditBeanDialogBuilder<T> setCloseOnEscape(boolean b) {
        this.closeOnEscape = b;
        return this;
    }

    public EditBeanDialogBuilder<T> setField(CustomField<T> field) {
        this.field = field;
        return this;
    }

    public EditBeanDialogBuilder<T> setOnConfirmCallback(Command onConfirmCallback) {
        this.onConfirmCallback = onConfirmCallback;
        return this;
    }

    public EditBeanDialogBuilder<T> setOnValueCallback(ValueCallback<T> onConfirmCallback) {
        this.onValueCallback = onConfirmCallback;
        return this;
    }

    public EditBeanDialogBuilder<T> setOnCancelCallback(Command onCancelCallback) {
        this.onCancelCallback = onCancelCallback;
        return this;
    }

    /**
     * Displays a dialog with save and cancel buttons, along with a custom field. The dialog is used
     * for editing a bean object.
     *
     * <p>Note: The method does not return anything and does not take any parameters.
     */
    public void show() {
        var dialog = new Dialog();
        dialog.setCloseOnEsc(closeOnEscape);
        dialog.setCloseOnOutsideClick(false);
        var layout = new VerticalLayout();

        var saveButton = new Button("Save", event -> {
            var close = true;
            if (onValueCallback != null) {
                close = onValueCallback.onValue(field.getValue());
            }
            dialog.close();
        });
        var cancelButton = new Button("Cancel", event -> {
            dialog.close();
            if (onCancelCallback != null) {
                onCancelCallback.execute();
            }
        });
        dialog.addDialogCloseActionListener(event -> {
            if (onCancelCallback != null) {
                onCancelCallback.execute();
            }
        });
        layout.add(new HorizontalLayout(saveButton, cancelButton));
        layout.add(field);

        dialog.add(layout);
        dialog.open();
    }
}
