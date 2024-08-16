/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.layout;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouteConfiguration;
import eu.domibus.connector.ui.view.DashboardView;

/**
 * Represents a vertical layout with a title and a help button.
 * Extends the VerticalLayout class from the Vaadin framework.
 */
@SuppressWarnings("checkstyle:MemberName")
public class DCVerticalLayoutWithTitleAndHelpButton extends VerticalLayout {
    public final String HELP_PAGE_PATH;
    public static final String DOC_PREFIX = "documentation/";
    public final String pageTitle;

    /**
     * Represents a vertical layout with a title and a help button.
     * Extends the VerticalLayout class from the Vaadin framework.
     *
     * @param helpPagePath the path of the help page
     * @param pageTitle the title of the page
     *
     * @see VerticalLayout
     * @see Div
     * @see Anchor
     * @see Button
     * @see H2
     * @see VaadinIcon
     * @see RouteConfiguration
     * @see DashboardView
     */
    public DCVerticalLayoutWithTitleAndHelpButton(String helpPagePath, String pageTitle) {
        this.HELP_PAGE_PATH = helpPagePath;
        this.pageTitle = pageTitle;
        initUI();
    }

    private void initUI() {

        var helpBar = new HorizontalLayout();
        var title = new Div();
        title.add(new H2(pageTitle));

        var help = new Div();
        help.add(new H2(createHelpButton(HELP_PAGE_PATH)));

        helpBar.add(title, help);

        helpBar.setHeight("70px");

        add(helpBar);
    }

    /**
     * Creates a help button as an {@link Anchor} component.
     *
     * @param helpId the ID of the help page
     * @return the created help button as an {@link Anchor} component
     */
    public Anchor createHelpButton(String helpId) {
        String htmlFile = DOC_PREFIX + helpId.replace("adoc", "html");

        var baseURL = RouteConfiguration.forSessionScope().getUrl(DashboardView.class);

        var helpLink = new Anchor();
        helpLink.setHref(baseURL + htmlFile + "?param=" + System.currentTimeMillis());
        helpLink.setTarget("_blank");
        helpLink.setTitle("Online help page for view " + pageTitle);
        helpLink.add(new Button(VaadinIcon.QUESTION_CIRCLE_O.create()));

        return helpLink;
    }
}
