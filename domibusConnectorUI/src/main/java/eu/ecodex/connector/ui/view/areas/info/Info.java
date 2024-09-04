/*
 * Copyright 2024 European Union Agency for the Operational Management of Large-Scale IT Systems
 * in the Area of Freedom, Security and Justice (eu-LISA)
 *
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by the
 * European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy at: https://joinup.ec.europa.eu/software/page/eupl
 */

package eu.ecodex.connector.ui.view.areas.info;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.ecodex.connector.ui.component.LumoCheckbox;
import eu.ecodex.connector.ui.component.LumoLabel;
import eu.ecodex.connector.ui.forms.FormsUtil;
import eu.ecodex.connector.ui.layout.DCMainLayout;
import eu.ecodex.connector.ui.utils.UiStyle;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

/**
 * This class represents the Info view in a web application. It is a component that displays various
 * information related to the application.
 */
@UIScope
@org.springframework.stereotype.Component
@Route(value = Info.ROUTE, layout = DCMainLayout.class)
public class Info extends VerticalLayout implements InitializingBean {
    public static final String ROUTE = "info";
    private static final String PROPERTY_FILESYSTEM_PATH =
        "connector.persistence.filesystem.storagePath";
    private static final String PROPERTY_CREATE_DIRS = "connector.persistence.filesystem.createDir";
    private static final String PROPERTY_USE_HTTP_PROXY = "http.proxy.enabled";
    private static final String PROPERTY_HTTP_PROXY_HOST = "http.proxy.host";
    private static final String PROPERTY_HTTP_PROXY_PORT = "http.proxy.port";
    private static final String PROPERTY_HTTP_PROXY_USER = "http.proxy.user";
    private static final String PROPERTY_HTTP_PROXY_PASS = "http.proxy.password";
    private static final String PROPERTY_USE_HTTPS_PROXY = "https.proxy.enabled";
    private static final String PROPERTY_HTTPS_PROXY_HOST = "https.proxy.host";
    private static final String PROPERTY_HTTPS_PROXY_PORT = "https.proxy.port";
    private static final String PROPERTY_HTTPS_PROXY_USER = "https.proxy.user";
    private static final String PROPERTY_HTTPS_PROXY_PASS = "https.proxy.password";
    @Autowired
    BuildProperties buildProperties;
    @Autowired
    DataSourceProperties dataSourceProperties;
    @Autowired
    Environment env;
    LumoLabel connectorVersion = new LumoLabel("");
    TextField dbUrlTextField;
    TextField dbDriverClassTextField;
    TextField dbUsernameTextField;
    TextField storagePath;
    LumoCheckbox createDirs;
    LumoCheckbox useHttpProxyBox = new LumoCheckbox();
    TextField httpProxyHostField = FormsUtil.getFormattedTextField();
    TextField httpProxyPortField = FormsUtil.getFormattedTextField();
    TextField httpProxyUserField = FormsUtil.getFormattedTextField();
    TextField httpProxyPasswordField = FormsUtil.getFormattedTextField();
    LumoCheckbox useHttpsProxyBox = new LumoCheckbox();
    TextField httpsProxyHostField = FormsUtil.getFormattedTextField();
    TextField httpsProxyPortField = FormsUtil.getFormattedTextField();
    TextField httpsProxyUserField = FormsUtil.getFormattedTextField();
    TextField httpsProxyPasswordField = FormsUtil.getFormattedTextField();

    /**
     * This class is a component that displays various pieces of information, such as connector
     * version, connected database information, message storage configuration, and proxy
     * configuration.
     */
    public Info() {
        Div areaConnectorVersion = createConnectorVersionArea();
        Div areaConnectedDatabase = createConnectedDatabaseArea();

        add(areaConnectorVersion);
        add(areaConnectedDatabase);
        add(createStorageArea());
        add(createProxyArea());
    }

