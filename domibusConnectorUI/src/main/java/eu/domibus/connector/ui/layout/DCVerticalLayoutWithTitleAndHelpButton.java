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


public class DCVerticalLayoutWithTitleAndHelpButton extends VerticalLayout {
    public static final String DOC_PREFIX = "documentation/";
    public final String HELP_PAGE_PATH;

    public final String pageTitle;

    public DCVerticalLayoutWithTitleAndHelpButton(String helpPagePath, String pageTitle) {
        this.HELP_PAGE_PATH = helpPagePath;
        this.pageTitle = pageTitle;
        initUI();
    }

    private void initUI() {
        HorizontalLayout helpBar = new HorizontalLayout();

        Div title = new Div();
        title.add(new H2(pageTitle));

        Div help = new Div();
        help.add(new H2(createHelpButton(HELP_PAGE_PATH)));

        helpBar.add(title, help);

        //		helpBar.setAlignItems(Alignment.STRETCH);
        //		helpBar.expand(title);
        //		helpBar.setJustifyContentMode(com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode
        //		.END);
        //		helpBar.setWidth("95%");
        helpBar.setHeight("70px");

        add(helpBar);
    }

    public Anchor createHelpButton(String helpid) {
        String htmlFile = DOC_PREFIX + helpid.replace("adoc", "html");

        String baseURL = RouteConfiguration.forSessionScope()
                                           .getUrl(DashboardView.class);

        Anchor helpLink = new Anchor();
        helpLink.setHref(baseURL + htmlFile + "?param=" + System.currentTimeMillis());
        helpLink.setTarget("_blank");
        helpLink.setTitle("Online help page for view " + pageTitle);
        helpLink.add(new Button(VaadinIcon.QUESTION_CIRCLE_O.create()));

        return helpLink;
    }
}
