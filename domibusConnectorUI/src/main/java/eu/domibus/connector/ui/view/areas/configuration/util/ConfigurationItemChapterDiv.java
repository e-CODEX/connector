/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.util;

import com.vaadin.flow.component.html.Div;
import eu.domibus.connector.ui.component.LumoLabel;

/**
 * The ConfigurationItemChapterDiv class represents a div element that acts as a chapter label in a
 * configuration item section.
 */
public class ConfigurationItemChapterDiv extends Div {
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.
     *
     * @param chapterTitle the title of the chapter
     */
    public ConfigurationItemChapterDiv(String chapterTitle) {
        var chapterLabel = new LumoLabel(chapterTitle);
        chapterLabel.getStyle().set("font-size", "20px");

        chapterLabel.getStyle().set("font-style", "italic");

        super.add(chapterLabel);
    }
}
