package ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import models.Doctor;
import models.Patient;
import models.User;
import java.net.URL;
import java.util.ResourceBundle;

//this class shows user profile
public class ProfileController implements Initializable { //initializable makes initialize() run when screen loads

    @FXML private Label welcomeProfLbl;
    @FXML private Label userLbl;
    @FXML private Label nameLbl;
    @FXML private Label surnameLbl;
    @FXML private Label typeLbl;
    @FXML private Label phoneLbl;
    @FXML private Label emailLbl;
    @FXML private Label patDetTitleLbl;
    @FXML private Label dobLbl;
    @FXML private Label bloodLbl;
    @FXML private Label docDetTitleLbl;
    @FXML private Label specLbl;

    private User currentUser; //current logged in user
    //this method runs when the screen opens
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        //hide all specific labels at start
        patDetTitleLbl.setVisible(false);
        patDetTitleLbl.setManaged(false);
        dobLbl.setVisible(false);
        dobLbl.setManaged(false);
        bloodLbl.setVisible(false);
        bloodLbl.setManaged(false);
        docDetTitleLbl.setVisible(false);
        docDetTitleLbl.setManaged(false);
        specLbl.setVisible(false);
        specLbl.setManaged(false);

        System.out.println("DEBUG: profilecont initialized start visibility set");
    }
    //set user for profile screen
    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("DEBUG: setuser called user type " + (currentUser != null ? currentUser.getUserType() : "null"));
        //check if labels are connected from fxml
        if (specLbl == null) {
            System.err.println("ERROR: speclbl not connected check fxml");
        }
        if (patDetTitleLbl == null) System.err.println("ERROR: pattitle not connected");
        if (dobLbl == null) System.err.println("ERROR: dob not connected");
        if (bloodLbl == null) System.err.println("ERROR: blood not connected");
        if (docDetTitleLbl == null) System.err.println("ERROR: doctitle not connected");
        if (currentUser != null) {
            //set common user info
            userLbl.setText("TC Kimlik No: " + (currentUser.getUsername() != null ? currentUser.getUsername() : "Belirtilmemiş"));
            nameLbl.setText("Ad: " + (currentUser.getName() != null ? currentUser.getName() : "Belirtilmemiş"));
            surnameLbl.setText("Soyad: " + (currentUser.getSurname() != null ? currentUser.getSurname() : "Belirtilmemiş"));
            typeLbl.setText("Kullanıcı Tipi: " + (currentUser.getUserType() != null ? currentUser.getUserType() : "Belirtilmemiş"));
            phoneLbl.setText("Telefon: " + (currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber() : "Belirtilmemiş"));
            emailLbl.setText("Email: " + (currentUser.getEmail() != null ? currentUser.getEmail() : "Belirtilmemiş"));

            //check user type for specific details
            if (currentUser instanceof Patient patient) { //if user is a Patient
                System.out.println("DEBUG: user is Patient setting patient details");
                //show patient labels
                if (patDetTitleLbl != null) { patDetTitleLbl.setVisible(true); patDetTitleLbl.setManaged(true); }
                if (dobLbl != null) { dobLbl.setVisible(true); dobLbl.setManaged(true); }
                if (bloodLbl != null) { bloodLbl.setVisible(true); bloodLbl.setManaged(true); }
                dobLbl.setText("Doğum Tarihi: " + (patient.getDateOfBirth() != null ? patient.getDateOfBirth() : "Belirtilmemiş"));
                bloodLbl.setText("kan Grubu: " + (patient.getBloodGroup() != null ? patient.getBloodGroup() : "Belirtilmemiş"));
                //hide doctor labels
                if (docDetTitleLbl != null) { docDetTitleLbl.setVisible(false); docDetTitleLbl.setManaged(false); }
                if (specLbl != null) { specLbl.setVisible(false); specLbl.setManaged(false); }


            } else if (currentUser instanceof Doctor doctor) { //if user is a doctor
                System.out.println("DEBUG: user is doc setting doc details");
                System.out.println("DEBUG: doctor obj spec " + doctor.getSpecialization());
                //show doctor labels
                if (docDetTitleLbl != null) { docDetTitleLbl.setVisible(true); docDetTitleLbl.setManaged(true); }
                if (specLbl != null) {
                    specLbl.setVisible(true);
                    specLbl.setManaged(true);
                    specLbl.setText("Uzmanlık: " + (doctor.getSpecialization() != null ? doctor.getSpecialization() : "Belirtilmemiş"));
                    System.out.println("DEBUG: speclbl visible managed text set");
                } else {
                    System.err.println("ERROR: speclbl null cant set text visibility managed");
                }
                //hide patient labels
                if (patDetTitleLbl != null) { patDetTitleLbl.setVisible(false); patDetTitleLbl.setManaged(false); }
                if (dobLbl != null) { dobLbl.setVisible(false); dobLbl.setManaged(false); }
                if (bloodLbl != null) { bloodLbl.setVisible(false); bloodLbl.setManaged(false); }

            } else { //unknown user
                System.out.println("DEBUG: unknown user type or no specific details hiding labels");
                if (patDetTitleLbl != null) { patDetTitleLbl.setVisible(false); patDetTitleLbl.setManaged(false); }
                if (dobLbl != null) { dobLbl.setVisible(false); dobLbl.setManaged(false); }
                if (bloodLbl != null) { bloodLbl.setVisible(false); bloodLbl.setManaged(false); }
                if (docDetTitleLbl != null) { docDetTitleLbl.setVisible(false); docDetTitleLbl.setManaged(false); }
                if (specLbl != null) { specLbl.setVisible(false); specLbl.setManaged(false); }
            }
        } else { //if user is null clear all labels
            System.out.println("DEBUG: user is null clear labels");
            userLbl.setText("Tc Kimlik No: ");
            nameLbl.setText("Ad: ");
            surnameLbl.setText("Soyad: ");
            typeLbl.setText("Kullanıcı Tipi: ");
            phoneLbl.setText("Telefon: ");
            emailLbl.setText("Email: ");

            //hide all specific labels
            if (patDetTitleLbl != null) { patDetTitleLbl.setVisible(false); patDetTitleLbl.setManaged(false); }
            if (dobLbl != null) { dobLbl.setVisible(false); dobLbl.setManaged(false); }
            if (bloodLbl != null) { bloodLbl.setVisible(false); bloodLbl.setManaged(false); }
            if (docDetTitleLbl != null) { docDetTitleLbl.setVisible(false); docDetTitleLbl.setManaged(false); }
            if (specLbl != null) { specLbl.setVisible(false); specLbl.setManaged(false); }
        }
        System.out.println("DEBUG: setuser finished");
    }
}
