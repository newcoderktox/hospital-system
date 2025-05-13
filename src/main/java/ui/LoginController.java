// src/main/java/ui/LoginUIController.java
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
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label messageLabel;

    private LoginDB loginDB;

    public void initialize() {
        loginDB = new LoginDB();
    }

    @FXML
    private void handleLoginButton() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Kullanıcı adı ve şifre boş bırakılamaz.");
            return;
        }

        User loggedInUser = loginDB.authenticateUser(username, password);

        if (loggedInUser != null) {
            // Giriş başarılı! Ana dashboard'a yönlendir
            loadMainDashboard(loggedInUser);
        } else {
            messageLabel.setText("Kullanıcı adı veya şifre yanlış.");
        }
    }

    @FXML
    private void handleRegisterButton() {
        // Kayıt olma ekranını yükle
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/RegisterUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Hastane Randevu Sistemi - Kayıt");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Kayıt ekranı yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Ana dashboard'u yükleyen yardımcı metod
    private void loadMainDashboard(User user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/MainDashboard.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setTitle("Hastane Randevu Sistemi - Ana Sayfa");
            stage.setScene(scene);
            stage.show();

            // MainDashboardController'a giriş yapan kullanıcıyı aktar
            MainDashboardController controller = fxmlLoader.getController();
            if (controller != null) {
                controller.setUser(user);
            }

        } catch (IOException e) {
            System.err.println("Ana dashboard yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}