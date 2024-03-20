package eu.domibus.connector.ui.view.areas.users;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.OptionalParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.ui.component.LumoLabel;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.forms.WebUserForm;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

//@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@UIScope
@Route(value = UserDetails.ROUTE, layout = UserLayout.class)
@Order(2)
@TabMetadata(title = "User Details", tabGroup = UserLayout.TAB_GROUP_NAME)
public class UserDetails extends VerticalLayout implements HasUrlParameter<String> {

	public static final String ROUTE = "userdetails";

	private WebUser user;
	private WebUserService userService;
	private WebUserForm userForm = new WebUserForm();
	
	LumoLabel result = new LumoLabel("");

	public UserDetails(@Autowired WebUserService service) {
		this.userService = service;
		
		Div resultDiv = new Div();
		
		result.getStyle().set("font-size", "20px");
		resultDiv.add(result);
//		resultDiv.setHeight("100vh");
		resultDiv.setWidth("100vw");

		VerticalLayout userDetailsArea = new VerticalLayout(); 

		userDetailsArea.add(userForm);
		
		userForm.setEnabled(true);

		Button edit = new Button(new Icon(VaadinIcon.EDIT));
		edit.getElement().setAttribute("title", "Save User");
		edit.setText("Save User");
		edit.addClickListener(e -> {
			boolean success = 	userService.saveUser(userForm.getUser());
		if(success) {
			result.setText("The user was successfully updated!");
			result.getStyle().set("color", "green");
		}else {
			result.setText("The update of the user failed!");
			result.getStyle().set("color", "red");
		}		
		});
		
		userDetailsArea.add(edit);
		
//		setSizeFull();
//		userDetailsArea.setHeight("100vh");
		userDetailsArea.setWidth("500px");
		add(userDetailsArea);

		
		Div reset = new Div();
		LumoLabel resetPasswordLabel = new LumoLabel("Reset the user's password");
		resetPasswordLabel.getStyle().set("font-size", "20px");
		reset.add(resetPasswordLabel);
		
		add(reset);
		
		
		HorizontalLayout resetPasswordArea = new HorizontalLayout();
		
		TextField newInitialPassword = new TextField("New initial Password:");
		resetPasswordArea.add(newInitialPassword);
		
		Button resetPasswordButton = new Button(
				new Icon(VaadinIcon.USER_CHECK));
		resetPasswordButton.setText("Reset Password");
		resetPasswordButton.addClickListener(e -> {
			boolean success = userService.resetUserPassword(user, newInitialPassword.getValue());
			if(success) {
				result.setText("The password was successfully reset to the new initial password!");
				result.getStyle().set("color", "green");
			}else {
				result.setText("The reset of the password failed!");
				result.getStyle().set("color", "red");
			}
			});
		resetPasswordArea.add(resetPasswordButton);
		resetPasswordArea.setAlignItems(Alignment.END);
		resetPasswordArea.setHeight("80px");
		resetPasswordArea.setWidth("100vw");
		add(resetPasswordArea);
		
		add(resultDiv);
		
//		setHeight("100vh");
	}

	public void showDetails(WebUser user) {
		UI.getCurrent().navigate(UserDetails.class, user.getUsername());
	}

	public void setUser(WebUser user) {
		this.result.setText("");
		this.user = user;
		userForm.setUser(user);
	}

	@Override
	public void setParameter(BeforeEvent event, @OptionalParameter String username)
	{
		WebUser user = userService.getAllUsers().stream()
				.filter(u -> u.getUsername().equals(username))
				.findFirst()
				.orElse(new WebUser());
		userForm.setUser(user);
	}
}
