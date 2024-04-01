package eu.domibus.connector.ui.view;

import com.vaadin.flow.component.html.IFrame;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;


public class StaticContentView extends VerticalLayout {
    public StaticContentView(String staticContentSrc) {
        String srcName = staticContentSrc + "?param=" + System.currentTimeMillis();
        IFrame staticContentFrame = new IFrame(srcName);
        staticContentFrame.setHeightFull();
        staticContentFrame.setSizeFull();
        staticContentFrame.getStyle().set("margin", "0");
        staticContentFrame.getStyle().set("padding", "0");
        staticContentFrame.getStyle().set("border", "none");

        add(staticContentFrame);

        setSizeFull();
    }
}
