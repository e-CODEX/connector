/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

/**
 * The StaticContentView class represents a custom Vaadin component that displays static content
 * from a specified source.
 */
public class StaticContentView extends VerticalLayout {
    /**
     * Constructor.
     */
    public StaticContentView(String staticContentSrc) {
        var srcName = staticContentSrc + "?param=" + System.currentTimeMillis();
        var staticContentFrame = new IFrame(srcName);
        staticContentFrame.setHeightFull();
        staticContentFrame.setSizeFull();
        staticContentFrame.getStyle().set("margin", "0");
        staticContentFrame.getStyle().set("padding", "0");
        staticContentFrame.getStyle().set("border", "none");

        add(staticContentFrame);

        setSizeFull();
    }
}
