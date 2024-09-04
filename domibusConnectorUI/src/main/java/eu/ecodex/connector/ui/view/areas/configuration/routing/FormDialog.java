/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.routing;

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
