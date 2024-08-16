/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view;

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
