/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
