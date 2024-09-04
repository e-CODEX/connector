/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.tools.logging;

import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.MarkerManager;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * Class to manage logging Markers.
 */
@UtilityClass
public class LoggingMarker {
    public static final String CONFIG_MARKER_TEXT = "CONFIG";
    public static final Marker CONFIG = MarkerFactory.getMarker(CONFIG_MARKER_TEXT);
    public static final String BUSINESS_LOG_MARKER_TEXT = "BUSINESS";
    public static final String BUSINESS_CERT_LOG_MARKER_TEXT = "BUSINESS_CERT";
    public static final String BUSINESS_EVIDENCE_LOG_MARKER_TEXT = "BUSINESS_EVIDENCE";
    public static final String BUSINESS_CONTENT_LOG_MARKER_TEXT = "BUSINESS_CONTENT";
    public static final String UI_LOG_MARKER_TEXT = "UI";
    public static final String TEST_LOG_MARKER_TEXT = "TEST";
    public static final Marker BUSINESS_LOG = MarkerFactory.getMarker(BUSINESS_LOG_MARKER_TEXT);
    public static final Marker BUSINESS_CERT_LOG =
        MarkerFactory.getMarker(BUSINESS_CERT_LOG_MARKER_TEXT);
    public static final Marker BUSINESS_EVIDENCE_LOG =
        MarkerFactory.getMarker(BUSINESS_EVIDENCE_LOG_MARKER_TEXT);
    public static final Marker BUSINESS_CONTENT_LOG =
        MarkerFactory.getMarker(BUSINESS_CONTENT_LOG_MARKER_TEXT);
    public static final Marker UI_LOG = MarkerFactory.getMarker(UI_LOG_MARKER_TEXT);

    /**
     * Class representing log markers for Log4j.
     */
    @UtilityClass
    public static class Log4jMarker {
        public static final org.apache.logging.log4j.Marker UI_LOG =
            MarkerManager.getMarker(UI_LOG_MARKER_TEXT);
        public static final org.apache.logging.log4j.Marker CONFIG =
            MarkerManager.getMarker(CONFIG_MARKER_TEXT);
        public static final org.apache.logging.log4j.Marker BUSINESS_LOG =
            MarkerManager.getMarker(BUSINESS_LOG_MARKER_TEXT);
        public static final org.apache.logging.log4j.Marker TEST_LOG =
            MarkerManager.getMarker(TEST_LOG_MARKER_TEXT);
    }

    static {
        BUSINESS_CERT_LOG.add(BUSINESS_LOG);
        BUSINESS_EVIDENCE_LOG.add(BUSINESS_LOG);
        BUSINESS_CONTENT_LOG.add(BUSINESS_LOG);
    }
}
