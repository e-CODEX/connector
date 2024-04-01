package eu.domibus.connector.ui.login;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyPressEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import eu.domibus.connector.ui.exception.InitialPasswordException;
import eu.domibus.connector.ui.exception.UserLoginException;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.view.DashboardView;
import eu.domibus.connector.ui.view.DomibusConnectorAdminHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;


@SpringComponent
@UIScope
@Route(value = LoginView.ROUTE)
@PageTitle("domibusConnector - Login")
public class LoginView extends VerticalLayout implements HasUrlParameter<String> {
    public static final String ROUTE = "login";
    public static final String PREVIOUS_ROUTE_PARAMETER = "afterLoginGoTo";
    private final AuthenticationManager authenticationManager;
    private final WebUserService webUserService;
    PasswordField password = new PasswordField();
    private final LoginOverlay login = new LoginOverlay();
    private String afterLoginGoTo = DashboardView.ROUTE;
    private final TextField username = new TextField();

    public LoginView(
            @Autowired WebUserService userService,
            @Autowired DomibusConnectorAdminHeader header,
            @Autowired AuthenticationManager authenticationManager
    ) {
        this.authenticationManager = authenticationManager;
        this.webUserService = userService;

        login.setAction("login"); //
        getElement().appendChild(login.getElement());

        add(header);

        HorizontalLayout login = new HorizontalLayout();
        VerticalLayout loginArea = new VerticalLayout();

        Button loginButton = new Button("Login");

        Div usernameDiv = new Div();

        username.setLabel("Username");
        username.setAutofocus(true);
        username.addKeyPressListener(Key.ENTER, new ComponentEventListener<KeyPressEvent>() {
            @Override
            public void onComponentEvent(KeyPressEvent event) {
                loginButton.click();
            }
        });
        usernameDiv.add(username);
        usernameDiv.getStyle().set("text-align", "center");
        loginArea.add(usernameDiv);

        Div passwordDiv = new Div();

        password.setLabel("Password");
        password.addKeyPressListener(Key.ENTER, new ComponentEventListener<KeyPressEvent>() {

            @Override
            public void onComponentEvent(KeyPressEvent event) {
                loginButton.click();
            }
        });
        passwordDiv.add(password);
        passwordDiv.getStyle().set("text-align", "center");
        loginArea.add(passwordDiv);

        Div loginButtonContent = new Div();
        loginButtonContent.getStyle().set("text-align", "center");
        loginButtonContent.getStyle().set("padding", "10px");

        loginButton.addClickListener(this::loginButtonClicked);
        loginButtonContent.add(loginButton);

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.addClickListener(e -> {
            if (username.getValue().isEmpty()) {
                Dialog errorDialog = new LoginErrorDialog("The field \"Username\" must not be empty!");
                username.clear();
                password.clear();
                errorDialog.open();
                return;
            }
            Dialog changePasswordDialog =
                    new ChangePasswordDialog(userService, username.getValue(), password.getValue());
            username.clear();
            password.clear();
            //			close();
            changePasswordDialog.open();
        });
        loginButtonContent.add(changePasswordButton);

        loginArea.add(loginButtonContent);

        loginArea.setSizeFull();
        loginArea.setAlignItems(Alignment.CENTER);
        loginArea.getStyle().set("align-items", "center");
        login.add(loginArea);
        login.setVerticalComponentAlignment(Alignment.CENTER, loginArea);

        add(loginArea);
    }

    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String parameter) {
        if (!StringUtils.isEmpty(parameter) && !LoginView.ROUTE.equals(parameter)) {
            afterLoginGoTo = parameter;
        }
    }

    private void loginButtonClicked(ClickEvent<Button> buttonClickEvent) {
        if (username.getValue().isEmpty()) {
            Dialog errorDialog = new LoginErrorDialog("The field \"Username\" must not be empty!");
            username.clear();
            password.clear();
            errorDialog.open();
            return;
        }
        if (password.getValue().isEmpty()) {
            Dialog errorDialog = new LoginErrorDialog("The field \"Password\" must not be empty!");
            password.clear();
            errorDialog.open();
            return;
        }
        try {
            Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    username.getValue(),
                    password.getValue()
            ));
            SecurityContextHolder.getContext().setAuthentication(authenticate);
            //				userService.login(username.getValue(), password.getValue());
        } catch (UserLoginException e1) {
            Dialog errorDialog = new LoginErrorDialog(e1.getMessage());
            username.clear();
            password.clear();
            errorDialog.open();
            return;
        } catch (InitialPasswordException e1) {
            Dialog changePasswordDialog =
                    new ChangePasswordDialog(webUserService, username.getValue(), password.getValue());
            username.clear();
            password.clear();
            //				close();
            changePasswordDialog.open();
        } catch (AuthenticationException authExceptoin) {
            // show error message...
        }
        // TODO: navigate to previous route...
        //			getUI().ifPresent(ui -> ui);
        this.getUI().ifPresent(ui -> ui.navigate(afterLoginGoTo));
        //			close();
    }
}
