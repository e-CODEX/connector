/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.dss.model;

import eu.ecodex.dss.service.impl.dss.DSSSignatureChecker;
import lombok.Getter;

/**
 * The SignatureCheckers class represents a container for three different DSSSignatureChecker
 * instances. It holds a DSSSignatureChecker for checking signatures on ASiC documents, a
 * DSSSignatureChecker for checking signatures on XML tokens, and a DSSSignatureChecker for checking
 * signatures on PDF tokens.
 *
 * <p>SignatureCheckers can be initialized with instances of DSSSignatureChecker specific to each
 * signature type.
 *
 * @see DSSSignatureChecker
 * @see ECodexContainer
 */
@Getter
public class SignatureCheckers {
    private final DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker;
    private final DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker;
    private final DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker;

    /**
     * The SignatureCheckers class represents a container for three different DSSSignatureChecker
     * instances. It holds a DSSSignatureChecker for checking signatures on ASiC documents, a
     * DSSSignatureChecker for checking signatures on XML tokens, and a DSSSignatureChecker for
     * checking signatures on PDF tokens.
     *
     * @param asicsSignatureChecker    The DSSSignatureChecker for checking signatures on ASiC
     *                                 documents.
     * @param xmlTokenSignatureChecker The DSSSignatureChecker for checking signatures on XML
     *                                 tokens.
     * @param pdfTokenSignatureChecker The DSSSignatureChecker for checking signatures on PDF
     *                                 tokens.
     */
    public SignatureCheckers(
        DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker,
        DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker,
        DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker) {
        this.asicsSignatureChecker = asicsSignatureChecker;
        this.xmlTokenSignatureChecker = xmlTokenSignatureChecker;
        this.pdfTokenSignatureChecker = pdfTokenSignatureChecker;
    }
}
