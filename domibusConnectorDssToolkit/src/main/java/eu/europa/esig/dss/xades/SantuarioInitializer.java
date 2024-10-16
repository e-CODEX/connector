/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.europa.esig.dss.xades;

import lombok.experimental.UtilityClass;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.keyresolver.KeyResolver;
import org.apache.xml.security.transforms.Transform;
import org.apache.xml.security.utils.ElementProxy;
import org.apache.xml.security.utils.I18n;
import org.apache.xml.security.utils.resolver.ResourceResolver;
import org.apache.xml.security.utils.resolver.implementations.ResolverXPointer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Customized Initialization of Santuario.
 *
 * <p>This class overwrites the class from dss toolkits to align it with a *  more recent xmlsec
 * library version
 *
 * <p>We don't use the secureValidation parameter because it ignores some signature algorithms
 */
@UtilityClass
public class SantuarioInitializer {
    private static final Logger LOG = LoggerFactory.getLogger(SantuarioInitializer.class);
    /**
     * Field alreadyInitialized.
     */
    private static boolean alreadyInitialized = false;

    /**
     * Method isInitialized.
     *
     * @return true if the library is already initialized.
     */
    public static synchronized boolean isInitialized() {
        if (Init.isInitialized()) {
            LOG.info("Santuario is already initialized with its default configuration");
            return true;
        }
        return SantuarioInitializer.alreadyInitialized;
    }

    /**
     * Method init.
     */
    public static synchronized void init() {
        if (isInitialized()) {
            return;
        }

        dynamicInit();

        alreadyInitialized = true;
    }

    /**
     * Dynamically initialise the library by registering the default algorithms/implementations.
     */
    private static void dynamicInit() {
        //
        // Load the Resource Bundle - the default is the English resource bundle.
        // To load another resource bundle, call I18n.init(...) before calling this
        // method.
        //
        I18n.init("en", "US");

        if (LOG.isDebugEnabled()) {
            LOG.debug("Registering default algorithms");
        }
        try {
            //
            // Bind the default prefixes
            //
            ElementProxy.registerDefaultPrefixes();
        } catch (XMLSecurityException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        //
        // Set the default Transforms
        //
        Transform.registerDefaultAlgorithms();

        //
        // Set the default signature algorithms
        //
        SignatureAlgorithm.registerDefaultAlgorithms();

        //
        // Set the default JCE algorithms
        //
        JCEMapper.registerDefaultAlgorithms();

        //
        // Set the default c14n algorithms
        //
        Canonicalizer.registerDefaultAlgorithms();

        //
        // Register the default resolvers (custom)
        //
        registerDefaultResolvers();

        //
        // Register the default key resolvers
        //
        KeyResolver.registerDefaultResolvers();
    }

    /**
     * Customized
     * org.apache.xml.security.utils.resolver.ResourceResolver.registerDefaultResolvers().
     *
     * <p>Ignore references which point to a file (file://) or external http urls Enforce
     * ResolverFragment against XPath injections
     */
    public static void registerDefaultResolvers() {
        try {
            ResourceResolver.register(EnforcedResolverFragment.class.getName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Cannot register resolver", e);
        }
        try {
            ResourceResolver.register(ResolverXPointer.class.getName());
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            throw new IllegalArgumentException("Cannot register resolver", e);
        }
    }
}

