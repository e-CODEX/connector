/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.persistence.service;

import eu.ecodex.connector.ui.dto.WebReportEntry;
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
