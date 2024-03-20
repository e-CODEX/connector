package eu.domibus.connector.ui.view.areas.users;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.UIScope;

import eu.domibus.connector.ui.component.LumoCheckbox;
import eu.domibus.connector.ui.dto.WebUser;
import eu.domibus.connector.ui.enums.UserRole;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.view.areas.configuration.TabMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

//@HtmlImport("styles/shared-styles.html")
//@StyleSheet("styles/grid.css")
@Component
@UIScope
@Route(value = UserList.ROUTE, layout = UserLayout.class)
@Order(1)
@TabMetadata(title = "All Users", tabGroup = UserLayout.TAB_GROUP_NAME)
public class UserList extends VerticalLayout implements AfterNavigationObserver {

	public static final String ROUTE = "userlist";

	private Grid<WebUser> grid = new Grid<>();
	private List<WebUser> fullList = null;
	private WebUserService userService;

	private UserDetails userDetails;

	TextField usernameFilterText = new TextField();
	ComboBox<UserRole> roleFilterBox = new ComboBox<UserRole>();
	LumoCheckbox lockedFilterBox = new LumoCheckbox();

	public UserList(@Autowired WebUserService service, @Autowired UserDetails userDetails) {
		this.userService = service;
		this.userDetails = userDetails;

		fullList = new ArrayList<>();

		grid.setItems(fullList);
		grid.addComponentColumn(webUser -> getDetailsLink(webUser)).setHeader("Details").setWidth("30px");
		grid.addColumn(WebUser::getUsername).setHeader("Username").setWidth("250px").setSortable(true).setResizable(true);
		grid.addColumn(WebUser::getRole).setHeader("Role").setWidth("70px").setSortable(true).setResizable(true);
		grid.addComponentColumn(webUser -> getCheckboxForList(webUser.isLocked())).setHeader("Locked").setWidth("30px");
		grid.setWidth("1800px");
		grid.setHeight("700px");
		grid.setMultiSort(true);

		HorizontalLayout filtering = createFilterLayout();


		VerticalLayout main = new VerticalLayout(filtering, grid);
		main.setAlignItems(Alignment.STRETCH);
		main.setHeight("700px");
		add(main);
		setHeight("100vh");
		setWidth("100vw");
	}

	private HorizontalLayout createFilterLayout() {

		usernameFilterText.setPlaceholder("Filter by Username");
		usernameFilterText.setWidth("180px");
		usernameFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		usernameFilterText.addValueChangeListener(e -> filter());


		roleFilterBox.setPlaceholder("Filter by role");
		roleFilterBox.setWidth("180px");
		roleFilterBox.setItems(EnumSet.allOf(UserRole.class));
//		roleFilterBox.setValueChangeMode(ValueChangeMode.EAGER);
		roleFilterBox.addValueChangeListener(e -> filter());


		lockedFilterBox.setLabel("Filter by locked");
		lockedFilterBox.setWidth("180px");

//		serviceFilterText.setValueChangeMode(ValueChangeMode.EAGER);
		lockedFilterBox.addValueChangeListener(e -> filter());


		Button clearAllFilterTextBtn = new Button(
				new Icon(VaadinIcon.CLOSE_CIRCLE));
		clearAllFilterTextBtn.setText("Clear Filter");
		clearAllFilterTextBtn.addClickListener(e -> {
			usernameFilterText.clear();
			roleFilterBox.clear();
			lockedFilterBox.clear();
			reloadList();
		});

		Button relaodListBtn = new Button(
				new Icon(VaadinIcon.REFRESH));
		relaodListBtn.setText("Reload Users");
		relaodListBtn.addClickListener(e -> {
			reloadList();
		});

		HorizontalLayout filtering = new HorizontalLayout(
				usernameFilterText,
				roleFilterBox,
				lockedFilterBox,
				clearAllFilterTextBtn,
				relaodListBtn
		);
		filtering.setAlignItems(Alignment.CENTER);
		filtering.setWidth("100vw");

		return filtering;
	}

	private void filter() {
		List<WebUser> target = new LinkedList<WebUser>();
		for(WebUser msg : fullList) {
			if((usernameFilterText.getValue().isEmpty() || (msg.getUsername()!=null && msg.getUsername().toUpperCase().contains(usernameFilterText.getValue().toUpperCase())))
					&& (roleFilterBox.getValue()==null || (msg.getRole()!=null && msg.getRole().equals(roleFilterBox.getValue())))
					&& (lockedFilterBox.getValue()==null || (msg.isLocked()==lockedFilterBox.getValue()))) {
				target.add(msg);
			}
		}

		grid.setItems(target);
	}

	private Button getDetailsLink(WebUser user) {
		Button getDetails = new Button(new Icon(VaadinIcon.SEARCH));
		getDetails.addClickListener(e -> userDetails.showDetails(user));
		return getDetails;
	}

	private Checkbox getCheckboxForList(boolean checked) {
		Checkbox isChecked = new Checkbox(checked);
		isChecked.setReadOnly(true);

		return isChecked;
	}

	public void reloadList() {
		grid.setItems(userService.getAllUsers());
	}


	@Override
	public void afterNavigation(AfterNavigationEvent event) {
		reloadList();
	}

}

