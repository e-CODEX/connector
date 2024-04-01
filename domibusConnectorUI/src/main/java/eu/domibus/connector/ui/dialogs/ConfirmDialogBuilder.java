package eu.domibus.connector.ui.dialogs;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.server.Command;
import org.springframework.util.StringUtils;


public class ConfirmDialogBuilder {
    private String messageText;
    private Command onConfirmCallback;
    private Command onCancelCallback;

    private ConfirmDialogBuilder() {

    }

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

    public void show() {
        if (StringUtils.isEmpty(messageText)) {
            throw new IllegalArgumentException("Message Text is not allowed to be null!");
        }

        Dialog dialog = new Dialog();
        dialog.add(new Text("The Form contains errors"));
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Span message = new Span();
        message.setText(messageText);
        dialog.add(message);

        Button confirmButton = new Button("Confirm", event -> {
            dialog.close();
            if (onConfirmCallback != null) {
                onConfirmCallback.execute();
            }
        });
        Button cancelButton = new Button("Cancel", event -> {
            dialog.close();
            if (onCancelCallback != null) {
                onCancelCallback.execute();
            }
        });
        dialog.addDialogCloseActionListener((event) -> {
            if (onCancelCallback != null) {
                onCancelCallback.execute();
            }
        });
        dialog.add(new Div(confirmButton, cancelButton));

        dialog.open();
    }
}

