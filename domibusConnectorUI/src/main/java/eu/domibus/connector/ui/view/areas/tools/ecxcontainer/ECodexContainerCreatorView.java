/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.domibus.connector.ui.view.areas.tools.ecxcontainer;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.common.service.CurrentBusinessDomain;
import eu.domibus.connector.domain.model.DomibusConnectorBusinessDomain;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageBuilder;
import eu.domibus.connector.domain.model.builder.DomibusConnectorMessageDetailsBuilder;
import eu.domibus.connector.security.container.service.ECodexContainerFactoryService;
import eu.domibus.connector.tools.logging.LoggingMarker;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.utils.UiStyle;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.tools.ToolsLayout;
import eu.ecodex.dss.model.BusinessContent;
import eu.ecodex.dss.model.ECodexContainer;
import eu.ecodex.dss.service.ECodexException;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.InMemoryDocument;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

/**
 * The ECodexContainerCreatorView class represents a view in the Domibus Connector Administration UI
 * for creating an ECodex container.
 *
 * @see VerticalLayout
 * @see Component
 * @see UIScope
 * @see Route
 * @see RoleRequired
 * @see TabMetadata
 */
@Component
@UIScope
@Route(value = ECodexContainerCreatorView.ROUTE, layout = ToolsLayout.class)
@RoleRequired(role = "ADMIN")
@TabMetadata(title = "Create ECodex Container", tabGroup = ToolsLayout.TAB_GROUP_NAME)
@SuppressWarnings("squid:S1135")
public class ECodexContainerCreatorView extends VerticalLayout {
    private static final Logger LOGGER = LogManager.getLogger(ECodexContainerCreatorView.class);
    public static final String ROUTE = "createEcodexContainer";
    private final ECodexContainerFactoryService containerFactoryService;
    private final HorizontalLayout resultArea = new HorizontalLayout();

    /**
     * Constructor.
     *
     * @param containerFactoryService the eCodexContainerFactoryService used for creating ECodex
     *                                containers
     */
    public ECodexContainerCreatorView(ECodexContainerFactoryService containerFactoryService) {
        this.containerFactoryService = containerFactoryService;
        this.initUI();
    }

    private void initUI() {
        var documentValidationLabel = new NativeLabel(
            "Upload any signed document and see the certificate validation result"
        );
        this.add(documentValidationLabel);

        var buffer = new MemoryBuffer();
        var upload = new Upload(buffer);
        upload.setMaxFiles(1);
        upload.setId("uploadBusinessDocTest");

        var uploadResultLabel = new NativeLabel("");

        upload.addStartedListener(event -> resultArea.removeAll());
        upload.addSucceededListener(event -> processUploadedFile(buffer, uploadResultLabel));
        upload.addFailedListener(e -> {
            uploadResultLabel.setText("File upload failed!");
            uploadResultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_RED);
        });

        this.add(upload);
        this.add(uploadResultLabel);
        this.add(resultArea);
    }

    private void processUploadedFile(MemoryBuffer buffer, NativeLabel uploadResultLabel) {
        try {
            CurrentBusinessDomain.setCurrentBusinessDomain(
                DomibusConnectorBusinessDomain.getDefaultMessageLaneId());
            String fileName = buffer.getFileName();

            var bytes = StreamUtils.copyToByteArray(buffer.getInputStream());
            DSSDocument document = new InMemoryDocument(bytes, fileName);

            var theMessage = DomibusConnectorMessageBuilder
                .createBuilder()
                .setMessageDetails(
                    DomibusConnectorMessageDetailsBuilder
                        .create()
                        .withOriginalSender("TheOriginalSender")
                        .build())
                .build();

            var containerService =
                containerFactoryService.createECodexContainerService(theMessage);
            var businessContent = new BusinessContent();
            businessContent.setDocument(document);

            var container = containerService.create(businessContent);
            writeFilesToTemp(container);

            uploadResultLabel.setText(
                "File " + fileName + " uploaded\n"
                    + "Legal Disclaimer " + container
                    .getToken()
                    .getLegalValidationResultDisclaimer()
                    + "\n"
                    + "Legal Trust Level "
                    + container.getToken()
                               .getLegalValidationResult()
                               .getTrustLevel()
                               .getText()
            );

            uploadResultLabel.getStyle().set(UiStyle.TAG_COLOR, UiStyle.COLOR_GREEN);

            // TODO: make ecodex container downloadable
            //  Button download = new Button("Download Container");
            //  download.addClickListener(event -> {
            //      StreamResource r = new StreamResource();
            //  })

        } catch (IOException ioe) {
            uploadResultLabel.setText("File upload failed!");
            uploadResultLabel.getStyle().set("color", "red");
        } catch (ECodexException e) {
            LOGGER.warn("Ecodex Exception occurred while testing in UI", e);
            e.printStackTrace();
            uploadResultLabel.setText("eCodex processing failed!");
            uploadResultLabel.getStyle().set("color", "red");
        } finally {
            CurrentBusinessDomain.setCurrentBusinessDomain(null);
        }
    }

    private void writeFilesToTemp(ECodexContainer container) {
        try {
            var asicsContainer = Files.createTempFile("asicsContainer_", null);
            Files.write(
                asicsContainer,
                StreamUtils.copyToByteArray(container.getAsicDocument().openStream()),
                StandardOpenOption.WRITE
            );

            var xmlToken = Files.createTempFile("xmlToken_", null);
            Files.write(
                xmlToken, StreamUtils.copyToByteArray(container.getTokenXML().openStream()),
                StandardOpenOption.WRITE
            );

            var downloadAsicsContainer = new Anchor(
                getStreamResource("ecodex.asics", asicsContainer),
                "Download created ASIC-S container"
            );
            downloadAsicsContainer.getElement().setAttribute("download", true);

            var downloadXmlToken = new Anchor(
                getStreamResource("tokenXml.xml", xmlToken),
                "Download created XML Token"
            );
            downloadXmlToken.getElement().setAttribute("download", true);

            resultArea.add(downloadAsicsContainer, downloadXmlToken);
        } catch (IOException e) {
            LOGGER.warn("IOException occurred while writing temp files", e);
        }
    }

    private StreamResource getStreamResource(String s, Path file) {
        return new StreamResource(s, () -> {
            try {
                return new ByteArrayInputStream(Files.readAllBytes(file));
            } catch (IOException exception) {
                LOGGER.warn(
                    LoggingMarker.Log4jMarker.UI_LOG, "IOException occurred while reading "
                        + "temp file",
                    exception
                );
                throw new RuntimeException("Download failed", exception);
            }
        });
    }
}
