package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.Doctor; // Doctor sınıfını import et
import models.Patient; // Patient sınıfını import et
import models.User; // User sınıfını import et

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    // Ortak Labellar
    @FXML
    private Label welcomeProfileLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label nameLabel;
    @FXML
    private Label surnameLabel;
    @FXML
    private Label userTypeLabel;
    @FXML
    private Label phoneNumberLabel;
    @FXML
    private Label emailLabel;

    // Patient'e özel Labellar (profile_view.fxml'deki fx:id'ler ile eşleşmeli)
    @FXML
    private Label patientDetailsTitleLabel;
    @FXML
    private Label dateOfBirthLabel;
    @FXML
    private Label bloodGroupLabel;

    // Doctor'a özel Labellar (profile_view.fxml'deki fx:id'ler ile eşleşmeli)
    @FXML
    private Label doctorDetailsTitleLabel;
    @FXML
    private Label specializationLabel; // profile_view.fxml'de bu fx:id olduğundan emin ol

    private User currentUser; // Giriş yapmış kullanıcı

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Başlangıçta kullanıcı tipine özel alanları gizle
        patientDetailsTitleLabel.setVisible(false);
        dateOfBirthLabel.setVisible(false);
        bloodGroupLabel.setVisible(false);
        doctorDetailsTitleLabel.setVisible(false);
        specializationLabel.setVisible(false);

        System.out.println("DEBUG: ProfileController initialize edildi.");
    }

    // MainDashboardController tarafından kullanıcı bilgisi buraya set edilir
    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("DEBUG: ProfileController - setUser metodu çağrıldı. Kullanıcı tipi: " + (currentUser != null ? currentUser.getUserType() : "null"));

        if (currentUser != null) {
            // Ortak kullanıcı bilgilerini ayarla
            usernameLabel.setText("Kullanıcı Adı: " + (currentUser.getUsername() != null ? currentUser.getUsername() : "Belirtilmemiş"));
            nameLabel.setText("Ad: " + (currentUser.getName() != null ? currentUser.getName() : "Belirtilmemiş"));
            surnameLabel.setText("Soyad: " + (currentUser.getSurname() != null ? currentUser.getSurname() : "Belirtilmemiş"));
            userTypeLabel.setText("Kullanıcı Tipi: " + (currentUser.getUserType() != null ? currentUser.getUserType() : "Belirtilmemiş"));
            phoneNumberLabel.setText("Telefon: " + (currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Belirtilmemiş"));
            emailLabel.setText("Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Belirtilmemiş"));

            // Kullanıcı tipine göre özel detayları ayarla
            if (currentUser instanceof Patient) {
                Patient patient = (Patient) currentUser;
                patientDetailsTitleLabel.setVisible(true);
                dateOfBirthLabel.setVisible(true);
                bloodGroupLabel.setVisible(true);

                dateOfBirthLabel.setText("Doğum Tarihi: " + (patient.getDateOfBirth() != null ? patient.getDateOfBirth() : "Belirtilmemiş"));
                bloodGroupLabel.setText("Kan Grubu: " + (patient.getBloodGroup() != null ? patient.getBloodGroup() : "Belirtilmemiş"));

                // Doktor'a özel labelları gizle
                doctorDetailsTitleLabel.setVisible(false);
                specializationLabel.setVisible(false);

            } else if (currentUser instanceof Doctor) { // Kullanıcı Doctor ise
                Doctor doctor = (Doctor) currentUser;
                doctorDetailsTitleLabel.setVisible(true); // Doktor detay başlığını görünür yap

                System.out.println("DEBUG: ProfileController - Doctor objesindeki uzmanlık: " + doctor.getSpecialization());

                specializationLabel.setVisible(true); // Uzmanlık labelını görünür yap

                // Uzmanlık metnini ayarla (Doctor objesinden çekerek)
                specializationLabel.setText("Uzmanlık: " + (doctor.getSpecialization() != null ? doctor.getSpecialization() : "Belirtilmemiş"));

                // Hasta'ya özel labelları gizle
                patientDetailsTitleLabel.setVisible(false);
                dateOfBirthLabel.setVisible(false);
                bloodGroupLabel.setVisible(false);

            } else { // Diğer kullanıcı tipleri için (örn. Admin)
                patientDetailsTitleLabel.setVisible(false);
                dateOfBirthLabel.setVisible(false);
                bloodGroupLabel.setVisible(false);
                doctorDetailsTitleLabel.setVisible(false);
                specializationLabel.setVisible(false);
            }
        } else { // Kullanıcı null ise (örneğin çıkış yapıldıktan sonra)
            usernameLabel.setText("Kullanıcı Adı: ");
            nameLabel.setText("Ad: ");
            surnameLabel.setText("Soyad: ");
            userTypeLabel.setText("Kullanıcı Tipi: ");
            phoneNumberLabel.setText("Telefon: ");
            emailLabel.setText("Email: ");
            patientDetailsTitleLabel.setVisible(false);
            dateOfBirthLabel.setVisible(false);
            bloodGroupLabel.setVisible(false);
            doctorDetailsTitleLabel.setVisible(false);
            specializationLabel.setVisible(false);
        }
        System.out.println("DEBUG: ProfileController - setUser metodu tamamlandı."); // DEBUG
    }

    // finalize metodu burada OLMAMALI
}