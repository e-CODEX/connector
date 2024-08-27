/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.enums.UserRole;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * The UserList class represents a vertical layout component that displays a list of users in a web
 * application.
 */
@Component
@UIScope
@Route(value = UserList.ROUTE, layout = UserLayout.class)
@Order(1)
@TabMetadata(title = "All Users", tabGroup = UserLayout.TAB_GROUP_NAME)
public class UserList extends VerticalLayout implements AfterNavigationObserver {
    public static final String ROUTE = "userlist";
    private final Grid<WebUser> grid = new Grid<>();
    private final List<WebUser> fullList;
    private final WebUserService userService;
    private final UserDetails userDetails;
    TextField usernameFilterText = new TextField();
    ComboBox<UserRole> roleFilterBox = new ComboBox<>();
    LumoCheckbox lockedFilterBox = new LumoCheckbox();

    /**
     * Constructor.
     *
     * @param service     The WebUserService instance used for performing operations related to user
     *                    management.
     * @param userDetails The UserDetails instance representing the current user.
     * @see WebUserService
     */
    public UserList(@Autowired WebUserService service, @Autowired UserDetails userDetails) {
        this.userService = service;
        this.userDetails = userDetails;

        fullList = new ArrayList<>();

        grid.setItems(fullList);
        grid.addComponentColumn(this::getDetailsLink).setHeader("Details")
            .setWidth("30px");
        grid.addColumn(WebUser::getUsername).setHeader("Username").setWidth("250px")
            .setSortable(true).setResizable(true);
        grid.addColumn(WebUser::getRole).setHeader("Role").setWidth("70px").setSortable(true)
            .setResizable(true);
        grid.addComponentColumn(webUser -> getCheckboxForList(webUser.isLocked()))
            .setHeader("Locked").setWidth("30px");
        grid.setWidth("1800px");
        grid.setHeight("700px");
        grid.setMultiSort(true);

        HorizontalLayout filtering = createFilterLayout();

        var main = new VerticalLayout(filtering, grid);
        main.setAlignItems(Alignment.STRETCH);
        main.setHeight("700px");
        add(main);
        setHeight("100vh");
        setWidth(UiStyle.WIDTH_100_VW);
    }

    private HorizontalLayout createFilterLayout() {

        usernameFilterText.setPlaceholder("Filter by Username");
        usernameFilterText.setWidth(UiStyle.WIDTH_180_PX);
        usernameFilterText.setValueChangeMode(ValueChangeMode.EAGER);
        usernameFilterText.addValueChangeListener(e -> filter());

        roleFilterBox.setPlaceholder("Filter by role");
        roleFilterBox.setWidth(UiStyle.WIDTH_180_PX);
        roleFilterBox.setItems(EnumSet.allOf(UserRole.class));
        roleFilterBox.addValueChangeListener(e -> filter());

        lockedFilterBox.setLabel("Filter by locked");
        lockedFilterBox.setWidth(UiStyle.WIDTH_180_PX);

        lockedFilterBox.addValueChangeListener(e -> filter());

        var clearAllFilterTextBtn = new Button(
            new Icon(VaadinIcon.CLOSE_CIRCLE));
        clearAllFilterTextBtn.setText("Clear Filter");
        clearAllFilterTextBtn.addClickListener(e -> {
            usernameFilterText.clear();
            roleFilterBox.clear();
            lockedFilterBox.clear();
            reloadList();
        });

        var reloadListBtn = new Button(new Icon(VaadinIcon.REFRESH));
        reloadListBtn.setText("Reload Users");
        reloadListBtn.addClickListener(e -> reloadList());

        var filtering = new HorizontalLayout(
            usernameFilterText,
            roleFilterBox,
            lockedFilterBox,
            clearAllFilterTextBtn,
            reloadListBtn
        );
        filtering.setAlignItems(Alignment.CENTER);
        filtering.setWidth(UiStyle.WIDTH_100_VW);

        return filtering;
    }

    private void filter() {
        List<WebUser> target = new LinkedList<>();
        for (WebUser msg : fullList) {
            if ((usernameFilterText.getValue().isEmpty()
                || (msg.getUsername() != null
                && msg.getUsername()
                      .toUpperCase()
                      .contains(usernameFilterText.getValue().toUpperCase())
            )
            ) && (roleFilterBox.getValue() == null || (msg.getRole() != null
                && msg.getRole().equals(roleFilterBox.getValue())
            )
            )
                && (lockedFilterBox.getValue() == null
                || (msg.isLocked() == lockedFilterBox.getValue()
            )
            )) {
                target.add(msg);
            }
        }

        grid.setItems(target);
    }

    private Button getDetailsLink(WebUser user) {
        var getDetails = new Button(new Icon(VaadinIcon.SEARCH));
        getDetails.addClickListener(e -> userDetails.showDetails(user));
        return getDetails;
    }

    private Checkbox getCheckboxForList(boolean checked) {
        var isChecked = new Checkbox(checked);
        isChecked.setReadOnly(true);

        return isChecked;
    }

    public void reloadList() {
        grid.setItems(userService.getAllUsers());
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        reloadList();
    }
}

