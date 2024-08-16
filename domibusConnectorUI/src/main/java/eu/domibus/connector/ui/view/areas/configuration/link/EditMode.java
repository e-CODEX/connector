/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.configuration.link;

/**
 * The EditMode enum represents different modes for editing or viewing data.
 * It is used in various classes to determine the behavior and appearance of UI components.
 * The available edit modes are:
 * - EDIT: Indicates that the data is in editable mode, allowing users to make changes.
 * - VIEW: Indicates that the data is in read-only mode, allowing users to view but not modify the
 *      data.
 * - DEL: Indicates that the data is in read-only mode and is being deleted.
 * - CREATE: Indicates that the data is in editable mode for creating new entries.
 */
public enum EditMode {
    EDIT,
    VIEW,
    DEL,
    CREATE
}
