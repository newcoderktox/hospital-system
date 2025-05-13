// src/main/java/ui/MainApp.java
package ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import database.Database; // Database sınıfını import etmeyi unutma

import java.io.IOException;

public class MainApp extends Application { // MainUI yerine MainApp yapıldıysa bu isim kalır

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Uygulama başlarken veritabanı tablolarını oluşturalım
        Database.createTables(); // Bu metod sadece ilk çalışmada tabloları oluşturacak, sonrasında mevcut olduğunu kontrol edecek.

        // Giriş ekranını yükle
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/fxml/LoginUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Hastane Randevu Sistemi - Giriş");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}