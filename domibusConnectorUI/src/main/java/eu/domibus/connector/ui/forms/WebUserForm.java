/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.forms;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.enums.UserRole;
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
