package eu.ecodex.dss.model;

import eu.ecodex.dss.service.impl.dss.DSSSignatureChecker;


public class SignatureCheckers {
    private final DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker;
    private final DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker;
    private final DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker;

    public SignatureCheckers(
            DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> asicsSignatureChecker,
            DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> xmlTokenSignatureChecker,
            DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> pdfTokenSignatureChecker) {
        this.asicsSignatureChecker = asicsSignatureChecker;
        this.xmlTokenSignatureChecker = xmlTokenSignatureChecker;
        this.pdfTokenSignatureChecker = pdfTokenSignatureChecker;
    }

    public DSSSignatureChecker<ECodexContainer.AsicDocumentTypeECodex> getAsicsSignatureChecker() {
        return asicsSignatureChecker;
    }

    public DSSSignatureChecker<ECodexContainer.TokenXmlTypesECodex> getXmlTokenSignatureChecker() {
        return xmlTokenSignatureChecker;
    }

    public DSSSignatureChecker<ECodexContainer.TokenPdfTypeECodex> getPdfTokenSignatureChecker() {
        return pdfTokenSignatureChecker;
    }
}
