/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.persistence.service.impl;

import eu.domibus.connector.ui.dto.WebReportEntry;
import eu.domibus.connector.ui.persistence.dao.DomibusConnectorWebReportDao;
import eu.domibus.connector.ui.persistence.service.DomibusConnectorWebReportPersistenceService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * The DomibusConnectorWebReportPersistenceServiceImpl class is an implementation of the
 * DomibusConnectorWebReportPersistenceService interface. It provides methods to load web-based
 * reports from the database. The class interacts with the DomibusConnectorWebReportDao to retrieve
 * the data. It has two methods to load reports: one with evidence and another one without
 * evidence.
 */
@org.springframework.stereotype.Service("webReportPersistenceService")
public class DomibusConnectorWebReportPersistenceServiceImpl
    implements DomibusConnectorWebReportPersistenceService {
    private DomibusConnectorWebReportDao reportDao;

    @Override
    public List<WebReportEntry> loadReportWithEvidences(Date fromDate, Date toDate) {
        return reportDao.loadReportWithEvidences(fromDate, toDate);
    }

    @Override
    public List<WebReportEntry> loadReport(Date fromDate, Date toDate) {
        return reportDao.loadReport(fromDate, toDate);
    }

    @Autowired
    public void setReportDao(DomibusConnectorWebReportDao reportDao) {
        this.reportDao = reportDao;
    }
}
