/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.dto;

import eu.ecodex.connector.ui.enums.UserRole;
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
