package eu.domibus.connector.ui.forms;

import java.util.EnumSet;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dependency.HtmlImport;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;

import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.enums.UserRole;

//@HtmlImport("styles/shared-styles.html")
public class WebUserForm extends FormLayout{

	private TextField username = FormsUtil.getFormattedTextFieldReadOnly();
	private ComboBox<UserRole> roleBox = new ComboBox<UserRole>("",EnumSet.allOf(UserRole.class));
	private LumoCheckbox lockedBox = new LumoCheckbox();
	private TextField createdString = FormsUtil.getFormattedTextFieldReadOnly();
	
	private WebUser user = null;
	
	private Binder<WebUser> binder = new Binder<>(WebUser.class);
			
	public WebUserForm() {
		fillForm();
	}
	
	private void fillForm() {
		binder.bindInstanceFields(this);
		
		addFormItem(username, "Username"); 
		addFormItem(roleBox, "User-Role");
		addFormItem(lockedBox, "Locked");
		addFormItem(createdString, "User created at"); 
	}

	public void setUser(WebUser user) {
		this.removeAll();
		if(user!=null && user.getRole()!=null)
			roleBox.setValue(user.getRole());
		else
			roleBox.clear();
		if(user!=null) {
			lockedBox.setValue(user.isLocked());
		}
		fillForm();
		this.user = user;
		binder.setBean(user);
		
		
	}
	
	public WebUser getUser() {
		this.user.setLocked(lockedBox.getValue());
		this.user.setRole(roleBox.getValue());
		return this.user;
	}

}
