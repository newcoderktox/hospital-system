package ui;

import database.AppointmentDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell; // TableCell importu
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Appointment;
import models.User;

import java.sql.SQLException;
import java.util.ArrayList; // <<<< BURAYI EKLE
import java.util.List;

// TableColumn için Appointment modelini kullanacağız
// İptal butonu sütunu için hücre içeriği boş (Void) olacak
public class MyAppointmentsController {

    @FXML
    private Label welcomeLabel; // FXML'de tanımlı Label
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> timeColumn;
    @FXML
    private TableColumn<Appointment, String> doctorColumn;
    @FXML
    private TableColumn<Appointment, String> specializationColumn;
    @FXML
    private TableColumn<Appointment, String> statusColumn;

    // **YENİ FX ID:** İptal butonu sütunu için
    @FXML
    private TableColumn<Appointment, Void> actionColumn; // <<<< YENİ FX ID DEĞİŞKENİ

    @FXML
    private Button refreshButton; // Yenile butonu (FXML'de tanımlı)

    private User loggedInUser; // Giriş yapan kullanıcı
    private AppointmentDB appointmentDB; // Veritabanı işlemleri için

    // MainDashboardController'dan çağrılacak, giriş yapan kullanıcıyı alacak metod
    public void setUser(User user) {
        this.loggedInUser = user;
        if (loggedInUser != null) {
            // Kullanıcı bilgisi set edildiğinde randevuları yükle
            loadAppointments();
            // Welcome label'ı kullanıcıya göre ayarla (eğer bu label Randevularım sekmesinde kullanılacaksa)
            // Ana dashboard'da zaten welcomeLabel olduğu için burada gerekmeyebilir.
            // welcomeLabel.setText(loggedInUser.getName() + " " + loggedInUser.getSurname() + ", randevularınız aşağıdadır.");
        } else {
            // Kullanıcı yoksa tabloyu temizle veya devre dışı bırak
            appointmentsTable.setItems(FXCollections.emptyObservableList());
            // welcomeLabel.setText("Kullanıcı bilgisi yüklenemedi.");
        }
    }

    @FXML
    public void initialize() {
        appointmentDB = new AppointmentDB(); // DB instance'ı oluştur

        // TableView sütunlarını Appointment modelindeki özelliklere bağla
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorName")); // Appointment modelinde olmalı
        specializationColumn.setCellValueFactory(new PropertyValueFactory<>("doctorSpecialization")); // Appointment modelinde olmalı
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // **YENİ KOD:** 'actionColumn' için Cell Factory'yi ayarla
        actionColumn.setCellFactory(param -> new TableCell<Appointment, Void>() {
            private final Button cancelButton = new Button("İptal Et");

            {
                // Butonun tıklanma olayını tanımla
                cancelButton.setOnAction(event -> {
                    // Tıklanan satırdaki Randevu objesini al
                    Appointment appointment = getTableView().getItems().get(getIndex());

                    // İptal işlemi için onay al (isteğe bağlı ama önerilir)
                    // Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bu randevuyu iptal etmek istediğinizden emin misiniz?", ButtonType.YES, ButtonType.NO);
                    // Optional<ButtonType> result = alert.showAndWait();
                    // if (result.isPresent() && result.get() == ButtonType.YES) {

                    // Randevunun durumunu veritabanında güncelle
                    boolean success = appointmentDB.updateAppointmentStatus(appointment.getId(), "Cancelled");

                    if (success) {
                        System.out.println("Randevu başarıyla iptal edildi: ID=" + appointment.getId());
                        // Tabloyu yeniden yükleyerek arayüzü güncelle
                        loadAppointments();
                    } else {
                        System.err.println("Randevu iptal edilirken bir hata oluştu: ID=" + appointment.getId());
                        // Kullanıcıya hata mesajı gösterilebilir
                    }
                    // }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                // Eğer satır boş değilse (randevu verisi varsa) butonu göster
                if (empty) {
                    setGraphic(null);
                } else {
                    // Sadece randevu durumu "Scheduled" ise iptal butonunu göster
                    Appointment appointment = getTableView().getItems().get(getIndex());
                    if ("Scheduled".equalsIgnoreCase(appointment.getStatus())) { // Durum "Scheduled" ise
                        setGraphic(cancelButton);
                    } else { // "Cancelled" veya "Completed" gibi durumlarda butonu gizle
                        setGraphic(null);
                    }
                }
                setText(null); // Hücrede yazı gösterme, sadece grafik (buton) göster
            }
        });

        // Yenile butonuna tıklandığında randevuları yeniden yükle
        if (refreshButton != null) { // FXML'de refreshButton varsa
            refreshButton.setOnAction(event -> loadAppointments());
        } else {
            System.err.println("Refresh button not found in FXML."); // Debug mesajı
        }


        // initialize metodu çağrıldığında kullanıcı henüz set edilmemiş olabilir.
        // loadAppointments çağrısı setUser metodunda yapılmalı.
    }

    // Randevuları veritabanından çekip tabloya dolduran metod
    private void loadAppointments() {
        if (loggedInUser == null) {
            appointmentsTable.setItems(FXCollections.emptyObservableList());
            return;
        }

        List<Appointment> appointments = null;
        // Kullanıcı tipine göre randevuları çek (Hasta veya Doktor)
        if ("patient".equalsIgnoreCase(loggedInUser.getUserType())) {
            appointments = appointmentDB.getAppointmentsByPatientId(loggedInUser.getId());
            System.out.println("DEBUG: Hasta randevular\u0131 yükleniyor. Kullan\u0131c\u0131 ID: " + loggedInUser.getId()); // DEBUG
        } else if ("doctor".equalsIgnoreCase(loggedInUser.getUserType())) {
            appointments = appointmentDB.getAppointmentsByDoctorId(loggedInUser.getId());
            System.out.println("DEBUG: Doktor randevular\u0131 yükleniyor. Kullan\u0131c\u0131 ID: " + loggedInUser.getId()); // DEBUG
            // Doktorlar için "İşlem" sütununu gizle veya farklı bir işlem koy (isteğe bağlı)
            if (actionColumn != null) {
                actionColumn.setVisible(false); // Doktor için iptal sütununu gizle
            }
        } else {
            appointments = new ArrayList<>(); // Diğer kullanıcı tipleri için boş liste
            System.out.println("DEBUG: Bilinmeyen kullanıcı tipi, randevu yüklenemedi: " + loggedInUser.getUserType()); // DEBUG
        }

        if (appointments != null) {
            ObservableList<Appointment> observableAppointments = FXCollections.observableArrayList(appointments);
            appointmentsTable.setItems(observableAppointments);
            System.out.println("DEBUG: Tabloya " + appointments.size() + " randevu yüklendi."); // DEBUG
        } else {
            appointmentsTable.setItems(FXCollections.emptyObservableList());
            System.out.println("DEBUG: Randevu listesi null geldi, tablo boşaltıldı."); // DEBUG
        }
    }

    // Yenile butonu için FXML'de handleRefreshAppointments metodu tanımlı.
    // Bu metot loadAppointments metodunu çağırarak tabloyu yeniler.
    @FXML
    private void handleRefreshAppointments(ActionEvent event) {
        System.out.println("DEBUG: Yenile butonuna tıklandı.");
        loadAppointments(); // Randevuları yeniden yükle
    }
}