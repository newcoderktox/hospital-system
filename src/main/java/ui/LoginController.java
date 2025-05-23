package ui;

import database.LoginDB;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import java.io.IOException;
public class LoginController {


    @FXML
    private TextField userField;
    @FXML
    private PasswordField pswField;
    @FXML
    private Label msgLabel;
    private LoginDB myLoginDB;

    //run when screen opens
    public void initialize() {
        myLoginDB = new LoginDB(); //make login db obj
    }
    @FXML
    private void doLogin() { //
        String username = userField.getText();
        String psw = pswField.getText();
        //check if empty
        if (username.isEmpty() || psw.isEmpty()) {
            msgLabel.setText("Kullanıcı adı ve şifre boş bırakılamaz.");
            return;
        }
        //check if valid input (tc no)
        if (!checkInput(username)) {
            msgLabel.setText("Lütfen geçerli bir 11 haneli rakamlardan oluşan TC Kimlik No girin.");
            return;
        }
        //try to login
        User loggedUser = myLoginDB.checkLogin(username, psw); //
        if (loggedUser != null) {
            //login ok go to dashboard
            openDashboard(loggedUser);
        } else {
            msgLabel.setText("Kullanıcı adı veya şifre yanlış.");
        }
    }
    //check if input is valid
    private boolean checkInput(String input) {
        //check length
        if (input == null || input.length() != 11) {
            return false;
        }
        //check if all digits
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true; //all good
    }

    @FXML
    private void RegisterScreen() {
        //open register screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/RegisterUI.fxml"));
            Scene newScene = new Scene(loader.load());
            Stage currentStage = (Stage) userField.getScene().getWindow();
            currentStage.setTitle("Hastane Randevu Sistemi - Kayıt");
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("ERROR: register screen load problem " + e.getMessage());
            e.printStackTrace();
        }
    }
    //load main dashboard screen
    private void openDashboard(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainDashboard.fxml"));
            Scene newScene = new Scene(loader.load());
            Stage currentStage = (Stage) userField.getScene().getWindow();
            currentStage.setTitle("Hastane Randevu Sistemi - Ana Sayfa");
            currentStage.setScene(newScene);
            currentStage.show();
            //pass user to dashboard controller
            MainDashboardController dashController = loader.getController();
            if (dashController != null) {
                dashController.setUser(user);
            }
        } catch (IOException e) {
            System.err.println("ERROR: main dashb load err " + e.getMessage());
            e.printStackTrace();
        }
    }
}