    private Div createConnectedDatabaseArea() {
        var connectedDatabaseLayout = new VerticalLayout();

        var header = new LumoLabel("Connected database information:");
        header.getStyle().set("font-size", "20px");

        connectedDatabaseLayout.add(header);

        dbDriverClassTextField = new TextField("Database Driver Class Name:");
        dbDriverClassTextField.setWidth(UiStyle.WIDTH_500_PX);
        dbDriverClassTextField.setReadOnly(true);

        connectedDatabaseLayout.add(dbDriverClassTextField);

        dbUrlTextField = new TextField("Database JDBC URL:");
        dbUrlTextField.setWidth(UiStyle.WIDTH_500_PX);
        dbUrlTextField.setReadOnly(true);

        connectedDatabaseLayout.add(dbUrlTextField);

        dbUsernameTextField = new TextField("Database Username:");
        dbUsernameTextField.setWidth(UiStyle.WIDTH_500_PX);
        dbUsernameTextField.setReadOnly(true);

        connectedDatabaseLayout.add(dbUsernameTextField);

        var areaConnectedDatabase = new Div();
        areaConnectedDatabase.add(connectedDatabaseLayout);

        return areaConnectedDatabase;
    }

    private Div createStorageArea() {
        var storageInformationLayout = new VerticalLayout();

        var header = new LumoLabel("Message Storage Configuration:");
        header.getStyle().set(UiStyle.FONT_SIZE_STYLE, "20px");

        storageInformationLayout.add(header);

        storagePath = new TextField("Message Storage Path:");
        storagePath.setWidth(UiStyle.WIDTH_500_PX);
        storagePath.setReadOnly(true);

        storageInformationLayout.add(storagePath);

        createDirs = new LumoCheckbox("Allow create directories");
        createDirs.setReadOnly(true);

        storageInformationLayout.add(createDirs);

        var areaStorageInformation = new Div();
        areaStorageInformation.add(storageInformationLayout);

        return areaStorageInformation;
    }

    private Div createProxyArea() {
        var proxyInformationLayout = new VerticalLayout();

        var header = new LumoLabel("Proxy Configuration:");
        header.getStyle().set(UiStyle.FONT_SIZE_STYLE, "20px");

        var header2 = new LumoLabel("Only used for OCSP and CRL validation");
        header2.getStyle().set(UiStyle.FONT_SIZE_STYLE, "15px");

        proxyInformationLayout.add(header);
        proxyInformationLayout.add(header2);

        useHttpProxyBox = new LumoCheckbox("HTTP Proxy set");
        useHttpProxyBox.setReadOnly(true);
        proxyInformationLayout.add(useHttpProxyBox);

        httpProxyHostField = new TextField("Proxy Host");
        httpProxyHostField.setWidth(UiStyle.WIDTH_500_PX);
        httpProxyHostField.setReadOnly(true);
        proxyInformationLayout.add(httpProxyHostField);

        httpProxyPortField = new TextField("Proxy Port");
        httpProxyPortField.setWidth(UiStyle.WIDTH_500_PX);
        httpProxyPortField.setReadOnly(true);
        proxyInformationLayout.add(httpProxyPortField);

        httpProxyUserField = new TextField("Proxy User");
        httpProxyUserField.setWidth(UiStyle.WIDTH_500_PX);
        httpProxyUserField.setReadOnly(true);
        proxyInformationLayout.add(httpProxyUserField);

        httpProxyPasswordField = new TextField("Proxy Password");
        httpProxyPasswordField.setWidth(UiStyle.WIDTH_500_PX);
        httpProxyPasswordField.setReadOnly(true);
        proxyInformationLayout.add(httpProxyPasswordField);

        useHttpsProxyBox = new LumoCheckbox("HTTPS Proxy set");
        useHttpsProxyBox.setReadOnly(true);
        proxyInformationLayout.add(useHttpsProxyBox);

        httpsProxyHostField = new TextField("Proxy Host");
        httpsProxyHostField.setWidth(UiStyle.WIDTH_500_PX);
        httpsProxyHostField.setReadOnly(true);
        proxyInformationLayout.add(httpsProxyHostField);

        httpsProxyPortField = new TextField("Proxy Port");
        httpsProxyPortField.setWidth(UiStyle.WIDTH_500_PX);
        httpsProxyPortField.setReadOnly(true);
        proxyInformationLayout.add(httpsProxyPortField);

        httpsProxyUserField = new TextField("Proxy User");
        httpsProxyUserField.setWidth(UiStyle.WIDTH_500_PX);
        httpsProxyUserField.setReadOnly(true);
        proxyInformationLayout.add(httpsProxyUserField);

        httpsProxyPasswordField = new TextField("Proxy Password");
        httpsProxyPasswordField.setWidth(UiStyle.WIDTH_500_PX);
        httpsProxyPasswordField.setReadOnly(true);
        proxyInformationLayout.add(httpsProxyPasswordField);

        var areaProxyInformation = new Div();
        areaProxyInformation.add(proxyInformationLayout);

        return areaProxyInformation;
    }

