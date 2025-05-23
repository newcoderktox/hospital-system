package ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainDashboardController implements Initializable {

    @FXML
    private TabPane mainTabPane;
    @FXML
    private Label welcomeTxt;

    private Tab profileTab;
    private Tab myApptsTab;
    private Tab createApptTab;

    @FXML
    private ProfileController profileRootController; //had to change the names bc of some errors idk
    @FXML
    private MyAppointmentsController myApptsRootController;
    @FXML
    private CreateAppointmentController createApptRootController;

    private User loggedInUser;
    //this method runs when the screen opens
    @Override
    public void initialize(URL url, ResourceBundle rb) {//get tabs from tab pane and save them
        profileTab = mainTabPane.getTabs().get(0);
        myApptsTab = mainTabPane.getTabs().get(1);
        createApptTab = mainTabPane.getTabs().get(2);

        //listen for tab changes to refresh data if needed
        mainTabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab == myApptsTab && myApptsRootController != null) {
                myApptsRootController.loadApps();
                System.out.println("DEBUG: my appts tab selected loading apps");
            } else if (newTab == createApptTab && createApptRootController != null) {
                System.out.println("DEBUG: create appt tab selected");
            } else if (newTab == profileTab && profileRootController != null) {
                System.out.println("DEBUG: profile tab selected");
            }
        });

        System.out.println("DEBUG: maindboardcont initialized");
    }
    //set user for dashboard and otherts
    public void setUser(User user) {
        this.loggedInUser = user;
        if (loggedInUser != null) {
            System.out.println("DEBUG: setmyuser got user type " + loggedInUser.getUserType());
            welcomeTxt.setText("Hoş Geldiniz, " + loggedInUser.getName() + " " + loggedInUser.getSurname() + "!");

            if (profileRootController != null) {
                profileRootController.setUser(loggedInUser);
                System.out.println("DEBUG: profilecont found user set");
            } else {
                System.err.println("ERROR: profilecont err or fx id wrong");
            }
            if (myApptsRootController != null) {
                System.out.println("DEBUG: myapptcont found user set");
                myApptsRootController.setUser(loggedInUser);
            } else {
                System.err.println("ERROR: myapptcont err or fx id wrong");
            }
            if (createApptRootController != null) {
                System.out.println("DEBUG: createapptcont found user set");
                createApptRootController.setUser(loggedInUser);
            } else {
                System.err.println("ERROR: createapptcont err or fx id wrong");
            }
            fixTabVis(); //show/hide tabs based on user type
        } else {
            mainTabPane.getTabs().clear(); //if user is null clear tabs
            welcomeTxt.setText("Lütfen giriş yapın.");
            System.out.println("DEBUG:user not set null tabs cleared");

            //clear user for others too
            if (profileRootController != null) profileRootController.setUser(null);
            if (myApptsRootController != null) myApptsRootController.setUser(null);
            if (createApptRootController != null) createApptRootController.setUser(null);
        }
    }

    //adjust tab visibility based on user type
    private void fixTabVis() {
        System.out.println("DEBUG: fixtabvis started");
        mainTabPane.getTabs().clear();
        if (profileTab == null || myApptsTab == null || createApptTab == null) {
            System.err.println("ERROR: fixtabvis init method err ");
            return;
        }
        if (loggedInUser != null) {
            if ("patient".equalsIgnoreCase(loggedInUser.getUserType())) { //patient tabs
                profileTab.setText("Profil");
                myApptsTab.setText("Randevularım");
                createApptTab.setText("Randevu Oluştur / Yönet");
                mainTabPane.getTabs().add(profileTab);
                mainTabPane.getTabs().add(myApptsTab);
                mainTabPane.getTabs().add(createApptTab);
                System.out.println("DEBUG: user is patient tabs addedd");
            } else if ("doctor".equalsIgnoreCase(loggedInUser.getUserType())) { //doctor tabs
                profileTab.setText("Profil");
                myApptsTab.setText("Randevularım");
                mainTabPane.getTabs().add(profileTab);
                mainTabPane.getTabs().add(myApptsTab);

            }
        }
    }
    //runs when logout button clicked
    @FXML
    private void handleLogout(ActionEvent event) {
        System.out.println("DEBUG: logout button clicked");
        setUser(null); //clear user info
        Stage stage = (Stage) mainTabPane.getScene().getWindow(); //go back to login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/LoginUI.fxml"));
            Parent root = loader.load();
            Scene newScene = new Scene(root);
            stage.setScene(newScene);
            stage.setTitle("Hastane Randevu Sistemi - Giriş");
            stage.show();
            System.out.println("DEBUG: logout ok back to login screen");
        } catch (IOException e) {
            System.err.println("ERROR: logout loginui load problem " + e.getMessage());
            e.printStackTrace();
        }
    }
}