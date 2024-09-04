/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.persistence.service.impl;

import eu.ecodex.connector.ui.dto.WebReportEntry;
import eu.ecodex.connector.ui.persistence.dao.DomibusConnectorWebReportDao;
import eu.ecodex.connector.ui.persistence.service.DomibusConnectorWebReportPersistenceService;
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
