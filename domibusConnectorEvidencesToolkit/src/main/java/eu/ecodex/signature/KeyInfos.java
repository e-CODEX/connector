/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.signature;

import java.lang.reflect.Array;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 * The KeyInfos class represents information about private and public keys, as well as the
 * certificate chain associated with the keys.
 */
@Getter
@Setter
public class KeyInfos {
    private PrivateKey privKey;
    private X509Certificate cert;
    private ArrayList<X509Certificate> certChain;

    /**
     * Adds the specified X509Certificate to the certificate chain in the KeyInfos object.
     *
     * @param cert the X509Certificate to be added to the certificate chain
     * @return true if the X509Certificate was added successfully, false otherwise
     */
    public boolean addToCertificateChain(X509Certificate cert) {
        if (certChain == null) {
            certChain = new ArrayList<>();
        }
        return certChain.add(cert);
    }

    /**
     * Sets the certificate chain for the KeyInfos object.
     *
     * @param certs the certificate chain to be set
     * @return true if the certificate chain was set successfully, false otherwise
     * @throws ClassCastException if the certificates in the array cannot be cast to
     *                            X509Certificate
     */
    public boolean setCertChain(Certificate[] certs) throws ClassCastException {
        X509Certificate[] x509Certs = KeyInfos.convert(certs, X509Certificate.class);

        for (var certificate : x509Certs) {
            if (!addToCertificateChain(certificate)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Converts an array of objects to the specified type.
     *
     * @param objects the array of objects to be converted
     * @param type    the target class type to convert the objects to
     * @return an array of objects converted to the specified type
     * @throws ClassCastException if the objects in the array cannot be cast to the specified type
     */
    public static <T> T[] convert(Object[] objects, Class type) throws ClassCastException {
        T[] convertedObjects = (T[]) Array.newInstance(type, objects.length);

        try {
            for (var i = 0; i < objects.length; i++) {
                convertedObjects[i] = (T) objects[i];
            }
        } catch (ClassCastException e) {
            throw new ClassCastException("Exception on convert() : " + e.getMessage());
        }

        return convertedObjects;
    }
}
