/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package View_Controller;

import Database.DBConnection;
import Model.Appointment;
import Model.Customer;
import Model.DataProvider;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author ryanh
 */
public class AddAppointmentController implements Initializable {

    @FXML
    private TextField addCustomerContactText;

    @FXML
    private TextField addAppointmentTitleText;

    @FXML
    private TextField addAppointmentTypeText;

    @FXML
    private ComboBox<String> addAppointmentStartTimeComboBox;

    @FXML
    private DatePicker addAppointmentDatePicker;

    @FXML
    private ComboBox<String> addAppointmentLocationText;

    @FXML
    private ComboBox<String> addAppointmentDescriptionText;

    @FXML
    private ComboBox<String> addAppointmentEndTimeComboBox;

    @FXML
    private TextField addAppointmentURLText;

    @FXML
    private Button saveAppointmentButton;

    @FXML
    private Button backAppointmentButton;

    @FXML
    private TableView<Appointment> addCustomerTableView;

    @FXML
    private TableColumn<Appointment, Integer> addAppointmentCustomerIDColumn;

    @FXML
    private TableColumn<Appointment, Integer> addAppointmentIDColumn;

    @FXML
    private TableColumn<Appointment, String> addAppointmentLocationColumn;

    @FXML
    private TableColumn<Appointment, String> addAppointmentLocalDateColumn;

    @FXML
    private TableColumn<Appointment, String> addAppointmentUTCDateColumn;
    
    Stage stage;
    Parent scene;
    DataProvider addCustomer;
    Appointment selectedCustomer;
    Appointment selectedAppointment;  
    
    public static int customerId;
    public static int userId;
   
    private ObservableList<Appointment> customerSelected = FXCollections.observableArrayList();
    ObservableList<String> appointmentTime = FXCollections.observableArrayList("09:00:00", "10:00:00", "11:00:00", "12:00:00", "13:00:00", "14:00:00", "15:00:00", "16:00:00", "17:00:00");
    ObservableList<String> appointmentLocation = FXCollections.observableArrayList("Phoenix", "New York", "London", "Internet");
    ObservableList<String> appointmentDescription = FXCollections.observableArrayList("There will be a meeting", "Documentation Discussion", "Planning & Coordination");

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
                
        DataProvider.getAllAppointmentsTableList().clear();
        addCustomerTableView.setItems(DataProvider.getSelectedAppointmentsForCustomer());
        
        DataProvider populateAppointments = new DataProvider();
        populateAppointments.populateAppointmentTable();
        
        addAppointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        addAppointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        addAppointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        addAppointmentLocalDateColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        addAppointmentUTCDateColumn.setCellValueFactory(new PropertyValueFactory<>("start"));
        
        

