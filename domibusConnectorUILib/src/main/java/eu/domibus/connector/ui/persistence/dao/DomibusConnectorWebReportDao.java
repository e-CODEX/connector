package eu.domibus.connector.ui.persistence.dao;

import java.util.Date;
import java.util.List;

import eu.domibus.connector.ui.dto.WebReportEntry;

public interface DomibusConnectorWebReportDao {

	List<WebReportEntry> loadReportWithEvidences(Date fromDate, Date toDate);

	List<WebReportEntry> loadReport(Date fromDate, Date toDate);

}
