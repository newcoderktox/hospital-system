package ui;

import database.LoginDB;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import models.Doctor;
import models.Patient;
import models.User;
import models.UserFactory;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;


//this class handles new user registration
public class RegisterController implements Initializable { //initializable makes initialize() run when screen loads

    @FXML
    private ComboBox<String> typeCombo;
    @FXML
    private TextField userTxt;
    @FXML
    private PasswordField passTxt;
    @FXML
    private PasswordField confirmPassTxt;
    @FXML
    private TextField nameTxt;
    @FXML
    private TextField surnameTxt;
    @FXML
    private TextField phoneTxt;
    @FXML
    private TextField emailTxt;
    @FXML
    private TextField specTxt;
    @FXML
    private TextField dobTxt;
    @FXML
    private TextField bloodTxt;
    @FXML
    private Label msgLbl;

    private LoginDB myLoginDB;

    //this metot runs when screen opens
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        myLoginDB = new LoginDB(); //make login db obj
        typeCombo.setItems(FXCollections.observableArrayList("doctor", "patient")); //add user types
        //listen for user type changes
        typeCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            //hide all specific fields first
            specTxt.setVisible(false);
            specTxt.setManaged(false);
            dobTxt.setVisible(false);
            dobTxt.setManaged(false);
            bloodTxt.setVisible(false);
            bloodTxt.setManaged(false);

            //show fields based on selected type
            if ("doctor".equals(newVal)) {
                specTxt.setVisible(true);
                specTxt.setManaged(true);
            } else if ("patient".equals(newVal)) {
                dobTxt.setVisible(true);
                dobTxt.setManaged(true);
                bloodTxt.setVisible(true);
                bloodTxt.setManaged(true);
            }
            msgLbl.setText(""); //clear message
        });
        //hide all specific fields at start
        specTxt.setVisible(false);
        specTxt.setManaged(false);
        dobTxt.setVisible(false);
        dobTxt.setManaged(false);
        bloodTxt.setVisible(false);
        bloodTxt.setManaged(false);
        System.out.println("DEBUG: registercont initialized");
    }

    @FXML
    private void registerBtn(ActionEvent event) { //added trim bc of an error
        String uType = typeCombo.getValue();
        String username = userTxt.getText().trim();
        String psw = passTxt.getText().trim();
        String confirmPsw = confirmPassTxt.getText().trim();
        String name = nameTxt.getText().trim();
        String surname = surnameTxt.getText().trim();
        String phone = phoneTxt.getText().trim();
        String email = emailTxt.getText().trim();

        //check if any empty
        if (uType == null || uType.isEmpty() || username.isEmpty() || psw.isEmpty() ||
                confirmPsw.isEmpty() || name.isEmpty() || surname.isEmpty() ||
                phone.isEmpty() || email.isEmpty()) {
            msgLbl.setText("Lütfen tüm zorunlu alanları doldurun.");
            return;
        }
        //check password match
        if (!psw.equals(confirmPsw)) {
            msgLbl.setText("Şifreler eşleşmiyor.");
            return;
        }
        //check if username is valid (tc)
        if (username == null || username.length() != 11) {
            msgLbl.setText("Hata: TC Kimlik No 11 haneli olmalıdır.");
            return;
        }
        for (char c : username.toCharArray()) {
            if (!Character.isDigit(c)) {
                msgLbl.setText("Hata: TC Kimlik No sadece rakamlardan oluşabilir.");
                return;
            }
        }
        //get extra data based on user type
        String extra1 = null;
        String extra2 = null;
        if ("doctor".equals(uType)) {
            extra1 = specTxt.getText().trim();
            if (extra1.isEmpty()) {
                msgLbl.setText("Lütfen uzmanlık alanını girin.");
                return;
            }
        } else if ("patient".equals(uType)) {
            extra1 = dobTxt.getText().trim();
            extra2 = bloodTxt.getText().trim();
            if (extra1.isEmpty() || extra2.isEmpty()) {
                msgLbl.setText("Lütfen doğum tarihi ve kan grubu bilgilerini girin.");
                return;
            }
        }

        //make new user obj with factory
        User newUser = UserFactory.createUser(uType, 0, username, psw, name, surname, phone, email, extra1, extra2);
        if (newUser != null) {
            System.out.println("DEBUG: register start " + username);
            //try to register user using logindb
            if (myLoginDB.register(newUser)) {
                msgLbl.setText("Kayıt başarıyla tamamlandı. Şimdi giriş yapabilirsiniz.");
                System.out.println("DEBUG: register success");
                //go back to login after success
                showLoginScreen(event);
            } else {
                msgLbl.setText("Kayıt başarısız. TC Kimlik No zaten mevcut olabilir.");
                System.err.println("ERROR: username exists");
            }
        } else {
            msgLbl.setText("Kullanıcı oluşturulurken bir hata oluştu.");
            System.err.println("ERROR: user obj null from fact");
        }
    }
    //go back to login
    @FXML
    private void goBack(ActionEvent event) {
        showLoginScreen(event);
    }
    //load login ui
    private void showLoginScreen(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginUI.fxml"));
            Scene newScene = new Scene(loader.load());
            Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            currentStage.setTitle("Hastane Randevu Sistemi - Giriş");
            currentStage.setScene(newScene);
            currentStage.show();
        } catch (IOException e) {
            System.err.println("ERROR: login screen load problem " + e.getMessage());
            e.printStackTrace();
        }
    }
}
