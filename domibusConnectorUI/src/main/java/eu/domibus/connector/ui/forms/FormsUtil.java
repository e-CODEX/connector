package eu.domibus.connector.ui.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;


public class FormsUtil {
    public static TextField getFormattedTextFieldReadOnly() {
        TextField loc = new TextField();
        loc.setReadOnly(true);
        loc.getStyle().set("fontSize", "13px");
        loc.setWidth("600px");
        return loc;
    }

    public static TextField getFormattedTextField() {
        TextField loc = new TextField();
        loc.getStyle().set("fontSize", "13px");
        loc.setWidth("600px");
        return loc;
    }

    public static TextField getFormattedRequiredTextField() {
        TextField loc = getFormattedTextField();
        loc.setRequired(true);
        return loc;
    }

    public static TextArea getFormattedTextArea() {
        TextArea loc = new TextArea();
        loc.getStyle().set("fontSize", "13px");
        loc.setWidth("600px");
        return loc;
    }

    public static ComboBox<?> getRequiredCombobox() {
        ComboBox<?> box = new ComboBox<>();
        box.getStyle().set("fontSize", "13px");
        box.setWidth("600px");
        box.setRequired(true);
        return box;
    }
}
