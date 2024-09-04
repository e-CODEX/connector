/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.firststartup;

import eu.ecodex.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.ecodex.connector.domain.model.DomibusConnectorKeystore;
import eu.ecodex.connector.tools.logging.LoggingMarker;
import eu.ecodex.connector.ui.service.WebPModeService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StreamUtils;

/**
 * The UploadPModesOnFirstStart class is responsible for uploading PMode configurations and trust
 * store files on the first start of the application.
 */
@SuppressWarnings({"squid:S1135", "checkstyle:ParameterName", "checkstyle:LocalVariableName"})
@Configuration
@EnableConfigurationProperties(UploadPModesOnFirstStartConfigurationProperties.class)
@DependsOn(CreateDefaultBusinessDomainOnFirstStart.BEAN_NAME)
@ConditionalOnProperty(
    prefix = UploadPModesOnFirstStartConfigurationProperties.PREFIX, name = "enabled",
    havingValue = "true"
)
public class UploadPModesOnFirstStart {
    private static final Logger LOGGER = LogManager.getLogger(UploadPModesOnFirstStart.class);
    private final WebPModeService webPModeService;
    private final UploadPModesOnFirstStartConfigurationProperties config;
    private final ApplicationContext ctx;

    /**
     * Constructs a new instance of UploadPModesOnFirstStart.
     *
     * @param webPModeService the WebPModeService used for uploading the PModes
     * @param config          the UploadPModesOnFirstStartConfigurationProperties object containing
     *                        configuration settings
     * @param ctx             the ApplicationContext for accessing application context resources
     */
    public UploadPModesOnFirstStart(
        WebPModeService webPModeService,
        UploadPModesOnFirstStartConfigurationProperties config,
        ApplicationContext ctx) {
        this.webPModeService = webPModeService;
        this.config = config;
        this.ctx = ctx;
    }

    @PostConstruct
    @Transactional
    public void startup() {
        config.getUpload().forEach(this::processUpload);
    }

    private void processUpload(
        UploadPModesOnFirstStartConfigurationProperties.PModeUpload pModeUpload) {
        // TODO: set correct business domain
        var businessDomainName = pModeUpload.getBusinessDomainName();
        if (!businessDomainName.equals(DomibusConnectorBusinessDomain.DEFAULT_LANE_NAME)) {
            throw new IllegalArgumentException(
                "Currently there is no business domain support for "
                    + UploadPModesOnFirstStartConfigurationProperties.PREFIX
            );
        }

        try {
            var pw = pModeUpload.getTrustStore().getPassword();
            var type = pModeUpload.getTrustStore().getType();
            var trustStoreBytes = StreamUtils.copyToByteArray(
                ctx.getResource(pModeUpload.getTrustStore().getPath()).getInputStream()
            );

            var domibusConnectorKeystore = webPModeService.importConnectorstore(
                trustStoreBytes,
                pw,
                DomibusConnectorKeystore.KeystoreType.ofDbName(type)
            );

            var pModeXml = StreamUtils.copyToByteArray(pModeUpload.getpModeXml().getInputStream());
            boolean success = webPModeService.importPModes(
                pModeXml,
                "Initially loaded by UploadPModesOnFirstStart",
                domibusConnectorKeystore
            );

            if (success) {
                LOGGER.info(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Successfully Uploaded configured p-Modes and trustStore: [{}]",
                    pModeUpload
                );
            } else {
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.CONFIG,
                    "Failed to upload configured p-Modes and trustStore: [{}]",
                    pModeUpload
                );
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while reading from provided resource", e);
        }
    }
}
