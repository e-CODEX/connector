package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.github.dockerjava.api.model.Bind;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import org.springframework.context.ApplicationContext;

public class FormDialog<T> extends Dialog {


    private final ApplicationContext ctx;

    public FormDialog(ApplicationContext ctx) {
        this.ctx = ctx;


    }

}
