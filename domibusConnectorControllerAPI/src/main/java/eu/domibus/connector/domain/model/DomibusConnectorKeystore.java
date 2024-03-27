package eu.domibus.connector.domain.model;

import org.springframework.core.style.ToStringCreator;

import java.util.Date;
import java.util.stream.Stream;


public class DomibusConnectorKeystore {
    private String uuid;
    private byte[] keystoreBytes;
    private String passwordPlain;
    private Date uploaded;
    private String description;
    private KeystoreType type;
    public DomibusConnectorKeystore() {
    }

    /**
     * @param uuid
     * @param keystoreBytes
     * @param passwordPlain
     * @param uploaded
     * @param description
     * @param type
     */
    public DomibusConnectorKeystore(
            String uuid, byte[] keystoreBytes, String passwordPlain, Date uploaded,
            String description, KeystoreType type) {
        super();
        this.uuid = uuid;
        this.keystoreBytes = keystoreBytes;
        this.passwordPlain = passwordPlain;
        this.uploaded = uploaded;
        this.description = description;
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public byte[] getKeystoreBytes() {
        return keystoreBytes;
    }

    public void setKeystoreBytes(byte[] keystoreBytes) {
        this.keystoreBytes = keystoreBytes;
    }

    public String getPasswordPlain() {
        return passwordPlain;
    }

    public void setPasswordPlain(String passwordPlain) {
        this.passwordPlain = passwordPlain;
    }

    public Date getUploaded() {
        return uploaded;
    }

    public void setUploaded(Date uploaded) {
        this.uploaded = uploaded;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public KeystoreType getType() {
        return type;
    }

    public void setType(KeystoreType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        ToStringCreator builder = new ToStringCreator(this);
        builder.append("uuid", this.uuid);
        builder.append("uploaded", this.uploaded);
        builder.append("description", this.description);
        builder.append("type", this.type);
        return builder.toString();
    }

    public enum KeystoreType {
        JKS("JKS", ".jks"),
        JCEKS("JCEKS", ".jceks"),
        PKCS12("PKCS12", ".pkcs12"),
        PKCS12S2("PKCS12S2", ".pkcs12s2");

        final String dbName;
        final String fileExtension;

        KeystoreType(String dbName, String fileExtension) {
            this.dbName = dbName;
            this.fileExtension = fileExtension;
        }

        public static KeystoreType ofDbName(String dbName) {
            return Stream.of(KeystoreType.values())
                         .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                         .findFirst().orElse(null);
        }

        public String getDbName() {
            return dbName;
        }

        public String getFileExtension() {
            return fileExtension;
        }
    }
}
