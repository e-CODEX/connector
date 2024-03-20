package eu.domibus.connector.ui.view.areas.info;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.forms.FormsUtil;
import eu.domibus.connector.ui.layout.DCMainLayout;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationProperties;

//@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@UIScope
@org.springframework.stereotype.Component
@Route(value = Info.ROUTE, layout = DCMainLayout.class)
public class Info extends VerticalLayout implements InitializingBean {

	public static final String ROUTE = "info";
	
	private static final String PROPERTY_FILESYSTEM_PATH="connector.persistence.filesystem.storagePath";
	private static final String PROPERTY_CREATE_DIRS="connector.persistence.filesystem.createDir";
	
	private static final String PROPERTY_USE_HTTP_PROXY="http.proxy.enabled";
	private static final String PROPERTY_HTTP_PROXY_HOST="http.proxy.host";
	private static final String PROPERTY_HTTP_PROXY_PORT="http.proxy.port";
	private static final String PROPERTY_HTTP_PROXY_USER="http.proxy.user";
	private static final String PROPERTY_HTTP_PROXY_PASS="http.proxy.password";
	
	private static final String PROPERTY_USE_HTTPS_PROXY="https.proxy.enabled";
	private static final String PROPERTY_HTTPS_PROXY_HOST="https.proxy.host";
	private static final String PROPERTY_HTTPS_PROXY_PORT="https.proxy.port";
	private static final String PROPERTY_HTTPS_PROXY_USER="https.proxy.user";
	private static final String PROPERTY_HTTPS_PROXY_PASS="https.proxy.password";
	
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
	
	public Info() {
		
		Div areaConnectorVersion = createConnectorVersionArea();
		
		Div areaConnectedDatabase = createConnectedDatabaseArea();
		
		
		add(areaConnectorVersion);
		add(areaConnectedDatabase);
		add(createStorageArea());
		add(createProxyArea());
	}

	private Div createConnectedDatabaseArea() {
		Div areaConnectedDatabase = new Div();
		
		VerticalLayout connectedDatabaseLayout = new VerticalLayout();
		
		LumoLabel header = new LumoLabel("Connected database information:");
		header.getStyle().set("font-size", "20px");
		
		connectedDatabaseLayout.add(header);
		
		dbDriverClassTextField = new TextField("Database Driver Class Name:");
		dbDriverClassTextField.setWidth("500px");
		dbDriverClassTextField.setReadOnly(true);
		
		connectedDatabaseLayout.add(dbDriverClassTextField);
		
		dbUrlTextField = new TextField("Database JDBC URL:");
		dbUrlTextField.setWidth("500px");
		dbUrlTextField.setReadOnly(true);
		
		connectedDatabaseLayout.add(dbUrlTextField);
		
		dbUsernameTextField = new TextField("Database Username:");
		dbUsernameTextField.setWidth("500px");
		dbUsernameTextField.setReadOnly(true);
		
		connectedDatabaseLayout.add(dbUsernameTextField);
		
		areaConnectedDatabase.add(connectedDatabaseLayout);
		
		return areaConnectedDatabase;
	}
	
	private Div createStorageArea() {
		Div areaStorageInformation = new Div();
		
		VerticalLayout storageInformationLayout = new VerticalLayout();
		
		LumoLabel header = new LumoLabel("Message Storage Configuration:");
		header.getStyle().set("font-size", "20px");
		
		storageInformationLayout.add(header);
		
		storagePath = new TextField("Message Storage Path:");
		storagePath.setWidth("500px");
		storagePath.setReadOnly(true);
		
		storageInformationLayout.add(storagePath);
		
		createDirs = new LumoCheckbox("Allow create directories");
		createDirs.setReadOnly(true);
		
		storageInformationLayout.add(createDirs);
		
		areaStorageInformation.add(storageInformationLayout);
		
		return areaStorageInformation;
	}
	
