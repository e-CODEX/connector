/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

import lombok.Data;

/**
 * The WebReportEntry class represents a single entry in a web-based report. It contains information
 * about the year, month, party, service, and message counts for both sent and received messages.
 */
@Data
public class WebReportEntry {
    private String year;
    private String month;
    private String party;
    private String service;
    private Integer received;
    private Integer sent;
}
