package eu.domibus.connector.evidences;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.apache.commons.codec.binary.Hex;

public class HashValueBuilder {

    private final MessageDigest digester;

    public HashValueBuilder(DigestAlgorithm digestAlgorithm) {
        try {
            digester = digestAlgorithm.getMessageDigest();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String buildHashValueAsString(byte[] originalMessage) {
        final byte[] resultByte = buildHashValue(originalMessage);
        final String result = new String(Hex.encodeHexString(resultByte));
        return result;
    }

    public byte[] buildHashValue(byte[] originalMessage) {
        digester.reset();
        digester.update(originalMessage);
        final byte[] resultByte = digester.digest();

        return resultByte;
    }

    public String getAlgorithm() {
        return digester.getAlgorithm();
    }

}
