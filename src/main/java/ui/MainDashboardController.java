package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader; // FXMLLoader importu eklendi (ileride logout için gerekebilir)
import javafx.fxml.Initializable;
import javafx.scene.Parent; // Parent importu eklendi
import javafx.scene.Scene; // Scene importu eklendi
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;// Stage importu eklendi (ileride logout için gerekebilir)

import models.User;
import database.LoginDB;

import ui.ProfileController;
import ui.MyAppointmentsController;
import ui.CreateAppointmentController;


import java.io.IOException; // IOException importu eklendi (ileride logout için gerekebilir)
import java.net.URL;
import java.util.ResourceBundle;
// import java.util.stream.Collectors;

public class MainDashboardController implements Initializable {

    @FXML
    private TabPane dashboardTabPane;
    @FXML
    private Label welcomeLabel;

    // Sekmeleri initialize sırasında saklamak için instance değişkenleri
    // Sekmelerin FXML'de fx:id'si olmadığı için index ile alınıp burada saklanıyor
    private Tab _storedProfileTab;
    private Tab _storedMyAppointmentsTab;
    private Tab _storedCreateAppointmentTab;
    // Admin sekmeleri için değişkenler şimdilik kaldırıldı

    // fx:include ile dahil edilen Controller'lara erişim
    // FXML'deki fx:id'ler buradaki değişken isimleriyle eşleşmeli
    @FXML
    private ProfileController profileIncludeController; // fx:id="profileInclude"
    @FXML
    private MyAppointmentsController myAppointmentsIncludeController; // fx:id="myAppointmentsInclude"
    @FXML
    private CreateAppointmentController createAppointmentIncludeController; // fx:id="createAppointmentInclude"
    // Yönetici sekmelerinin controller'ları şimdilik kaldırıldı


    private User currentUser; // Giriş yapmış kullanıcı

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // initialize metodu FXML yüklendiğinde çağrılır.
        // Bu aşamada TabPane ve Tab'lar FXML'den yüklenmiş olur.
        // Sekmeleri TabPane'den alıp instance değişkenlerinde saklayalım
        // ki adjustTabVisibility metodunda kullanabilelim.

        // Sekmeleri index ile alıyoruz. Sekmelerin FXML'deki sırasına dikkat edin:
        // Profil (0), Randevularım (1), Randevu Oluştur/Yönet (2), Doktor Yönetimi (3), Hasta Yönetimi (4)
        _storedProfileTab = dashboardTabPane.getTabs().get(0);
        _storedMyAppointmentsTab = dashboardTabPane.getTabs().get(1);
        _storedCreateAppointmentTab = dashboardTabPane.getTabs().get(2);

        // Eğer Admin sekmeleri FXML'de tanımlıysa ve onları da tutmak istersek, devam edebiliriz.
        // Şu an adjustTabVisibility metodu sadece ilk 3 sekmeyi kullanıyor.


        // Initialize aşamasında kullanıcı henüz set edilmemiştir.
        // Kullanıcı bilgisi LoginController'dan gelir ve setUser metodunu çağırır.
        // Sekme görünürlük ayarı setUser metodunda yapılmalı.
        // loadAppointments, loadDoctors vb. çağrıları da setUser veya ilgili sekme controller'ında yapılmalı.

