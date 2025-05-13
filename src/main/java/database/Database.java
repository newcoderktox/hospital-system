// src/main/java/database/Database.java
package database;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {
    // Veritabanı dosyasının yolu. "src/main/resources/" altına koyabiliriz.
    // IDE'den veya Maven ile çalıştırıldığında, bu yol projenin root dizinine göre ayarlanır.
    private static final String DATABASE_URL = "jdbc:sqlite:hospital_appointment_system.db";

    // Veritabanı bağlantısını dönecek metod
    public static Connection connect() {
        Connection conn = null;
        try {
            Class.forName("org.sqlite.JDBC");

            // **BURAYI EKLE**
            // Veritabanı dosyasının mutlak yolunu konsola yazdır
            String currentDir = Paths.get(".").toAbsolutePath().normalize().toString();
            String dbAbsolutePath = Paths.get(currentDir, "hospital_appointment_system.db").normalize().toString();
            System.out.println("DEBUG: Veritabanına bağlanılmaya çalışılıyor: " + dbAbsolutePath);
            // **EKLEME SONU**

            conn = DriverManager.getConnection(DATABASE_URL);
            System.out.println("Veritabanına başarıyla bağlandı.");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC sürücüsü bulunamadı: " + e.getMessage());
        e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Veritabanı bağlantı hatası: " + e.getMessage());
            e.printStackTrace(); // Hata detaylarını daha fazla görmek için
        }
        return conn;
    }

    // Gerekli tabloları oluşturan metod
    public static void createTables() {
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {

            // Kullanıcılar Tablosu (Doktorlar ve Hastalar için genel bilgiler)
            String usersSql = "CREATE TABLE IF NOT EXISTS users (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "username TEXT NOT NULL UNIQUE," +
                    "password TEXT NOT NULL," +
                    "name TEXT NOT NULL," +
                    "surname TEXT NOT NULL," +
                    "phoneNumber TEXT," +
                    "email TEXT," +
                    "userType TEXT NOT NULL" + // 'Doctor' veya 'Patient'
                    ");";
            stmt.execute(usersSql);
            System.out.println("users tablosu oluşturuldu veya zaten mevcuttu.");

            // Doktorlar Tablosu (Doktorlara özgü bilgiler)
            String doctorsSql = "CREATE TABLE IF NOT EXISTS doctors (" +
                    "id INTEGER PRIMARY KEY," +
                    "specialization TEXT NOT NULL," +
                    "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(doctorsSql);
            System.out.println("doctors tablosu oluşturuldu veya zaten mevcuttu.");

            // Hastalar Tablosu (Hastalara özgü bilgiler)
            String patientsSql = "CREATE TABLE IF NOT EXISTS patients (" +
                    "id INTEGER PRIMARY KEY," +
                    "dateOfBirth TEXT," +
                    "bloodGroup TEXT," +
                    "FOREIGN KEY (id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(patientsSql);
            System.out.println("patients tablosu oluşturuldu veya zaten mevcuttu.");

            // Randevular Tablosu
            String appointmentsSql = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "patient_id INTEGER NOT NULL," +
                    "doctor_id INTEGER NOT NULL," +
                    "appointment_date TEXT NOT NULL," + // YYYY-MM-DD formatında
                    "appointment_time TEXT NOT NULL," + // HH:MM formatında
                    "status TEXT NOT NULL," + // 'Scheduled', 'Completed', 'Cancelled' gibi
                    "FOREIGN KEY (patient_id) REFERENCES users(id) ON DELETE CASCADE," +
                    "FOREIGN KEY (doctor_id) REFERENCES users(id) ON DELETE CASCADE" +
                    ");";
            stmt.execute(appointmentsSql);
            System.out.println("appointments tablosu oluşturuldu veya zaten mevcuttu.");

            System.out.println("Tüm tablolar başarıyla oluşturuldu veya güncellendi.");

        } catch (SQLException e) {
            System.err.println("Veritabanı tablo oluşturma hatası: " + e.getMessage());
        }
    }

    // Bağlantıyı kapatmak için yardımcı metod (try-with-resources kullanıldığı için genellikle gerekmez)
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Veritabanı bağlantısı kapatıldı.");
            } catch (SQLException e) {
                System.err.println("Veritabanı bağlantısını kapatma hatası: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        // Uygulama başladığında tabloların oluşturulması için bu metodu çağıracağız.
        // Test amaçlı main metodu, veritabanı dosyasının oluştuğunu görmek için çalıştırılabilir.
        createTables();
        // Bu main metodunu direkt çalıştırmak için:
        // IntelliJ'de: Bu dosyayı aç, sağ tıkla -> Run 'Database.main()'
        // Terminalde: mvn exec:java -Dexec.mainClass="database.Database"
    }
}