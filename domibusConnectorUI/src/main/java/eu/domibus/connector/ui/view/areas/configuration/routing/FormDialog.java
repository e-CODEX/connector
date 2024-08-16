/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.configuration.routing;

import com.vaadin.flow.component.dialog.Dialog;
import org.springframework.context.ApplicationContext;

/**
 * Represents a dialog box for displaying a form.
 */
public class FormDialog<T> extends Dialog {
    private final ApplicationContext ctx;

    /**
     * Constructor.
     *
     * @param ctx the ApplicationContext to use for creating the dialog
     */
    public FormDialog(ApplicationContext ctx) {
        this.ctx = ctx;
    }
}
