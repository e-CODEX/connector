/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.Command;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

/**
 * The ConfirmDialogBuilder class is used to build and display a confirm dialog box. It allows
 * setting the message text and callback functions for the confirmation and cancel actions. Once the
 * dialog is built, the show() method is called to display the dialog.
 *
 * @see Command
 */
@NoArgsConstructor
public class ConfirmDialogBuilder {
    private String messageText;
    private Command onConfirmCallback;
    private Command onCancelCallback;

    public static ConfirmDialogBuilder getBuilder() {
        return new ConfirmDialogBuilder();
    }

    public ConfirmDialogBuilder setMessage(String messageText) {
        this.messageText = messageText;
        return this;
    }

    public ConfirmDialogBuilder setOnConfirmCallback(Command onConfirmCallback) {
        this.onConfirmCallback = onConfirmCallback;
        return this;
    }

    public ConfirmDialogBuilder setOnCancelCallback(Command onCancelCallback) {
        this.onCancelCallback = onCancelCallback;
        return this;
    }

    /**
     * Displays a dialog box with a given message.
     *
     * @throws IllegalArgumentException If the message text is empty
     */
    public void show() {
        if (!StringUtils.hasLength(messageText)) {
            throw new IllegalArgumentException("Message Text is not allowed to be null!");
        }

        var dialog = new Dialog();
        dialog.add(new Text("The Form contains errors"));
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        var message = new Span();
        message.setText(messageText);
        dialog.add(message);

        var confirmButton = new Button("Confirm", event -> {
            dialog.close();
            if (onConfirmCallback != null) {
                onConfirmCallback.execute();
            }
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
        dialog.add(new Div(confirmButton, cancelButton));

        dialog.open();
    }
}

