
/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.security.libtests.dss;

import static org.assertj.core.api.Assertions.assertThat;

import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import org.junit.jupiter.api.Test;

/**
 * This class represents a unit test for the MimeType class. It contains a single test method that
 * verifies the correctness of the MimeType.fromMimeTypeString() method.
 */
class MimeTypeTest {
    @Test
    void testFromMimeTypeString() {
        var pdfMimeTypeString = "application/pdf";

        var mimeType = MimeType.fromMimeTypeString(pdfMimeTypeString);
        assertThat(mimeType).isEqualTo(MimeTypeEnum.PDF);
    }
}
