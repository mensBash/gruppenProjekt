package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;


import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ModifyBlindBookingController {


    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private TextField city1;

    @FXML
    private TextField image1;

    @FXML
    private TextField city2;

    @FXML
    private TextField image2;

    @FXML
    private TextField city3;

    @FXML
    private TextField image3;

    @FXML
    private TextField city4;

    @FXML
    private TextField image4;

    @FXML
    private TextField city5;

    @FXML
    private TextField image5;

    @FXML
    private TextField city6;

    @FXML
    private TextField image6;

    @FXML
    private Button cancelButton;

    private TextField [] cities;
    private TextField [] images;
    private List<City> databaseCities;

    private List<String> listOfLabels = new ArrayList<>();
    private BookingService bookingService;

    @Autowired
    public ModifyBlindBookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    public void setValues(List<City> cityList, Label[] labels, List<City> databaseCities){

        cities = new TextField[]{city1, city2, city3, city4, city5, city6};
        images = new TextField[]{image1, image2, image3, image4, image5, image6};
        this.databaseCities = databaseCities;
        for(int i = 0; i< labels.length; i++){
            TextFields.bindAutoCompletion(cities[i], databaseCities);
            cities[i].setText(labels[i].getText());
            images[i].setText(cityList.get(i).getPicture());
        }

    }

    public List<String> getLables(){
        return listOfLabels;
    }


    public void onSaveClicked(ActionEvent event) {
        LOG.info("On save button clicked. Saving information.");
        listOfLabels.clear();
        for (TextField city : cities) {
            if(!listOfLabels.contains(city.getText())){
                listOfLabels.add(city.getText());
            }else {
                UserAlert.showWarning("You have duplicates. Please consider your input one more time.");
                return;
            }
        }
        try {

            bookingService.validateEntryForBlindBookingModification(listOfLabels);
            for(int i = 0; i<databaseCities.size(); i++){
                databaseCities.get(i).setBlindBooking(false);
                databaseCities.get(i).setPicture(null);
                for(int j = 0; j < listOfLabels.size(); j++){
                    if(databaseCities.get(i).getName().equals(listOfLabels.get(j))){
                        databaseCities.get(i).setBlindBooking(true);
                        databaseCities.get(i).setPicture(images[j].getText());
                    }
                }
            }
            bookingService.updateCitiesBlindBooking(databaseCities);
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        } catch (ServiceException e) {
            UserAlert.showWarning(e.getMessage());
        }
    }

    public void onCancelClicked(ActionEvent event) {
        LOG.debug("On cancel button clicked. Closing stage");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        listOfLabels.clear();
        stage.close();

    }

   public void onBrowseClicked(ActionEvent event) {
        LOG.info("On browse clicked. Opening browse dialog.");
        Button button = (Button) event.getSource();
        String buttonNumber = button.getAccessibleText();
        LOG.info("User clicked 'Browse...'");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose picture");
        FileChooser.ExtensionFilter allowedFileTypes = new FileChooser.ExtensionFilter("Pictures (*.jpg;*.jpeg;*.png)", "*.jpg","*.jpeg","*.png");
        chooser.getExtensionFilters().add(allowedFileTypes);
        File file = chooser.showOpenDialog(stage);
        if(file != null){
            switch(buttonNumber){
                case "browse1":
                    images[0].setText(file.getAbsolutePath());
                    break;
                case "browse2":
                    images[1].setText(file.getAbsolutePath());
                    break;
                case "browse3":
                    images[2].setText(file.getAbsolutePath());
                    break;
                case "browse4":
                    images[3].setText(file.getAbsolutePath());
                    break;
                case "browse5":
                    images[4].setText(file.getAbsolutePath());
                    break;
                case "browse6":
                    images[5].setText(file.getAbsolutePath());
                    break;
            }
        }
    }



}
