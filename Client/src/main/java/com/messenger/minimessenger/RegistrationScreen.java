package com.messenger.minimessenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RegistrationScreen implements Initializable {

    @FXML
    private Button registerButton;

    @FXML
    private Button backButton;

    @FXML
    private Label registrationStatusLabel;

    @FXML
    private TextField usernameField;

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label usernameWarning;

    @FXML
    private Label nameWarning;

    @FXML
    private Label emailWarning;

    @FXML
    private Label passwordWarning;

    @FXML private  Button closeButton;
    @FXML private  Button minButton;
    @FXML private Pane RegToBqck;
    Stage stage = null;

    @FXML
    private void goBack(ActionEvent e) throws IOException {
        Main m = new Main();
        m.changeScene("hello-view.fxml");
    }

    @FXML
    private void register (ActionEvent event) throws IOException {

        if (!checkFields())
            return;
        else {
            if (JavaPostgreSql.registerUser(usernameField.getText(), nameField.getText(),
                    emailField.getText(), passwordField.getText()))
            {
                registrationStatusLabel.setText("You're registered");
                Main m = new Main();
                Main.user = JavaPostgreSql.getUserByUsername(usernameField.getText());
                m.changeScene("afterLogin.fxml");
            }
            else
            {
                registrationStatusLabel.setText("Username/email already registered");
            }

        }
    }

    boolean checkFields()
    {

        boolean ifFieldsOK = true;

        TextField [] textFields = {usernameField, nameField, emailField, passwordField};
        Label [] labels = {usernameWarning, nameWarning, emailWarning, passwordWarning};
        registrationStatusLabel.setText("");
        for (int i=0; i < labels.length;i++)
        {
            labels[i].setText("");
            textFields[i].setStyle("-fx-text-box-border:#bababa");
        }
        //String [] fields = {"Username", "Name", "E-mail", "Password"};
        int [] sizes = {10, 40, 50, 10};

        for (int i=0; i < textFields.length;i++)
        {
            if (textFields[i].getText().isEmpty())
            {
                registrationStatusLabel.setText("Entry empty fields");
                textFields[i].setStyle("-fx-text-box-border: #cb1919");
                ifFieldsOK = false;
            }
            else if (textFields[i].getText().length() > sizes[i])
            {
                labels[i].setText("Can not be greater than " + sizes[i]);
                textFields[i].setStyle("-fx-text-box-border: #cb1919");
                ifFieldsOK = false;
            }
        }

        if (ifFieldsOK == false)
        {
            return false;
        }

        if (!Character.isAlphabetic(usernameField.getText().charAt(0))
                && !Character.isDigit(usernameField.getText().charAt(0)))
        {
            usernameWarning.setText("1st character of name should be alpha or digit");
            usernameField.setStyle("-fx-text-box-border: #cb1919");
            ifFieldsOK = false;
        }

        int index = emailField.getText().indexOf('@', 0);
        if (index != -1 && ((emailField.getText().length() != index+1)
                && (emailField.getText().charAt(index+1) != '.')))
        {
            index = emailField.getText().indexOf('.', index);
            if (index == -1)
            {
                emailWarning.setText("Enter correct email");
                emailField.setStyle("-fx-text-box-border: #cb1919");
                ifFieldsOK = false;
            }
            else
            {
                if(emailField.getText().length() == index +1
                        || !Character.isAlphabetic(emailField.getText().charAt(index+1)))
                {
                    emailWarning.setText("Enter correct email");
                    emailField.setStyle("-fx-text-box-border: #cb1919");
                    ifFieldsOK = false;
                }
            }
        }
        else {
            emailWarning.setText("Enter correct email");
            emailField.setStyle("-fx-text-box-border: #cb1919");
            ifFieldsOK = false;
        }

        return ifFieldsOK;
    }
    @FXML
    private void handleMinButtonAction(ActionEvent event) {
        stage = (Stage) RegToBqck.getScene().getWindow();
        stage.setIconified(true);
    }


    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        stage = (Stage) RegToBqck.getScene().getWindow();
        stage.close();
    }
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Image CloseImg2;
        try {
            CloseImg2 = new Image(new FileInputStream("CloseWindow.png"), 20, 20, false, false);
            closeButton.setGraphic((new ImageView(CloseImg2)));
        } catch (
                FileNotFoundException ex) {
            Logger.getLogger(AfterLoginUser.class.getName()).log(Level.SEVERE, null, ex);
        }

        Image MinimizeWindowImg2;
        try {
            MinimizeWindowImg2 = new Image(new FileInputStream("MinimizeWindow.png"), 20, 20, false, false);
            minButton.setGraphic((new ImageView(MinimizeWindowImg2)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AfterLoginUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
