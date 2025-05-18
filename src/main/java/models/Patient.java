package models;

// User'dan miras aldığı için UserActions implementasyonu otomatik gelir.
// import database.AppointmentDB; // User'dan miras alındığı için tekrar import etmeye gerek yok
import java.util.List;
import java.util.ArrayList;


// Patient extends User ve UserActions implementasyonunu miras alır
public class Patient extends User {
    private String dateOfBirth;
    private String bloodGroup;

    // Constructor (Mevcut) - super çağrısından userType kaldırıldı
    public Patient(int id, String username, String password, String name, String surname, String phoneNumber, String email, String dateOfBirth, String bloodGroup) {
        super(id, username, password, name, surname, phoneNumber, email); // <<<< super çağrısından "patient" kaldırıldı
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        // User constructor userAppointments listesini ve appointmentDB instance'ını başlatır
    }

    // Getters (Mevcut)
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    // **IMPLEMENT: getUserType metodu**
    @Override
    public String getUserType() { // <<<< Abstract metot implemente edildi
        return "patient";
    }


    // **OVERRIDE: viewAppointments method** (Mevcut fonksiyonel implementasyon)
    @Override
    public void viewAppointments() {
        System.out.println("DEBUG (UserActions - Patient): Patient " + getUsername() + " randevular\u0131n\u0131 çekiyor.");
        try {
            // Miras alınan appointmentDB'yi kullanarak hastanın randevularını çek
            // ve miras alınan userAppointments listesine ata.
            this.userAppointments = appointmentDB.getAppointmentsByPatientId(this.getId()); // User'daki appointmentDB'yi kullan
            System.out.println("DEBUG (UserActions - Patient): " + this.userAppointments.size() + " randevu hasta i\u00E7in \u00E7ekildi.");
        } catch (Exception e) {
            System.err.println("HATA (UserActions - Patient): Randevular\u0131 \u00E7ekerken hata olu\u015ftu: " + e.getMessage());
            e.printStackTrace();
            this.userAppointments = new ArrayList<>();
        }
    }

    // viewOwnInformation() User'dan miras alınır.
    // manageAppointments() ve logout() interface'den kaldırıldığı için burada olmamalı.


    // toString method (Mevcut)
    @Override
    public String toString() {
        return "Patient{" +
                super.toString() + // User detaylarını dahil et (getUserType() çağrısı buradan yapılacak)
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", bloodGroup='" + bloodGroup + '\'' +
                '}';
    }
}
