/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
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
