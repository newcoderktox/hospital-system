package database;

import models.Doctor; // Doctor modelini import et
import models.User; // User importu gerekli (Doctor User'dan kalıtım aldığı için)
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorDB {

    // Tüm doktorları veritabanından çeker
    // CreateAppointmentController bu metodu kullanarak doktor listesini alır
    public List<Doctor> getAllDoctors() {
        List<Doctor> doctors = new ArrayList<>();
        // Doktor bilgileri hem users hem de doctors tablolarında tutuluyor
        // İki tabloyu JOIN yaparak veya ayrı ayrı çekip Doctor objeleri oluşturabiliriz.
        // Ayrı ayrı çekmek daha basit olabilir şimdilik.

        // Önce users tablosundan userType='Doctor' olanları çek
        String userSql = "SELECT id, username, password, name, surname, phoneNumber, email FROM users WHERE userType = 'Doctor'";
        // Sonra doctors tablosundan uzmanlıklarını çek

        try (Connection conn = Database.connect();
             PreparedStatement userPstmt = conn.prepareStatement(userSql);
             ResultSet userRs = userPstmt.executeQuery()) {

            while (userRs.next()) {
                int userId = userRs.getInt("id");
                String username = userRs.getString("username");
                String password = userRs.getString("password"); // Real app: don't load/store passwords like this
                String name = userRs.getString("name");
                String surname = userRs.getString("surname");
                String phoneNumber = userRs.getString("phoneNumber");
                String email = userRs.getString("email");

                // Her doktor kullanıcısı için doctors tablosundan uzmanlığı çek
                // Düzeltme: doctors tablosundaki ID kolonu kullanılmalı
                String doctorSql = "SELECT specialization FROM doctors WHERE id = ?"; // <<<< BURASI DÜZELTİLDİ (doctor_id yerine id)
                try (Connection innerConn = Database.connect(); // Yeni bir bağlantı kullan
                     PreparedStatement doctorPstmt = innerConn.prepareStatement(doctorSql)) { // veya dış bağlantıyı kullanabilirsin dikkatli ol
                    doctorPstmt.setInt(1, userId);
                    ResultSet doctorRs = doctorPstmt.executeQuery();
                    if (doctorRs.next()) {
                        String specialization = doctorRs.getString("specialization");
                        // Doctor objesi oluştur (User constructor'ı çağrılır)
                        Doctor doctor = new Doctor(userId, username, password, name, surname, phoneNumber, email, specialization);
                        doctors.add(doctor);
                        System.out.println("DEBUG: getAllDoctors - Doktor eklendi: " + name + " " + surname + " (" + specialization + ")"); // DEBUG
                    }
                    doctorRs.close(); // İçteki ResultSet'i kapat
                } catch (SQLException e) {
                    System.err.println("Doktor uzmanlık bilgisi çekme hatası (ID: " + userId + "): " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("DEBUG: getAllDoctors - Toplam " + doctors.size() + " doktor bulundu."); // DEBUG

        } catch (SQLException e) {
            System.err.println("Tüm doktorları çekme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return doctors;
    }

    // Buraya tek bir doktoru ID'ye göre çekme gibi metodlar da eklenebilir
}