/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
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
