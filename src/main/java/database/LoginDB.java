package database;

import models.Doctor;
import models.Patient;
import models.User;
import models.UserFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // last_insert_rowid() için gerekli

public class LoginDB {

    // Kullanıcı adı zaten mevcut mu diye kontrol eder
    private boolean isUsernameTaken(String username, Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("DEBUG: Kullanıcı adı zaten mevcut: " + username);
                return true;
            }
            System.out.println("DEBUG: Kullanıcı adı mevcut değil: " + username);
            return false;
        }
    }

    // Yeni bir kullanıcıyı (doktor veya hasta) veritabanına kaydeder
    // Bu metodun direkt sorgu düzeltmelerine ihtiyacı yok çünkü INSERT yapıyor.
    public boolean registerUser(User user) {
        Connection conn = null;

        try {
            conn = Database.connect();
            conn.setAutoCommit(false);

            System.out.println("DEBUG: Kayıt işlemi başladı for user: " + user.getUsername());

            if (isUsernameTaken(user.getUsername(), conn)) {
                System.out.println("DEBUG: registerUser: Kullanıcı adı (" + user.getUsername() + ") zaten alınmış. Rollback ediliyor.");
                conn.rollback();
                return false;
            }

            String userSql = "INSERT INTO users(username, password, name, surname, phoneNumber, email, userType) VALUES(?,?,?,?,?,?,?)";
            try (PreparedStatement pstmtUser = conn.prepareStatement(userSql)) {
                pstmtUser.setString(1, user.getUsername());
                pstmtUser.setString(2, user.getPassword()); // Şifre hashleme EKLENMELİ!
                pstmtUser.setString(3, user.getName());
                pstmtUser.setString(4, user.getSurname());
                pstmtUser.setString(5, user.getPhoneNumber());
                pstmtUser.setString(6, user.getEmail());
                pstmtUser.setString(7, user.getUserType());

                System.out.println("DEBUG: users tablosuna ekleniyor: " + user.getUsername() + ", " + user.getUserType());
                int affectedRows = pstmtUser.executeUpdate();
                System.out.println("DEBUG: users tablosu INSERT affectedRows: " + affectedRows);

                int userId = -1;
                if (affectedRows > 0) {
                    try (Statement stmt = conn.createStatement()) {
                        ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()");
                        if (rs.next()) {
                            userId = rs.getInt(1);
                            user.setId(userId);
                            System.out.println("DEBUG: users tablosuna başarıyla eklendi. Oluşturulan ID (last_insert_rowid): " + userId + ", Etkilenen satır: " + affectedRows);
                        } else {
                            throw new SQLException("Kullanıcı ID'si last_insert_rowid() ile alınamadı.");
                        }
                        rs.close();
                    }
                } else {
                    throw new SQLException("Kullanıcı ekleme başarısız, hiçbir satır etkilenmedi.");
                }
            }

            String specificSql = "";
            boolean isDoctor = user instanceof Doctor;

            if (isDoctor) {
                specificSql = "INSERT INTO doctors(id, specialization) VALUES(?,?)";
            } else { // Patient
                specificSql = "INSERT INTO patients(id, dateOfBirth, bloodGroup) VALUES(?,?,?)";
            }

            try (PreparedStatement pstmtSpecific = conn.prepareStatement(specificSql)) {
                pstmtSpecific.setInt(1, user.getId());

                if (isDoctor) {
                    Doctor doctor = (Doctor) user;
                    pstmtSpecific.setString(2, doctor.getSpecialization());
                    System.out.println("DEBUG: doctors tablosuna ekleniyor: ID=" + user.getId() + ", Uzmanlık=" + doctor.getSpecialization());
                } else { // Patient
                    Patient patient = (Patient) user;
                    pstmtSpecific.setString(2, patient.getDateOfBirth());
                    pstmtSpecific.setString(3, patient.getBloodGroup());
                    System.out.println("DEBUG: patients tablosuna ekleniyor: ID=" + user.getId() + ", DT=" + patient.getDateOfBirth() + ", KG=" + patient.getBloodGroup());
                }
                int affectedRowsSpecific = pstmtSpecific.executeUpdate();
                System.out.println("DEBUG: Özel kullanıcı tablosuna eklendi. Etkilenen satır: " + affectedRowsSpecific);
            }

            conn.commit();
            System.out.println("DEBUG: " + user.getUserType() + " " + user.getUsername() + " başarıyla kaydedildi. Transaction commit edildi.");
            return true;

        } catch (SQLException e) {
            System.err.println("HATA: Kullanıcı kaydetme işlemi sırasında SQL Hatası oluştu:");
            e.printStackTrace();
            if (conn != null) {
                try {
                    System.err.println("DEBUG: Hata oluştuğu için transaction rollback ediliyor.");
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("HATA: Veritabanı rollback hatası: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                    System.out.println("DEBUG: Veritabanı bağlantısı kapatıldı.");
                } catch (SQLException e) {
                    System.err.println("HATA: Bağlantı kapatılırken hata oluştu: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

    // Kullanıcı adı ve şifre ile kimlik doğrulaması yapar
    // Bu metod, JOIN sorgusunun getiremediği alanları direkt sorgularla tamamlayacak şekilde güncellendi.
    public User authenticateUser(String username, String password) {
        // Ana JOIN sorgusu hala tüm bilgileri çekmeye çalışır, ancak doktor ve hasta
        // detayları için direkt sorgu sonuçlarını kullanacağız.
        String sql = "SELECT u.id, u.username, u.password, u.name, u.surname, u.phoneNumber, u.email, u.userType " +
                "FROM users u " + // Direkt sorgu kullanacağımız için JOIN kısmını silebiliriz
                "WHERE u.username = ? AND u.password = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("userType");
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String phoneNumber = rs.getString("phoneNumber");
                String email = rs.getString("email");
                String passwordFromDb = rs.getString("password"); // Şifreyi de çekelim

                User user = null;
                if ("doctor".equalsIgnoreCase(userType)) {
                    // Doktor için direkt uzmanlık alanı sorgusu
                    String specialization = null;
                    String directDoctorSql = "SELECT specialization FROM doctors WHERE id = ?";
                    System.out.println("DEBUG: LoginDB - Veritabanından çekilen uzmanlık (Doctor login): " + specialization);
                    try (PreparedStatement directPstmt = conn.prepareStatement(directDoctorSql)) {
                        directPstmt.setInt(1, id);
                        ResultSet directRs = directPstmt.executeQuery();
                        if (directRs.next()) {
                            specialization = directRs.getString("specialization");
                            System.out.println("DEBUG: LoginDB - ResultSet'ten okunan specialization (DİREKT SORGUSU): " + specialization);
                        } else {
                            System.out.println("DEBUG: LoginDB - Doğrudan doctors tablosunda veri bulunamadı. ID: " + id);
                        }
                    } catch (SQLException directEx) {
                        System.err.println("HATA: LoginDB - Doğrudan doctors sorgusu hatası: " + directEx.getMessage());
                        directEx.printStackTrace();
                    }

                    // UserFactory'ye direkt sorgudan gelen specialization değerini gönder
                    user = UserFactory.createUser("doctor", id, username, passwordFromDb, name, surname, phoneNumber, email, specialization, null);

                } else if ("patient".equalsIgnoreCase(userType)) {
                    // Hasta için direkt doğum tarihi ve kan grubu sorgusu
                    String dateOfBirth = null;
                    String bloodGroup = null;

                    String directPatientSql = "SELECT dateOfBirth, bloodGroup FROM patients WHERE id = ?";
                    try (PreparedStatement directPstmt = conn.prepareStatement(directPatientSql)) {
                        directPstmt.setInt(1, id);
                        ResultSet directRs = directPstmt.executeQuery();
                        if (directRs.next()) {
                            dateOfBirth = directRs.getString("dateOfBirth");
                            bloodGroup = directRs.getString("bloodGroup");
                            System.out.println("DEBUG: LoginDB - ResultSet'ten okunan dateOfBirth (DİREKT SORGUSU): " + dateOfBirth);
                            System.out.println("DEBUG: LoginDB - ResultSet'ten okunan bloodGroup (DİREKT SORGUSU): " + bloodGroup);
                        } else {
                            System.out.println("DEBUG: LoginDB - Doğrudan patients tablosunda veri bulunamadı. ID: " + id);
                        }
                    } catch (SQLException directEx) {
                        System.err.println("HATA: LoginDB - Doğrudan patients sorgusu hatası: " + directEx.getMessage());
                        directEx.printStackTrace();
                    }

                    // UserFactory'ye direkt sorgudan gelen değerleri gönder
                    user = UserFactory.createUser("patient", id, username, passwordFromDb, name, surname, phoneNumber, email, dateOfBirth, bloodGroup);
                }
                System.out.println("Kullanıcı kimlik doğrulandı: " + username + " (" + userType + ")");
                return user;
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı kimlik doğrulama hatası: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("Kullanıcı adı veya şifre yanlış.");
        return null; // Kimlik doğrulama başarısız
    }

    // Kullanıcı ID'si ile kullanıcı bilgilerini getirir
    public User getUserById(int userId) {
        // Bu metod da direkt sorgularla güncellenebilir, authenticateUser metoduna benzer şekilde.
        // Şimdilik orijinal JOIN'li haliyle bırakıyorum, sadece authenticateUser kritik.
        String sql = "SELECT u.id, u.username, u.password, u.name, u.surname, u.phoneNumber, u.email, u.userType, " +
                "d.specialization, p.dateOfBirth, p.bloodGroup " +
                "FROM users u " +
                "LEFT JOIN doctors d ON u.id = d.id AND u.userType = 'doctor' " +
                "LEFT JOIN patients p ON u.id = p.id AND u.userType = 'patient' " +
                "WHERE u.id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("userType");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String phoneNumber = rs.getString("phoneNumber");
                String email = rs.getString("email");

                if ("doctor".equalsIgnoreCase(userType)) {
                    // Burada da direkt sorgu kullanmak daha güvenli olabilir
                    String specialization = rs.getString("specialization");
                    return UserFactory.createUser("doctor", userId, username, password, name, surname, phoneNumber, email, specialization, null);
                } else if ("patient".equalsIgnoreCase(userType)) {
                    // Burada da direkt sorgu kullanmak daha güvenli olabilir
                    String dateOfBirth = rs.getString("dateOfBirth");
                    String bloodGroup = rs.getString("bloodGroup");
                    return UserFactory.createUser("patient", userId, username, password, name, surname, phoneNumber, email, dateOfBirth, bloodGroup);
                }
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı ID ile getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Kullanıcı adı ile kullanıcı bilgilerini getirir
    public User getUserByUsername(String username) {
        // Bu metod da direkt sorgularla güncellenebilir, authenticateUser metoduna benzer şekilde.
        // Şimdilik orijinal JOIN'li haliyle bırakıyorum.
        String sql = "SELECT u.id, u.username, u.password, u.name, u.surname, u.phoneNumber, u.email, u.userType, " +
                "d.specialization, p.dateOfBirth, p.bloodGroup " +
                "FROM users u " +
                "LEFT JOIN doctors d ON u.id = d.id AND u.userType = 'doctor' " +
                "LEFT JOIN patients p ON u.id = p.id AND u.userType = 'patient' " +
                "WHERE u.username = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userType = rs.getString("userType");
                int id = rs.getInt("id");
                String password = rs.getString("password");
                String name = rs.getString("name");
                String surname = rs.getString("surname");
                String phoneNumber = rs.getString("phoneNumber");
                String email = rs.getString("email");

                if ("doctor".equalsIgnoreCase(userType)) {
                    // Burada da direkt sorgu kullanmak daha güvenli olabilir
                    String specialization = rs.getString("specialization");
                    return UserFactory.createUser("doctor", id, username, password, name, surname, phoneNumber, email, specialization, null);
                } else if ("patient".equalsIgnoreCase(userType)) {
                    // Burada da direkt sorgu kullanmak daha güvenli olabilir
                    String dateOfBirth = rs.getString("dateOfBirth");
                    String bloodGroup = rs.getString("bloodGroup");
                    return UserFactory.createUser("patient", id, username, password, name, surname, phoneNumber, email, dateOfBirth, bloodGroup);
                }
            }

        } catch (SQLException e) {
            System.err.println("Kullanıcı adıyla getirme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Diğer kullanıcı yönetimi metodları (güncelleme, silme vb.) buraya eklenebilir.
}