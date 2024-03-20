package eu.domibus.connector.ui.view.areas.configuration.environment;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.forms.FormsUtil;
import eu.domibus.connector.ui.layout.DCVerticalLayoutWithTitleAndHelpButton;
import eu.domibus.connector.ui.service.WebPModeService;
import eu.domibus.connector.ui.utils.RoleRequired;
import eu.domibus.connector.ui.view.areas.configuration.ConfigurationLayout;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationItemChapterDiv;
import eu.domibus.connector.ui.view.areas.configuration.util.ConfigurationUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;


/**
 * @author riederb
 *
 * This class should handle the following parameters:
 * 
 * 	connector.gatewaylink.ws.submissionEndpointAddress

	connector.test.service
	connector.test.action

	http.proxy.enabled
	http.proxy.host
	http.proxy.port
	http.proxy.user=
	http.proxy.password=

	https.proxy.enabled
	https.proxy.host
	https.proxy.port
	https.proxy.user=
	https.proxy.password=

	spring.datasource.driver-class-name
	spring.datasource.username
	spring.datasource.url
	spring.datasource.password
	spring.jpa.properties.hibernate.dialect

 */
//@Component
//@UIScope
//@Route(value = EnvironmentConfiguration.ROUTE, layout = ConfigurationLayout.class)
//@TabMetadata(title = "Environment Configuration", tabGroup = ConfigurationLayout.TAB_GROUP_NAME)
//@RoleRequired(role = "ADMIN")
//@Order(1)
public class EnvironmentConfiguration extends DCVerticalLayoutWithTitleAndHelpButton{

	public static final String ROUTE = "environment";

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final String TITLE = "Environment Configuration";
	public static final String HELP_ID = "ui/configuration/environment_configuration.html";

	ConfigurationUtil util;
	
//	TextField persistenceFSStoragePathField = FormsUtil.getFormattedTextField();
//	LumoCheckbox persistenceFSStorageCreateDirBox = new LumoCheckbox();
	
//	ComboBox<String> serviceBox = new ComboBox<String>();
//	ComboBox<String> actionBox = new ComboBox<String>();
	
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
	
//	TextField databaseConnectionStringField = FormsUtil.getFormattedTextField();
//	TextField databaseUserField = FormsUtil.getFormattedTextField();
//	TextField databasePasswordField = FormsUtil.getFormattedTextField();
//	TextField databaseDriverClassField = FormsUtil.getFormattedTextField();
//	TextField databaseDialectField = FormsUtil.getFormattedTextField();

	public EnvironmentConfiguration(@Autowired ConfigurationUtil util) {
		super(HELP_ID, TITLE);
		
		this.util = util;
		
//		add(new ConfigurationItemChapterDiv("Message Storage Configuration:"));
//		
//		add(util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.connectorPersistenceFileSystemStoragePathLabels, persistenceFSStoragePathField));
//		
//		add(util.createConfigurationItemCheckboxDiv(EnvironmentConfigurationLabels.connectorPersistenceFileSystemCreateDirLabels, persistenceFSStorageCreateDirBox));
		

//		createAndAddServiceComboBox(pmodeService);
//
//		createAndAddActionComboBox(pmodeService);
		
		add(new ConfigurationItemChapterDiv("Proxy Configuration:"));
		
		useHttpProxyBox.addValueChangeListener(e -> {
			httpProxyHostField.setReadOnly(!e.getValue());
			httpProxyPortField.setReadOnly(!e.getValue());
			httpProxyUserField.setReadOnly(!e.getValue());
			httpProxyPasswordField.setReadOnly(!e.getValue());
		});
		Div useHttpProxy = util.createConfigurationItemCheckboxDiv(EnvironmentConfigurationLabels.useHttpProxyLabels, useHttpProxyBox);
		add(useHttpProxy);
		
		Div httpProxyHost = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpProxyHostLabels, httpProxyHostField);
		add(httpProxyHost);
		
		httpProxyPortField.setWidth("300px");
		Div httpProxyPort = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpProxyPortLabels, httpProxyPortField);
		add(httpProxyPort);
		
		Div httpProxyUser = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpProxyUserLabels, httpProxyUserField);
		add(httpProxyUser);
		
		Div httpProxyPassword = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpProxyPasswordLabels, httpProxyPasswordField);
		add(httpProxyPassword);
		
		useHttpsProxyBox.addValueChangeListener(e -> {
			httpsProxyHostField.setReadOnly(!e.getValue());
			httpsProxyPortField.setReadOnly(!e.getValue());
			httpsProxyUserField.setReadOnly(!e.getValue());
			httpsProxyPasswordField.setReadOnly(!e.getValue());
		});
		Div useHttpsProxy = util.createConfigurationItemCheckboxDiv(EnvironmentConfigurationLabels.useHttpsProxyLabels, useHttpsProxyBox);
		add(useHttpsProxy);
		
		Div httpsProxyHost = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpsProxyHostLabels, httpsProxyHostField);
		add(httpsProxyHost);
		
		httpsProxyPortField.setWidth("300px");
		Div httpsProxyPort = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpsProxyPortLabels, httpsProxyPortField);
		add(httpsProxyPort);
		
		Div httpsProxyUser = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpsProxyUserLabels, httpsProxyUserField);
		add(httpsProxyUser);
		
		Div httpsProxyPassword = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.httpsProxyPasswordLabels, httpsProxyPasswordField);
		add(httpsProxyPassword);
		
		// riederb: deactivated - db setting must come from container configuration!
		
//		add(new ConfigurationItemChapterDiv("Database Connection Configuration:"));
//		
//		Div databaseUrl = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.databaseConnectionStringLabels, databaseConnectionStringField);
//		add(databaseUrl);
//		
//		Div databaseUser = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.databaseUserLabels, databaseUserField);
//		add(databaseUser);
//		
//		Div databasePassword = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.databasePasswordLabels, databasePasswordField);
//		add(databasePassword);
//		
//		Div databaseDriver = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.databaseDriverClassNameLabels, databaseDriverClassField);
//		add(databaseDriver);
//		
//		Div databaseDialect = util.createConfigurationItemTextFieldDiv(EnvironmentConfigurationLabels.databaseDialectLabels, databaseDialectField);
//		add(databaseDialect);
		
	}

//	private void createAndAddActionComboBox(WebPModeService pmodeService) {
//		Collection<String> actionList = pmodeService.getActionListString();
//		add(this.util.createConfigurationItemComboBoxDiv(EnvironmentConfigurationLabels.connectorTestActionLabels, actionBox, actionList));
//
//	}
//
//	private void createAndAddServiceComboBox(WebPModeService pmodeService) {
//		List<String> serviceList = pmodeService.getServiceListString();
//		add(util.createConfigurationItemComboBoxDiv(EnvironmentConfigurationLabels.connectorTestServiceLabels, serviceBox, serviceList));
//
//	}
	
	

}
