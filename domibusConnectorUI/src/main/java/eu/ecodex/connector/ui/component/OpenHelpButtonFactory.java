/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

/**
 * The OpenHelpButtonFactory class is responsible for creating a help button that opens the help
 * content specified by its ID in a new browser tab.
 */
@Component
@UIScope
public class OpenHelpButtonFactory {
    public static final String DOC_PREFIX = "documentation/";

    /**
     * Creates a help button with the given help ID.
     *
     * @param helpId the ID of the help content, should be an AsciiDoc file name without extension
     * @return the help button
     */
    public Button createHelpButton(String helpId) {
        String htmlFile = DOC_PREFIX + helpId.replace("adoc", "html");

        var button = new Button(VaadinIcon.QUESTION.create());
        button.addClickListener(
            e ->
                UI.getCurrent().getPage().open(htmlFile, "_blank")
        );
        return button;
    }
}
