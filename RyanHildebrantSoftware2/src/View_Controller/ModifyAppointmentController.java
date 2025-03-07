/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Database.DBConnection;
import Model.Appointment;
import Model.DataProvider;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ryanh
 */
public class ModifyAppointmentController implements Initializable {

    @FXML
    private TextField modifyContactNameText;
    @FXML
    private TextField modifyTitleText;
    @FXML
    private ComboBox<String> modifyLocationComboBox;
    @FXML
    private TextField modifyTypeText;
    @FXML
    private TextField modifyURLText;
    @FXML
    private ComboBox<String> modifyDescriptionComboBox;
    @FXML
    private DatePicker modifyDate;
    @FXML
    private ComboBox<String> modifyStartComboBox;
    @FXML
    private ComboBox<String> modifyEndComboBox;
    @FXML
    private Button saveModifyAppointmentButton;
    @FXML
    private Button cancelModifyAppointmentButton;

    Appointment selectedAppointment;
    int selectedIndex;
    
    ObservableList<String> appointmentTime = FXCollections.observableArrayList("09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00", "16:00:00", "17:00:00");
    ObservableList<String> appointmentLocation = FXCollections.observableArrayList("Phoenix", "New York", "London", "Internet");
    ObservableList<String> appointmentDescription = FXCollections.observableArrayList("There will be a meeting", "Documentation Discussion", "Planning & Coordination");
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        modifyStartComboBox.setItems(appointmentTime);
        modifyEndComboBox.setItems(appointmentTime);
        modifyDescriptionComboBox.setItems(appointmentDescription);
        modifyLocationComboBox.setItems(appointmentLocation);
    }    

    @FXML
    private void saveMondifyAppointmentHandler(ActionEvent event) throws IOException {
        
        try {
        
        int appointmentId = selectedAppointment.getAppointmentId();
        int customerId = selectedAppointment.getCustomerId();
        int userId = selectedAppointment.getUserId();
        
        String title = modifyTitleText.getText();
        String description = modifyDescriptionComboBox.getSelectionModel().getSelectedItem();
        String location = modifyLocationComboBox.getSelectionModel().getSelectedItem();
        String assignedContact = modifyContactNameText.getText();
        String type = modifyTypeText.getText();
        String url = modifyURLText.getText();
        
        
        LocalDate date = modifyDate.getValue();
        String startTime = modifyStartComboBox.getSelectionModel().getSelectedItem();
        String endTime = modifyEndComboBox.getSelectionModel().getSelectedItem();
        String selectedStartTime = date + " " + startTime;
        String selectedEndTime = date + " " + endTime;
        
        if(startTime == null || endTime == null) {
            
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("No appointment time selected");
            alert.setContentText("Please select a start and end time to add to this appointment!");
            alert.showAndWait();  
        }
        
        if(location == null) {
                        
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("No location selected");
            alert.setContentText("Please select a location to add to this appointment!");
            alert.showAndWait();  
        }
        
        if(description == null) {
                        
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("No description selected");
            alert.setContentText("Please select a description to add to this appointment!");
            alert.showAndWait();
            
        }
        
        if(date == null) {
                       
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error");
            alert.setHeaderText("No date selected");
            alert.setContentText("Please select a valid date to add to this appointment!");
            alert.showAndWait();
        }
        
        
        
        LocalDateTime localTime = LocalDateTime.now();
        ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID());
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        
        LocalDateTime startDateTime = LocalDateTime.parse(selectedStartTime, format);
        LocalDateTime endDateTime = LocalDateTime.parse(selectedEndTime, format);
        
        ZonedDateTime zonedStartLocal = ZonedDateTime.of(startDateTime, localZoneId);
        ZonedDateTime zonedStartUTC = zonedStartLocal.withZoneSameInstant(ZoneId.of("UTC"));
        
        ZonedDateTime zonedEndLocal = ZonedDateTime.of(endDateTime, localZoneId);
        ZonedDateTime zonedEndUTC = zonedEndLocal.withZoneSameInstant(ZoneId.of("UTC"));
        
        int startUTCYear = zonedStartUTC.getYear();
        int startUTCMonth = zonedStartUTC.getMonthValue();
        int startUTCDay = zonedStartUTC.getDayOfMonth();
        int startUTCHour = zonedStartUTC.getHour();
        int startUTCMinute = zonedStartUTC.getMinute();
        
        String testZonedStart = Integer.toString(startUTCYear) + "-" + Integer.toString(startUTCMonth) + "-" + Integer.toString(startUTCDay) + " " 
                + Integer.toString(startUTCHour) + ":" + Integer.toString(startUTCMinute) + ":" + Integer.toString(startUTCMinute);
        
        int endUTCYear = zonedEndUTC.getYear();
        int endUTCMonth = zonedEndUTC.getMonthValue();
        int endUTCDay = zonedEndUTC.getDayOfMonth();
        int endUTCHour = zonedEndUTC.getHour();
        int endUTCMinute = zonedEndUTC.getMinute();
        
        String testZonedEnd = Integer.toString(endUTCYear) + "-" + Integer.toString(endUTCMonth) + "-" + Integer.toString(endUTCDay) + " " 
                + Integer.toString(endUTCHour) + ":" + Integer.toString(endUTCMinute) + ":" + Integer.toString(endUTCMinute);
        
        String startConstructorValue = zonedStartLocal.toString();
        String endConstructorValue = zonedEndLocal.toString();
        
        if(AddAppointmentController.validateAppointmentStart(testZonedStart, testZonedEnd)) {
        
        Appointment appointment = new Appointment(appointmentId, customerId, userId, title, description, location, assignedContact, type, url, startConstructorValue, endConstructorValue);
        DataProvider.getAllAppointmentsTableList().set(selectedIndex, appointment);
       
        Statement statement = DBConnection.getConnection().createStatement();
        ResultSet selectAppointment = statement.executeQuery("SELECT * FROM appointment WHERE appointmentId = " + appointmentId + "");
        
        while(selectAppointment.next()) {
            
            customerId = selectAppointment.getInt("customerId");
        }
        
        
        String updateAppointment = "UPDATE appointment SET title = '" + title + "', description = '" + description + "', location = '" + location + "', contact = '"
                + assignedContact + "', type = '" + type + "', url = '" + url + "', start = '" + testZonedStart + "', end = '" + testZonedEnd + "' WHERE appointmentId = " + appointmentId;
        int updatedAppointment = statement.executeUpdate(updateAppointment);
        if(updatedAppointment == 1) {
            System.out.println("Appointment was updated successfully!");
             
                }
            }
        }
        catch(SQLException ex) {
            System.out.println("Error " + ex.getMessage());
        }
        
        catch(NullPointerException ex) {   
            System.out.println("Error " + ex.getMessage());  
        }
        
        catch(DateTimeParseException ex) {
            System.out.println("Error " + ex.getMessage());
        }
        
        Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
        Object scene = FXMLLoader.load(getClass().getResource("/View_Controller/AppointmentScreen.fxml"));
        stage.setScene(new Scene((Parent) scene));
        stage.show();
        
    }

    @FXML
    private void cancelModifyAppointmentHandler(ActionEvent event) throws IOException {
        
         Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This will clear all text field vlaues, do you want to continue?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Object scene = FXMLLoader.load(getClass().getResource("/View_Controller/AppointmentScreen.fxml"));
            stage.setScene(new Scene((Parent) scene));
            stage.show();
        }
    }
    
    public void setAppointment(Appointment appointment, int index) {
        
        selectedAppointment = appointment;
        selectedIndex = index;
        
        Appointment newAppointment = (Appointment) appointment;           
           
        this.modifyContactNameText.setText(newAppointment.getContact());
        this.modifyTitleText.setText((newAppointment.getTitle()));
        this.modifyURLText.setText((newAppointment.getUrl()));
        this.modifyTypeText.setText((newAppointment.getType()));

    }
    
}
