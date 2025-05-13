// src/main/java/models/Doctor.java
package models;

public class Doctor extends User {
    private String specialization; // Uzmanlık alanı

    // Constructor
    public Doctor(int id, String username, String password, String name, String surname, String phoneNumber, String email, String specialization) {
        super(id, username, password, name, surname, phoneNumber, email);
        this.specialization = specialization;
    }

    // Getter and Setter for specialization
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getUserType() {
        return "Doctor";
    }

    @Override
    public String toString() {
        return "Dr. " + getName() + " " + getSurname() + " - " + (specialization != null ? specialization : "Belirtilmemiş");
    }
}