        addAppointmentStartTimeComboBox.setItems(appointmentTime);
        addAppointmentEndTimeComboBox.setItems(appointmentTime);
        addAppointmentDescriptionText.setItems(appointmentDescription);
        addAppointmentLocationText.setItems(appointmentLocation);
        
    }    
    
    public void setSelectedCustomerId(int customerIdValue) {    
        
        customerId = customerIdValue;
    }
    
    public void setSelectedUserId(String selectedUserName) {
        
        try {
            Connection conn = DBConnection.getConnection();
            PreparedStatement getUserId = conn.prepareStatement("SELECT userId FROM user WHERE userName = '" + selectedUserName + "'");
            
            ResultSet resultSetStatement = getUserId.executeQuery();
            
            while(resultSetStatement.next()) {
                
                userId = resultSetStatement.getInt(1);
                
            }
            
        }
        catch(SQLException ex) {
            System.out.println("Error " + ex.getMessage());
        }
    }
    
    @FXML
    private void saveAppointmentHandler(ActionEvent event) throws IOException, ParseException, ClassNotFoundException, SQLException {

        if(addAppointmentCustomerIDColumn.getCellData(0) != null) {
            customerId = addAppointmentCustomerIDColumn.getCellData(0);
        }
        
        String title = addAppointmentTitleText.getText();
        String description = addAppointmentDescriptionText.getSelectionModel().getSelectedItem();
        String location = addAppointmentLocationText.getSelectionModel().getSelectedItem(); 
        String assignedContact = addCustomerContactText.getText();
        String type = addAppointmentTypeText.getText();
        String url = addAppointmentURLText.getText();
        LocalDate appointmentDate = addAppointmentDatePicker.getValue();
        String startTime = addAppointmentStartTimeComboBox.getValue();
        String endTime = addAppointmentEndTimeComboBox.getSelectionModel().getSelectedItem();
           
        String selectedStartDateTime = appointmentDate + " " + startTime;
        String selectedEndDateTime = appointmentDate + " " + endTime;
        
        System.out.println(selectedStartDateTime);

        ZoneId localZoneId = ZoneId.of(TimeZone.getDefault().getID());
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
               
        LocalDateTime startDateTime = LocalDateTime.parse(selectedStartDateTime, format);
        LocalDateTime endDateTime = LocalDateTime.parse(selectedEndDateTime, format);
        
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
        
        if(validateAppointmentStart(testZonedStart, testZonedEnd)) {
        
            Statement statement = DBConnection.getConnection().createStatement();

            String appointmentQuery = "INSERT INTO appointment(customerId, userId, title, description, location, contact, type, url, start, end, createDate, createdBy, lastUpdate, lastUpdateBy)"
                    + " VALUES (" + customerId + ", " + 1 + ", '" + title + "', '" + description + "', '" + location + "', '" + assignedContact + "', '" + type + "', '" + url + "', '" + testZonedStart
                            + "', '" + testZonedEnd + "', NOW(), '" + assignedContact + "', NOW(), '')";
  
            int appointmentExecuteUpdate = statement.executeUpdate(appointmentQuery);            
            
            if(appointmentExecuteUpdate == 1) {
                
                System.out.println("Insert into appointment table was succcessful!");
            }
            
                      
            Appointment appointment = new Appointment(1, customerId, userId, title, description, location, assignedContact, type, url, startConstructorValue, endConstructorValue);
            
            appointment.setAppointmentId(1);
            appointment.setCustomerId(customerId);
            appointment.setUserId(userId);
            appointment.setTitle(title);
            appointment.setDescription(description);
            appointment.setLocation(location);
            appointment.setContact(assignedContact);
            appointment.setType(type);
            appointment.setUrl(url);
            appointment.setStart(selectedStartDateTime);
            appointment.setEnd(selectedEndDateTime);
            
            DataProvider.addAppointment(appointment);
            
            Stage stage = (Stage)((Button)event.getSource()).getScene().getWindow();
            Object scene = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
            stage.setScene(new Scene((Parent) scene));
            stage.show();
            
        }
    }

    @FXML
    private void backAppointmentHandler(ActionEvent event) throws IOException {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Alert!");
            alert.setHeaderText("Warning");
            alert.setContentText("This will clear all text field vlaues, do you want to continue?");
        //Lambda expression to handle alert back to mainscreen    
        alert.showAndWait().ifPresent((response -> {
                if (response == ButtonType.OK) {
                    Parent main = null;
                    try {
                        main = FXMLLoader.load(getClass().getResource("/View_Controller/MainScreen.fxml"));
                        Scene scene = new Scene(main);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        stage.setScene(scene);

                        stage.show();
                    } 
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        }));
    }
    
    public static boolean validateAppointmentStart (String appointmentStartTime, String appointmentEndTime) {
        
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-M-d H:m:s");
               
        LocalDateTime startDateTime = LocalDateTime.parse(appointmentStartTime, format);
        LocalDateTime endDateTime = LocalDateTime.parse(appointmentEndTime, format);
            
            
        boolean earlyAppointment = endDateTime.isBefore(startDateTime);
        boolean sameAppointment = endDateTime.isEqual(startDateTime);
        
            if(sameAppointment) {
                     
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid time interval");
                alert.setContentText("Appointment must have a duration. Please select an approiate time interval!");
                alert.showAndWait();
                return false;
            }

            if(earlyAppointment) {
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid time interval");
                alert.setContentText("Appointment cannot end before it has started. Please select an approiate time interval!");
                alert.showAndWait();
                return false;  
            }    

            try {
            
            Statement statement = DBConnection.getConnection().createStatement();
            String currentAppointments = "SELECT * FROM appointment WHERE ('" + appointmentStartTime + "' BETWEEN start AND end OR '" + appointmentEndTime + "' BETWEEN start AND end OR '"
                    + appointmentStartTime + "' > start AND '" + appointmentEndTime + "' < end)";
            
            ResultSet checkAppointmentTimes = statement.executeQuery(currentAppointments);
            
            if(checkAppointmentTimes.next()) {
  
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid time interval");
                alert.setContentText("There is a customer who already has an appointment within this time frame. Please select an approiate time interval!");
                alert.showAndWait();
                return false;
                } 
            }
            catch(SQLException ex) {
            
                    ex.getMessage();
            }
       return true; 
    }    
}