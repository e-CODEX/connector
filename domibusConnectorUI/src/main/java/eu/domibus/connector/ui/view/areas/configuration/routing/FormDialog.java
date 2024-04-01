package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.dialog.Dialog;
import org.springframework.context.ApplicationContext;


public class FormDialog<T> extends Dialog {
    private final ApplicationContext ctx;

    public FormDialog(ApplicationContext ctx) {
        this.ctx = ctx;
    }
}
