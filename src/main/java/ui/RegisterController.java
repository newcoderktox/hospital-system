// src/main/java/ui/RegisterUIController.java
package ui;

import database.LoginDB;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.Doctor;
import models.Patient;
import models.User;
import models.UserFactory;
import javafx.stage.Stage; // Stage sınıfını import et
import javafx.scene.Scene; // Scene sınıfını import et
import javafx.fxml.FXMLLoader; // FXMLLoader sınıfını import et
import java.io.IOException; // IOException'ı import et

public class RegisterController {

    @FXML
    private ComboBox<String> userTypeComboBox;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private TextField nameField;
    @FXML
    private TextField surnameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField specializationField; // Doktor için
    @FXML
    private TextField dateOfBirthField;    // Hasta için
    @FXML
    private TextField bloodGroupField;     // Hasta için
    @FXML
    private Label messageLabel;

    private LoginDB loginDB;

    @FXML
    public void initialize() {
        loginDB = new LoginDB();
        // ComboBox'a kullanıcı tiplerini ekle
        userTypeComboBox.setItems(FXCollections.observableArrayList("doctor", "patient"));

        // ComboBox'ta seçim değiştiğinde tetiklenecek olay dinleyicisi
        userTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            // Tüm özel alanları gizle ve yönetimi kapat
            specializationField.setVisible(false);
            specializationField.setManaged(false);
            dateOfBirthField.setVisible(false);
            dateOfBirthField.setManaged(false);
            bloodGroupField.setVisible(false);
            bloodGroupField.setManaged(false);

            // Seçilen kullanıcı tipine göre ilgili alanları göster
            if ("doctor".equals(newValue)) {
                specializationField.setVisible(true);
                specializationField.setManaged(true);
            } else if ("patient".equals(newValue)) {
                dateOfBirthField.setVisible(true);
                dateOfBirthField.setManaged(true);
                bloodGroupField.setVisible(true);
                bloodGroupField.setManaged(true);
            }
        });
    }

    @FXML
    private void handleRegisterButton() {
        String userType = userTypeComboBox.getValue().trim();
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();

        // Zorunlu alan kontrolü
        if (userType == null || userType.isEmpty() || username.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || name.isEmpty() || surname.isEmpty() ||
                phoneNumber.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Lütfen tüm alanları doldurun.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Şifreler eşleşmiyor.");
            return;
        }

        // Kullanıcı tipine göre ek alan kontrolü
        String extraData1 = null;
        String extraData2 = null;
        if ("doctor".equals(userType)) {
            extraData1 = specializationField.getText().trim();
            if (extraData1.isEmpty()) {
                messageLabel.setText("Lütfen uzmanlık alanını girin.");
                return;
            }
        } else if ("patient".equals(userType)) {
            extraData1 = dateOfBirthField.getText().trim(); // Doğum tarihi
            extraData2 = bloodGroupField.getText().trim();   // Kan grubu
            if (extraData1.isEmpty() || extraData2.isEmpty()) {
                messageLabel.setText("Lütfen doğum tarihi ve kan grubu bilgilerini girin.");
                return;
            }
        }

        // UserFactory kullanarak kullanıcı nesnesi oluştur
        User newUser = UserFactory.createUser(userType, 0, username, password, name, surname, phoneNumber, email, extraData1, extraData2);

        if (newUser != null) {
            if (loginDB.registerUser(newUser)) {
                messageLabel.setText("Kayıt başarıyla tamamlandı. Şimdi giriş yapabilirsiniz.");
                // Kayıt başarılı olunca giriş ekranına geri dön
                loadLoginUI();
            } else {
                messageLabel.setText("Kayıt başarısız. Kullanıcı adı zaten mevcut olabilir.");
            }
        } else {
            messageLabel.setText("Kullanıcı nesnesi oluşturulurken bir hata oluştu.");
        }
    }

    @FXML
    private void handleBackButton() {
        loadLoginUI(); // Giriş ekranına geri dön
    }

    // Giriş ekranını yükleyen yardımcı metod
    private void loadLoginUI() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow(); // Mevcut pencereyi al
            stage.setTitle("Hastane Randevu Sistemi - Giriş");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Giriş ekranı yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}