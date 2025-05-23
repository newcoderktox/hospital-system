package models;
import models.UserActions; 
import database.AppointmentDB; 
import java.util.List; 
import java.util.ArrayList; 

public abstract class User implements UserActions { 
    private int id;
    private String username; 
    private String password; 
    private String name; 
    private String surname; 
    private String phoneNumber; 
    private String email;
    protected List<Appointment> userAppointments; 
    protected AppointmentDB appointmentDB = new AppointmentDB(); 

    public User(int id, String username, String password, String name, String surname, String phoneNumber, String email) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.userAppointments = new ArrayList<>(); 
    }

    public int getId() { return id; }
    public String getUsername() { return username; } 
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getEmail() { return email; }

    public void setId(int id) { 
        this.id = id;
    }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setEmail(String email) { this.email = email; }

    public List<Appointment> getUserAppointments() {
        return userAppointments;
    }
    public abstract String getUserType(); 

    @Override
    public void viewOwnInformation() {
        System.out.println("DEBUG useractions " + getUsername() + " wants to see thier own info");

    }
    @Override
    public abstract void viewAppointments(); 

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + getUsername() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ", userType='" + getUserType() + '\'' + 
                '}';
    }
}
