// src/main/java/models/UserActions.java

package models;

import java.util.List;

public interface UserActions {
    // Kullanıcının kendi bilgilerini görüntülemesi
    void viewOwnInformation();

    // Randevuları görüntülemesi (tüm randevular veya kendi randevuları olabilir)
    void viewAppointments();

    // Randevu alması veya iptal etmesi (Hasta için)
    // Doktor için randevu onaylaması veya iptal etmesi
    void manageAppointments();

    // Sistemden çıkış yapması
    void logout();
}