/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.configuration.security.importoldconfig;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import eu.ecodex.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.Data;

/**
 * The {@code AImportOldConfigDialog} class is an abstract class that represents a dialog for
 * importing old configuration files.
 */
@Data
public abstract class AImportOldConfigDialog extends Dialog {
    private final ConfigurationPanelFactory configurationPanelFactory;
    private ConfigurationPanelFactory.DialogCloseCallback dialogCloseCallback;
    private final VerticalLayout layout = new VerticalLayout();
    // upload result area
    private final VerticalLayout resultArea = new VerticalLayout();
    // Upload
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);

    /**
     * Constructor.
     *
     * @param configurationPanelFactory The configuration panel factory used for creating
     *                                  configuration panels.
     */
    public AImportOldConfigDialog(ConfigurationPanelFactory configurationPanelFactory) {
        this.configurationPanelFactory = configurationPanelFactory;
        initUi();
    }

    private void initUi() {
        this.setWidth("80%");
        this.setHeightFull();

        add(layout);

        upload.addSucceededListener(this::uploadSucceded);

        layout.add(upload, resultArea);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(true);
        this.addDialogCloseActionListener(event -> this.close());
    }

    private void uploadSucceded(SucceededEvent succeededEvent) {
        try {
            var inputStream = buffer.getInputStream();

            var properties = new Properties();
            properties.load(inputStream);
            Map<String, String> p = properties.entrySet().stream()
                                              .collect(Collectors.toMap(
                                                  e -> e.getKey().toString(),
                                                  e -> e.getValue().toString()
                                              ));

            // show imported config...
            var div = new Div();
            Object configBean = showImportedConfig(div, p);

            // add save button...
            var saveButton = new Button("Save Imported Config");
            saveButton.addClickListener(event -> this.save(configBean));
            resultArea.add(saveButton);
            resultArea.add(div);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse uploaded file", e);
        }
    }

    protected abstract Object showImportedConfig(Div div, Map<String, String> p);

    protected void save(Object configClass) {
        configurationPanelFactory.showChangedPropertiesDialog(
            configClass, AImportOldConfigDialog.this::close);
    }

    @Override
    public void setOpened(boolean opened) {
        super.setOpened(opened);
        if (!opened && dialogCloseCallback != null) {
            dialogCloseCallback.dialogHasBeenClosed();
        }
    }
}
