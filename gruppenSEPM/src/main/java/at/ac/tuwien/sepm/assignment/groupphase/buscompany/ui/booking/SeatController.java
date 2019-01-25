package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;


import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import at.ac.tuwien.sepm.assignment.groupphase.exception.NoCustomersException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SeatController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private Rectangle s1,s2,s3,s4,s5,s6,s7,s8,s9,s10,s11,s12,s13,s14,s15,s16,s17,s18,s19,s20,
    s21,s22,s23,s24,s25,s26,s27,s28,s29,s30,s31,s32,s33,s34, s35, s36, s37, s38, s39, s40, s41,
    s42,s43,s44,s45,s46,s47,s48,s49,s50,s51,s52,s53;

    private Integer seatNumber;

    private boolean isClicked;

    private boolean onCancel;
    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;

    @FXML
    private Label numberLabel;

    @FXML
    private Label startLabel;

    @FXML
    private Label endLabel;

    private Integer countSelectedSeats = 0;
    private Map<Customer, Integer> seatMap = new HashMap<>();


    @Autowired
    private RouteService routeService;

    private final Color SELECTED = Color.GREEN;
    private final Color NOT_AVAILABLE = Color.RED;
    private final Color AVAILABLE = Color.web("rgba(97,122,132,1.0)");
    private List<Rectangle> list = new ArrayList<>();



    private void addToList(){
        list.clear();
        list.add(s1);list.add(s2);list.add(s3);list.add(s4);list.add(s5);list.add(s6);list.add(s7);list.add(s8);list.add(s9);list.add(s10);list.add(s11);list.add(s12);list.add(s13);list.add(s14);list.add(s15);list.add(s16);list.add(s17);list.add(s18);
        list.add(s19);list.add(s20);list.add(s21);list.add(s22);list.add(s23);list.add(s24);list.add(s25);list.add(s26); list.add(s27);list.add(s28);list.add(s29);list.add(s30);list.add(s31);list.add(s32);list.add(s33);list.add(s34);list.add(s35);
        list.add(s36);list.add(s37);list.add(s38);list.add(s39);list.add(s40);list.add(s41);list.add(s42);list.add(s43);list.add(s44);list.add(s45);list.add(s46);list.add(s47);list.add(s48);list.add(s49);list.add(s50);list.add(s51);list.add(s52);list.add(s53);
    }

    public void initialize(Map<Customer, Integer> mappingSeats, Route route) throws NoCustomersException {
        onCancel = false;
        this.seatMap = mappingSeats;
        countSelectedSeats = 0;
        LOG.info("Loading seats");
        if(mappingSeats.isEmpty()){
            throw new NoCustomersException("Please enter at least one customer");
        }
        for (Rectangle r : list) {
            r.setFill(AVAILABLE);
        }
        numberLabel.setText(String.valueOf(seatMap.size()));
        startLabel.setText(route.getStartDestination().getName());
        endLabel.setText(route.getEndDestination().getName());
        addToList();
        try {
            List<Integer> seatsFromDB = routeService.loadSeats(route);
            Rectangle r;
            if(!seatsFromDB.isEmpty()){
                for(int i = 0; i < seatsFromDB.size(); i++){
                    r = list.get(seatsFromDB.get(i) - 1);

                    r.setFill(NOT_AVAILABLE);
                }
            }
        } catch (ServiceException e) {
            LOG.error("Error: ",e);
            UserAlert.showError(e.getMessage());
        }
    }

    @FXML
    void seatClick(MouseEvent event) {
        LOG.info("User clicked on a seat");
        Rectangle rectangle = (Rectangle) event.getSource();
        if(countSelectedSeats >= seatMap.size() && rectangle.getFill() != SELECTED){
            UserAlert.showInfo("You have selected enough seats.\nPlease proceed to the next step");
            return;
        }
        LOG.info("Selecting seat");
        if(!rectangle.getFill().equals(NOT_AVAILABLE)){
            if(rectangle.getFill().equals(SELECTED)){
                rectangle.setFill(AVAILABLE);
                countSelectedSeats--;
            }else{
                rectangle.setFill(SELECTED);
                countSelectedSeats++;
            }
        }
    }

    @FXML
    public void clickedCancelButton() {
        LOG.info("Cancel button clicked, closing stage");
        onCancel = true;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void clickedSaveButton(){
        LOG.info("Save button clicked. Saving information about seats.");
        ArrayList<Integer> listForMap = new ArrayList<>();
        LOG.info("Save button clicked, saving seat");
        if(countSelectedSeats < seatMap.size()){
            UserAlert.showInfo("You have not selected enough seats.\nPlease select " + (seatMap.size()-countSelectedSeats) + " more seat(s)");
            return;
        }
        seatNumber = null;
        for(int i = 0; i<list.size(); i++){
            if(list.get(i).getFill().equals(SELECTED)) {
                seatNumber = i+1;
                listForMap.add(seatNumber);
            }
        }
        int i = 0;
        String s = "";
        for(Map.Entry<Customer, Integer> entry : seatMap.entrySet()){
                seatMap.put(entry.getKey(), listForMap.get(i));
                s += (entry.getKey().getFirstName()) + " " + (entry.getKey().getLastName()) + " - " + String.valueOf(entry.getValue()) +"\n";
                i++;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(s);
        alert.setHeaderText("Assigned seats: ");
        alert.setTitle("Notice");
        alert.showAndWait();
        isClicked = true;
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    public Map<Customer, Integer> getSeatMap() {
        return seatMap;
    }

    public boolean onCancel(){
        return onCancel;
    }
    public void setCancel(boolean var){
        onCancel = var;
    }
    public boolean getIsClicked(){
        return isClicked;
    }
}
