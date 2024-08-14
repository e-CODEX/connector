/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

import eu.domibus.connector.ui.enums.UserRole;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The WebUser class represents a user in a web application. Each WebUser has a username, role,
 * password, locked status, and creation date.
 *
 * <p>Note: The UserRole enumeration determines the possible roles for a WebUser.
 *
 * @see UserRole
 */
@Data
@NoArgsConstructor
public class WebUser {
    private String username;
    private UserRole role;
    private String password;
    private boolean locked;
    private Date created;

    public String getCreatedString() {
        return created != null ? created.toString() : null;
    }
}
