// src/main/java/models/UserFactory.java
package models;

public class UserFactory {

    // Bu metod, verilen türe göre (Doktor veya Hasta) bir User nesnesi oluşturur
    public static User createUser(String userType, int id, String username, String password, String name, String surname, String phoneNumber, String email, String extraData1, String extraData2) {
        switch (userType.toLowerCase()) { // Kullanıcı türünü küçük harfe çevirerek karşılaştırıyoruz
            case "doctor":
                // Doktor için extraData1 uzmanlık alanı olacak
                return new Doctor(id, username, password, name, surname, phoneNumber, email, extraData1);
            case "patient":
                // Hasta için extraData1 doğum tarihi, extraData2 kan grubu olacak
                return new Patient(id, username, password, name, surname, phoneNumber, email, extraData1, extraData2);
            default:
                // Bilinmeyen bir kullanıcı tipi gelirse hata fırlatırız veya null döneriz
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    // Overload edilmiş metot, eğer extraData1 ve extraData2 her zaman kullanılmayacaksa
    public static User createUser(String userType, int id, String username, String password, String name, String surname, String phoneNumber, String email) {
        return createUser(userType, id, username, password, name, surname, phoneNumber, email, null, null);
    }
}