    private Div createConnectorVersionArea() {
        var connectorVersionLayout = new HorizontalLayout();

        var connectorVersionLabel = new LumoLabel("domibusConnector Version:");
        connectorVersionLabel.getStyle().set(UiStyle.FONT_SIZE_STYLE, "20px");

        connectorVersion.getStyle().set(UiStyle.FONT_SIZE_STYLE, "20px");

        connectorVersionLayout.add(connectorVersionLabel);
        connectorVersionLayout.add(connectorVersion);
        connectorVersionLayout.setPadding(true);

        var areaConnectorVersion = new Div();
        areaConnectorVersion.add(connectorVersionLayout);
        areaConnectorVersion.setVisible(true);

        return areaConnectorVersion;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        connectorVersion.setText("Version: " + buildProperties.getVersion() + " build time: "
                                     + buildProperties.getTime());
        dbUrlTextField.setValue(dataSourceProperties.getUrl());
        dbDriverClassTextField.setValue(dataSourceProperties.getDriverClassName());
        dbUsernameTextField.setValue(dataSourceProperties.getUsername());

        storagePath.setValue(env.getProperty(PROPERTY_FILESYSTEM_PATH) != null
                                 ? env.getProperty(PROPERTY_FILESYSTEM_PATH)
                                 : ""
        );
        createDirs.setValue(env.getProperty(PROPERTY_CREATE_DIRS) != null
                                && Boolean.parseBoolean(env.getProperty(PROPERTY_CREATE_DIRS))
        );

        useHttpProxyBox.setValue(env.getProperty(PROPERTY_USE_HTTP_PROXY) != null
                                     && Boolean.parseBoolean(
            env.getProperty(PROPERTY_USE_HTTP_PROXY)));
        httpProxyHostField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_HOST) != null
                                        ? env.getProperty(PROPERTY_HTTP_PROXY_HOST)
                                        : ""
        );
        httpProxyPortField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_PORT) != null
                                        ? env.getProperty(PROPERTY_HTTP_PROXY_PORT)
                                        : ""
        );
        httpProxyUserField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_USER) != null
                                        ? env.getProperty(PROPERTY_HTTP_PROXY_USER)
                                        : ""
        );
        httpProxyPasswordField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_PASS) != null
                                            ? env.getProperty(PROPERTY_HTTP_PROXY_PASS)
                                            : ""
        );

        useHttpsProxyBox.setValue(env.getProperty(PROPERTY_USE_HTTPS_PROXY) != null
                                      && Boolean.parseBoolean(
            env.getProperty(PROPERTY_USE_HTTPS_PROXY))
        );
        httpsProxyHostField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_HOST) != null
                                         ? env.getProperty(PROPERTY_HTTPS_PROXY_HOST)
                                         : ""
        );
        httpsProxyPortField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_PORT) != null
                                         ? env.getProperty(PROPERTY_HTTPS_PROXY_PORT)
                                         : ""
        );
        httpsProxyUserField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_USER) != null
                                         ? env.getProperty(PROPERTY_HTTPS_PROXY_USER)
                                         : ""
        );
        httpsProxyPasswordField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_PASS) != null
                                             ? env.getProperty(PROPERTY_HTTPS_PROXY_PASS)
                                             : ""
        );
    }
}
