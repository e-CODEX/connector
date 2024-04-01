package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.checkbox.Checkbox;


public class LumoCheckbox extends Checkbox {

    {
        super.getStyle().set(
                "font-family",
                "-apple-system, BlinkMacSystemFont, \"Roboto\", \"Segoe UI\", Helvetica, Arial, sans-serif, \"Apple " +
                        "Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
        );
    }

    public LumoCheckbox() {
        // TODO Auto-generated constructor stub
    }

    public LumoCheckbox(String labelText) {
        super(labelText);
        // TODO Auto-generated constructor stub
    }

    public LumoCheckbox(boolean initialValue) {
        super(initialValue);
        // TODO Auto-generated constructor stub
    }

    public LumoCheckbox(String labelText, boolean initialValue) {
        super(labelText, initialValue);
        // TODO Auto-generated constructor stub
    }

    public LumoCheckbox(String label, ValueChangeListener<ComponentValueChangeEvent<Checkbox, Boolean>> listener) {
        super(label, listener);
        // TODO Auto-generated constructor stub
    }
}
