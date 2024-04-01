package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.html.Label;


public class LumoLabel extends Label {

    {
        super.getStyle().set(
                "font-family",
                "-apple-system, BlinkMacSystemFont, \"Roboto\", \"Segoe UI\", Helvetica, Arial, sans-serif, \"Apple " +
                        "Color Emoji\", \"Segoe UI Emoji\", \"Segoe UI Symbol\""
        );
    }

    public LumoLabel() {
        // TODO Auto-generated constructor stub
    }

    public LumoLabel(String text) {
        super(text);
        // TODO Auto-generated constructor stub
    }
}
