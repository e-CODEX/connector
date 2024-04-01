package eu.domibus.connector.ui.view.areas.configuration.security.importoldconfig;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.SucceededEvent;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationPanelFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;


public abstract class AImportOldConfigDialog extends Dialog {
    private final ConfigurationPanelFactory configurationPanelFactory;
    private ConfigurationPanelFactory.DialogCloseCallback dialogCloseCallback;
    private final VerticalLayout layout = new VerticalLayout();
    // upload result area
    private final VerticalLayout resultArea = new VerticalLayout();
    // Upload
    private final MemoryBuffer buffer = new MemoryBuffer();
    private final Upload upload = new Upload(buffer);

    public AImportOldConfigDialog(ConfigurationPanelFactory configurationPanelFactory) {
        this.configurationPanelFactory = configurationPanelFactory;
        initUi();
    }

    private void initUi() {
        this.setWidth("80%");
        this.setHeightFull();

        add(layout);

        upload.addSucceededListener(this::uploadSecceeded);

        layout.add(upload, resultArea);

        this.setCloseOnEsc(true);
        this.setCloseOnOutsideClick(true);
        this.addDialogCloseActionListener(event -> this.close());
    }

    private void uploadSecceeded(SucceededEvent succeededEvent) {
        try {
            InputStream inputStream = buffer.getInputStream();

            Properties properties = new Properties();
            properties.load(inputStream);
            Map<String, String> p = properties.entrySet().stream()
                                              .collect(Collectors.toMap(
                                                      e -> e.getKey().toString(),
                                                      e -> e.getValue().toString()
                                              ));

            // show imported config...
            Div div = new Div();
            Object configBean = showImportedConfig(div, p);

            // add save button...
            Button saveButton = new Button("Save Imported Config");
            saveButton.addClickListener(event -> {
                this.save(configBean);
            });
            resultArea.add(saveButton);
            resultArea.add(div);
        } catch (IOException e) {
            throw new RuntimeException("Unable to parse uploaded file", e);
        }
    }

    protected abstract Object showImportedConfig(Div div, Map<String, String> p);

    protected void save(Object configClass) {
        configurationPanelFactory.showChangedPropertiesDialog(configClass, AImportOldConfigDialog.this::close);
    }

    public void setDialogCloseCallback(ConfigurationPanelFactory.DialogCloseCallback dialogCloseCallback) {
        this.dialogCloseCallback = dialogCloseCallback;
    }

    public void setOpened(boolean opened) {
        super.setOpened(opened);
        if (!opened && dialogCloseCallback != null) {
            dialogCloseCallback.dialogHasBeenClosed();
        }
    }
}
