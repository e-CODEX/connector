
package eu.domibus.connector.security.libtests.dss;

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
