package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.html.Div;
import eu.domibus.connector.ui.component.LumoLabel;


public class ConfigurationItemChapterDiv extends Div {
    private static final long serialVersionUID = 1L;

    public ConfigurationItemChapterDiv(String chapterTitle) {
        LumoLabel chapterLabel = new LumoLabel(chapterTitle);
        chapterLabel.getStyle().set("font-size", "20px");

        chapterLabel.getStyle().set("font-style", "italic");

        super.add(chapterLabel);
    }
}
