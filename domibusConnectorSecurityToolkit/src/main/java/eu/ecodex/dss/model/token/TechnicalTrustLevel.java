/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/token/TechnicalTrustLevel.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model.token;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlEnumValue;
import jakarta.xml.bind.annotation.XmlType;
import lombok.Getter;
import org.apache.commons.lang.StringUtils;

/**
 * This class holds the type of the trust level.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@XmlType(name = "TechnicalTrustLevelEnum")
@XmlEnum
@Getter
public enum TechnicalTrustLevel {
    /**
     * Aka RED.
     */
    @XmlEnumValue("FAIL")
    FAIL("FAIL", "Failed"),

    /**
     * Aka YELLOW.
     */
    @XmlEnumValue("SUFFICIENT")
    SUFFICIENT("SUFFICIENT", "Sufficient"),

    /**
     * Aka GREEN.
     */
    @XmlEnumValue("SUCCESSFUL")
    SUCCESSFUL("SUCCESSFUL", "Successful");
    private final String value;
    private final String text;

    /**
     * Constructor.
     *
     * @param value the value "FAIL" or "SUFFICIENT" or "SUCCESSFUL"
     * @param text  the textual representation "Fail" or "Sufficient" or "Successful"
     */
    TechnicalTrustLevel(final String value, final String text) {
        this.value = value;
        this.text = text;
    }

    /**
     * The underlying string value.
     *
     * @return "SUCCESSFUL" or "NOT_SUCCESSFUL"
     */
    public String value() {
        return getValue();
    }

    /**
     * Checks whether the parameter is {@link #FAIL}.
     *
     * @param level the optional value
     * @return true, if {@link #FAIL}
     */
    public static boolean isFail(final TechnicalTrustLevel level) {
        return level == FAIL;
    }

    /**
     * Checks whether the parameter is {@link #SUFFICIENT}.
     *
     * @param level the optional value
     * @return true, if {@link #SUFFICIENT}
     */
    public static boolean isSufficient(final TechnicalTrustLevel level) {
        return level == SUFFICIENT;
    }

    /**
     * Checks whether the parameter is {@link #SUCCESSFUL}.
     *
     * @param level the optional value
     * @return true, if {@link #SUCCESSFUL}
     */
    public static boolean isSuccessful(final TechnicalTrustLevel level) {
        return level == SUCCESSFUL;
    }

    /**
     * Detects the worst level in the provided array of levels: the precedence order is.
     * {@link #FAIL}, {@link #SUFFICIENT}, {@link #SUCCESSFUL}
     *
     * @param levels the values (with null supported)
     * @return null, if levels are null or empty; otherwise the detected worst level
     */
    public static TechnicalTrustLevel worst(final TechnicalTrustLevel... levels) {
        if (levels == null || levels.length == 0) {
            return null;
        }

        TechnicalTrustLevel result = null;

        for (final TechnicalTrustLevel level : levels) {
            if (level == null) {
                // ignore null values
                continue;
            }
            if (result == null) {
                // initialise the result
                result = level;
            } else if (isFail(level)) {
                // the worst case
                result = level;
            } else if (isSufficient(level)) {
                // check if can apply the level (that is not overwrite a worse value)
                if (!isFail(result)) {
                    result = level;
                }
            } else if (isSuccessful(level)) {
                // check if can apply the level (that is not overwrite a worse value)
                if (!isFail(result) && !isSufficient(result)) {
                    result = level;
                }
            }
            if (isFail(result)) {
                return result; // this is the worst case, so we can ignore all others
            }
        }

        return result;
    }

    /**
     * factory retrieval method; if the instance is not found, then an IllegalArgumentException is
     * thrown.
     *
     * @param v either "SUCCESSFUL" or "SUFFICIENT" or "FAIL"
     * @return the enum instance
     */
    public static TechnicalTrustLevel fromValue(final String v) {
        if (StringUtils.isEmpty(v)) {
            throw new IllegalArgumentException("value must not be empty");
        }
        for (TechnicalTrustLevel c : TechnicalTrustLevel.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}
