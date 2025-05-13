package ui;

import database.AppointmentDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable; // Initializable importu gerekli
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell; // TableCell importu
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback; // Callback importu gerekli
import models.Appointment;
import models.User;

import java.net.URL; // URL importu gerekli
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle; // ResourceBundle importu gerekli

// Initializable arayüzünü uygula
public class MyAppointmentsController implements Initializable {

    @FXML
    private Label welcomeLabel; // FXML'de tanımlı Label (eğer Randevularım sekmesinde varsa)
    @FXML
    private TableView<Appointment> appointmentsTable;
    @FXML
    private TableColumn<Appointment, String> dateColumn;
    @FXML
    private TableColumn<Appointment, String> timeColumn;
    @FXML
    private TableColumn<Appointment, String> doctorColumn; // For Patient: Doctor Name, For Doctor: Patient Name
    @FXML
    private TableColumn<Appointment, String> specializationColumn; // Only for Patient: Doctor Specialization
    @FXML
    private TableColumn<Appointment, String> statusColumn;

    // fx:id="cancelColumn" ile eşleşmeli
    @FXML
    private TableColumn<Appointment, Void> cancelColumn; // <<<< DEĞİŞKEN ADI CANCELCOLUMN OLARAK DÜZELTİLDİ

    @FXML
    private Button refreshButton; // Yenile butonu (FXML'de tanımlı)

    private User loggedInUser; // Giriş yapan kullanıcı
    private AppointmentDB appointmentDB; // Veritabanı işlemleri için

    // initialize metodu (FXML yüklendiğinde otomatik çağrılır)
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        appointmentDB = new AppointmentDB(); // DB instance'ı oluştur

        // Sabit kalan TableView sütunlarını Appointment modelindeki özelliklere bağla
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // specializeColumn ve doctorColumn'un PropertyValueFactory'leri ve görünürlükleri
        // Kullanıcı tipi belirlendikten sonra adjustTableViewForUserType metodunda ayarlanacak.

        // actionColumn (şimdi cancelColumn) için Cell Factory'yi ayarla - Bu kısım init'te kalabilir
        setupCancelButtonColumn();


        // Yenile butonuna tıklandığında randevuları yeniden yükle
        if (refreshButton != null) { // FXML'de refreshButton varsa
            refreshButton.setOnAction(event -> loadAppointments());
            System.out.println("DEBUG: MyAppointmentsController - Refresh button action set."); // DEBUG
        } else {
            System.err.println("HATA: MyAppointmentsController - refreshButton @FXML injection başarısız!"); // DEBUG
        }

