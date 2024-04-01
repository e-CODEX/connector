package eu.domibus.connector.ui.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;


@Component
@UIScope
public class OpenHelpButtonFactory {
    public static final String DOC_PREFIX = "documentation/";

    public Button createHelpButton(String helpid) {
        String htmlFile = DOC_PREFIX + helpid.replace("adoc", "html");

        Button b = new Button(VaadinIcon.QUESTION.create());
        b.addClickListener(e -> {
            UI.getCurrent().getPage().open(htmlFile, "_blank");
            // should be sufficient for now. Later navigate to a help view
        });
        return b;
    }
}
