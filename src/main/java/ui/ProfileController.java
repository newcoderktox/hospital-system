package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.Doctor;
import models.Patient;
import models.User;

import java.net.URL;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {

    @FXML private Label welcomeProfileLabel;
    @FXML private Label usernameLabel;
    @FXML private Label nameLabel;
    @FXML private Label surnameLabel;
    @FXML private Label userTypeLabel;
    @FXML private Label phoneNumberLabel;
    @FXML private Label emailLabel;

    // Patient specific labels
    @FXML private Label patientDetailsTitleLabel;
    @FXML private Label dateOfBirthLabel;
    @FXML private Label bloodGroupLabel;

    // Doctor specific labels
    @FXML private Label doctorDetailsTitleLabel;
    @FXML private Label specializationLabel;

    private User currentUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Başlangıçta tüm özel labelları gizle ve managed özelliğini kapat
        patientDetailsTitleLabel.setVisible(false);
        patientDetailsTitleLabel.setManaged(false); // <<<< EKLE: Managed özelliğini kapat
        dateOfBirthLabel.setVisible(false);
        dateOfBirthLabel.setManaged(false); // <<<< EKLE: Managed özelliğini kapat
        bloodGroupLabel.setVisible(false);
        bloodGroupLabel.setManaged(false); // <<<< EKLE: Managed özelliğini kapat

        doctorDetailsTitleLabel.setVisible(false);
        doctorDetailsTitleLabel.setManaged(false); // <<<< EKLE: Managed özelliğini kapat
        specializationLabel.setVisible(false);
        specializationLabel.setManaged(false); // <<<< EKLE: Managed özelliğini kapat

        System.out.println("DEBUG: ProfileController initialize edildi. Başlangıç managed/visible state ayarlandı."); // DEBUG
    }

    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("DEBUG: ProfileController - setUser metodu çağrıldı. Kullanıcı tipi: " + (currentUser != null ? currentUser.getUserType() : "null"));

        // **DEBUG:** FXML injection kontrolü (önceki hataları görmek için kalsın)
        if (specializationLabel == null) {
            System.err.println("HATA: ProfileController - specializationLabel @FXML injection başarısız! profile_view.fxml dosyasındaki fx:id=\"specializationLabel\" kontrol edin.");
        }
        if (patientDetailsTitleLabel == null) System.err.println("HATA: ProfileController - patientDetailsTitleLabel @FXML injection başarısız!");
        if (dateOfBirthLabel == null) System.err.println("HATA: ProfileController - dateOfBirthLabel @FXML injection başarısız!");
        if (bloodGroupLabel == null) System.err.println("HATA: ProfileController - bloodGroupLabel @FXML injection başarısız!");
        if (doctorDetailsTitleLabel == null) System.err.println("HATA: ProfileController - doctorDetailsTitleLabel @FXML injection başarısız!");


        if (currentUser != null) {
            // Genel kullanıcı bilgileri (bu labellar her zaman görünür ve managed olmalı)
            usernameLabel.setText("Kullanıcı Adı: " + (currentUser.getUsername() != null ? currentUser.getUsername() : "Belirtilmemiş"));
            nameLabel.setText("Ad: " + (currentUser.getName() != null ? currentUser.getName() : "Belirtilmemiş"));
            surnameLabel.setText("Soyad: " + (currentUser.getSurname() != null ? currentUser.getSurname() : "Belirtilmemiş"));
            userTypeLabel.setText("Kullanıcı Tipi: " + (currentUser.getUserType() != null ? currentUser.getUserType() : "Belirtilmemiş"));
            phoneNumberLabel.setText("Telefon: " + (currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Belirtilmemiş"));
            emailLabel.setText("Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Belirtilmemiş"));

            // Kullanıcı tipine göre özel detayları ayarla
            if (currentUser instanceof Patient) { // Kullanıcı hasta ise
                Patient patient = (Patient) currentUser;
                System.out.println("DEBUG: ProfileController - Kullanıcı Patient, hasta detayları ayarlanıyor."); // DEBUG

                // Hasta'ya özel labelları görünür yap ve managed özelliğini aç
                if (patientDetailsTitleLabel != null) { patientDetailsTitleLabel.setVisible(true); patientDetailsTitleLabel.setManaged(true); } // <<<< EKLE: Managed özelliğini aç
                if (dateOfBirthLabel != null) { dateOfBirthLabel.setVisible(true); dateOfBirthLabel.setManaged(true); } // <<<< EKLE: Managed özelliğini aç
                if (bloodGroupLabel != null) { bloodGroupLabel.setVisible(true); bloodGroupLabel.setManaged(true); } // <<<< EKLE: Managed özelliğini aç

                dateOfBirthLabel.setText("Doğum Tarihi: " + (patient.getDateOfBirth() != null ? patient.getDateOfBirth() : "Belirtilmemiş"));
                bloodGroupLabel.setText("Kan Grubu: " + (patient.getBloodGroup() != null ? patient.getBloodGroup() : "Belirtilmemiş"));

                // Doktor'a özel labelları gizle ve managed özelliğini kapat
                if (doctorDetailsTitleLabel != null) { doctorDetailsTitleLabel.setVisible(false); doctorDetailsTitleLabel.setManaged(false); } // <<<< EKLE: Managed özelliğini kapat
                if (specializationLabel != null) { specializationLabel.setVisible(false); specializationLabel.setManaged(false); } // <<<< EKLE: Managed özelliğini kapat


            } else if (currentUser instanceof Doctor) { // Kullanıcı doktor ise
                Doctor doctor = (Doctor) currentUser;
                System.out.println("DEBUG: ProfileController - Kullanıcı Doctor, doktor detayları ayarlanıyor."); // DEBUG
                System.out.println("DEBUG: ProfileController - Doctor objesindeki uzmanlık: " + doctor.getSpecialization());

                // Doktor'a özel labelları görünür yap ve managed özelliğini aç
                if (doctorDetailsTitleLabel != null) { doctorDetailsTitleLabel.setVisible(true); doctorDetailsTitleLabel.setManaged(true); } // <<<< EKLE: Managed özelliğini aç
                if (specializationLabel != null) {
                    specializationLabel.setVisible(true);
                    specializationLabel.setManaged(true); // <<<< EKLE: Managed özelliğini aç
                    specializationLabel.setText("Uzmanlık: " + (doctor.getSpecialization() != null ? doctor.getSpecialization() : "Belirtilmemiş"));
                    System.out.println("DEBUG: ProfileController - specializationLabel visible, managed ve text ayarlandı."); // DEBUG
                } else {
                    System.err.println("HATA: ProfileController - specializationLabel null, text/visibility/managed ayarlanamadı.");
                }

                // Hasta'ya özel labelları gizle ve managed özelliğini kapat
                if (patientDetailsTitleLabel != null) { patientDetailsTitleLabel.setVisible(false); patientDetailsTitleLabel.setManaged(false); } // <<<< EKLE: Managed özelliğini kapat
                if (dateOfBirthLabel != null) { dateOfBirthLabel.setVisible(false); dateOfBirthLabel.setManaged(false); } // <<<< EKLE: Managed özelliğini kapat
                if (bloodGroupLabel != null) { bloodGroupLabel.setVisible(false); bloodGroupLabel.setManaged(false); } // <<<< EKLE: Managed özelliğini kapat

            } else { // Diğer kullanıcı tipleri (örn. Admin) veya null kullanıcı
                System.out.println("DEBUG: ProfileController - Kullanıcı tipi bilinmiyor veya özel detay yok, özel labellar gizleniyor."); // DEBUG
                if (patientDetailsTitleLabel != null) { patientDetailsTitleLabel.setVisible(false); patientDetailsTitleLabel.setManaged(false); }
                if (dateOfBirthLabel != null) { dateOfBirthLabel.setVisible(false); dateOfBirthLabel.setManaged(false); }
                if (bloodGroupLabel != null) { bloodGroupLabel.setVisible(false); bloodGroupLabel.setManaged(false); }
                if (doctorDetailsTitleLabel != null) { doctorDetailsTitleLabel.setVisible(false); doctorDetailsTitleLabel.setManaged(false); }
                if (specializationLabel != null) { specializationLabel.setVisible(false); specializationLabel.setManaged(false); }
            }
        } else { // Kullanıcı null ise (logout sonrası gibi)
            System.out.println("DEBUG: ProfileController - Kullanıcı null, tüm labellar temizleniyor/gizleniyor."); // DEBUG
            usernameLabel.setText("Kullanıcı Adı: ");
            nameLabel.setText("Ad: ");
            surnameLabel.setText("Soyad: ");
            userTypeLabel.setText("Kullanıcı Tipi: ");
            phoneNumberLabel.setText("Telefon: ");
            emailLabel.setText("Email: ");

            if (patientDetailsTitleLabel != null) { patientDetailsTitleLabel.setVisible(false); patientDetailsTitleLabel.setManaged(false); }
            if (dateOfBirthLabel != null) { dateOfBirthLabel.setVisible(false); dateOfBirthLabel.setManaged(false); }
            if (bloodGroupLabel != null) { bloodGroupLabel.setVisible(false); bloodGroupLabel.setManaged(false); }
            if (doctorDetailsTitleLabel != null) { doctorDetailsTitleLabel.setVisible(false); doctorDetailsTitleLabel.setManaged(false); }
            if (specializationLabel != null) { specializationLabel.setVisible(false); specializationLabel.setManaged(false); }
        }
        System.out.println("DEBUG: ProfileController - setUser metodu tamamlandı."); // DEBUG
    }

    // initialize metodunda başlangıç managed state'lerini set ettim (yukarıda güncellendi)

    // finalize metodu olmamalı
}