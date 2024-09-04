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

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import eu.ecodex.connector.ui.exception.InitialPasswordException;
import eu.ecodex.connector.ui.exception.UserLoginException;
import eu.ecodex.connector.ui.service.WebUserService;
import eu.ecodex.connector.ui.utils.UiStyle;
import eu.ecodex.connector.ui.view.DashboardView;

/**
 * The LoginDialog class represents a dialog that is used for user login.
 *
 * @see Dialog
 */
public class LoginDialog extends Dialog {
    private final WebUserService userService;
    private final Button loginButton = new Button("Login");
    private final Button changePasswordButton;
    private PasswordField password;
    private TextField username;

    /**
     * The LoginDialog class represents a dialog that allows users to enter their username and
     * password to log in to the system.
     *
     * @param userService the WebUserService instance used for login and password reset operations
     */
    public LoginDialog(WebUserService userService) {
        this.userService = userService;

        var usernameTextField = new TextField();
        usernameTextField.setLabel("Username");
        usernameTextField.setAutofocus(true);
        usernameTextField.addKeyPressListener(
            Key.ENTER, (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );
        var usernameDiv = new Div();
        usernameDiv.add(usernameTextField);
        usernameDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        add(usernameDiv);

        var passwordDiv = new Div();
        var passwordField = new PasswordField();
        passwordField.setLabel("Password");
        passwordField.addKeyPressListener(
            Key.ENTER, (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );
        passwordDiv.add(passwordField);
        passwordDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        add(passwordDiv);

        var loginButtonContent = new Div();
        loginButtonContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        loginButtonContent.getStyle().set("padding", "10px");

        loginButton.addClickListener(this::loginButtonClicked);
        loginButtonContent.add(loginButton);

        changePasswordButton = new Button("Change Password");
        changePasswordButton.addClickListener(e -> {
            if (usernameTextField.getValue().isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The field \"Username\" must not be empty!");
                usernameTextField.clear();
                passwordField.clear();
                errorDialog.open();
                return;
            }
            usernameTextField.clear();
            passwordField.clear();
            close();
            var changePasswordDialog = new ChangePasswordDialog(
                userService, usernameTextField.getValue(), passwordField.getValue()
            );
            changePasswordDialog.open();
        });
        loginButtonContent.add(changePasswordButton);

        add(loginButtonContent);
    }

    private void loginButtonClicked(ClickEvent<Button> buttonClickEvent) {
        if (username.getValue().isEmpty()) {
            Dialog errorDialog = new LoginErrorDialog("The field \"Username\" must not be empty!");
            username.clear();
            password.clear();
            errorDialog.open();
            return;
        }
        if (password.getValue().isEmpty()) {
            Dialog errorDialog = new LoginErrorDialog("The field \"Password\" must not be empty!");
            password.clear();
            errorDialog.open();
            return;
        }
        try {
            userService.login(username.getValue(), password.getValue());
        } catch (UserLoginException e1) {
            Dialog errorDialog = new LoginErrorDialog(e1.getMessage());
            username.clear();
            password.clear();
            errorDialog.open();
            return;
        } catch (InitialPasswordException e1) {
            username.clear();
            password.clear();
            close();
            var changePasswordDialog =
                new ChangePasswordDialog(userService, username.getValue(), password.getValue());
            changePasswordDialog.open();
        }
        this.getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        close();
    }
}
