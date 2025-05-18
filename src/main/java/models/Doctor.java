package models;

// User'dan miras aldığı için UserActions implementasyonu otomatik gelir.
// import database.AppointmentDB; // User'dan miras alındığı için tekrar import etmeye gerek yok
import java.util.List;
import java.util.ArrayList;

// Doctor extends User ve UserActions implementasyonunu miras alır
public class Doctor extends User {
    private String specialization;

    // Constructor (Mevcut) - super çağrısından userType kaldırıldı
    public Doctor(int id, String username, String password, String name, String surname, String phoneNumber, String email, String specialization) {
        super(id, username, password, name, surname, phoneNumber, email); // <<<< super çağrısından "doctor" kaldırıldı
        this.specialization = specialization;
        // User constructor userAppointments listesini ve appointmentDB instance'ını başlatır
    }

    // Getter for specialization (Mevcut)
    public String getSpecialization() {
        return specialization;
    }

    // Setter for specialization (Mevcut)
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    // **IMPLEMENT: getUserType metodu**
    @Override
    public String getUserType() { // <<<< Abstract metot implemente edildi
        return "doctor";
    }


    // **OVERRIDE: viewAppointments method** (Mevcut fonksiyonel implementasyon)
    @Override
    public void viewAppointments() {
        System.out.println("DEBUG (UserActions - Doctor): Doctor " + getUsername() + " randevular\u0131n\u0131 çekiyor.");
        try {
            // Miras alınan appointmentDB'yi kullanarak doktorun randevularını çek
            // ve miras alınan userAppointments listesine ata.
            this.userAppointments = appointmentDB.getAppointmentsByDoctorId(this.getId()); // User'daki appointmentDB'yi kullan
            System.out.println("DEBUG (UserActions - Doctor): " + this.userAppointments.size() + " randevu doktor i\u00E7in \u00E7ekildi.");
        } catch (Exception e) {
            System.err.println("HATA (UserActions - Doctor): Randevular\u0131 \u00E7ekerken hata olu\u015ftu: " + e.getMessage());
            e.printStackTrace();
            this.userAppointments = new ArrayList<>();
        }
    }

    // viewOwnInformation() User'dan miras alınır.
    // manageAppointments() ve logout() interface'den kaldırıldığı için burada olmamalı.


    // toString method (Mevcut)
    @Override
    public String toString() {
        return "Doctor " + getName() + " " + getSurname() + " -" + getSpecialization();
    }
}
