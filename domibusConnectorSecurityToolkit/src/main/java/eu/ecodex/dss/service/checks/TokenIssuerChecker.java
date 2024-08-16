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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/checks/TokenIssuerChecker.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.checks;

import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.model.token.TokenIssuer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * Tests that the tokenissuer is not null, the service provider is set, the country is an ISO 3166
 * 2-letter code and the advancedelectronicsystem is set.
 *
 * <p>DISCLAIMER: Project owner e-CODEX</p>
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class TokenIssuerChecker extends AbstractChecker<TokenIssuer> {
    private static final Set<String> ISO_COUNTRIES_LOOKUP;

    static {
        final String[] countries = Locale.getISOCountries();
        ISO_COUNTRIES_LOOKUP = new HashSet<>(countries == null ? 1 : countries.length);
        if (countries != null) {
            for (final String country : countries) {
                ISO_COUNTRIES_LOOKUP.add(country.toLowerCase());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CheckResult run(final TokenIssuer object) {
        final var checkResult = new CheckResult();

        if (object == null) {
            detect(checkResult, true, "the token issuer must not be null");
            return checkResult;
        }

        if (StringUtils.isEmpty(object.getServiceProvider())) {
            detect(
                checkResult,
                true,
                "the token issuer's attribute 'service provider' must not be null or empty"
            );
        }
        if (StringUtils.isEmpty(object.getCountry())) {
            detect(
                checkResult,
                true,
                "the token issuer's attribute 'country' must not be null or empty"
            );
        } else {
            if (!ISO_COUNTRIES_LOOKUP.contains(object.getCountry().toLowerCase())) {
                detect(
                    checkResult,
                    true,
                    "the token issuer's attribute 'country' is not a valid (is must be a "
                        + "2-letter country code defined in ISO 3166)"
                );
            }
        }
        if (object.getAdvancedElectronicSystem() == null) {
            detect(
                checkResult, true,
                "the token issuer's attribute 'advanced electronic system' must not be null"
            );
        }

        return checkResult;
    }
}
