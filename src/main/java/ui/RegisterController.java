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
import javafx.event.ActionEvent; // ActionEvent importu ekle

import java.io.IOException; // IOException'ı import et
import java.net.URL; // URL importu ekle
import java.util.ResourceBundle; // ResourceBundle importu ekle
import javafx.fxml.Initializable; // Initializable importu ekle


// Initializable arayüzünü uygula
public class RegisterController implements Initializable { // <<<< Initializable eklendi

    @FXML
    private ComboBox<String> userTypeComboBox;
    @FXML
    private TextField usernameField; // TC Kimlik No için
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

    private LoginDB loginDB; // Veritabanı işlemleri için

    // initialize metodu (FXML yüklendiğinde otomatik çağrılır)
    @Override // <<<< Override anotasyonu eklendi
    public void initialize(URL url, ResourceBundle rb) { // <<<< Parametreler eklendi
        loginDB = new LoginDB(); // DB instance'ı oluştur
        // ComboBox'a kullanıcı tiplerini ekle
        userTypeComboBox.setItems(FXCollections.observableArrayList("doctor", "patient"));

        // ComboBox'ta seçim değiştiğinde tetiklenecek olay dinleyicisi
        userTypeComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            // Tüm özel alanları başlangıçta gizle ve yönetimi kapat (initialize'da zaten yapılıyor ama dinleyici içinde de emin olalım)
            specializationField.setVisible(false);
            specializationField.setManaged(false);
            dateOfBirthField.setVisible(false);
            dateOfBirthField.setManaged(false);
            bloodGroupField.setVisible(false);
            bloodGroupField.setManaged(false);

            // Seçilen kullanıcı tipine göre ilgili alanları göster ve yönetimi aç
            if ("doctor".equals(newValue)) {
                specializationField.setVisible(true);
                specializationField.setManaged(true);
            } else if ("patient".equals(newValue)) {
                dateOfBirthField.setVisible(true);
                dateOfBirthField.setManaged(true);
                bloodGroupField.setVisible(true);
                bloodGroupField.setManaged(true);
            }
            // Mesaj label'ını temizle
            messageLabel.setText("");
        });

        // Alanların başlangıç managed/visible durumu initialize'da ayarlanmalı (Tekrar emin olmak için)
        specializationField.setVisible(false);
        specializationField.setManaged(false);
        dateOfBirthField.setVisible(false);
        dateOfBirthField.setManaged(false);
        bloodGroupField.setVisible(false);
        bloodGroupField.setManaged(false);


        System.out.println("DEBUG: RegisterController initialize edildi."); // DEBUG
    }

    // Kayıt butonuna tıklandığında çağrılır (FXML'de onAction="#handleRegisterButton" tanımlı olmalı)
    @FXML
    private void handleRegisterButton(ActionEvent event) { // <<<< ActionEvent parametresi eklendi
        String userType = userTypeComboBox.getValue(); // .trim() combobox değeri için genellikle gerekmez
        String username = usernameField.getText().trim(); // TC Kimlik No değeri
        String password = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        String name = nameField.getText().trim();
        String surname = surnameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();

        // Zorunlu alan kontrolü (userType null kontrolü başlangıçta eklendi)
        if (userType == null || userType.isEmpty() || username.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || name.isEmpty() || surname.isEmpty() ||
                phoneNumber.isEmpty() || email.isEmpty()) {
            messageLabel.setText("Lütfen tüm zorunlu alanları doldurun."); // Mesajı güncelle
            return;
        }

        // Şifrelerin eşleşip eşleşmediğini kontrol et
        if (!password.equals(confirmPassword)) {
            messageLabel.setText("Şifreler eşleşmiyor.");
            return;
        }

        // **TC KİMLİK NO VALIDASYONU EKLE**
        // username alanı için validasyon kontrolü
        if (!isValidInput(username)) { // isValidInput metodunu çağır
            messageLabel.setText("Hata: TC Kimlik No 11 haneli rakamlardan oluşmalıdır."); // Hata mesajı
            return; // Validasyon başarısız olursa işlemi durdur
        }


        // Kullanıcı tipine göre ek alan kontrolü (Validasyondan sonra gelmeli)
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
        // id 0 olarak geçiliyor, veritabanı otomatik atayacaktır
        User newUser = UserFactory.createUser(userType, 0, username, password, name, surname, phoneNumber, email, extraData1, extraData2);

        if (newUser != null) {
            System.out.println("DEBUG: Kayıt işlemi başladı for user: " + username); // DEBUG
            if (loginDB.registerUser(newUser)) {
                messageLabel.setText("Kayıt başarıyla tamamlandı. Şimdi giriş yapabilirsiniz.");
                System.out.println("DEBUG: Kayıt başarıyla tamamlandı."); // DEBUG
                // Kayıt başarılı olunca giriş ekranına geri dön
                // Küçük bir gecikme ekleyebiliriz mesajın görülmesi için (isteğe bağlı)
                // Platform.runLater(() -> loadLoginUI(event)); // Event'i pass et
                loadLoginUI(event); // <<<< Event'i pass et

            } else {
                messageLabel.setText("Kayıt başarısız. TC Kimlik No zaten mevcut olabilir."); // Mesajı güncelle
                System.err.println("HATA: Kayıt başarısız. Kullanıcı adı mevcut."); // DEBUG
            }
        } else {
            messageLabel.setText("Kullanıcı nesnesi oluşturulurken bir hata oluştu.");
            System.err.println("HATA: Kullanıcı nesnesi null döndü UserFactory."); // DEBUG
        }
    }

    // TC Kimlik No format validasyon metodu (LoginController'dan kopyalandı, en ilkel versiyon)
    // Bu metod, girilen stringin 11 haneli olup olmadığını ve sadece rakamlardan oluşup oluşmadığını en temel şekilde kontrol eder
    private boolean isValidInput(String input) {
        // Girdinin null olup olmadığını veya tam olarak 11 karakter uzunluğunda olup olmadığını kontrol et
        if (input == null || input.length() != 11) {
            return false; // null ise veya 11 hane değilse geçerli değil
        }

        // Girdinin her karakterinin bir rakam olup olmadığını kontrol et
        for (int i = 0; i < input.length(); i++) {
            // Döngünün her adımında sıradaki karakteri alalım
            char currentChar = input.charAt(i);

            // Aldığımız karakterin bir rakam olup olmadığını kontrol edelim ('0'dan '9'a kadar)
            if (currentChar < '0' || currentChar > '9') { // Rakam değilse
                return false; // Rakamsal olmayan bir karakter bulduk, o yüzden girdi geçerli değil.
            }
        }
        // Tüm kontrollerden geçtiyse geçerlidir
        return true;
    }


    // Geri butonu veya başarılı kayıt sonrası giriş ekranına döner
    @FXML
    private void handleBackButton(ActionEvent event) { // <<<< ActionEvent parametresi eklendi
        loadLoginUI(event); // Giriş ekranına geri dön, event'i pass et
    }

    // Giriş ekranını yükleyen yardımcı metod
    // Event parametresi ekledik, böylece mevcut pencereyi kullanabiliriz.
    private void loadLoginUI(ActionEvent event) { // <<<< ActionEvent parametresi eklendi
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/LoginUI.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            // Stage'i almak için event kaynağından yararlan
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Hastane Randevu Sistemi - Giriş");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Giriş ekranı yüklenirken hata oluştu: " + e.getMessage());
            e.printStackTrace();
        }
    }
}