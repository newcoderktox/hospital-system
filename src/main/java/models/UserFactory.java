package models;
//facotry design pattern
public class UserFactory {
    public static User createUser(String userType, int id, String username, String password, String name, String surname, String phoneNumber, String email, String extraData1, String extraData2) {
        switch (userType.toLowerCase()) { 
            case "doctor":
                return new Doctor(id, username, password, name, surname, phoneNumber, email, extraData1);
            case "patient":
                return new Patient(id, username, password, name, surname, phoneNumber, email, extraData1, extraData2);
            default:
                throw new IllegalArgumentException("Invalid user type: " + userType);
        }
    }

    public static User createUser(String userType, int id, String username, String password, String name, String surname, String phoneNumber, String email) {
        return createUser(userType, id, username, password, name, surname, phoneNumber, email, null, null);
    }
}