package database; //for appt database

import models.Appointment;
import java.sql.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AppointmentDB {

    //method to add appt
    public boolean createAppointment(int patientID, int doctorID, String dateStr, String timeStr) {
        //the appt info that we are gonna add
        String sql = "INSERT INTO appointments (patient_id, doctor_id, appointment_date, appointment_time, status) VALUES (?, ?, ?, ?, ?)";
        //db con
        try (Connection dbCon = Database.connect();
             PreparedStatement myStmt = dbCon.prepareStatement(sql)) {
            //put values
            myStmt.setInt(1, patientID);
            myStmt.setInt(2, doctorID);
            myStmt.setString(3, dateStr);
            myStmt.setString(4, timeStr);
            myStmt.setString(5, "Scheduled"); // make it scheduled
            //run the query
            int rowsAffected = myStmt.executeUpdate();
            System.out.println("added? " + rowsAffected);
            return rowsAffected > 0; //if added return tru

        } catch (SQLException e) {
            //error happened while making appt
            System.err.println("error happened while making appt " + e.getMessage());
            e.printStackTrace();

            //time slot full maybe
            if (e.getMessage().contains("SQLITE_CONSTRAINT")) {
                System.err.println("time slot full maybe??");
            }
            return false; //appt not added
        }
    }
    //doctors free times find
    public List<String> getAvailableTimeSlots(int doctorID, String dateInfo) {
        List<String> Times = Arrays.asList(
                "09:00", "09:30", "10:00", "10:30", "11:00", "11:30",
                "13:00", "13:30", "14:00", "14:30", "15:00", "15:30",
                "16:00", "16:30", "17:00"
        );
        //taken times collect here
        List<String> takenTimes = new ArrayList<>();
        //sql query
        String sql = "SELECT appointment_time FROM appointments " +
                "WHERE doctor_id = ? AND appointment_date = ? AND status != 'Cancelled'";

        try (Connection dbCon = Database.connect(); // connect
             PreparedStatement myStmt = dbCon.prepareStatement(sql)) { // prepare query
            myStmt.setInt(1, doctorID);
            myStmt.setString(2, dateInfo);
            //run query
            ResultSet queryResults = myStmt.executeQuery();
            //read results
            while (queryResults.next()) {
                takenTimes.add(queryResults.getString("appointment_time"));
            }
        } catch (SQLException e) {
            System.err.println("cant get times " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>(); //if error return empty list
        }

        //find free times
        List<String> freeTimes = new ArrayList<>(Times);
        freeTimes.removeAll(takenTimes); //remove taken ones
        //sort times
        DateTimeFormatter timeF = DateTimeFormatter.ofPattern("HH:mm"); //format time
        List<LocalTime> localFreeT = new ArrayList<>();
        for (String slot : freeTimes) {
            localFreeT.add(LocalTime.parse(slot, timeF));
        }
        Collections.sort(localFreeT); // sort
        //back to string
        List<String> sortedFreeT = new ArrayList<>();
        for (LocalTime time : localFreeT) {
            sortedFreeT.add(time.format(timeF));
        }
        return sortedFreeT; // give sorted free times
    }

    //get patient appts
    public List<Appointment> getAppointmentsByPatientId(int patientID) {
        List<Appointment> apptList = new ArrayList<>();

        //sql query with doctor
        String sql = "SELECT a.id, a.appointment_date, a.appointment_time, a.status, a.patient_id, a.doctor_id, " +
                "d.specialization, u.name as doctor_name, u.surname as doctor_surname " +
                "FROM appointments a " +
                "JOIN doctors d ON a.doctor_id = d.id " +
                "JOIN users u ON d.id = u.id " +
                "WHERE a.patient_id = ?";

        try (Connection dbCon = Database.connect();
             PreparedStatement myStmt = dbCon.prepareStatement(sql)) {
            myStmt.setInt(1, patientID);
            ResultSet queryResults = myStmt.executeQuery();

            while (queryResults.next()) {
                int apptID = queryResults.getInt("id");
                String date = queryResults.getString("appointment_date");
                String time = queryResults.getString("appointment_time");
                String status = queryResults.getString("status");
                int currentPatID = queryResults.getInt("patient_id");
                int currentDocID = queryResults.getInt("doctor_id");
                String docSpecialty = queryResults.getString("specialization");
                String docName = queryResults.getString("doctor_name");
                String docSurname = queryResults.getString("doctor_surname");
                String fullDocName = docName + " " + docSurname;
                Appointment oneAppt = new Appointment(apptID, currentPatID, currentDocID, date, time, status, fullDocName, docSpecialty, null);
                apptList.add(oneAppt);
            }
        } catch (SQLException e) {
            System.err.println("patient appt get error " + e.getMessage());
            e.printStackTrace();
        }
        return apptList;
    }

    //get doctor  appts
    public List<Appointment> getAppointmentsByDoctorId(int doctorID) {
        List<Appointment> apptList = new ArrayList<>();

        //sql query with patient
        String sql = "SELECT a.id, a.appointment_date, a.appointment_time, a.status, a.patient_id, a.doctor_id, " +
                "u.name as patient_name, u.surname as patient_surname " +
                "FROM appointments a " +
                "JOIN patients p ON a.patient_id = p.id " +
                "JOIN users u ON p.id = u.id " +
                "WHERE a.doctor_id = ?";

        try (Connection dbCon = Database.connect();
             PreparedStatement myStmt = dbCon.prepareStatement(sql)) {
            myStmt.setInt(1, doctorID);
            ResultSet queryResults = myStmt.executeQuery();

            while (queryResults.next()) {
                int apptID = queryResults.getInt("id");
                String date = queryResults.getString("appointment_date");
                String time = queryResults.getString("appointment_time");
                String status = queryResults.getString("status");
                int currentPatID = queryResults.getInt("patient_id");
                int currentDocID = queryResults.getInt("doctor_id");
                String patName = queryResults.getString("patient_name");
                String patSurname = queryResults.getString("patient_surname");
                String fullPatName = patName + " " + patSurname;
                Appointment oneAppt = new Appointment(apptID, currentPatID, currentDocID, date, time, status, null, null, fullPatName);
                apptList.add(oneAppt);
            }
        } catch (SQLException e) {
            System.err.println("doctor appt get error " + e.getMessage());
            e.printStackTrace();
        }
        return apptList;
    }
    //cancel appt
    public boolean cancelMyAppt(int cancelID) {
        System.out.println("cancel attempt " + cancelID);
        return updateApptStatus(cancelID, "Cancelled");
    }



    //update appt status
    public boolean updateApptStatus(int updateID, String newStatus) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (Connection dbCon = Database.connect();
             PreparedStatement myStmt = dbCon.prepareStatement(sql)) {
            myStmt.setString(1, newStatus);
            myStmt.setInt(2, updateID);
            int rowsAffected = myStmt.executeUpdate();
            System.out.println("status changed? " + updateID + " " + newStatus);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("status update error " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
