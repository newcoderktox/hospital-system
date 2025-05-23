package ui;

import database.AppointmentDB;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import models.Appointment;
import models.User;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class MyAppointmentsController implements Initializable {

    @FXML //the thing we called from fxml
    private Label welcomeLabel;
    @FXML
    private TableView<Appointment> apptsTable;
    @FXML
    private TableColumn<Appointment, String> dateCol;
    @FXML
    private TableColumn<Appointment, String> timeCol;
    @FXML
    private TableColumn<Appointment, String> doctorCol;
    @FXML
    private TableColumn<Appointment, String> specialtyCol;
    @FXML
    private TableColumn<Appointment, String> statusCol;
    @FXML
    private TableColumn<Appointment, Void> cancelCol;
    @FXML
    private Button refreshBtn;

    private User currentUser;
    private AppointmentDB myApptDB;

    //init method
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        myApptDB = new AppointmentDB(); //make db object
        //connect cols to data
        dateCol.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        //setup cancel button
        makeCancelBtnCol();
        //load appts on click
        if (refreshBtn != null) {
            refreshBtn.setOnAction(event -> loadApps());
            System.out.println("DEBUG: refreshbtn action");
        } else {
            System.err.println("ERROR: refreshb err");
        }
        System.out.println("DEBUG:myapptscont initialized");
    }

    //set user
    public void setUser(User user) {
        this.currentUser = user;
        System.out.println("DEBUG: set user called user type " + (currentUser != null ? currentUser.getUserType() : "null"));
        //check for nulls
        if (cancelCol == null) System.err.println("ERROR: cancelCol is null check fxml");
        if (doctorCol == null) System.err.println("ERROR: doctorCol is null");
        if (specialtyCol == null) System.err.println("ERROR: specialtyCol is null");
        //adjust table look
        fixTableLook();
        //load appts
        loadApps();
    }

    //change table for user
    private void fixTableLook() {
        boolean isPat = (currentUser != null && "patient".equalsIgnoreCase(currentUser.getUserType()));
        System.out.println("DEBUG: adjusttable running is pat " + isPat);
        //null check again
        if (doctorCol == null || specialtyCol == null || cancelCol == null) {
            System.err.println("ERROR: some cols null cant adjust table");
            return;
        }
        if (isPat) { //if patient
            System.out.println("DEBUG: patient view setup");
            doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorNameAndSurname"));
            specialtyCol.setCellValueFactory(new PropertyValueFactory<>("doctorSpecialization"));
            specialtyCol.setVisible(true);
            cancelCol.setVisible(true);
        } else { //if doctor
            System.out.println("DEBUG: doctor view setup");
            doctorCol.setCellValueFactory(new PropertyValueFactory<>("patientNameAndSurname"));
            specialtyCol.setVisible(false);
            cancelCol.setVisible(false);
        }
        System.out.println("DEBUG: table adjustments done");
    }
    //load appts
    public void loadApps() {
        if (currentUser == null) {
            apptsTable.setItems(FXCollections.emptyObservableList());
            System.out.println("DEBUG: apps not loaded user null");
            return;
        }
        List<Appointment> myAppts = null;
        if ("patient".equalsIgnoreCase(currentUser.getUserType())) {
            //get patient apps
            myAppts = myApptDB.getAppointmentsByPatientId(currentUser.getId());
            System.out.println("DEBUG: pat apps loading user ID " + currentUser.getId());
        } else if ("doctor".equalsIgnoreCase(currentUser.getUserType())) {
            //get doctor apps
            myAppts = myApptDB.getAppointmentsByDoctorId(currentUser.getId());
            System.out.println("DEBUG: doc apps loading user ID " + currentUser.getId());
        } else {
            myAppts = new ArrayList<>(); //unknown
            System.out.println("DEBUG:  unknown apps not loaded " + currentUser.getUserType());
        }
        if (myAppts != null) {
            ObservableList<Appointment> obsAppts = FXCollections.observableArrayList(myAppts);
            apptsTable.setItems(obsAppts);
            System.out.println("DEBUG: table loaded " + myAppts.size() + " appts");
        } else {
            apptsTable.setItems(FXCollections.emptyObservableList());
            System.out.println("DEBUG: appt list null or empty table cleared");
        }
    }
    //setup cancel button column
    private void makeCancelBtnCol() {


        if (cancelCol == null) {
            System.err.println("ERROR: cancelCol null cant set cell factory");
            return;
        }
        Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>> cellFactory = new Callback<TableColumn<Appointment, Void>, TableCell<Appointment, Void>>() {
            @Override
            public TableCell<Appointment, Void> call(final TableColumn<Appointment, Void> param) {
                final TableCell<Appointment, Void> cell = new TableCell<Appointment, Void>() {
                    private final Button cancelBtn = new Button("İptal Et"); //make button
                    {
                        cancelBtn.setOnAction((javafx.event.ActionEvent event) -> {
                            //get appt
                            Appointment apptToCancel = getTableView().getItems().get(getIndex());
                            //try to cancel
                            boolean success = myApptDB.updateApptStatus(apptToCancel.getId(), "Cancelled");
                            if (success) {
                                System.out.println("DEBUG: appt canceled ID " + apptToCancel.getId());
                                //refresh table
                                loadApps();
                            } else {
                                System.err.println("ERROR: appt cancel error ID " + apptToCancel.getId());
                            }
                        });
                        cancelBtn.getStyleClass().add("cancel-button"); //add css class
                    }


                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        //if empty no graphic
                        if (empty) {
                            setGraphic(null);
                        } else {
                            //if not empty show button
                            Appointment appt = getTableView().getItems().get(getIndex());
                            if ("Scheduled".equalsIgnoreCase(appt.getStatus())) { //only show if scheduled
                                setGraphic(cancelBtn);
                            } else { //other cases
                                setGraphic(null);
                            }
                        }
                        setText(null); //no text
                    }
                };
                return cell;
            }
        };
        cancelCol.setCellFactory(cellFactory); //set cell factory
    }

    @FXML
    private void handleRefreshAppts(ActionEvent event) {
        System.out.println("DEBUG: refresh button clicked");
        loadApps();
    }
}
