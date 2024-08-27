/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.login;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.textfield.PasswordField;
import eu.domibus.connector.ui.exception.UserLoginException;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.DashboardView;

/**
 * The {@code ChangePasswordDialog} class represents a dialog that allows the user to change their
 * password.
 */
public class ChangePasswordDialog extends Dialog {
    private Button changePasswordButton;
    private final PasswordField currentPwField;
    private final PasswordField newPwField;
    private final PasswordField confirmNewPw;
    WebUserService userService;
    String username;

    /**
     * Constructor.
     *
     * @param userService the WebUserService instance used for password change operations
     * @param username    the username of the user whose password is being changed
     * @param password    the current password of the user
     */
    public ChangePasswordDialog(WebUserService userService, String username, String password) {
        this.userService = userService;
        this.username = username;

        var changePasswordDiv = new Div();
        var changePassword = new NativeLabel("Change Password for User " + username);
        changePassword.getStyle().set("font-weight", "bold");
        changePasswordDiv.add(changePassword);
        changePasswordDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        changePasswordDiv.setVisible(true);
        add(changePasswordDiv);

        var changePassword2Div = new Div();
        var changePassword2 = new NativeLabel("Your password must be changed.");
        changePassword2Div.add(changePassword2);
        changePassword2Div.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        changePassword2Div.setVisible(true);
        add(changePassword2Div);

        currentPwField = new PasswordField();
        currentPwField.setLabel("Current Password:");
        currentPwField.setValue(password);
        var currentPwDiv = new Div();
        currentPwDiv.add(currentPwField);
        currentPwDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        add(currentPwDiv);

        newPwField = new PasswordField();
        newPwField.setLabel("New Password:");
        var newPwDiv = new Div();
        newPwDiv.add(newPwField);
        newPwDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        add(newPwDiv);

        confirmNewPw = new PasswordField();
        confirmNewPw.setLabel("Confirm new Password:");
        var confirmNewPwDiv = new Div();
        confirmNewPwDiv.add(confirmNewPw);
        confirmNewPwDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        confirmNewPw.addKeyPressListener(Key.ENTER, e -> this.changePasswordButton.click());
        add(confirmNewPwDiv);

        var changePasswordButtonContent = new Div();
        changePasswordButtonContent.getStyle()
                                   .set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        changePasswordButtonContent.getStyle().set("padding", "10px");
        changePasswordButton = new Button("Change Password");
        changePasswordButton.addClickListener(this::changePasswordButtonPressed);
        changePasswordButtonContent.add(changePasswordButton);

        add(changePasswordButtonContent);
    }

    private void changePasswordButtonPressed(ClickEvent<Button> buttonClickEvent) {
        if (currentPwField.isEmpty()) {
            Dialog errorDialog =
                new LoginErrorDialog("The Field 'Current Password' must not be empty!");
            errorDialog.open();
            return;
        }
        if (newPwField.isEmpty()) {
            Dialog errorDialog =
                new LoginErrorDialog("The Field 'New Password' must not be empty!");
            errorDialog.open();
            return;
        }
        if (confirmNewPw.isEmpty()) {
            Dialog errorDialog =
                new LoginErrorDialog("The Field 'Confirm new Password' must not be empty!");
            errorDialog.open();
            return;
        }
        if (!newPwField.getValue().equals(confirmNewPw.getValue())) {
            Dialog errorDialog = new LoginErrorDialog(
                "The Fields 'New Password' and 'Confirm new Password' must have the same values!");
            newPwField.clear();
            confirmNewPw.clear();
            errorDialog.open();
            return;
        }

        String currentPw = currentPwField.getValue();
        String newPw = newPwField.getValue();

        try {
            userService.changePasswordLogin(username, currentPw, newPw);
        } catch (UserLoginException e1) {
            currentPwField.clear();
            newPwField.clear();
            confirmNewPw.clear();
            var errorDialog = new LoginErrorDialog(e1.getMessage());
            errorDialog.open();
            return;
        }

        this.getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        close();
    }
}
