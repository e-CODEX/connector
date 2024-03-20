
package eu.domibus.connector.security.libtests.dss;

import eu.europa.esig.dss.enumerations.MimeType;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class MimeTypeTest {

    
    @Test
    public void testFromMimeTypeString() {        
        String pdfMimeTypeString = "application/pdf";
        
        MimeType mimeType = MimeType.fromMimeTypeString(pdfMimeTypeString);
        assertThat(mimeType).isEqualTo(MimeTypeEnum.PDF);
        
    }

    
}
