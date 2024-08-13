/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

/**
 * The WebMessageFileType enum represents the different types of files that can be attached to a web
 * message.
 *
 * <p>This enum is used in the WebMessageFile class to specify the type of a file, and in several
 * methods of other classes for handling web messages.
 */
public enum WebMessageFileType {
    BUSINESS_CONTENT,
    BUSINESS_DOCUMENT,
    BUSINESS_ATTACHMENT,
    DETACHED_SIGNATURE
}
