package eu.domibus.connector.persistence.service.impl.helper;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;

public class MapperHelper {

    @SuppressWarnings("squid:S00112")
    @Nullable
    public static String convertByteArrayToString(@Nullable byte[] bytes) {
        try {
            if (bytes == null) {
                return null;
            }
            return new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

}
