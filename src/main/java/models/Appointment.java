package models;

public class Appointment {  //class for appointments
    private final int id;
    private final int patientId;
    private final int doctorId;
    private final String appointmentDate;
    private final String appointmentTime;
    private final String status;
    private final String doctorNameAndSurname;
    private final String doctorSpecialization;
    private final String patientNameAndSurname; 

    
    
    public Appointment(int id, int patientId, int doctorId, String appointmentDate, String appointmentTime, String status, String doctorNameAndSurname, String doctorSpecialization, String patientNameAndSurname) {
        this.id = id;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.doctorNameAndSurname = doctorNameAndSurname;
        this.doctorSpecialization = doctorSpecialization;
        this.patientNameAndSurname = patientNameAndSurname;
    }

    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getDoctorNameAndSurname() { return doctorNameAndSurname; }
    public String getDoctorSpecialization() { return doctorSpecialization; }
    public String getPatientNameAndSurname() { return patientNameAndSurname; }

    
}