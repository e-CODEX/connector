/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import eu.ecodex.connector.ui.utils.UiStyle;

/**
 * The LoginErrorDialog class represents a dialog that is displayed when there is an error during
 * the login process.
 */
public class LoginErrorDialog extends Dialog {
    /**
     * Constructor.
     *
     * @param errorMessage the error message to display in the dialog
     */
    public LoginErrorDialog(String errorMessage) {
        var loginExceptionDiv = new Div();
        var loginException = new NativeLabel(errorMessage);
        loginException.getStyle().set("font-weight", "bold");
        loginException.getStyle().set("color", "red");
        loginExceptionDiv.add(loginException);
        loginExceptionDiv.getStyle().set("text-align", "center");
        loginExceptionDiv.setVisible(true);
        add(loginExceptionDiv);

        var okContent = new Div();
        okContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        okContent.getStyle().set("padding", "10px");
        var okButton = new Button("OK");
        okButton.addClickListener(e2 -> close());
        okButton.setAutofocus(true);
        okContent.add(okButton);

        add(okContent);
    }
}
