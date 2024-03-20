
package eu.domibus.connector.domain.transformer.util;

import javax.activation.DataHandler;
import javax.activation.DataSource;

/**
 *
 * @author {@literal Stephan Spindler <stephan.spindler@extern.brz.gv.at> }
 */
public class InputStreamDataHandler extends DataHandler {

    public InputStreamDataHandler(DataSource ds) {
        super(ds);
    }

    
}
