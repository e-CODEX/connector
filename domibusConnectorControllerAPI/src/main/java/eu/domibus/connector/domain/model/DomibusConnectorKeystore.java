/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.domain.model;

import java.util.Date;
import java.util.stream.Stream;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.core.style.ToStringCreator;

/**
 * Represents a keystore used by the Domibus connector.
 */
@Data
@NoArgsConstructor
public class DomibusConnectorKeystore {
    private String uuid;
    private byte[] keystoreBytes;
    private String passwordPlain;
    private Date uploaded;
    private String description;
    private KeystoreType type;

    /**
     * Represents a Keystore used by the Domibus Connector. A Keystore contains an encrypted private
     * key and the corresponding digital certificates.
     */
    public DomibusConnectorKeystore(String uuid, byte[] keystoreBytes, String passwordPlain,
                                    Date uploaded,
                                    String description, KeystoreType type) {
        super();
        this.uuid = uuid;
        this.keystoreBytes = keystoreBytes;
        this.passwordPlain = passwordPlain;
        this.uploaded = uploaded;
        this.description = description;
        this.type = type;
    }

    @Override
    public String toString() {
        var builder = new ToStringCreator(this);
        builder.append("uuid", this.uuid);
        builder.append("uploaded", this.uploaded);
        builder.append("description", this.description);
        builder.append("type", this.type);
        return builder.toString();
    }

    /**
     * Represents the types of keystore used in the application. Each keystore type has a name and a
     * file extension.
     */
    public enum KeystoreType {
        JKS("JKS", ".jks"),
        JCEKS("JCEKS", ".jceks"),
        PKCS12("PKCS12", ".pkcs12"),
        PKCS12S2("PKCS12S2", ".pkcs12s2");
        @Getter
        final String dbName;
        @Getter
        final String fileExtension;

        KeystoreType(String dbName, String fileExtension) {
            this.dbName = dbName;
            this.fileExtension = fileExtension;
        }

        /**
         * Converts a given database name to a KeystoreType.
         *
         * @param dbName the database name
         * @return the KeystoreType associated with the given database name, or null if no
         *      KeystoreType is found
         */
        public static KeystoreType ofDbName(String dbName) {
            return Stream.of(KeystoreType.values())
                .filter(l -> l.dbName.equalsIgnoreCase(dbName))
                .findFirst().orElse(null);
        }
    }
}
