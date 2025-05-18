package models;

import models.UserActions; // UserActions interface'ini import et
import database.AppointmentDB; // AppointmentDB sınıfını import et
import java.util.List; // List interface'ini import et
import java.util.ArrayList; // ArrayList sınıfını import et


// UserActions interface'ini implemente eden abstract sınıf
public abstract class User implements UserActions { // <<<< abstract sınıf tanımı
    private int id;
    private String username; // TC Kimlik No
    private String password; // Şifre
    private String name; // Ad
    private String surname; // Soyad
    private String phoneNumber; // Telefon Numarası
    private String email; // E-posta

    // private String userType; // <<<< userType alanı orijinal yapıda yoktu, kaldırıldı

    // **Kullanıcının randevularını tutacak liste ve DB instance'ı**
    // protected yapalım ki alt sınıflar (Doctor/Patient) erişebilsin
    protected List<Appointment> userAppointments; // <<<< protected olarak ayarlandı
    protected AppointmentDB appointmentDB = new AppointmentDB(); // <<<< protected olarak ayarlandı


    // Constructor - Orijinal yapıdaki gibi sadece temel alanları alır
    public User(int id, String username, String password, String name, String surname, String phoneNumber, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userAppointments = new ArrayList<>(); // Randevu listesini başlangıçta boş oluştur
    }

    // Getters (Mevcut Getter metotları)
    public int getId() { return id; }
    public String getUsername() { return username; } // TC Kimlik No değerini döndürür
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }
    // Orijinal yapıda somut getUserType() yoktu.


    // Setters (password ve username dışındakiler değiştirilebilir olsun)
    public void setId(int id) { // <<<< setId metodu (LoginDB'de hata alınan satırda kullanılmıştı)
        this.id = id;
    }
    // username ve password setter'ları güvenlik nedeniyle dikkatli kullanılmalıdır.
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }


    // **Kullanıcının çektiği randevuları döndüren getter**
    // MyAppointmentsController bu metodu kullanarak randevu listesini alacak.
    public List<Appointment> getUserAppointments() {
        return userAppointments;
    }

    // **Abstract metod: Kullanıcı türünü dönecek**
    // Alt sınıflar (Doctor, Patient) bu metodu implemente edecek.
    public abstract String getUserType(); // <<<< Abstract getUserType metodu orijinal yapıdan


    // **UserActions Interface Metotlarının Implementasyonu**
    // Interface sadece viewOwnInformation ve viewAppointments içeriyor.

    @Override
    public void viewOwnInformation() {
        System.out.println("DEBUG (UserActions - Base): User " + username + " kendi bilgilerini g\u00F6r\u00FCnt\u00FClemek istiyor (Yer Tutucu).");
        // Bu metod, kullanıcının bilgilerini almak gibi mantıksal bir eylemi temsil eder.
        // Kullanıcı objesi zaten bilgileri tuttuğu için burada ek bir işlem yapmaya gerek yok.
        // ProfileController, User objesinin getter metotlarını kullanarak bilgileri alır.
    }

    // **Abstract metod: Randevuları görüntüleme eylemi**
    // Bu metod, User'dan abstract olacak. Alt sınıflar (Doctor, Patient) kendi viewAppointments implementasyonlarını sağlayacak (DB sorgusu).
    @Override
    public abstract void viewAppointments(); // <<<< viewAppointments metodu abstract yapıldı. Gerçek implementasyon alt sınıflarda olacak.


    // UserActions interface'inden kaldırılan metotlar (manageAppointments, logout) burada da olmamalı.


    // toString metodu - getUserType() çağrısı alt sınıflardaki implementasyonu çağıracak
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", userType='" + getUserType() + '\'' + // <<<< Abstract/override edilmiş getUserType çağrılıyor
                '}';
    }
}
