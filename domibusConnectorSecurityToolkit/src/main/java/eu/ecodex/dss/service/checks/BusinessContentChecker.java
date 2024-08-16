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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/checks/BusinessContentChecker.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service.checks;

import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.checks.CheckResult;
import eu.ecodex.dss.service.ContainerFileDefinitions;
import eu.ecodex.dss.util.DocumentStreamUtil;
import eu.europa.esig.dss.model.DSSDocument;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

/**
 * Tests that the businesscontent is not null, at least one document with data and name is provided.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public class BusinessContentChecker extends AbstractChecker<BusinessContent> {
    /**
     * {@inheritDoc}
     */
    @Override
    public CheckResult run(final BusinessContent object) {
        final var checkResult = new CheckResult();

        if (object == null) {
            detect(checkResult, true, "the business content must not be null");
            return checkResult;
        }

        final Set<String> docNames = new HashSet<>();

        // check the mandatory business document
        final DSSDocument document = object.getDocument();
        checkCompliance(checkResult, docNames, "business-content", document);

        // check an optional detached signature
        final DSSDocument signature = object.getDetachedSignature();
        if (signature != null) {
            checkCompliance(checkResult, docNames, "detached-signature", signature);
        }

        // check the optional attachments
        final List<DSSDocument> attachments = object.getAttachments();
        for (var i = 0; i < attachments.size(); i++) {
            final DSSDocument attachment = attachments.get(i);
            checkCompliance(checkResult, docNames, "attachment#" + i, attachment);
        }

        return checkResult;
    }

    private void checkCompliance(
        final CheckResult r, final Set<String> docNames, final String docReference,
        final DSSDocument document) {
        if (document == null) {
            detect(r, true, "the document [" + docReference + "] must not be null");
            return;
        }

        // it must have data
        if (!DocumentStreamUtil.hasData(document)) {
            detect(
                r, true,
                "the document [" + docReference + "] must not have a null/empty data stream"
            );
        }

        var checkDuplicates = true;

        final String nameRaw = document.getName();
        // check that a name is available 
        if (StringUtils.isEmpty(nameRaw)) {
            detect(r, true, "the document [" + docReference + "] must not have a null/empty name");
            checkDuplicates = false;
        }
        // check that it has not the name of any additional file, that will be put in the
        // SignedContent.zip by the library
        if (ContainerFileDefinitions.TOKEN_PDF_REF.equalsIgnoreCase(nameRaw)) {
            detect(r, true, "the document [" + docReference + "] has a name that is reserved");
            checkDuplicates = false;
        }

        // NICE-TO-HAVE: check for other sensitive names
        // for example: .exe .mp3 .avi .mpg ...
        // e.g. via a (properties) file in the classpath

        // check for duplications
        if (checkDuplicates) {
            if (docNames.contains(nameRaw)) {
                detect(
                    r, false, "the document [" + docReference + "] has the name '"
                        + nameRaw + "' that already been used before and the previous document "
                        + "will be overwritten in the final asic-container.");
            }
            final String nameNormalised = nameRaw.toUpperCase();
            if (!nameRaw.equals(nameNormalised) && docNames.contains(nameNormalised)) {
                    detect(
                        r, false, "the document [" + docReference + "] has the name '"
                            + nameRaw + "' that already been used before with another case "
                            + "(upper and/or lower) and it is likely that this may lead to some "
                            + "confusion."
                    );
                }

            docNames.add(nameRaw);
            docNames.add(nameNormalised);
        }
    }
}
