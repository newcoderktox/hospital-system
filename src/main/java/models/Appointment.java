package models;

public class Appointment {
    private int id;
    private int patientId;
    private int doctorId;
    private String appointmentDate;
    private String appointmentTime;
    private String status;
    // Arayüzde göstermek için eklenen alanlar (JOIN sorgularından çekilir)
    private String doctorNameAndSurname;
    private String doctorSpecialization;
    private String patientNameAndSurname; // Doktorun randevularını listelerken hasta bilgisi için

    // **DÜZELTİLMİŞ YAPICI METOT (Constructor)** - 9 parametre alacak şekilde ayarlandı
    // AppointmentDB metotları bu yapıcı metodu çağıracak
    public Appointment(int id, int patientId, int doctorId, String appointmentDate, String appointmentTime, String status,
                       String doctorNameAndSurname, String doctorSpecialization, String patientNameAndSurname) {
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

    // Getters (Bu alanları arayüzde kullanmak için gereklidir)...
    public int getId() { return id; }
    public int getPatientId() { return patientId; }
    public int getDoctorId() { return doctorId; }
    public String getAppointmentDate() { return appointmentDate; }
    public String getAppointmentTime() { return appointmentTime; }
    public String getStatus() { return status; }
    public String getDoctorNameAndSurname() { return doctorNameAndSurname; }
    public String getDoctorSpecialization() { return doctorSpecialization; }
    public String getPatientNameAndSurname() { return patientNameAndSurname; }

    // Setters (Eğer randevu bilgilerini düzenleme gibi bir özellik eklenirse gerekebilir, şu anlık yok)...
}