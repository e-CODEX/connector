/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.view.areas.users;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.enums.UserRole;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import java.util.EnumSet;
import java.util.Optional;
import javax.annotation.PostConstruct;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The NewUser class represents a component that allows the creation of a new user in a web
 * application.
 *
 * @see VerticalLayout
 * @see Component
 * @see UIScope
 * @see Route
 * @see TabMetadata
 * @see WebUserService
 */
@Component
@UIScope
@Route(value = NewUser.ROUTE, layout = UserLayout.class)
@Order(3)
@TabMetadata(title = "Add new User", tabGroup = "User")
@SuppressWarnings("squid:S1135")
public class NewUser extends VerticalLayout {
    public static final String ROUTE = "newuser";
    private final WebUserService userService;
    TextField username = new TextField("Username");
    TextField initialPassword = new TextField("Initial password");
    ComboBox<UserRole> role = new ComboBox<>();

    /**
     * Constructor.
     *
     * @param service The WebUserService used to create the new user.
     */
    public NewUser(WebUserService service) {
        this.userService = service;
    }

    @PostConstruct
    void init() {
        initialPassword.setMinLength(4);

        var newUserArea = new VerticalLayout();

        newUserArea.add(username);

        role.setLabel("Role");
        role.setItems(EnumSet.allOf(UserRole.class));

        newUserArea.add(role);

        newUserArea.add(initialPassword);

        var createUserResult = new Div();

        var resultLabel = new LumoLabel("");
        resultLabel.getStyle().set("font-size", "20px");
        createUserResult.add(resultLabel);
        createUserResult.setWidth(UiStyle.WIDTH_100_VW);

        var createUser = new Button(
            new Icon(VaadinIcon.USER_CHECK));
        createUser.setText("Create User");
        createUser.addClickListener(e -> {
            Optional<String> createdUsername = createUser();
            if (createdUsername.isPresent()) {
                resultLabel.setText("The user was successfully created!");
                resultLabel.getStyle().set("color", "green");
                UI.getCurrent().navigate(UserDetails.class, createdUsername.get());
            } else {
                resultLabel.setText("The creation of the user failed!");
                resultLabel.getStyle().set("color", "red");
            }
        });
        newUserArea.add(createUser);

        setWidth(UiStyle.WIDTH_100_VW);
        add(newUserArea);
        add(createUserResult);
    }

    private Optional<String> createUser() {
        // TODO: this just a little bit of validation - one could create users with empty string
        if (username.getValue().isEmpty() || initialPassword.isEmpty()
            || initialPassword.getValue().length() < initialPassword.getMinLength()) {
            return Optional.empty();
        }

        var newUser = new WebUser();

        newUser.setUsername(username.getValue());
        newUser.setRole(role.getValue());
        newUser.setPassword(initialPassword.getValue());

        return userService.createNewUser(newUser)
            ? Optional.of(username.getValue())
            : Optional.empty();
    }
}
