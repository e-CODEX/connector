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
