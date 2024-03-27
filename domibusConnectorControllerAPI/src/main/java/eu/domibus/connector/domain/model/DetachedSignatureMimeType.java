package eu.domibus.connector.domain.model;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.stream.Stream;


/**
 * @author riederb
 * @version 1.0
 */
public enum DetachedSignatureMimeType implements Serializable {
    /**
     * application/octet-stream
     */
    BINARY("application/octet-stream"),
    /**
     * text/xml
     */
    XML("text/xml"),
    /**
     * application/pkcs7-signature
     */
    PKCS7("application/pkcs7-signature");

    private final String code;

    /**
     * constructor
     *
     * @param code the value of the mime-type's code
     */
    DetachedSignatureMimeType(final String code) {
        this.code = code;
    }

    /**
     * @param code the code we are looking for
     * @return the found DetachedSignatureMimeType
     * @throws NoSuchElementException if there is no
     *                                DetachedSignatureMimeType with code
     */
    public static DetachedSignatureMimeType fromCode(String code) {
        return Stream.of(DetachedSignatureMimeType.values())
                     .filter(detachedSignatureMimeType -> detachedSignatureMimeType.getCode().equals(code))
                     .findFirst()
                     .get();
    }

    /**
     * @return the code
     */
    public String getCode() {
        return this.code;
    }
}