        System.out.println("DEBUG: MainDashboardController initialize edildi."); // DEBUG
    }

    // LoginController'dan çağrılacak, giriş yapan kullanıcıyı alacak metod
    public void setUser(User user) {
        this.currentUser = user;

        if (currentUser != null) {
            System.out.println("DEBUG: setUser metodunda alınan kullanıcı tipi: " + currentUser.getUserType()); // DEBUG

            welcomeLabel.setText("Hoş Geldiniz, " + currentUser.getName() + " " + currentUser.getSurname() + "!");

            // Sekmelerin Controller'larına kullanıcıyı ilet
            // fx:include fx:id'leri doğruysa bu controller değişkenleri otomatik olarak doldurulur.
            // Null kontrolü yaparak güvenli bir şekilde kullanıcıyı iletelim.

            if (profileIncludeController != null) {
                profileIncludeController.setUser(currentUser);
                System.out.println("DEBUG: ProfileController (embedded) bulundu ve kullanıcı set edildi."); // DEBUG
            } else {
                System.err.println("HATA: ProfileController (embedded) henüz yüklenmedi veya fx:id yanlış (profileInclude)."); // HATA
            }

            if (myAppointmentsIncludeController != null) {
                System.out.println("DEBUG: MyAppointmentsController (embedded) bulundu ve kullanıcı set edildi."); // DEBUG
                myAppointmentsIncludeController.setUser(currentUser);
            } else {
                System.err.println("HATA: MyAppointmentsController (embedded) henüz yüklenmedi veya fx:id yanlış (myAppointmentsInclude)."); // HATA
            }

            if (createAppointmentIncludeController != null) {
                System.out.println("DEBUG: CreateAppointmentController (embedded) bulundu ve kullanıcı set edildi."); // DEBUG
                createAppointmentIncludeController.setUser(currentUser);
            } else {
                System.err.println("HATA: CreateAppointmentController (embedded) henüz yüklenmedi veya fx:id yanlış (createAppointmentInclude)."); // HATA
            }

            // Kullanıcı set edildikten sonra sekme görünürlüklerini kullanıcı tipine göre ayarla
            adjustTabVisibility(); // Sekme ayarlamasını çağır


        } else {
            // Kullanıcı yoksa veya logout olduysa tüm dinamik sekmeleri gizle
            dashboardTabPane.getTabs().clear();
            welcomeLabel.setText("Lütfen Giriş Yapın.");
            System.out.println("DEBUG: Kullanıcı set edilmedi (null). Sekmeler temizlendi."); // DEBUG

            // Logout olduğunda include edilen controller'lara null kullanıcıyı iletmek
            // veya içlerini temizlemek isteyebiliriz.
            if (profileIncludeController != null) profileIncludeController.setUser(null);
            if (myAppointmentsIncludeController != null) myAppointmentsIncludeController.setUser(null);
            if (createAppointmentIncludeController != null) createAppointmentIncludeController.setUser(null);
        }
    }


    // Kullanıcı tipine göre sekmelerin görünürlüğünü ve metnini ayarlar
    private void adjustTabVisibility() {
        System.out.println("DEBUG: adjustTabVisibility çalıştı."); // DEBUG
        // Önce tüm sekmeleri kaldır (herhangi bir kalıntı olmaması için)
        dashboardTabPane.getTabs().clear(); // Tüm sekmeleri temizle

        // _stored...Tab değişkenlerinin initialize metodunda doğru şekilde doldurulduğunu varsayıyoruz.
        if (_storedProfileTab == null || _storedMyAppointmentsTab == null || _storedCreateAppointmentTab == null) {
            System.err.println("HATA: adjustTabVisibility - Saklanmış sekmeler bulunamadı! initialize metodu düzgün çalışmamış olabilir veya FXML yapısı değişmiş olabilir."); // HATA
            return; // Sekmeler yoksa devam etme
        }


        if (currentUser != null) {
            if ("patient".equalsIgnoreCase(currentUser.getUserType())) {
                // Hasta ise
                _storedProfileTab.setText("Profil");
                _storedMyAppointmentsTab.setText("Randevularım");
                _storedCreateAppointmentTab.setText("Randevu Oluştur / Yönet");

                dashboardTabPane.getTabs().add(_storedProfileTab);
                dashboardTabPane.getTabs().add(_storedMyAppointmentsTab);
                dashboardTabPane.getTabs().add(_storedCreateAppointmentTab);
                System.out.println("DEBUG: Kullan\u0131c\u0131 Patient, sekmeler eklendi: Profil, Randevular\u0131m, Randevu Olu\u015Ftur / Y\u00F6net"); // DEBUG


            } else if ("doctor".equalsIgnoreCase(currentUser.getUserType())) {
                // Doktor ise
                _storedProfileTab.setText("Profil");
                _storedMyAppointmentsTab.setText("Takvimimi Yönet"); // Sekme başlığını doktor için ayarla
                _storedCreateAppointmentTab.setText("Randevu Oluştur / Yönet"); // Orijinal metni geri set et (göstermeyeceğiz)

                dashboardTabPane.getTabs().add(_storedProfileTab);
                // Doktor için Randevu Oluştur sekmesini DEĞİL, Randevularım sekmesini (adı değişmiş haliyle) ekliyoruz
                dashboardTabPane.getTabs().add(_storedMyAppointmentsTab); // Şimdi adı "Takvimimi Yönet" oldu

                // Randevu Oluştur sekmesini doktor için eklemiyoruz.

                System.out.println("DEBUG: Kullan\u0131c\u0131 Doctor, sekmeler eklendi: Profil, Takvimimi Y\u00F6net"); // DEBUG

            }
            // Admin kullanıcı tipi ve yönetim sekmeleri (ileride eklenecek)
        }
        // Kullanıcı yoksa zaten sekmeler clear() ile kaldırıldı.
    }

    // Logout metodu (şimdi ekleyeceğiz)
    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("DEBUG: Logout butonuna tıklandı."); // DEBUG
        // Kullanıcıyı null yap
        setUser(null);

        // Login ekranına geri dön
        // Mevcut sahneyi (Scene) ve pencereyi (Stage) al
        Stage stage = (Stage) dashboardTabPane.getScene().getWindow();
        try {
            // login_view.fxml dosyasını yükle
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginUI.fxml"));
            Parent root = loader.load();

            // Yeni sahneyi oluştur ve pencereye set et
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Hastane Randevu Sistemi - Giriş"); // Pencere başlığını değiştir
            stage.show(); // Pencereyi göster

            System.out.println("DEBUG: Logout başarılı, Login ekranına dönüldü."); // DEBUG

        } catch (IOException e) {
            System.err.println("HATA: Logout sırasında login_view.fxml yüklenemedi."); // HATA
            e.printStackTrace();
        }
    }

}