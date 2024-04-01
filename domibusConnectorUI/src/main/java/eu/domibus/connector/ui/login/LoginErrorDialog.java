package eu.domibus.connector.ui.login;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;


public class LoginErrorDialog extends Dialog {
    public LoginErrorDialog(String errorMessage) {
        Div loginExceptionDiv = new Div();
        Label loginException = new Label(errorMessage);
        loginException.getStyle().set("font-weight", "bold");
        loginException.getStyle().set("color", "red");
        loginExceptionDiv.add(loginException);
        loginExceptionDiv.getStyle().set("text-align", "center");
        loginExceptionDiv.setVisible(true);
        add(loginExceptionDiv);

        Div okContent = new Div();
        okContent.getStyle().set("text-align", "center");
        okContent.getStyle().set("padding", "10px");
        Button okButton = new Button("OK");
        okButton.addClickListener(e2 -> close());
        okButton.setAutofocus(true);
        okContent.add(okButton);

        add(okContent);
    }
}
