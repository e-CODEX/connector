/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.persistence.dao;

import eu.domibus.connector.ui.dto.WebReportEntry;
import java.util.Date;
import java.util.List;

/**
 * The DomibusConnectorWebReportDao interface provides methods to load web-based reports from the
 * database.
 */
public interface DomibusConnectorWebReportDao {
    List<WebReportEntry> loadReportWithEvidences(Date fromDate, Date toDate);

    List<WebReportEntry> loadReport(Date fromDate, Date toDate);
}
