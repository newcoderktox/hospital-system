package database;

import models.Doctor;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DoctorDB {
    public List<Doctor> getDocList() {
        List<Doctor> docList = new ArrayList<>(); //doctor list
        //get doctors from user table
        String userQ = "SELECT u.id, u.username, u.password, u.name, u.surname, u.phoneNumber, u.email, d.specialization " +
                "FROM users u " +
                "JOIN doctors d ON u.id = d.id " +
                "WHERE u.userType = 'doctor'"; //get docs only

        try (Connection dbCon = Database.connect(); //connect db
             PreparedStatement userStmt = dbCon.prepareStatement(userQ); //prepare query
             ResultSet userRes = userStmt.executeQuery()) { //run query get results
            //read results
            while (userRes.next()) {
                int uID = userRes.getInt("id");
                String username = userRes.getString("username");
                String psw = userRes.getString("password");
                String fName = userRes.getString("name");
                String lName = userRes.getString("surname");
                String phone = userRes.getString("phoneNumber");
                String mail = userRes.getString("email");
                String spec = userRes.getString("specialization");

                //make doctor obj add to list
                Doctor oneDoc = new Doctor(uID, username, psw, fName, lName, phone, mail, spec);
                docList.add(oneDoc);
                System.out.println("DEBUG: getdoclist - doc added " + fName + " " + lName + " (" + spec + ")");
            }
            System.out.println("DEBUG: getdoclist - total " + docList.size() + " docs found");
        } catch (SQLException e) {
            System.err.println("ERROR: all docs get error " + e.getMessage());
            e.printStackTrace();
        }
        return docList; //return doctor list
    }
}