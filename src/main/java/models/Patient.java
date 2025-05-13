// src/main/java/models/Patient.java
package models;

public class Patient extends User {
    private String dateOfBirth; // Doğum tarihi (örneğin "GG.AA.YYYY" formatında)
    private String bloodGroup;  // Kan grubu (örneğin "A Rh+")

    // Constructor
    public Patient(int id, String username, String password, String name, String surname, String phoneNumber, String email, String dateOfBirth, String bloodGroup) {
        super(id, username, password, name, surname, phoneNumber, email);
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
    }

    // Getters and Setters
    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    @Override
    public String getUserType() {
        return "Patient";
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + getId() +
                ", username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                '}';
    }
}