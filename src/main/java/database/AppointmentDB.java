package database;

import models.Appointment; // Appointment modelini import et
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppointmentDB {

    // Randevu oluşturur (CreateAppointmentController bu metodu çağırır)
    public boolean createAppointment(int patientId, int doctorId, String date, String time) {
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            pstmt.setInt(2, doctorId);
            pstmt.setString(3, date);
            pstmt.setString(4, time);
            pstmt.setString(5, "Scheduled"); // Yeni randevunun durumu 'Scheduled'

            int affectedRows = pstmt.executeUpdate();
            System.out.println("DEBUG: createAppointment - Patient ID: " + patientId + ", Doctor ID: " + doctorId + ", Date: " + date + ", Time: " + time + " - Etkilenen satır sayısı: " + affectedRows); // DEBUG
            return affectedRows > 0; // Eğer en az bir satır etkilendiyse başarılı demektir

        } catch (SQLException e) {
            System.err.println("Randevu oluşturma hatası: " + e.getMessage());
            e.printStackTrace();
            // SQLITE_CONSTRAINT (UNIQUE hatası) yakalanabilir eğer aynı saatte başka randevu varsa
            if (e.getMessage().contains("SQLITE_CONSTRAINT")) {
                System.err.println("HATA: Randevu oluşturma - Seçilen saat zaten dolu veya çakışıyor."); // DEBUG
                // Arayüzde kullanıcıya bu bilgiyi iletmek için daha detaylı hata yönetimi gerekebilir (messageLabel güncellendi CreateAppointmentController'da)
            }
            return false; // Hata oluştu
        }
    }

    // Seçilen doktor ve tarihe göre müsait saat dilimlerini bulur (Önceki kodda düzeltilmişti)
    public List<String> getAvailableTimeSlots(int doctorId, String date) {
        // Standart randevu saat dilimleri (örneğin 09:00 - 17:00 arası her yarım saat)
        List<String> standardSlots = Arrays.asList(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00"
        );

        List<String> bookedSlots = new ArrayList<>();
        // Seçilen doktorun, seçilen tarihteki ve durumu İPTAL EDİLMEMİŞ tüm randevu saatlerini çek
        // status != 'Cancelled' koşulu eklendi ki iptal edilen saatler boş gözüksün
        String sql = "SELECT appointment_time FROM appointments " +
                "WHERE doctor_id = ? AND appointment_date = ? AND status != 'Cancelled'";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            pstmt.setString(2, date); // Tarihi string olarak sorguya ekle
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                bookedSlots.add(rs.getString("appointment_time"));
            }
        } catch (SQLException e) {
            System.err.println("Müsait saat dilimlerini getirme hatası: " + e.getMessage());
            e.printStackTrace();
            // Hata durumunda boş liste döndür veya hata yönetimi yap
            return new ArrayList<>();
        }

        // Standart saat dilimlerinden alınmış (iptal edilmemiş) saatleri çıkararak müsait saatleri bul
        List<String> availableSlots = new ArrayList<>(standardSlots);
        availableSlots.removeAll(bookedSlots);

        // Saatleri LocalTime objelerine dönüştürerek doğru sıralama yap
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        List<LocalTime> availableLocalTimes = new ArrayList<>();
        for (String slot : availableSlots) {
            availableLocalTimes.add(LocalTime.parse(slot, formatter));
        }

        Collections.sort(availableLocalTimes); // LocalTime objelerini sırala

        // Sıralanmış LocalTime objelerini tekrar String'e çevir
        List<String> sortedAvailableSlots = new ArrayList<>();
        for (LocalTime time : availableLocalTimes) {
            sortedAvailableSlots.add(time.format(formatter));
        }

        return sortedAvailableSlots; // Sıralanmış listeyi döndür
    }

    // Hastanın randevularını çeker
    public List<Appointment> getAppointmentsByPatientId(int patientId) {
        List<Appointment> appointments = new ArrayList<>();
        // Randevu, doktor ve kullanıcı bilgilerini birleştiren sorgu
        // Düzeltme: JOIN koşulunda d.doctor_id yerine d.id kullanılmalı ve users ile join d.id üzerinden yapılmalı
        String sql = "SELECT a.id, a.appointment_date, a.appointment_time, a.status, a.patient_id, a.doctor_id, " + // patient_id ve doctor_id de çekilmeli
                "d.specialization, u.name as doctor_name, u.surname as doctor_surname " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.id " + // <<<< BURASI DÜZELTİLDİ (d.doctor_id yerine d.id)
                "JOIN users u ON d.id = u.id " + // <<<< BURASI DA DÜZELTİLDİ (d.doctor_id yerine d.id)
                "WHERE a.patient_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("appointment_date");
                String time = rs.getString("appointment_time");
                String status = rs.getString("status");
                int appPatientId = rs.getInt("patient_id"); // Sorgudan gelen patient_id
                int appDoctorId = rs.getInt("doctor_id"); // Sorgudan gelen doctor_id

                String doctorSpecialization = rs.getString("specialization");
                String doctorName = rs.getString("doctor_name");
                String doctorSurname = rs.getString("doctor_surname");
                String doctorFullName = doctorName + " " + doctorSurname;

                // Hastanın görünümünde hasta adı/soyadı gerekmez, null gönderilebilir
                // DÜZELTİLMİŞ YAPICI METOT ÇAĞRISI (9 parametre)
                Appointment appointment = new Appointment(id, appPatientId, appDoctorId, date, time, status,
                        doctorFullName, doctorSpecialization, null); // patientNameAndSurname null
                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Hastanın randevularını çekme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }

    // Doktorun randevularını çeker
    public List<Appointment> getAppointmentsByDoctorId(int doctorId) {
        List<Appointment> appointments = new ArrayList<>();
        // Randevu, hasta ve kullanıcı bilgilerini birleştiren sorgu
        // Düzeltme: JOIN koşulunda p.patient_id yerine p.id kullanılmalı ve users ile join p.id üzerinden yapılmalı
        String sql = "SELECT a.id, a.appointment_date, a.appointment_time, a.status, a.patient_id, a.doctor_id, " + // patient_id ve doctor_id de çekilmeli
                "u.name as patient_name, u.surname as patient_surname " + // Hasta kullanıcı bilgilerini çekmek için
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " + // <<<< BURASI DÜZELTİLDİ (p.patient_id yerine p.id)
                "JOIN users u ON p.id = u.id " + // <<<< BURASI DA DÜZELTİLDİ (p.patient_id yerine p.id)
                "WHERE a.doctor_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String date = rs.getString("appointment_date");
                String time = rs.getString("appointment_time");
                String status = rs.getString("status");
                int appPatientId = rs.getInt("patient_id"); // Sorgudan gelen patient_id
                int appDoctorId = rs.getInt("doctor_id"); // Sorgudan gelen doctor_id

                String patientName = rs.getString("patient_name");
                String patientSurname = rs.getString("patient_surname");
                String patientFullName = patientName + " " + patientSurname;


                // Doktorun görünümünde doktor adı/uzmanlığı gerekmez, null gönderilebilir
                // DÜZELTİLMİŞ YAPICI METOT ÇAĞRISI (9 parametre)
                Appointment appointment = new Appointment(id, appPatientId, appDoctorId, date, time, status,
                        null, null, patientFullName); // doctorName/Specialization null

                appointments.add(appointment);
            }
        } catch (SQLException e) {
            System.err.println("Doktorun randevularını çekme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return appointments;
    }


    // Randevu iptal eder (Bu metod MyAppointmentsController tarafından çağrılır)
    // Şimdi bu metod, daha genel olan updateAppointmentStatus metodunu çağıracak
    public boolean cancelAppointment(int appointmentId) {
        System.out.println("DEBUG: cancelAppointment çağrıldı, Randevu ID: " + appointmentId); // DEBUG
        // Status'ü 'Cancelled' olarak güncelle
        return updateAppointmentStatus(appointmentId, "Cancelled"); // <<<< updateAppointmentStatus'ı çağırıyor
    }

    // Randevunun durumunu günceller (MyAppointmentsController bu metodu bekliyor)
    // MyAppointmentsController bu metodu kullanarak randevunun durumunu değiştirir (örn. iptal).
    public boolean updateAppointmentStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status); // Yeni status değeri
            pstmt.setInt(2, appointmentId);

            int affectedRows = pstmt.executeUpdate();
            System.out.println("DEBUG: updateAppointmentStatus - Randevu ID: " + appointmentId + ", Yeni Status: " + status + " - Etkilenen satır sayısı: " + affectedRows); // DEBUG
            return affectedRows > 0; // Eğer en az bir satır etkilendiyse başarılı demektir

        } catch (SQLException e) {
            System.err.println("Randevu durumunu güncelleme hatası (ID: " + appointmentId + ", Status: " + status + "): " + e.getMessage());
            e.printStackTrace();
            return false; // Hata oluştu
        }
    }

    // Buraya randevuyu tamamlama veya silme gibi metodlar da eklenebilir
}