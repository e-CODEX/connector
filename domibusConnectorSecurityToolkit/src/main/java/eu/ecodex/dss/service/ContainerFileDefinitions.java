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
 * $HeadURL: http://forge.aris-lux.lan/svn/dgmarktdss/ecodex/src/main/java/eu/ecodex/dss/service/ContainerFileDefinitions.java $
 * $Revision: 1879 $
 * $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 * $Author: meyerfr $
 */

package eu.ecodex.dss.service;

import lombok.Getter;

/**
 * Defines the locations and names of files regarding the container.
 *
 * <p>DISCLAIMER: Project owner e-CODEX
 *
 * @author <a href="mailto:eCodex.Project-DSS@arhs-developments.com">ARHS Developments</a>
 * @version $Revision: 1879 $ - $Date: 2013-04-18 09:39:53 +0200 (jeu., 18 avr. 2013) $
 */
public interface ContainerFileDefinitions {
    /**
     * The name of the signed content ZIP.
     */
    FileDef SIGNED_CONTENT = new FileDef(null, "SignedContent.zip");
    String SIGNED_CONTENT_REF = SIGNED_CONTENT.getReference();
    /**
     * The name of the PDF TrustOkToken , inside the inner ZIP.
     */
    FileDef TOKEN_PDF = new FileDef(null, "TrustOkToken.pdf");
    String TOKEN_PDF_REF = TOKEN_PDF.getReference();
    /**
     * The name of the XML TrustOkToken.
     */
    FileDef TOKEN_XML = new FileDef("META-INF", "trustOkToken.xml");
    String TOKEN_XML_REF = TOKEN_XML.getReference();
    /**
     * The path of the XML signatures , inside the ZIP.
     */
    FileDef SIGNATURES = new FileDef("META-INF", "signatures.xml");
    String SIGNATURES_REF = SIGNATURES.getReference();
    /**
     * The name of the Asic container.
     */
    FileDef SIGNED_CONTENT_ASIC = new FileDef(null, "SignedContent.zip.ASIC");
    String SIGNED_CONTENT_ASIC_REF = SIGNED_CONTENT_ASIC.getReference();

    /**
     * defines the location and name of a file. and derives a "full" reference.
     */
    @Getter
    class FileDef {
        private final String location;
        private final String name;
        private final String reference;

        /**
         * Immutable constructor.
         *
         * @param location the nullable location aka folder
         * @param name     the raw name of the file
         */
        public FileDef(final String location, final String name) {
            this.location = clean(location);
            this.name = clean(name);
            var ref = "";
            ref += this.location;
            if (!ref.isEmpty() && !ref.endsWith("/")) {
                ref += "/";
            }
            ref += this.name;
            this.reference = ref;
        }

        /**
         * Utility method to have a good name = not null and no spaces or slashes around.
         *
         * @param s the input
         * @return the output
         */
        private static String clean(final String s) {
            String t = s == null ? "" : s.trim();
            while (t.startsWith("/")) {
                t = t.substring(1);
                t = t.trim();
            }
            while (t.endsWith("/")) {
                t = t.substring(0, t.length() - 1);
                t = t.trim();
            }
            return t;
        }
    }
}
