/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.dto;

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
