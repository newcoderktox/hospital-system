package ui;

import database.AppointmentDB;
import database.DoctorDB;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Doctor;
import models.User;
import javafx.event.ActionEvent;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class CreateAppointmentController implements Initializable { //initializable makes initialize() run when screen loads
    @FXML
    private ComboBox<Doctor> docCombo; //doctor select combobox
    @FXML
    private DatePicker datePick; //date select picker
    @FXML
    private ComboBox<String> timeCombo; //time select combobox
    @FXML
    private Button createBtn; //create appt button
    @FXML
    private Label loadingMsg; //loading times message
    @FXML
    private Label msgLabel; //error or success messages
    @FXML
    private Label docMsgLabel; //message for doctors

    private User currentLoggedInUser; //logged in user
    private DoctorDB myDocDB; //doctor db obj
    private AppointmentDB myApptDB; //appt db obj

    //this method is called from MainDashboardController to set user
    public void setUser(User user) {
        this.currentLoggedInUser = user;
        System.out.println("DEBUG: setuser called user type " + (currentLoggedInUser != null ? currentLoggedInUser.getUserType() : "null"));
        //adjust UI based on user type
        fixVisibility(); //
        //if user is patient load doctor list
        if (currentLoggedInUser != null && "patient".equalsIgnoreCase(currentLoggedInUser.getUserType())) {
            getDocsList();
        } else {
            //if not patient disable combos
            docCombo.setDisable(true);
            datePick.setDisable(true);
            timeCombo.setDisable(true);
            createBtn.setDisable(true);
            loadingMsg.setVisible(false);
            msgLabel.setVisible(false);
        }
    }
    //this method runs when the screen opens
    @Override
    public void initialize(URL url, ResourceBundle rb) { //init runs first when screen loads
        myDocDB = new DoctorDB(); //make db objs
        myApptDB = new AppointmentDB();
        //set initial states for elements
        timeCombo.setDisable(true);
        createBtn.setDisable(true);
        loadingMsg.setVisible(false);
        msgLabel.setText(""); //no message at start
        //doctor selection changed load available times
        docCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            //disable time combobox if doctor or date not selected
            timeCombo.setDisable(newVal == null || datePick.getValue() == null);
            //update time slots and button state
            fixTimeSlots();
        });
        //date selection changed load available times
        datePick.valueProperty().addListener((obs, oldVal, newVal) -> {
            //disable time combobox if doctor or date not selected
            timeCombo.setDisable(newVal == null || docCombo.getValue() == null);
            //update time slots and button state
            fixTimeSlots();
        });
        //time selection changed update button state
        timeCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            fixCreateBtnState();
        });
        //disable past dates for date picker
        datePick.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                //make today or future dates selectable
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });
        System.out.println("DEBUG: create appt controller initialized");
    }

    //adjust visib based on user type
    private void fixVisibility() {
        boolean isPat = (currentLoggedInUser != null && "patient".equalsIgnoreCase(currentLoggedInUser.getUserType()));
        //if patient show form elements hide doctor message
        docCombo.setVisible(isPat);
        datePick.setVisible(isPat);
        timeCombo.setVisible(isPat);
        createBtn.setVisible(isPat);
        docMsgLabel.setVisible(!isPat);
        //if not patient disable combobox and datepicker
        if (!isPat) {
            docCombo.setDisable(true);
            datePick.setDisable(true);
            timeCombo.setDisable(true);
            createBtn.setDisable(true);
        } else {
            //if patient enable combobox and datepicker at start
            docCombo.setDisable(false);
            datePick.setDisable(false);
        }
    }
    //load doctor list from db and fill combobox
    private void getDocsList() {
        //only load doctors if user is patient
        if (currentLoggedInUser != null && "patient".equalsIgnoreCase(currentLoggedInUser.getUserType())) {
            List<Doctor> allDocs = myDocDB.getDocList();
            docCombo.setItems(FXCollections.observableArrayList(allDocs));
            System.out.println("DEBUG: getdocslist  " + allDocs.size() + " docs");
        } else {
            docCombo.setItems(FXCollections.emptyObservableList()); //if not patient empty list
            System.out.println("DEBUG: getdocslist user not patient no docs loaded");
        }
    }
    //load available times based on selected doctor and date
    private void fixTimeSlots() {
        Doctor chosenDoc = docCombo.getValue();
        LocalDate chosenDate = datePick.getValue();
        //dont load times if no doctor date or user not patient
        if (currentLoggedInUser == null || !"patient".equalsIgnoreCase(currentLoggedInUser.getUserType()) || chosenDoc == null || chosenDate == null) {
            timeCombo.setItems(FXCollections.emptyObservableList());
            timeCombo.setDisable(true);
            loadingMsg.setVisible(false);
            fixCreateBtnState();
            return;
        }
        loadingMsg.setVisible(true); //show load message
        timeCombo.setDisable(true); //disable time selec
        timeCombo.setItems(FXCollections.emptyObservableList()); //clear old times
        msgLabel.setText(""); //clear old mes
        msgLabel.setTextFill(javafx.scene.paint.Color.BLACK); //set message color to default
        //convert date to db format
        String dateString = chosenDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //get available times from appt db
        List<String> availableTimes = myApptDB.getAvailableTimeSlots(chosenDoc.getId(), dateString);
        timeCombo.setItems(FXCollections.observableArrayList(availableTimes));
        timeCombo.setDisable(false); //enable time selec again
        loadingMsg.setVisible(false); //hide loading message
        System.out.println("DEBUG: fixtimeslot available times loaded " + availableTimes.size() + " times");
        fixCreateBtnState(); //update button state after times loaded
    }


    //check create appt button state
    private void fixCreateBtnState() {
        boolean isDocSel = docCombo.getValue() != null;
        boolean isDateSel = datePick.getValue() != null;
        boolean isTimeSel = timeCombo.getValue() != null;
        boolean isUserPat = (currentLoggedInUser != null && "patient".equalsIgnoreCase(currentLoggedInUser.getUserType())); //only patient can create
        //button enabled only if user is patient and doctor date time selected
        boolean enableBtn = isUserPat && isDocSel && isDateSel && isTimeSel;
        createBtn.setDisable(!enableBtn); //enable/disable button based on state
        System.out.println("DEBUG: fixcreatebtnstate doc=" + isDocSel + ", date=" + isDateSel + ", time=" + isTimeSel + ", UserIsPat=" + isUserPat + ". button disabled: " + !enableBtn);
    }


    //runs when create appt button clicked
    //this method's name must match FXML onAction
    @FXML
    private void createAppointment(ActionEvent event) {
        System.out.println("DEBUG: create appt button clicked");
        //get selected doctor date and time
        Doctor chosenDoc = docCombo.getValue();
        LocalDate chosenDate = datePick.getValue();
        String chosenTime = timeCombo.getValue();

        //check if selections are null again
        if (currentLoggedInUser == null || !"patient".equalsIgnoreCase(currentLoggedInUser.getUserType()) || chosenDoc == null || chosenDate == null || chosenTime == null || chosenTime.isEmpty()) {
            msgLabel.setText("Randevu oluşturulamadı: Lütfen tüm bilgileri seçin veya kullanıcı hasta değil.");
            msgLabel.setTextFill(javafx.scene.paint.Color.RED); //red error message
            System.err.println("ERROR: create appt ");
            return;
        }
        //convert date to db format (yyyy-mm-ddd)
        String dateString = chosenDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        //try to create appt
        boolean success = myApptDB.createAppointment(currentLoggedInUser.getId(), chosenDoc.getId(), dateString, chosenTime);
        if (success) {
            msgLabel.setText("Randevu başarıyla oluşturuldu!");
            msgLabel.setTextFill(javafx.scene.paint.Color.GREEN);
            System.out.println("DEBUG: appt created ok");
            //clear fields after appt created
            docCombo.getSelectionModel().clearSelection();
            datePick.setValue(null);
            timeCombo.setItems(FXCollections.emptyObservableList());
            timeCombo.setDisable(true);
            fixCreateBtnState(); //disable button
            loadingMsg.setVisible(false); //auto refresh
        } else {
            msgLabel.setText("Randevu oluşturulurken bir hata oluştu.");
            msgLabel.setTextFill(javafx.scene.paint.Color.RED);
            System.err.println("ERROR: appt creation db error or other idk");
            //clear fields on error too
            docCombo.getSelectionModel().clearSelection();
            datePick.setValue(null);
            timeCombo.setItems(FXCollections.emptyObservableList());
            timeCombo.setDisable(true);
            fixCreateBtnState(); //disable btn
            loadingMsg.setVisible(false);
        }
    }
}
