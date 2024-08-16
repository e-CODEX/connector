/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.login.LoginView;
import eu.domibus.connector.ui.utils.UiStyle;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * The UserInfo class is a component that represents the user information section in a UI layout.
 */
@UIScope
@org.springframework.stereotype.Component
public class UserInfo extends HorizontalLayout implements AfterNavigationObserver {
    LumoLabel username;

    /**
     * Constructor.
     */
    public UserInfo() {
        var userDiv = new HorizontalLayout();
        var userIcon = new Icon(VaadinIcon.USER);
        userIcon.getStyle().set("margin-right", "10px");
        userIcon.setSize("20px");
        userDiv.add(userIcon);
        username = new LumoLabel("");
        username.getStyle().set("font-size", "15px");
        userDiv.add(username);
        add(userDiv);

        var logoutDiv = new Div();
        logoutDiv.getStyle().set("text-align", "center");
        logoutDiv.getStyle().set("padding", "10px");
        var logoutButton = new Button("Logout");
        logoutButton.addClickListener(e -> {
            var logout2Div = new Div();
            var logoutText = new Label("Logout call success!");
            logoutText.getStyle().set("font-weight", "bold");
            logoutText.getStyle().set("color", "red");
            logout2Div.add(logoutText);
            logout2Div.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
            logout2Div.setVisible(true);

            var logoutDialog = new Dialog();
            logoutDialog.add(logout2Div);

            var okContent = new Div();
            okContent.getStyle().set(UiStyle.ALIGNMENT_STYLE, UiStyle.ALIGNMENT_CENTER);
            okContent.getStyle().set("padding", "10px");
            var okButton = new Button("OK");
            okButton.addClickListener(e2 -> {
                SecurityContextHolder.getContext().setAuthentication(null);
                logoutDialog.close();
                this.getUI().ifPresent(ui -> ui.navigate(LoginView.class));
            });
            okContent.add(okButton);

            logoutDialog.add(okContent);
            logoutDialog.open();
        });
        logoutDiv.add(logoutButton);
        add(logoutDiv);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(
            com.vaadin.flow.component.orderedlayout.FlexComponent.JustifyContentMode.END);
        setWidth("95%");
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        var context = SecurityContextHolder.getContext();
        if (context != null) {
            Object principal = context.getAuthentication().getPrincipal();
            if (principal instanceof WebUser webUser) {
                this.username.setText(webUser.getUsername());
            } else {
                this.username.setText(principal.toString());
            }
        }
    }
}
