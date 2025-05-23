package models;

import java.util.List;
import java.util.ArrayList;

public class Doctor extends User {
    private String specialization;

    public Doctor(int id, String username, String password, String name, String surname, String phoneNumber, String email, String specialization) {
        super(id, username, password, name, surname, phoneNumber, email); 
        this.specialization = specialization;
        
    }

    public String getSpecialization() {
        return specialization;
    }
    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String getUserType() { 
        return "doctor";
    }

    @Override
    public void viewAppointments() {
        System.out.println("DEBUG useractions- d " + getUsername() + " getting appts.");
        try {
            this.userAppointments = appointmentDB.getAppointmentsByDoctorId(this.getId()); 
            System.out.println("DEBUG: useractions- d " + this.userAppointments.size() + " got appts.");
        } catch (Exception e) {
            System.err.println("HATA useractions- d there is an err: " + e.getMessage());
            e.printStackTrace();
            this.userAppointments = new ArrayList<>();
        }
    }
    @Override
    public String toString() {
        return "Doctor " + getName() + " " + getSurname() + " -" + getSpecialization();
    }
}