        System.out.println("DEBUG: MyAppointmentsController initialize edildi."); // DEBUG
        // initialize çağrıldığında kullanıcı henüz set edilmemiş olabilir.
        // loadAppointments çağrısı setUser metodunda yapılmalı.
    }

    // Kullanıcı bilgisi set edildiğinde çağrılacak metod
    public void setUser(User user) {
        this.loggedInUser = user;
        System.out.println("DEBUG: MyAppointmentsController - setUser metodu çağrıldı. Kullanıcı tipi: " + (loggedInUser != null ? loggedInUser.getUserType() : "null")); // DEBUG

        // **DEBUG:** FXML injection kontrolü
        if (cancelColumn == null) System.err.println("HATA: MyAppointmentsController - cancelColumn @FXML injection başarısız! my_appointments_view.fxml dosyasındaki fx:id=\"cancelColumn\" kontrol edin.");
        if (doctorColumn == null) System.err.println("HATA: MyAppointmentsController - doctorColumn @FXML injection başarısız!");
        if (specializationColumn == null) System.err.println("HATA: MyAppointmentsController - specializationColumn @FXML injection başarısız!");
        // Diğer kolonlar initialize'da kontrol ediliyor

        // Kullanıcı tipine göre TableView kolonlarını ayarla
        adjustTableViewForUserType();

        // Randevuları yükle
        loadAppointments();

        // Welcome label'ı kullanıcıya göre ayarla (eğer bu label Randevularım sekmesinde kullanılacaksa)
        // Ana dashboard'da zaten welcomeLabel olduğu için burada gerekmeyebilir.
        // if (welcomeLabel != null) {
        //     if (loggedInUser != null) {
        //         welcomeLabel.setText(loggedInUser.getName() + " " + loggedInUser.getSurname() + ", randevularınız aşağıdadır.");
        //     } else {
        //         welcomeLabel.setText("Kullanıcı bilgisi yüklenemedi.");
        //     }
        // }
    }

    // Kullanıcı tipine göre TableView kolonlarını ayarlayan metod
    private void adjustTableViewForUserType() {
        boolean isPatient = (loggedInUser != null && "patient".equalsIgnoreCase(loggedInUser.getUserType()));

        System.out.println("DEBUG: MyAppointmentsController - adjustTableViewForUserType çalıştı. Kullanıcı Patient mi?: " + isPatient); // DEBUG

        // Kolonların FXML'den enjekte edildiğinden emin ol
        if (doctorColumn == null || specializationColumn == null || cancelColumn == null) {
            System.err.println("HATA: MyAppointmentsController - adjustTableViewForUserType: Bazı TableColumn'lar null, ayarlama yapılamadı."); // DEBUG
            return; // Kolonlar null ise ayarlama yapma
        }


        if (isPatient) { // Kullanıcı hasta ise
            System.out.println("DEBUG: MyAppointmentsController - Patient görünümü ayarlanıyor."); // DEBUG
            // Hasta randevularında doktor adını ve uzmanlığını göster
            doctorColumn.setCellValueFactory(new PropertyValueFactory<>("doctorNameAndSurname")); // <<<< PROPERTY ADI DÜZELTİLDİ
            specializationColumn.setCellValueFactory(new PropertyValueFactory<>("doctorSpecialization")); // Doğru
            specializationColumn.setVisible(true);
            // İptal kolonunu göster ve buton factory'sini kullan
            cancelColumn.setVisible(true); // <<<< CANCELCOLUMN KULLANILDI
            // Cell Factory zaten initialize'da ayarlandı

        } else { // Kullanıcı doktor ise
            System.out.println("DEBUG: MyAppointmentsController - Doktor görünümü ayarlanıyor."); // DEBUG
            // Doktor randevularında hasta adını göster
            doctorColumn.setCellValueFactory(new PropertyValueFactory<>("patientNameAndSurname")); // <<<< PROPERTY ADI DÜZELTİLDİ
            // Uzmanlık ve iptal kolonlarını gizle
            specializationColumn.setVisible(false);
            cancelColumn.setVisible(false); // <<<< CANCELCOLUMN KULLANILDI

            // Doktor görünümü için iptal kolonunun Cell Factory'sini temizle (isteğe bağlı)
            // cancelColumn.setCellFactory(null);
        }
        System.out.println("DEBUG: MyAppointmentsController - TableView ayarlamalar\u0131 tamamland\u0131."); // DEBUG
    }

    // Randevıları veritabanından çekip tabloya dolduran metod
    private void loadAppointments() {
        if (loggedInUser == null) {
            appointmentsTable.setItems(FXCollections.emptyObservableList());
            System.out.println("DEBUG: Randevular yüklenemedi: Kullanıcı bilgisi null."); // DEBUG
            return;
        }

        List<Appointment> appointments = null;
        // Kullanıcı tipine göre randevuları çek (Hasta veya Doktor)
        if ("patient".equalsIgnoreCase(loggedInUser.getUserType())) {
            appointments = appointmentDB.getAppointmentsByPatientId(loggedInUser.getId());
            System.out.println("DEBUG: Hasta randevuları yükleniyor. Kullanıcı ID: " + loggedInUser.getId()); // DEBUG
        } else if ("doctor".equalsIgnoreCase(loggedInUser.getUserType())) {
            appointments = appointmentDB.getAppointmentsByDoctorId(loggedInUser.getId());
            // Doktor görünümünde TableView zaten ayarlandığı için burada ekstra bir şeye gerek yok
            System.out.println("DEBUG: Doktor randevuları yükleniyor. Kullanıcı ID: " + loggedInUser.getId()); // DEBUG
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
            System.out.println("DEBUG: Randevu listesi null geldi veya boş, tablo boşaltıldı."); // DEBUG
        }
    }

    // İptal butonu sütunu için Cell Factory kurulumu
    private void setupCancelButtonColumn() {
        // cancelColumn değişkeninin FXML'den doğru enjekte edildiğinden emin ol
        if (cancelColumn == null) {
            System.err.println("HATA: MyAppointmentsController - setupCancelButtonColumn: cancelColumn null, Cell Factory ayarlanamadı."); // DEBUG
            return;
        }

        Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> cellFactory = new Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>>() {
            @Override
            public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                final TableCell<Appointment, Void> cell = new TableCell<Appointment, Void>() {
                    private final Button cancelBtn = new Button("İptal Et");

                    {
                        // Butonun tıklanma olayını tanımla
                        cancelBtn.setOnAction((javafx.event.ActionEvent event) -> {
                            // Tıklanan satırdaki Randevu objesini al
                            Appointment appointment = getTableView().getItems().get(getIndex());

                            // İptal işlemi için onay al (isteğe bağlı ama önerilir)
                            // Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Bu randevuyu iptal etmek istediğinizden emin misunuz?", ButtonType.YES, ButtonType.NO);
                            // Optional<ButtonType> result = alert.showAndWait();
                            // if (result.isPresent() && result.get() == ButtonType.YES) {

                            // Randevunun durumunu veritabanında güncelle
                            boolean success = appointmentDB.updateAppointmentStatus(appointment.getId(), "Cancelled");

                            if (success) {
                                System.out.println("DEBUG: Randevu başarıyla iptal edildi: ID=" + appointment.getId()); // DEBUG
                                // Tabloyu yeniden yükleyerek arayüzü güncelle
                                loadAppointments();
                            } else {
                                System.err.println("HATA: Randevu iptal edilirken bir sorun oluştu: ID=" + appointment.getId()); // DEBUG
                                // Kullanıcıya hata mesajı gösterilebilir
                            }
                            // }
                        });
                        cancelBtn.getStyleClass().add("cancel-button"); // İptal butonu için CSS sınıfı (application.css'te tanımlayabilirsin)
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
                                setGraphic(cancelBtn);
                            } else { // "Cancelled" veya "Completed" gibi durumlarda butonu gizle
                                setGraphic(null);
                            }
                        }
                        setText(null); // Hücrede yazı gösterme, sadece grafik (buton) göster
                    }
                };
                return cell;
            }
        };

        cancelColumn.setCellFactory(cellFactory); // <<<< CANCELCOLUMN KULLANILDI
    }


    // Yenile butonu için FXML'de onAction="#handleRefreshAppointments" tanımlı.
    // Bu metot loadAppointments metodunu çağırarak tabloyu yeniler.
    @FXML
    private void handleRefreshAppointments(ActionEvent event) {
        System.out.println("DEBUG: Yenile butonuna tıklandı."); // DEBUG
        loadAppointments(); // Randevuları yeniden yükle
    }

    // finalize metodu olmamalı
}