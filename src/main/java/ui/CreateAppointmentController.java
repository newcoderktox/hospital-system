package ui;

import database.AppointmentDB;
import database.DoctorDB; // Doktorları getirmek için DoctorDB
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Doctor; // Doctor modelini import et
import models.User; // User modelini import et
import javafx.event.ActionEvent; // ActionEvent importu

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CreateAppointmentController implements Initializable {

    @FXML
    private ComboBox<Doctor> doctorComboBox; // Doktor seçim ComboBox'ı
    @FXML
    private DatePicker datePicker; // Tarih seçim DatePicker'ı
    @FXML
    private ComboBox<String> timeComboBox; // Saat seçim ComboBox'ı
    @FXML
    private Button createAppointmentButton; // Randevu Oluştur butonu
    @FXML
    private Label loadingTimeSlotsLabel; // Müsait saatler yükleniyor mesajı
    @FXML
    private Label messageLabel; // Hata veya başarı mesajları için
    @FXML
    private Label doctorMessageLabel; // Doktorlar için gösterilecek mesaj

    private User loggedInUser; // Giriş yapmış kullanıcı
    private DoctorDB doctorDB; // Doktor veritabanı işlemleri için
    private AppointmentDB appointmentDB; // Randevu veritabanı işlemleri için

    // MainDashboardController'dan çağrılacak, giriş yapan kullanıcıyı alacak metod
    public void setUser(User user) {
        this.loggedInUser = user;
        System.out.println("DEBUG: CreateAppointmentController - setUser metodu çağrıldı. Kullanıcı tipi: " + (loggedInUser != null ? loggedInUser.getUserType() : "null")); // DEBUG

        // Kullanıcı tipine göre arayüz elementlerinin görünürlüğünü ayarla
        adjustVisibilityByUserType();

        // Eğer kullanıcı hastaysa doktor listesini yükle
        if (loggedInUser != null && "patient".equalsIgnoreCase(loggedInUser.getUserType())) {
            loadDoctors();
        } else {
            // Doktor veya Admin ise ComboBox'ları devre dışı bırak/temizle
            // Bu zaten adjustVisibilityByUserType içinde de yapılıyor, sağlamlık için bırakılabilir.
            doctorComboBox.setDisable(true);
            datePicker.setDisable(true);
            timeComboBox.setDisable(true);
            createAppointmentButton.setDisable(true);
            loadingTimeSlotsLabel.setVisible(false); // Doktor ise yükleniyor mesajını gizle
            messageLabel.setVisible(false); // Doktor ise mesaj labelını gizle
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        doctorDB = new DoctorDB(); // DB instance'larını oluştur
        appointmentDB = new AppointmentDB();

        // Elementlerin başlangıç durumlarını ayarla
        timeComboBox.setDisable(true);
        createAppointmentButton.setDisable(true);
        loadingTimeSlotsLabel.setVisible(false);
        messageLabel.setText(""); // Başlangıçta mesaj yok

        // --- Event Handlers ---

        // Doktor seçimi değiştiğinde müsait saatleri yükle
        doctorComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            // Doktor veya tarih seçilmeden saat ComboBox'ını devre dışı bırak
            timeComboBox.setDisable(newVal == null || datePicker.getValue() == null);
            // Doktor veya tarih değiştiğinde saat ComboBox'ını ve buton durumunu güncelle
            updateTimeSlots();
            // updateCreateButtonState(); // updateTimeSlots içinde çağrılıyor
        });

        // Tarih seçimi değiştiğinde müsait saatleri yükle
        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Doktor veya tarih seçilmeden saat ComboBox'ını devre dışı bırak
            timeComboBox.setDisable(newVal == null || doctorComboBox.getValue() == null);
            // Tarih veya doktor değiştiğinde saat ComboBox'ını ve buton durumunu güncelle
            updateTimeSlots();
            // updateCreateButtonState(); // updateTimeSlots içinde çağrılıyor
        });

        // Saat seçimi değiştiğinde buton durumunu güncelle
        timeComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            updateCreateButtonState();
        });

        // --- Diğer Initialize Ayarları ---

        // DatePicker için geçmiş tarihleri devre dışı bırak
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Bugünkü veya gelecekteki tarihleri seçilebilir yap
                setDisable(empty || date.compareTo(LocalDate.now()) < 0);
            }
        });

        System.out.println("DEBUG: CreateAppointmentController initialize edildi."); // DEBUG
    }

    // Kullanıcı tipine göre arayüz elementlerinin görünürlüğünü ayarlar
    private void adjustVisibilityByUserType() {
        // currentUser null olabilir, LoginController'dan setUser çağrılmadan önce initialize çalışabilir.
        // setUser metodu bu metodu çağırmalı ve currentUser'ı set etmeli.
        boolean isPatient = (loggedInUser != null && "patient".equalsIgnoreCase(loggedInUser.getUserType()));

        // Hasta ise: Form elementlerini göster, doktor mesajını gizle
        doctorComboBox.setVisible(isPatient);
        datePicker.setVisible(isPatient);
        timeComboBox.setVisible(isPatient);
        createAppointmentButton.setVisible(isPatient);
        // loadingTimeSlotsLabel ve messageLabel sadece hasta ise görünür olmalı, visibility'leri adjustVisibilityByUserType dışında da ayarlanıyor
        // loadingTimeSlotsLabel.setVisible(isPatient ? loadingTimeSlotsLabel.isVisible() : false);
        // messageLabel.setVisible(isPatient);

        doctorMessageLabel.setVisible(!isPatient); // Doktor veya Admin ise mesajı göster

        // Hasta değilse, ComboBox ve DatePicker'ı devre dışı bırak
        if (!isPatient) {
            doctorComboBox.setDisable(true);
            datePicker.setDisable(true);
            timeComboBox.setDisable(true);
            createAppointmentButton.setDisable(true);
        } else {
            // Hasta ise, başlangıçta ComboBox ve DatePicker etkin olmalı (içleri boş olsa da)
            doctorComboBox.setDisable(false);
            datePicker.setDisable(false);
        }
    }


    // Veritabanından doktor listesini yükler ve ComboBox'a doldurur
    private void loadDoctors() {
        // Sadece hasta ise doktorları yükle
        if (loggedInUser != null && "patient".equalsIgnoreCase(loggedInUser.getUserType())) {
            List<Doctor> doctors = doctorDB.getAllDoctors(); // <<<< BU ÇAĞRI DoctorDB'de olmalı
            doctorComboBox.setItems(FXCollections.observableArrayList(doctors));
            System.out.println("DEBUG: CreateAppointmentController - Doktor listesi yüklendi: " + doctors.size() + " doktor."); // DEBUG
        } else {
            doctorComboBox.setItems(FXCollections.emptyObservableList()); // Hasta değilse boş liste
            System.out.println("DEBUG: CreateAppointmentController - Kullanıcı hasta değil, doktor listesi yüklenmedi."); // DEBUG
        }
    }

    // Seçilen doktor ve tarihe göre müsait saatleri yükler ve ComboBox'a doldurur
    private void updateTimeSlots() {
        Doctor selectedDoctor = doctorComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        // Doktor, tarih seçilmeden VEYA kullanıcı hasta değilse saatleri yükleme
        if (loggedInUser == null || !"patient".equalsIgnoreCase(loggedInUser.getUserType()) || selectedDoctor == null || selectedDate == null) {
            timeComboBox.setItems(FXCollections.emptyObservableList());
            timeComboBox.setDisable(true);
            loadingTimeSlotsLabel.setVisible(false);
            updateCreateButtonState();
            return;
        }

        loadingTimeSlotsLabel.setVisible(true); // Yükleniyor mesajını göster
        timeComboBox.setDisable(true); // Saat seçimini devre dışı bırak
        timeComboBox.setItems(FXCollections.emptyObservableList()); // Eski saatleri temizle
        messageLabel.setText(""); // Eski mesajı temizle
        messageLabel.setTextFill(javafx.scene.paint.Color.BLACK); // Mesaj rengini varsayılana çevir

        // Tarihi veritabanı formatına dönüştür (YYYY-MM-DD)
        String dateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Arka planda müsait saatleri çek (AppointmentDB içinde olmalı)
        // UI thread'ini bloke etmemek için ayrı bir Task içinde yapmak daha iyidir,
        // ancak basitlik için şimdilik ana UI thread'inde yapalım
        List<String> availableTimes = appointmentDB.getAvailableTimeSlots(selectedDoctor.getId(), dateString); // <<<< BU ÇAĞRI AppointmentDB'de olmalı

        timeComboBox.setItems(FXCollections.observableArrayList(availableTimes));
        timeComboBox.setDisable(false); // Saat seçimini tekrar etkinleştir
        loadingTimeSlotsLabel.setVisible(false); // Yükleniyor mesajını gizle
        System.out.println("DEBUG: CreateAppointmentController - Müsait saatler yüklendi: " + availableTimes.size() + " saat."); // DEBUG

        updateCreateButtonState(); // Saatler yüklendikten sonra buton durumunu güncelle

    }

    // Randevu oluştur butonunun durumunu kontrol eder (Doktor, Tarih, Saat seçili mi ve kullanıcı hasta mı?)
    private void updateCreateButtonState() {
        boolean isDoctorSelected = doctorComboBox.getValue() != null;
        boolean isDateSelected = datePicker.getValue() != null;
        boolean isTimeSelected = timeComboBox.getValue() != null;
        boolean isUserPatient = (loggedInUser != null && "patient".equalsIgnoreCase(loggedInUser.getUserType())); // Sadece hasta oluşturabilir

        // Buton, sadece hasta ise ve doktor, tarih ve saat seçiliyse etkin olur
        boolean enableButton = isUserPatient && isDoctorSelected && isDateSelected && isTimeSelected;

        createAppointmentButton.setDisable(!enableButton); // Duruma göre butonu etkinleştir/devre dışı bırak

        System.out.println("DEBUG: updateCreateButtonState çalıştı. Se\u00E7imler durumu: Doctor=" + isDoctorSelected + ", Date=" + isDateSelected + ", Time=" + isTimeSelected + ", UserIsPatient=" + isUserPatient + ". Buton disabled: " + !enableButton); // DEBUG
    }


    // Randevu Oluştur butonuna tıklandığında çalışır
    // Bu metodun signature'ı FXML'deki onAction="#handleCreateAppointment" ile eşleşmeli
    @FXML
    private void handleCreateAppointment(ActionEvent event) {
        System.out.println("DEBUG: Randevu Oluştur butonuna tıklandı."); // DEBUG
        // Seçilen doktor, tarih ve saati al
        Doctor selectedDoctor = doctorComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        String selectedTime = timeComboBox.getValue(); // Assuming time is a String, ensure it's not null

        // Seçimlerin null olmadığını tekrar kontrol et (UI disabled olsa da backend kontrolü)
        if (loggedInUser == null || !"patient".equalsIgnoreCase(loggedInUser.getUserType()) || selectedDoctor == null || selectedDate == null || selectedTime == null || selectedTime.isEmpty()) {
            messageLabel.setText("Randevu oluşturulamadı: Lütfen tüm bilgileri seçin veya kullanıcı hasta değil.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED); // Kırmızı hata mesajı
            System.err.println("HATA: Randevu Oluştur - Gerekli bilgiler seçilmemiş veya kullanıcı hasta değil."); // HATA
            return;
        }

        // Tarihi veritabanı formatına dönüştür (YYYY-MM-DD)
        String dateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        // Randevu oluşturmayı dene (AppointmentDB içinde olmalı)
        boolean success = appointmentDB.createAppointment(loggedInUser.getId(), selectedDoctor.getId(), dateString, selectedTime); // <<<< BU ÇAĞRI AppointmentDB'de olmalı

        if (success) {
            messageLabel.setText("Randevu başarıyla oluşturuldu!");
            messageLabel.setTextFill(javafx.scene.paint.Color.GREEN); // Yeşil başarı mesajı
            System.out.println("DEBUG: Randevu başarıyla oluşturuldu."); // DEBUG

            // Randevu oluşturulduktan sonra alanları temizle/sıfırla
            doctorComboBox.getSelectionModel().clearSelection();
            datePicker.setValue(null);
            timeComboBox.setItems(FXCollections.emptyObservableList());
            timeComboBox.setDisable(true);
            updateCreateButtonState(); // Butonu devre dışı bırak
            loadingTimeSlotsLabel.setVisible(false);

            // Randevularım sekmesini otomatik yenileme:
            // Bunun için MainDashboardController üzerinden MyAppointmentsController'a bir callback veya event mekanizması gerekebilir.
            // Şimdilik manuel olarak Randevularım sekmesine gidip Yenile butonuna basılması beklenebilir.

        } else {
            messageLabel.setText("Randevu oluşturulurken bir hata oluştu.");
            messageLabel.setTextFill(javafx.scene.paint.Color.RED); // Kırmızı hata mesajı
            System.err.println("HATA: Randevu oluşturulurken DB hatası veya başka bir sorun oluştu."); // HATA
            // Hata durumunda da alanları temizleyebiliriz
            doctorComboBox.getSelectionModel().clearSelection();
            datePicker.setValue(null);
            timeComboBox.setItems(FXCollections.emptyObservableList());
            timeComboBox.setDisable(true);
            updateCreateButtonState(); // Butonu devre dışı bırak
            loadingTimeSlotsLabel.setVisible(false);
        }
    }

    // **Finalize metodu kaldırıldı.** <<<< BURASI SİLİNDİ
}