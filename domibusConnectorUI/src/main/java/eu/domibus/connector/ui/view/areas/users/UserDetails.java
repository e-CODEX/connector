/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.users;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.forms.WebUserForm;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The UserDetails class represents a vertical layout component that displays the details of a
 * user.
 *
 * @see VerticalLayout
 * @see HasUrlParameter
 * @see WebUser
 * @see WebUserService
 * @see UserLayout
 * @see WebUserForm
 * @see LumoLabel
 * @see Button
 * @see Icon
 * @see VaadinIcon
 * @see TextField
 * @see Div
 * @see HorizontalLayout
 * @since 1.0
 */
@Component
@UIScope
@Route(value = UserDetails.ROUTE, layout = UserLayout.class)
@Order(2)
@TabMetadata(title = "User Details", tabGroup = UserLayout.TAB_GROUP_NAME)
public class UserDetails extends VerticalLayout implements HasUrlParameter<String> {
    public static final String ROUTE = "userdetails";
    private WebUser user;
    private final WebUserService userService;
    private final WebUserForm userForm = new WebUserForm();
    LumoLabel result = new LumoLabel("");

    /**
     * Constructor.
     *
     * @param service The WebUserService used for performing user-related operations.
     */
    public UserDetails(@Autowired WebUserService service) {
        this.userService = service;

        var resultDiv = new Div();

        result.getStyle().set("font-size", "20px");
        resultDiv.add(result);
        resultDiv.setWidth("100vw");

        var userDetailsArea = new VerticalLayout();

        userDetailsArea.add(userForm);

        userForm.setEnabled(true);

        var edit = new Button(new Icon(VaadinIcon.EDIT));
        edit.getElement().setAttribute("title", "Save User");
        edit.setText("Save User");
        edit.addClickListener(e -> {
            boolean success = userService.saveUser(userForm.getUser());
            if (success) {
                result.setText("The user was successfully updated!");
                result.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);
            } else {
                result.setText("The update of the user failed!");
                result.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);
            }
        });

        userDetailsArea.add(edit);

        userDetailsArea.setWidth("500px");
        add(userDetailsArea);

        var reset = new Div();
        var resetPasswordLabel = new LumoLabel("Reset the user's password");
        resetPasswordLabel.getStyle().set(UiStyle.FONT_SIZE_STYLE, "20px");
        reset.add(resetPasswordLabel);

        add(reset);

        var resetPasswordArea = new HorizontalLayout();

        var newInitialPassword = new TextField("New initial Password:");
        resetPasswordArea.add(newInitialPassword);

        var resetPasswordButton = new Button(new Icon(VaadinIcon.USER_CHECK));
        resetPasswordButton.setText("Reset Password");
        resetPasswordButton.addClickListener(e -> {
            boolean success = userService.resetUserPassword(user, newInitialPassword.getValue());
            if (success) {
                result.setText("The password was successfully reset to the new initial password!");
                result.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);
            } else {
                result.setText("The reset of the password failed!");
                result.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);
            }
        });
        resetPasswordArea.add(resetPasswordButton);
        resetPasswordArea.setAlignItems(Alignment.END);
        resetPasswordArea.setHeight("80px");
        resetPasswordArea.setWidth(UiStyle.WIDTH_100_VW);
        add(resetPasswordArea);

        add(resultDiv);
    }

    public void showDetails(WebUser user) {
        UI.getCurrent().navigate(UserDetails.class, user.getUsername());
    }

    /**
     * Sets the WebUser for the UserDetails class.
     *
     * @param user the WebUser to be set as the current user.
     */
    public void setUser(WebUser user) {
        this.result.setText("");
        this.user = user;
        userForm.setUser(user);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String username) {
        var webUser = userService.getAllUsers()
                                 .stream()
                                 .filter(u -> u.getUsername().equals(username))
                                 .findFirst()
                                 .orElse(new WebUser());
        userForm.setUser(webUser);
    }
}
