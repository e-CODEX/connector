package eu.domibus.connector.ui.dialogs;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.server.Command;


public class EditBeanDialogBuilder<T> {
    private Command onConfirmCallback;
    private Command onCancelCallback;
    private ValueCallback<T> onValueCallback;

    private T value;
    private CustomField<T> field;
    private boolean closeOnEscape;

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

    public void show() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(closeOnEscape);
        dialog.setCloseOnOutsideClick(false);
        VerticalLayout layout = new VerticalLayout();

        Button saveButton = new Button("Save", event -> {
            boolean close = true;
            if (onValueCallback != null) {
                close = onValueCallback.onValue(field.getValue());
            }
            dialog.close();
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
        layout.add(new HorizontalLayout(saveButton, cancelButton));
        layout.add(field);

        dialog.add(layout);
        dialog.open();
    }

    public interface ValueCallback<T> {
        boolean onValue(T o);
    }
}
