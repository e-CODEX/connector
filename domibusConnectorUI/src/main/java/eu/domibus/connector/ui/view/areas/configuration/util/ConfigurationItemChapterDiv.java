/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
