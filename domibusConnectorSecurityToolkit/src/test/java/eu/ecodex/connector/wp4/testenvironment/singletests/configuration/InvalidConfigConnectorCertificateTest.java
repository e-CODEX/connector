/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.wp4.testenvironment.singletests.configuration;

import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_BusinessContent;
import eu.ecodex.connector.wp4.testenvironment.configurations.ValidConfig_TokenIssuer;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.token.TokenIssuer;
import eu.ecodex.dss.service.ECodexException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

/**
 * Contains tests being able to create a valid ASiC-S container using various possibilities to
 * configure the connector certificates. SUB-CONF-2.
 */
@SuppressWarnings({"checkstyle:TypeName", "squid:S1135"})
// TODO Repair the tests
@Disabled("Repair tests")
class InvalidConfigConnectorCertificateTest {
    /**
     * Initializes all test cases with the same, working configuration. Test case specific
     * configurations are done within each test case itself.
     */
    @BeforeAll
    static void init() {
        // TODO see why the method body is empty
    }

    /**
     * Variant 1 No Private Key.
     */
    @Test
    void test_NoKey() {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
    }

    /**
     * Variant 2 No Certificate.
     */
    @Test
    void test_NoCert() {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
    }

    /**
     * Variant 3 No Certificate Chain.
     */
    @Test
    void test_NoChain() {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
    }

    /**
     * Variant 4 No Digest Algorithm.
     */
    @Test
    void test_NoDigest() {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();

        Assertions.assertThrows(eu.ecodex.dss.service.ECodexException.class, () -> {
        });
    }

    /**
     * Variant 5 No Signature Algorithm.
     */
    @Test
    void test_NoEncryption() {
        BusinessContent content = ValidConfig_BusinessContent.get_UnsignedFile_WithoutAttachments();
        TokenIssuer issuer = ValidConfig_TokenIssuer.get_FullSignatureBased();
        Assertions.assertThrows(ECodexException.class, () -> {
        });
    }
}

