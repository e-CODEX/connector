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
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.exception.InitialPasswordException;
import eu.ecodex.connector.ui.exception.UserLoginException;
import eu.ecodex.connector.ui.service.WebUserService;
import eu.ecodex.connector.ui.utils.UiStyle;
import eu.ecodex.connector.ui.view.DashboardView;
import eu.ecodex.connector.ui.view.DomibusConnectorAdminHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

/**
 * Represents a login view for the application.
 */
@SuppressWarnings("squid:S1135")
@SpringComponent
@UIScope
@Route(value = LoginView.ROUTE)
@PageTitle("domibusConnector - Login")
public class LoginView extends VerticalLayout implements HasUrlParameter<String> {
    public static final String ROUTE = "login";
    public static final String PREVIOUS_ROUTE_PARAMETER = "afterLoginGoTo";
    private final AuthenticationManager authenticationManager;
    private final WebUserService webUserService;
    private final LoginOverlay login = new LoginOverlay();
    private String afterLoginGoTo = DashboardView.ROUTE;
    private final TextField username = new TextField();
    PasswordField password = new PasswordField();

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (StringUtils.hasLength(parameter) && !LoginView.ROUTE.equals(parameter)) {
            afterLoginGoTo = parameter;
        }
    }

    /**
     * LoginView is a class that represents the view for user login functionality.
     *
     * @param userService           WebUserService instance used for user related operations
     * @param header                DomibusConnectorAdminHeader instance used for displaying header
     *                              information
     * @param authenticationManager AuthenticationManager instance used for user authentication
     */
    public LoginView(
        @Autowired WebUserService userService,
        @Autowired DomibusConnectorAdminHeader header,
        @Autowired AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
        this.webUserService = userService;

        login.setAction("login"); //
        getElement().appendChild(login.getElement());

        add(header);

        var loginButton = new Button("Login");

        username.setLabel("Username");
        username.setAutofocus(true);
        username.addKeyPressListener(
            Key.ENTER, (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );
        var usernameDiv = new Div();
        usernameDiv.add(username);
        usernameDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);

        var loginArea = new VerticalLayout();

        loginArea.add(usernameDiv);

        var passwordDiv = new Div();

        password.setLabel("Password");
        password.addKeyPressListener(
            Key.ENTER, (ComponentEventListener<KeyPressEvent>) event -> loginButton.click()
        );
        passwordDiv.add(password);
        passwordDiv.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        loginArea.add(passwordDiv);

        var loginButtonContent = new Div();
        loginButtonContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
        loginButtonContent.getStyle().set("padding", "10px");

        loginButton.addClickListener(this::loginButtonClicked);
        loginButtonContent.add(loginButton);

        var changePasswordButton = new Button("Change Password");
        changePasswordButton.addClickListener(e -> {
            if (username.getValue().isEmpty()) {
                Dialog errorDialog =
                    new LoginErrorDialog("The field \"Username\" must not be empty!");
                username.clear();
                password.clear();
                errorDialog.open();
                return;
            }
            Dialog changePasswordDialog =
                new ChangePasswordDialog(userService, username.getValue(), password.getValue());
            username.clear();
            password.clear();
            changePasswordDialog.open();
        });
        loginButtonContent.add(changePasswordButton);

        loginArea.add(loginButtonContent);

        loginArea.setSizeFull();
        loginArea.setAlignItems(Alignment.CENTER);
        loginArea.getStyle().set("align-items", UiStyle.ALIGNMENT_CENTER);

        var loginLayout = new HorizontalLayout();
        loginLayout.add(loginArea);
        loginLayout.setVerticalComponentAlignment(Alignment.CENTER, loginArea);

        add(loginArea);
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
            var authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username.getValue(), password.getValue())
            );
            SecurityContextHolder.getContext().setAuthentication(authenticate);
        } catch (UserLoginException e1) {
            Dialog errorDialog = new LoginErrorDialog(e1.getMessage());
            username.clear();
            password.clear();
            errorDialog.open();
            return;
        } catch (InitialPasswordException e1) {
            Dialog changePasswordDialog =
                new ChangePasswordDialog(webUserService, username.getValue(), password.getValue());
            username.clear();
            password.clear();
            changePasswordDialog.open();
        } catch (AuthenticationException authException) {
            // show error message...
        }
        // TODO: navigate to previous route...
        //  getUI().ifPresent(ui -> ui);
        this.getUI().ifPresent(ui -> ui.navigate(afterLoginGoTo));
    }
}
