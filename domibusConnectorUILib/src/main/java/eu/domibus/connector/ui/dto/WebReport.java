/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

package eu.domibus.connector.ui.dto;

import java.util.List;
import lombok.Data;

/**
 * The WebReport class represents a web-based report that contains information about a specific
 * period of time. It includes details such as the period, year, month, total messages sent, total
 * messages received, and a list of report entries.
 */
@Data
public class WebReport {
    private String period;
    private String year;
    private String month;
    private long sumSent;
    private long sumReceived;
    private List<WebReportEntry> entries;
}
