package models;

// Interface'de sadece iki temel aksiyon metodu

public interface UserActions {
    // Kullanıcının kendi bilgilerini görüntülemesi eylemi
    void viewOwnInformation();

    // Randevuları görüntüleme eylemi
    void viewAppointments();

    // manageAppointments() ve logout() metotları buradan kaldırıldı.
}