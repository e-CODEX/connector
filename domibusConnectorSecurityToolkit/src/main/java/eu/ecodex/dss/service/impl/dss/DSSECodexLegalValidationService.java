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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/impl/dss/DSSECodexLegalValidationService.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.impl.dss;

import eu.ecodex.dss.model.token.AdvancedSystemType;
import eu.ecodex.dss.model.token.LegalTrustLevel;
import eu.ecodex.dss.model.token.LegalValidationResult;
import eu.ecodex.dss.model.token.Signature;
import eu.ecodex.dss.model.token.TechnicalTrustLevel;
import eu.ecodex.dss.model.token.TechnicalValidationResult;
import eu.ecodex.dss.model.token.Token;
import eu.ecodex.dss.model.token.TokenValidation;
import eu.ecodex.dss.service.ECodexException;
import eu.ecodex.dss.service.ECodexLegalValidationService;
import eu.ecodex.dss.util.LogDelegate;
import java.util.List;

/**
 * The DSS implementation of the services.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class DSSECodexLegalValidationService implements ECodexLegalValidationService {
    private static final LogDelegate LOG = new LogDelegate(DSSECodexLegalValidationService.class);
    public static final String E_CODEX_DOES_NOT_APPROVE_THE_VALIDITY_OF_THE_DOCUMENT =
        "e-CODEX does not approve the validity of the document.";
    public static final String CREATE_LOG = "create";
    public static final String REQUIREMENTS_TO_BE_LEGALLY_VALID_IN_THE_SENDING_COUNTRY =
        "e-CODEX approves the validity of the document. It is attested that it fulfils the "
            + "requirements to be legally valid in the sending country.";

    /**
     * {@inheritDoc}
     *
     * <p>The approach is quite simple: If the technical validation result is
     * {@link TechnicalTrustLevel#SUCCESSFUL} then the legal result is also
     * {@link LegalTrustLevel#SUCCESSFUL}. In all other cases it is
     * {@link LegalTrustLevel#NOT_SUCCESSFUL}.
     */
    @Override
    public LegalValidationResult create(final Token token) throws ECodexException {
        LOG.mEnter(CREATE_LOG, token);
        try {
            return createImpl(token);
        } catch (Exception e) {
            LOG.mCause(CREATE_LOG, e, token);
            throw ECodexException.wrap(e);
        } finally {
            LOG.mExit(CREATE_LOG, token);
        }
    }

    /**
     * Creates a LegalValidationResult based on the given Token.
     *
     * @param token The Token object to create legal validation result from.
     * @return The created LegalValidationResult object.
     * @throws ECodexException If the token is null or it does not have the necessary validation
     *                         objects.
     */
    private LegalValidationResult createImpl(final Token token) throws ECodexException {
        if (token == null) {
            throw new ECodexException("the token (in parameter) must not be null");
        }
        final var tokenValidation = token.getValidation();
        if (tokenValidation == null) {
            throw new ECodexException("the token (in parameter) must have a validation object");
        }
        final TechnicalValidationResult technicalResult = tokenValidation.getTechnicalResult();
        if (technicalResult == null) {
            throw new ECodexException(
                "the token (in parameter) must have a technical validation result object");
        }

        final var result = new LegalValidationResult();
        final TechnicalTrustLevel trustLevel = technicalResult.getTrustLevel();

        TokenValidation val = token.getValidation();
        var verificationData = val.getVerificationData();
        List<Signature> signatures =
            (verificationData != null) ? verificationData.getSignatureData() : null;

        // klara: added switch for authentication-based certificate decision      
        if (token.getAdvancedElectronicSystem() == AdvancedSystemType.AUTHENTICATION_BASED) {
            if (signatures != null && signatures.size() == 1
                && signatures.getFirst().getAuthenticationCertValidation() != null
                && signatures.getFirst().getAuthenticationCertValidation().isValidationSuccessful()
                && token.getTechnicalValidationResult().getTrustLevel()
                        .equals(TechnicalTrustLevel.SUCCESSFUL)) {
                result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
                result.setDisclaimer(
                    "e-CODEX approves the validity of the document. The signature on the document "
                        + "has been created by a valid authentication service provider. It is "
                        + "attested that the document fulfils the requirements to be legally "
                        + "valid in the sending country.");
            } else if (signatures != null && !signatures.isEmpty() && signatures.size() > 1) {
                result.setTrustLevel(LegalTrustLevel.NOT_SUCCESSFUL);
                result.setDisclaimer(
                    "e-CODEX does not approve the validity of the document. There are too many "
                        + "signatures of authentication service providers present."
                );
            } else if (!token.getTechnicalValidationResult().getTrustLevel()
                             .equals(TechnicalTrustLevel.SUCCESSFUL)) {
                result.setTrustLevel(LegalTrustLevel.NOT_SUCCESSFUL);
                result.setDisclaimer(E_CODEX_DOES_NOT_APPROVE_THE_VALIDITY_OF_THE_DOCUMENT);
            } else if ((signatures == null || signatures.isEmpty())
                && token.getValidationVerificationAuthenticationData() != null
                && trustLevel == TechnicalTrustLevel.SUCCESSFUL) {
                result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
                result.setDisclaimer(
                    REQUIREMENTS_TO_BE_LEGALLY_VALID_IN_THE_SENDING_COUNTRY
                );
            } else {
                result.setTrustLevel(LegalTrustLevel.NOT_SUCCESSFUL);
                result.setDisclaimer(
                    "e-CODEX does not approve the validity of the document. The signatory of the "
                        + "document could not be verified as being a valid authentication service "
                        + "provider!"
                );
            }
        } else {

            var validCount = 0;
            var invalidCount = 0;

            if (signatures != null) {
                for (var signature : signatures) {
                    var techResult = signature.getTechnicalResult();

                    if (techResult != null) {
                        var curLvl = techResult.getTrustLevel();

                        if (curLvl == TechnicalTrustLevel.SUCCESSFUL) {
                            validCount++;
                        } else {
                            invalidCount++;
                        }
                    }
                }
            }

            if (invalidCount == 0 && validCount == 0) {
                if (trustLevel == TechnicalTrustLevel.SUCCESSFUL) {
                    result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
                    result.setDisclaimer(REQUIREMENTS_TO_BE_LEGALLY_VALID_IN_THE_SENDING_COUNTRY);
                } else {
                    result.setTrustLevel(LegalTrustLevel.NOT_SUCCESSFUL);
                    result.setDisclaimer(E_CODEX_DOES_NOT_APPROVE_THE_VALIDITY_OF_THE_DOCUMENT);
                }
            } else if (invalidCount > 0 && validCount > 0) {
                result.setTrustLevel(LegalTrustLevel.NOT_SUCCESSFUL);
                result.setDisclaimer(
                    "e-CODEX cannot approve the validity of the document as there are valid "
                        + "and invalid signatures present on the document. Please check the "
                        + "technical validation report!"
                );
            } else if (validCount > 0) {
                result.setTrustLevel(LegalTrustLevel.SUCCESSFUL);
                result.setDisclaimer(REQUIREMENTS_TO_BE_LEGALLY_VALID_IN_THE_SENDING_COUNTRY);
            } else {
                result.setTrustLevel(LegalTrustLevel.NOT_SUCCESSFUL);
                result.setDisclaimer(E_CODEX_DOES_NOT_APPROVE_THE_VALIDITY_OF_THE_DOCUMENT);
            }
        }

        LOG.lInfo(
            "technical trust-level is {}} -> legal trust-level {}", trustLevel,
            result.getTrustLevel()
        );

        return result;
    }
}
