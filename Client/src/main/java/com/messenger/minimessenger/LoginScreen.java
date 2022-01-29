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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginScreen  implements Initializable  {
    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginButton;

    @FXML
    private Label warningLabel;

    @FXML
    private TextField email;

    @FXML
    private Button getDataButton;

    @FXML
    private Button createAccountButton;

    @FXML Button toChats;

    @FXML private Pane mainPane;
    Stage stage = null;

    @FXML private Button closeButton;
    @FXML private Button minButton;
    @FXML
    private URL location;
    @FXML
    private ResourceBundle resources;
    @FXML
    private void userLogin(ActionEvent event) throws IOException {
        warningLabel.setText("");
        boolean isOk = true;
        if (password.getText().isEmpty()) {
            password.setStyle("-fx-text-box-border: #cb1919");
            isOk = false;
        }
        if (email.getText().isEmpty()) {
            email.setStyle("-fx-text-box-border: #cb1919");
            isOk = false;
        }

        if (isOk == false) {
            warningLabel.setText("Enter empty fields");
            return;
        }

        email.setStyle("-fx-text-box-border:#bababa");
        password.setStyle("-fx-text-box-border:#bababa");

        if (email.getText().charAt(0) == '@') {
            warningLabel.setText("Enter correct email");
            email.setStyle("-fx-text-box-border: #cb1919");
            return;
        }

        int index = email.getText().indexOf('@', 0);
        if (index != -1 && ((email.getText().length() != index + 1)
                && (email.getText().charAt(index + 1) != '.'))) {
            index = email.getText().indexOf('.', index);
            if (index == -1) {
                warningLabel.setText("Enter correct email");
                email.setStyle("-fx-text-box-border: #cb1919");
                return;
            } else {
                if (email.getText().length() == index + 1
                        || !Character.isAlphabetic(email.getText().charAt(index + 1))) {
                    warningLabel.setText("Enter correct email");
                    email.setStyle("-fx-text-box-border: #cb1919");
                    return;
                }
            }
        } else {
            warningLabel.setText("Enter correct email");
            email.setStyle("-fx-text-box-border: #cb1919");
            return;
        }

        if (!JavaPostgreSql.isUserAccountActive(email.getText()))
        {
            warningLabel.setText("Account is deleted");
            return;
        }

        String password = JavaPostgreSql.checkIfUserExists(email.getText());
        if (password.length() != 0) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(this.password.getText(), password)) {

                Main.user = JavaPostgreSql.getUserByEmail(email.getText());

                if (Main.user.getUsername().equals(""))
                {
                    warningLabel.setText("Can't get info about your account");
                    return;
                }

                Main m = new Main();
                m.changeScene("afterLogin.fxml");

            } else
                warningLabel.setText("Incorrect Password");
        } else
            warningLabel.setText("User not found");
    }

    @FXML
    private void getData(ActionEvent event) {
        JavaPostgreSql.readFromDatabase();
    }

    @FXML
    private void createAccount(ActionEvent event) throws IOException {
        Main m = new Main();
        m.changeScene("RegistrationScreen.fxml");
    }

    @FXML
    private void changeSceneToChats() throws IOException
    {
        Main m = new Main();
        Main.user = JavaPostgreSql.getUserByUsername("ILA");

        m.changeScene("afterLogin.fxml");
    }



    @FXML
    private void handleMinButtonAction(ActionEvent event) {
        stage = (Stage) mainPane.getScene().getWindow();
        stage.setIconified(true);
    }


    @FXML
    private void handleCloseButtonAction(ActionEvent event) {
        stage = (Stage) mainPane.getScene().getWindow();
        stage.close();
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

        Image CloseImg2;

            CloseImg2 = new Image(getClass().getClassLoader().getResourceAsStream("CloseWindow.png"), 20, 20, false, false);
            closeButton.setGraphic((new ImageView(CloseImg2)));


        Image MinimizeWindowImg2;
            MinimizeWindowImg2 = new Image(getClass().getClassLoader().getResourceAsStream("MinimizeWindow.png"), 20, 20, false, false);
            minButton.setGraphic((new ImageView(MinimizeWindowImg2)));

    }
}