	private Div createProxyArea() {
		
		Div areaProxyInformation = new Div();
		
		VerticalLayout proxyInformationLayout = new VerticalLayout();
		
		LumoLabel header = new LumoLabel("Proxy Configuration:");
		header.getStyle().set("font-size", "20px");
		
		LumoLabel header2 = new LumoLabel("Only used for OCSP and CRL validation");
		header2.getStyle().set("font-size", "15px");
		
		proxyInformationLayout.add(header);
		proxyInformationLayout.add(header2);
		
		 useHttpProxyBox = new LumoCheckbox("HTTP Proxy set");
		 useHttpProxyBox.setReadOnly(true);
		 proxyInformationLayout.add(useHttpProxyBox);
		 
		 httpProxyHostField = new TextField("Proxy Host");
		 httpProxyHostField.setWidth("500px");
		 httpProxyHostField.setReadOnly(true);
		 proxyInformationLayout.add(httpProxyHostField);
		 
		 httpProxyPortField = new TextField("Proxy Port");
		 httpProxyPortField.setWidth("500px");
		 httpProxyPortField.setReadOnly(true);
		 proxyInformationLayout.add(httpProxyPortField);
		 
		 httpProxyUserField = new TextField("Proxy User");
		 httpProxyUserField.setWidth("500px");
		 httpProxyUserField.setReadOnly(true);
		 proxyInformationLayout.add(httpProxyUserField);
		 
		 httpProxyPasswordField = new TextField("Proxy Password");
		 httpProxyPasswordField.setWidth("500px");
		 httpProxyPasswordField.setReadOnly(true);
		 proxyInformationLayout.add(httpProxyPasswordField);
		
		 useHttpsProxyBox = new LumoCheckbox("HTTPS Proxy set");
		 useHttpsProxyBox.setReadOnly(true);
		 proxyInformationLayout.add(useHttpsProxyBox);
		 
		 httpsProxyHostField = new TextField("Proxy Host");
		 httpsProxyHostField.setWidth("500px");
		 httpsProxyHostField.setReadOnly(true);
		 proxyInformationLayout.add(httpsProxyHostField);
		 
		 httpsProxyPortField = new TextField("Proxy Port");
		 httpsProxyPortField.setWidth("500px");
		 httpsProxyPortField.setReadOnly(true);
		 proxyInformationLayout.add(httpsProxyPortField);
		 
		 httpsProxyUserField = new TextField("Proxy User");
		 httpsProxyUserField.setWidth("500px");
		 httpsProxyUserField.setReadOnly(true);
		 proxyInformationLayout.add(httpsProxyUserField);
		 
		 httpsProxyPasswordField = new TextField("Proxy Password");
		 httpsProxyPasswordField.setWidth("500px");
		 httpsProxyPasswordField.setReadOnly(true);
		 proxyInformationLayout.add(httpsProxyPasswordField);
		 
		 areaProxyInformation.add(proxyInformationLayout);
		 
		return areaProxyInformation;
	}

	private Div createConnectorVersionArea() {
		Div areaConnectorVersion = new Div();
		
		HorizontalLayout connectorVersionLayout = new HorizontalLayout();
		
		LumoLabel connectorVersionLabel = new LumoLabel("domibusConnector Version:");
		connectorVersionLabel.getStyle().set("font-size", "20px");
		
//		connectorVersion = new TextField("domibusConnector Version: ");
//		connectorVersion.setReadOnly(true);
		
		connectorVersion.getStyle().set("font-size", "20px");
		
		connectorVersionLayout.add(connectorVersionLabel);
		connectorVersionLayout.add(connectorVersion);
		connectorVersionLayout.setPadding(true);
		
		areaConnectorVersion.add(connectorVersionLayout);
		areaConnectorVersion.setVisible(true);
		return areaConnectorVersion;
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		connectorVersion.setText("Version: " + buildProperties.getVersion() + " build time: " + buildProperties.getTime());
		dbUrlTextField.setValue(dataSourceProperties.getUrl());
		dbDriverClassTextField.setValue(dataSourceProperties.getDriverClassName());
		dbUsernameTextField.setValue(dataSourceProperties.getUsername());
		
		storagePath.setValue(env.getProperty(PROPERTY_FILESYSTEM_PATH)!=null?env.getProperty(PROPERTY_FILESYSTEM_PATH):"");
		createDirs.setValue(env.getProperty(PROPERTY_CREATE_DIRS)!=null?Boolean.valueOf(env.getProperty(PROPERTY_CREATE_DIRS)):false);
		
		useHttpProxyBox.setValue(env.getProperty(PROPERTY_USE_HTTP_PROXY)!=null?Boolean.valueOf(env.getProperty(PROPERTY_USE_HTTP_PROXY)):false);
		 httpProxyHostField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_HOST)!=null?env.getProperty(PROPERTY_HTTP_PROXY_HOST):"");
		 httpProxyPortField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_PORT)!=null?env.getProperty(PROPERTY_HTTP_PROXY_PORT):"");
		 httpProxyUserField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_USER)!=null?env.getProperty(PROPERTY_HTTP_PROXY_USER):"");
		 httpProxyPasswordField.setValue(env.getProperty(PROPERTY_HTTP_PROXY_PASS)!=null?env.getProperty(PROPERTY_HTTP_PROXY_PASS):"");
		
		 useHttpsProxyBox.setValue(env.getProperty(PROPERTY_USE_HTTPS_PROXY)!=null?Boolean.valueOf(env.getProperty(PROPERTY_USE_HTTPS_PROXY)):false);
		 httpsProxyHostField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_HOST)!=null?env.getProperty(PROPERTY_HTTPS_PROXY_HOST):"");
		 httpsProxyPortField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_PORT)!=null?env.getProperty(PROPERTY_HTTPS_PROXY_PORT):"");
		 httpsProxyUserField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_USER)!=null?env.getProperty(PROPERTY_HTTPS_PROXY_USER):"");
		 httpsProxyPasswordField.setValue(env.getProperty(PROPERTY_HTTPS_PROXY_PASS)!=null?env.getProperty(PROPERTY_HTTPS_PROXY_PASS):"");
	}

	
}
