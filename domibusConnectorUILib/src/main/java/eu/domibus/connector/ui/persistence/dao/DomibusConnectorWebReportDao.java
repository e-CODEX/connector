package eu.domibus.connector.ui.persistence.dao;

import eu.domibus.connector.ui.dto.WebReportEntry;

import java.util.Date;
import java.util.List;


public interface DomibusConnectorWebReportDao {
    List<WebReportEntry> loadReportWithEvidences(Date fromDate, Date toDate);

    List<WebReportEntry> loadReport(Date fromDate, Date toDate);
}
