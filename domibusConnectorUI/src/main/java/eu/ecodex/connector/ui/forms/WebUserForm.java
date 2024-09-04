/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import eu.ecodex.connector.ui.component.LumoCheckbox;
import eu.ecodex.connector.ui.dto.WebUser;
import eu.ecodex.connector.ui.enums.UserRole;
import java.util.EnumSet;

/**
 * The WebUserForm class represents a form for displaying and editing the details of a web user. It
 * extends the FormLayout class provided by Vaadin Flow.
 *
 * @see FormLayout
 * @see Binder
 * @see TextField
 * @see ComboBox
 * @see LumoCheckbox
 * @see WebUser
 */
public class WebUserForm extends FormLayout {
    private final TextField username = FormsUtil.getFormattedTextFieldReadOnly();
    private final ComboBox<UserRole> roleBox = new ComboBox<>("", EnumSet.allOf(UserRole.class));
    private final LumoCheckbox lockedBox = new LumoCheckbox();
    private final TextField createdString = FormsUtil.getFormattedTextFieldReadOnly();
    private WebUser user = null;
    private final Binder<WebUser> binder = new Binder<>(WebUser.class);

    public WebUserForm() {
        fillForm();
    }

    private void fillForm() {
        binder.bindInstanceFields(this);

        addFormItem(username, "Username");
        addFormItem(roleBox, "User-Role");
        addFormItem(lockedBox, "Locked");
        addFormItem(createdString, "User created at");
    }

    /**
     * Sets the WebUser for the WebUserForm.
     *
     * @param user the WebUser to set for the form
     */
    public void setUser(WebUser user) {
        this.removeAll();
        if (user != null && user.getRole() != null) {
            roleBox.setValue(user.getRole());
        } else {
            roleBox.clear();
        }
        if (user != null) {
            lockedBox.setValue(user.isLocked());
        }
        fillForm();
        this.user = user;
        binder.setBean(user);
    }

    /**
     * Retrieves the WebUser object associated with this WebUserForm.
     *
     * @return the WebUser object associated with this WebUserForm
     */
    public WebUser getUser() {
        this.user.setLocked(lockedBox.getValue());
        this.user.setRole(roleBox.getValue());
        return this.user;
    }
}
