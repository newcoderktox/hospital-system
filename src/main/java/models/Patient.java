package models;
import java.util.List;
import java.util.ArrayList;

public class Patient extends User {
    private String dateOfBirth;
    private String bloodGroup;

    public Patient(int id, String username, String password, String name, String surname, String phoneNumber, String email, String dateOfBirth, String bloodGroup) {
        super(id, username, password, name, surname, phoneNumber, email); 
        this.dateOfBirth = dateOfBirth;
        this.bloodGroup = bloodGroup;
        
    }
    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    @Override
    public String getUserType() { 
        return "patient";
    }

    @Override
    public void viewAppointments() {
        System.out.println("DEBUG useractions patient " + getUsername() + " getting appts.");
        try {
            this.userAppointments = appointmentDB.getAppointmentsByPatientId(this.getId()); 
            System.out.println("DEBUG useractions patient " + this.userAppointments.size() + " got the appts");
        } catch (Exception e) {
            System.err.println("HATA useractions patient err in getting appts " + e.getMessage());
            e.printStackTrace();
            this.userAppointments = new ArrayList<>();
        }
    }
    @Override
    public String toString() {
        return "Patient{" +
                super.toString() + 
                ", dateOfBirth='" + getDateOfBirth() + '\'' + ", bloodGroup='" + getBloodGroup() + '\'' + '}';
    }
}
