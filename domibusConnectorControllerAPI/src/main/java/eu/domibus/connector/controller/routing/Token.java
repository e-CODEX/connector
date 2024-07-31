/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.controller.routing;

import java.io.Serializable;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a token.
 */
@Data
@NoArgsConstructor
public class Token implements Serializable {
    TokenType tokenType;
    String value;
    int start;
    int end;
}
