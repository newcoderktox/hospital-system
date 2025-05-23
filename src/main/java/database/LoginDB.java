package database;

import models.User;
import models.Doctor;
import models.Patient;
import models.UserFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginDB {
    public User checkLogin(String username, String psw) {
        //sql query to check user and pass
        String sql = "SELECT id, username, password, name, surname, phoneNumber, email, userType FROM users WHERE username = ? AND password = ?";
        //db connection and prepare query
        try (Connection myCon = Database.connect(); //connect to db
             PreparedStatement myStmt = myCon.prepareStatement(sql)) { //prepare query
            myStmt.setString(1, username);
            myStmt.setString(2, psw);
            //run query get results
            ResultSet res = myStmt.executeQuery();
            //if result exists user found
            if (res.next()) {
                int id = res.getInt("id");
                String usernameFromDb = res.getString("username");
                String passwordFromDb = res.getString("password"); //pass was checked here
                String name = res.getString("name");
                String surname = res.getString("surname");
                String phoneNum = res.getString("phoneNumber");
                String mail = res.getString("email");
                String userType = res.getString("userType");
                String extra1 = null; //geting extra for info
                String extra2 = null;

                //if doctor get specialization
                if ("doctor".equalsIgnoreCase(userType)) {
                    String docSql = "SELECT specialization FROM doctors WHERE id = ?";
                    try (PreparedStatement docStmt = myCon.prepareStatement(docSql)) {
                        docStmt.setInt(1, id);
                        ResultSet docRes = docStmt.executeQuery();
                        if (docRes.next()) {
                            extra1 = docRes.getString("specialization");

                        }
                    }
                }
                //if patient get dob and blood group
                else if ("patient".equalsIgnoreCase(userType)) {
                    String patSql = "SELECT dateOfBirth, bloodGroup FROM patients WHERE id = ?";
                    try (PreparedStatement patStmt = myCon.prepareStatement(patSql)) {
                        patStmt.setInt(1, id);
                        ResultSet patRes = patStmt.executeQuery();
                        if (patRes.next()) {
                            extra1 = patRes.getString("dateOfBirth");
                            extra2 = patRes.getString("bloodGroup");
                        }
                    }
                }
                //make user obj with UserFactory
                User foundUser = UserFactory.createUser(userType, id, usernameFromDb, passwordFromDb, name, surname, phoneNum, mail, extra1, extra2);
                System.out.println("DEBUG: user found " + foundUser.getUsername());
                return foundUser; //return user

            } else {
                System.out.println("DEBUG: user not found or wrong pass");

            }
        } catch (SQLException e) {
            System.err.println("ERROR: db login problem " + e.getMessage());
            e.printStackTrace();
        }
        return null; //if error or not found return null

    }

    //add new user
    public boolean register(User newUser) {
        Connection myCon = null; //conn obj
        try {
            myCon = Database.connect(); // conn db
            if (myCon == null) {
                System.err.println("ERROR: db err.");
                return false;
            }
            myCon.setAutoCommit(false);
            System.out.println("DEBUG: register  started for user " + newUser.getUsername());

            //check username
            String checkSql = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement checkStmt = myCon.prepareStatement(checkSql)) {
                checkStmt.setString(1, newUser.getUsername());
                ResultSet checkRes = checkStmt.executeQuery();
                if (checkRes.next() && checkRes.getInt(1) > 0) {
                    System.err.println("ERROR: this tc already exists " + newUser.getUsername());
                    myCon.rollback(); //if tc is already there go backk
                    return false;
                }
            }

            //add user
            String userSql = "INSERT INTO users (username, password, name, surname, phoneNumber, email, userType) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userStmt = myCon.prepareStatement(userSql);

            userStmt.setString(1, newUser.getUsername());
            userStmt.setString(2, newUser.getPassword());
            userStmt.setString(3, newUser.getName());
            userStmt.setString(4, newUser.getSurname());
            userStmt.setString(5, newUser.getPhoneNumber());
            userStmt.setString(6, newUser.getEmail());
            userStmt.setString(7, newUser.getUserType());
            userStmt.executeUpdate();

            //getting the id
            int newUserID = -1;
            try (Statement stmt = myCon.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid()")) {
                if (rs.next()) {
                    newUserID = rs.getInt(1);
                    newUser.setId(newUserID); //change the user id
                    System.out.println("DEBUG: new user id " + newUserID);
                } else {
                    //if error rollback
                    System.err.println("ERROR: it returned null");
                    myCon.rollback();
                    return false;
                }
            }

            //add doctor or patient to table
            if ("doctor".equalsIgnoreCase(newUser.getUserType())) {
                String docSql = "INSERT INTO doctors (id, specialization) VALUES (?, ?)";
                PreparedStatement docStmt = myCon.prepareStatement(docSql);
                docStmt.setInt(1, newUserID);
                docStmt.setString(2, ((Doctor) newUser).getSpecialization());
                docStmt.executeUpdate();
                System.out.println("DEBUG: doc info added");
            } else if ("patient".equalsIgnoreCase(newUser.getUserType())) {
                String patSql = "INSERT INTO patients (id, dateOfBirth, bloodGroup) VALUES (?, ?, ?)";
                PreparedStatement patStmt = myCon.prepareStatement(patSql);
                patStmt.setInt(1, newUserID);
                patStmt.setString(2, ((Patient) newUser).getDateOfBirth());
                patStmt.setString(3, ((Patient) newUser).getBloodGroup());
                patStmt.executeUpdate();
                System.out.println("DEBUG: pat info added");
            }

            myCon.commit(); //save the changes
            System.out.println("DEBUG: " + newUser.getUserType() + " " + newUser.getUsername() + " saved");
            return true;

        } catch (SQLException e) {
            System.err.println("ERROR: db err " + e.getMessage());
            e.printStackTrace();
            if (myCon != null) {
                try {
                    System.err.println("DEBUG: rollback bc err.");
                    myCon.rollback(); // Hata olursa geri al
                } catch (SQLException rollbackEx) {
                    System.err.println("ERROR: rollback err " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            return false;
        } finally {
            if (myCon != null) {
                try {
                    myCon.setAutoCommit(true);
                    myCon.close(); //close conn
                    System.out.println("DEBUG: db closed after success");
                } catch (SQLException ex) {
                    System.err.println("ERROR: db close err: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }
}