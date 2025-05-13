package ui; // Senin paket adın farklı olabilir

// Gerekli importlar
import database.Database; // Veritabanı işlemleri için
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL; // URL sınıfı için import

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        System.out.println("DEBUG: App start metodu başladı."); // DEBUG 1

        try {
            // Veritabanı tablolarını oluştur veya kontrol et (uygulama başlangıcında)
            Database.createTables();
            System.out.println("DEBUG: Veritabanı tabloları oluşturuldu/kontrol edildi."); // DEBUG 2

            // login_view.fxml yerine LoginUI.fxml dosyasını yükle
            // <<<< BURASI DÜZELTİLDİ >>>>
            URL loginViewUrl = getClass().getResource("/fxml/LoginUI.fxml");
            if (loginViewUrl == null) {
                System.err.println("HATA: LoginUI.fxml classpath'te bulunamadı! Lütfen dosya yolunu ve adını kontrol edin: /fxml/LoginUI.fxml"); // DEBUG FXML Path Hatası
                // FXML bulunamazsa uygulamadan çıkış yapalım
                System.out.println("DEBUG: FXML bulunamadı, uygulama kapatılıyor.");
                System.exit(1); // Uygulamadan çık
            }
            System.out.println("DEBUG: LoginUI.fxml URL bulundu: " + loginViewUrl); // DEBUG 3


            // FXMLLoader objesini oluştur
            FXMLLoader fxmlLoader = new FXMLLoader(loginViewUrl);
            System.out.println("DEBUG: FXMLLoader oluşturuldu."); // DEBUG 4

            // Scene objesini oluştur ve FXML'i yükle
            Scene scene = new Scene(fxmlLoader.load(), 800, 600); // Boyutları kendi FXML'ine göre ayarla
            System.out.println("DEBUG: Scene oluşturuldu ve FXML yüklendi."); // DEBUG 5

            // CSS dosyasını sahneye ekleme kısmı (Eğer FXML'de yüklüyorsan burası yorum satırı kalmalı)
            try {
                URL cssUrl = getClass().getResource("/resoruces/application.css");
                if (cssUrl == null) {
                    System.err.println("HATA: application.css classpath'te bulunamadı! Lütfen dosya yolunu kontrol edin: /resouces/application.css"); // DEBUG CSS Path Hatası
                } else {
                    // Eğer CSS'i buradan yüklemek istiyorsan aşağıdaki satırın yorumunu kaldır
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("DEBUG: application.css URL bulundu (yüklenip yüklenmediği start metoduna bağlı): " + cssUrl); // DEBUG CSS Found
                }
            } catch (Exception e) { // CSS yüklenirken olası hatalar
                System.err.println("HATA: CSS yüklenirken beklenmedik hata: " + e.getMessage());
                e.printStackTrace();
            }


            // Sahne ve Stage ayarları
            stage.setTitle("Hospital Appointment System");
            System.out.println("DEBUG: Stage başlığı ayarlandı."); // DEBUG 6
            stage.setScene(scene);
            System.out.println("DEBUG: Scene sahneye set edildi."); // DEBUG 7
            stage.show();
            System.out.println("DEBUG: Sahne gösterildi. App start metodu tamamlandı."); // DEBUG 8

        } catch (IOException e) {
            // FXML yükleme hatalarını yakala
            System.err.println("HATA: FXML yüklenirken veya başlangıçta IO hatası oluştu: " + e.getMessage()); // DEBUG Catch IO
            e.printStackTrace();
            System.out.println("DEBUG: IO Hatası, uygulama kapatılıyor.");
            System.exit(1); // Hata durumunda uygulamadan çıkış yap
        } catch (Exception e) {
            // Diğer genel hataları yakala
            System.err.println("HATA: Uygulama başlangıcında beklenmedik genel hata: " + e.getMessage()); // DEBUG General Catch
            e.printStackTrace();
            System.out.println("DEBUG: Genel hata, uygulama kapatılıyor.");
            System.exit(1); // Hata durumunda uygulamadan çıkış yap
        }
    }

    public static void main(String[] args) {
        System.out.println("DEBUG: App main metodu başladı."); // DEBUG Main
        launch(); // JavaFX uygulamasını başlatır
        System.out.println("DEBUG: App main metodu tamamlandı."); // DEBUG Main End
    }
}