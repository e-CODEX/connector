package eu.domibus.connector.evidences;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


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
        return new String(Hex.encodeHexString(resultByte));
    }

    public byte[] buildHashValue(byte[] originalMessage) {
        digester.reset();
        digester.update(originalMessage);
        return digester.digest();
    }

    public String getAlgorithm() {
        return digester.getAlgorithm();
    }
}
