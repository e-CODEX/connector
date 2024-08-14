/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.persistence.service;

import eu.domibus.connector.ui.dto.WebReportEntry;
import java.util.Date;
import java.util.List;

/**
 * The DomibusConnectorWebReportPersistenceService interface provides methods to load web-based
 * reports from the database. It has two methods to load reports: one with evidence and another one
 * without evidence.
 */
public interface DomibusConnectorWebReportPersistenceService {
    List<WebReportEntry> loadReportWithEvidences(Date fromDate, Date toDate);

    List<WebReportEntry> loadReport(Date fromDate, Date toDate);
}
