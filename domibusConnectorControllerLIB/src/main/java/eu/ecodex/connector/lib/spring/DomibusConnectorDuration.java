/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.lib.spring;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import org.springframework.util.StringUtils;

/**
 * The DomibusConnectorDuration class represents a duration of time in the Domibus connector. It
 * provides methods to convert the duration to milliseconds and to obtain a Duration object.
 */
public class DomibusConnectorDuration {
    private static final Map<String, TimeUnit> UNITS = new HashMap<>() {
        {
            put("ms", TimeUnit.MILLISECONDS);
            put("s", TimeUnit.SECONDS);
            put("m", TimeUnit.MINUTES);
            put("h", TimeUnit.HOURS);
            put("d", TimeUnit.DAYS);
        }
    };
    private static final Pattern PARSE_PATTERN = Pattern.compile("([0-9]+)(ms|s|m|h|d)");
    private final long duration;
    private final TimeUnit unit;

    private DomibusConnectorDuration(long value, TimeUnit unit) {
        this.duration = value;
        this.unit = unit;
    }

    public DomibusConnectorDuration(long milliseconds) {
        this(milliseconds, TimeUnit.MILLISECONDS);
    }

    public long getMilliseconds() {
        return unit.toMillis(duration);
    }

    public Duration getDuration() {
        return Duration.ofMillis(getMilliseconds());
    }

    /**
     * Factory method constructing an instance from a string.
     *
     * @param s the input string representing the duration
     * @return a {@link DomibusConnectorDuration} instance.
     * @throws NumberFormatException in case the string does not represent a valid duration
     */
    public static DomibusConnectorDuration valueOf(String s) throws NumberFormatException {
        if (!StringUtils.hasText(s)) {
            throw new NumberFormatException(
                "Null or empty string cannot be converted to duration.");
        }

        var m = PARSE_PATTERN.matcher(s.trim());
        if (!m.matches()) {
            throw new NumberFormatException("Invalid duration format: " + s);
        }

        var value = Long.valueOf(m.group(1));
        String unit = m.group(2);

        return new DomibusConnectorDuration(value, UNITS.get(unit));
    }
}
