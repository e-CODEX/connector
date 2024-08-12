/*
 * Copyright 2024 European Union. All rights reserved.
 * European Union EUPL version 1.1.
 */

/*
 * Project: e-CODEX Connector - Container Services/DSS
 * Contractor: ARHS-Developments
 *
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/model/ECodexContainer.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.model;

import eu.ecodex.dss.model.token.Token;
import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

/**
 * This holds the to be signed content plus the created asic document.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
@Getter
public class ECodexContainer {
    public static final String E_CODEX_CONTAINER_MUST_NOT_BE_NULL =
        "eCodex container must not be null!";
    /**
     * The documents that are stored within the container.
     */
    private BusinessContent businessContent;
    /**
     * The token about the trust as object structure.
     */
    private Token token;
    /**
     * The signed XML representation of the {@link #token}.
     */
    private DSSDocument tokenXML;
    /**
     * The signed PDF representation of the {@link #token}.
     */
    private DSSDocument tokenPDF;
    /**
     * The generated ASiC-S "file".
     */
    private DSSDocument asicDocument;

    /**
     * Convenience method to give access to the main document of the (signed) business content.
     *
     * @return the value
     */
    public DSSDocument getBusinessDocument() {
        return (businessContent == null) ? null : businessContent.getDocument();
    }

    /**
     * Convenience method to give access to the detached signature of the main document of the
     * (signed) business content.
     *
     * @return the value
     */
    public DSSDocument getBusinessSignature() {
        return (businessContent == null) ? null : businessContent.getDetachedSignature();
    }

    /**
     * Convenience method to give access to the attachments of the (signed) business content.
     *
     * @return the value
     */
    public List<DSSDocument> getBusinessAttachments() {
        return (businessContent == null) ? null : businessContent.getAttachments();
    }

    /**
     * The documents that are stored within the container.
     *
     * @param businessContent the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setBusinessContent(final BusinessContent businessContent) {
        this.businessContent = businessContent;
        return this;
    }

    /**
     * The token about the trust as object structure.
     *
     * @param token the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setToken(final Token token) {
        this.token = token;
        return this;
    }

    /**
     * The signed XML representation of the {@link #token}.
     *
     * @param tokenXML the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setTokenXML(final DSSDocument tokenXML) {
        this.tokenXML = tokenXML;
        return this;
    }

    /**
     * The signed PDF representation of the {@link #token}.
     *
     * @param tokenPDF the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setTokenPDF(final DSSDocument tokenPDF) {
        this.tokenPDF = tokenPDF;
        return this;
    }

    /**
     * The generated ASiC-S "file".
     *
     * @param asicDocument the value
     * @return this class' instance for chaining
     */
    public ECodexContainer setAsicDocument(final DSSDocument asicDocument) {
        this.asicDocument = asicDocument;
        return this;
    }

    /**
     * This abstract class represents a specific type of document in the ECodex format. Subclasses
     * of this class are used to define different types of ECodex documents and provide
     * implementations for the required methods.
     */
    public abstract static class ECodexDSSDocumentType {
        public abstract DSSDocument getDSSDocument(ECodexContainer container);

        public abstract List<MimeType> getValidMimeTypes();
    }

    /**
     * The TokenXmlTypesECodex class is a subclass of the ECodexDSSDocumentType class. It represents
     * the XML token type in the ECodexContainer class. It provides functionality to get the XML
     * representation of the token and a list of valid MimeTypes. It overrides the getDSSDocument
     * method to return the tokenXML document from the ECodexContainer. The TokenXmlTypesECodex
     * class must be used as a parameter for DSSSignatureChecker instances in the SignatureCheckers
     * class.
     *
     * @see ECodexDSSDocumentType
     * @see ECodexContainer
     * @see TokenPdfTypeECodex
     * @see AsicDocumentTypeECodex
     * @see eu.ecodex.dss.service.impl.dss.DSSSignatureChecker
     * @see SignatureCheckers
     */
    public static class TokenXmlTypesECodex extends ECodexDSSDocumentType {
        public static final String E_CODEX_CONTAINER_MUST_NOT_BE_NULL =
            "eCodex container must not be null!";

        @Override
        public DSSDocument getDSSDocument(ECodexContainer container) {
            Objects.requireNonNull(container, E_CODEX_CONTAINER_MUST_NOT_BE_NULL);
            return container.getTokenXML();
        }

        @Override
        public List<MimeType> getValidMimeTypes() {
            return Stream.of(MimeTypeEnum.ASICS, MimeTypeEnum.ASICE).collect(Collectors.toList());
        }
    }

    /**
     * This class represents a specific type of ECodexDSSDocumentType, called TokenPdfTypeECodex. It
     * is used to retrieve the DSSDocument representing the signed PDF token from an
     * ECodexContainer.
     */
    public static class TokenPdfTypeECodex extends ECodexDSSDocumentType {
        /**
         * Retrieves the DSSDocument representing the signed PDF token from the given
         * ECodexContainer.
         *
         * @param container the ECodexContainer object containing the signed PDF token
         * @return the DSSDocument representing the signed PDF token
         * @throws NullPointerException if the eCodexContainer is null
         */
        @Override
        public DSSDocument getDSSDocument(ECodexContainer container) {
            Objects.requireNonNull(container, E_CODEX_CONTAINER_MUST_NOT_BE_NULL);
            return container.getTokenPDF();
        }

        /**
         * Retrieves the valid MIME types for the ECodexDSSDocumentType.
         *
         * @return the list of valid MIME types for the ECodexDSSDocumentType
         */
        @Override
        public List<MimeType> getValidMimeTypes() {
            return Stream.of(MimeTypeEnum.PDF).collect(Collectors.toList());
        }
    }

    /**
     * This class represents a specific type of document in the ECodex format, called
     * AsicDocumentTypeECodex. It extends the ECodexDSSDocumentType class and overrides its abstract
     * methods.
     */
    public static class AsicDocumentTypeECodex extends ECodexDSSDocumentType {
        @Override
        public DSSDocument getDSSDocument(ECodexContainer container) {
            Objects.requireNonNull(container, E_CODEX_CONTAINER_MUST_NOT_BE_NULL);
            return container.getAsicDocument();
        }

        @Override
        public List<MimeType> getValidMimeTypes() {
            return Stream.of(MimeTypeEnum.XML).collect(Collectors.toList());
        }
    }
}
