package eu.domibus.connector.ui.login;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.textfield.PasswordField;
import eu.domibus.connector.ui.exception.UserLoginException;
import eu.domibus.connector.ui.service.WebUserService;
import eu.domibus.connector.ui.view.DashboardView;


public class ChangePasswordDialog extends Dialog {
    WebUserService userService;
    String username;
    private Button changePasswordButton;
    private final PasswordField currentPwField;
    private final PasswordField newPwField;
    private final PasswordField confirmNewPw;

    public ChangePasswordDialog(WebUserService userService, String username, String password) {
        this.userService = userService;
        this.username = username;

        Div changePasswordDiv = new Div();
        Label changePassword = new Label("Change Password for User " + username);
        changePassword.getStyle().set("font-weight", "bold");
        changePasswordDiv.add(changePassword);
        changePasswordDiv.getStyle().set("text-align", "center");
        changePasswordDiv.setVisible(true);
        add(changePasswordDiv);

        Div changePassword2Div = new Div();
        Label changePassword2 = new Label("Your password must be changed.");
        changePassword2Div.add(changePassword2);
        changePassword2Div.getStyle().set("text-align", "center");
        changePassword2Div.setVisible(true);
        add(changePassword2Div);

        Div currentPwDiv = new Div();
        currentPwField = new PasswordField();
        currentPwField.setLabel("Current Password:");
        currentPwField.setValue(password);
        currentPwDiv.add(currentPwField);
        currentPwDiv.getStyle().set("text-align", "center");
        add(currentPwDiv);

        Div newPwDiv = new Div();
        newPwField = new PasswordField();
        newPwField.setLabel("New Password:");
        newPwDiv.add(newPwField);
        newPwDiv.getStyle().set("text-align", "center");
        add(newPwDiv);

        Div confirmNewPwDiv = new Div();
        confirmNewPw = new PasswordField();
        confirmNewPw.setLabel("Confirm new Password:");
        confirmNewPwDiv.add(confirmNewPw);
        confirmNewPwDiv.getStyle().set("text-align", "center");
        confirmNewPw.addKeyPressListener(Key.ENTER, (e) -> this.changePasswordButton.click());
        add(confirmNewPwDiv);

        Div changePasswordButtonContent = new Div();
        changePasswordButtonContent.getStyle().set("text-align", "center");
        changePasswordButtonContent.getStyle().set("padding", "10px");
        changePasswordButton = new Button("Change Password");
        changePasswordButton.addClickListener(this::changePasswordButtonPressed);
        changePasswordButtonContent.add(changePasswordButton);

        add(changePasswordButtonContent);
    }

    private void changePasswordButtonPressed(ClickEvent<Button> buttonClickEvent) {
        if (currentPwField.isEmpty()) {
            Dialog errorDialog = new LoginErrorDialog("The Field 'Current Password' must not be empty!");
            errorDialog.open();
            return;
        }
        if (newPwField.isEmpty()) {
            Dialog errorDialog = new LoginErrorDialog("The Field 'New Password' must not be empty!");
            errorDialog.open();
            return;
        }
        if (confirmNewPw.isEmpty()) {
            Dialog errorDialog = new LoginErrorDialog("The Field 'Confirm new Password' must not be empty!");
            errorDialog.open();
            return;
        }
        if (!newPwField.getValue().equals(confirmNewPw.getValue())) {
            Dialog errorDialog = new LoginErrorDialog(
                    "The Fields 'New Password' and 'Confirm new Password' must have the same values!");
            newPwField.clear();
            confirmNewPw.clear();
            errorDialog.open();
            return;
        }

        String currentPw = currentPwField.getValue();
        String newPw = newPwField.getValue();

        try {
            userService.changePasswordLogin(username, currentPw, newPw);
        } catch (UserLoginException e1) {
            Dialog errorDialog = new LoginErrorDialog(e1.getMessage());
            currentPwField.clear();
            newPwField.clear();
            confirmNewPw.clear();
            errorDialog.open();
            return;
        }

        this.getUI().ifPresent(ui -> ui.navigate(DashboardView.class));
        close();
    }
}
