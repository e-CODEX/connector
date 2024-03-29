package eu.domibus.connector.persistence.service.impl.helper;

import javax.annotation.Nullable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;


public class MapperHelper {
    @SuppressWarnings("squid:S00112")
    @Nullable
    public static String convertByteArrayToString(@Nullable byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }
}
