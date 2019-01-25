package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Controller
public class BlindBookingController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    private final ColorAdjust greyscale = new ColorAdjust();

    @Autowired
    private ApplicationContext context;


    @FXML
    private ImageView imageView1;

    @FXML
    private ImageView imageView3;

    @FXML
    private ImageView imageView5;

    @FXML
    private ImageView imageView4;

    @FXML
    private ImageView imageView2;

    @FXML
    private ImageView imageView6;
    @FXML
    private Label label1;

    @FXML
    private Label label2;

    @FXML
    private Label label3;

    @FXML
    private Label label4;

    @FXML
    private Label label5;

    @FXML
    private Label label6;

    @FXML
    private ComboBox<City> departureComboBox;

    @FXML
    private Label priceLabel;

    @FXML
    private Button modifyButton;

    private List<City> cities = new ArrayList<>();
    private List<City> offeredCities = new ArrayList<>();
    private ObservableList<City> departureCities = FXCollections.observableArrayList();

    private City selectedCity;
    private String chosenCity;

    private RouteService routeService;
    private BookingService bookingService;

    private List<String> disabledCities;
    private ImageView [] imageViews;
    private Label [] labels;

    @Autowired
    public BlindBookingController(RouteService routeService,BookingService bookingService) {
        this.routeService = routeService;
        this.bookingService = bookingService;
    }

    public void initialize(){
        disabledCities = new ArrayList<>();
        priceLabel.setText("50.0");
        imageViews = new ImageView[]{imageView1, imageView2, imageView3, imageView4, imageView5, imageView6};
        labels = new Label[]{label1, label2, label3, label4, label5, label6};
        populateCities();
        checkRoutes();
        departureComboBox.setItems(departureCities);

    }

    private void checkRoutes(){
        Route blindRoute = new Route();
        blindRoute.setDepartureDate(LocalDate.now());
        blindRoute.setIncludeReturn(false);
        blindRoute.setWithTransfer(true);
        List<List<Route>> routeList = new ArrayList<>();
        boolean notConnectedCity = false;
        for (City city: cities ) {
            for (City offeredCity : offeredCities) {
                blindRoute.setStartDestination(city);
                blindRoute.setEndDestination(offeredCity);
                try {
                    if (!city.getName().equals(offeredCity.getName())) {
                        routeList = routeService.searchRoutes(blindRoute,false, "default",true);
                    }
                } catch (ServiceException e) {
                    UserAlert.showError(e.getMessage());
                    LOG.error("Error:",e);
                }
                if (routeList.isEmpty()){
                    notConnectedCity = true;
                    break;
                }
                else {
                    notConnectedCity = false;
                }
            }
            if (notConnectedCity){
                departureCities.remove(city);
            }
        }
    }

    private void populateCities(){
        departureCities.clear();
        offeredCities.clear();
        try {
            cities = routeService.getAllCities();
        } catch (ServiceException e) {
            UserAlert.showError(e.getMessage());
            LOG.error("Error:",e);
        }
        int cityCounter = 0;
        for (City city: cities) {
            try {
                if (city.isBlindBooking()){
                    FileInputStream file = new FileInputStream(city.getPicture());
                    imageViews[cityCounter].setImage(new Image(file,342.0, 144.0, false, false));
                    labels[cityCounter].setText(StringUtils.capitalize(city.getName()));
                    cityCounter++;
                    offeredCities.add(city);
                    file.close();
                }
                departureCities.add(city);
            } catch (IOException e) {
                LOG.error("File could not be found");
            }
        }

    }

    @FXML
    public void onCancelClicked(ActionEvent event) {
        LOG.debug("On cancel button clicked. Closing stage.");
        Stage stage = (Stage) imageView1.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onNextClicked(ActionEvent event) {
        LOG.info("User clicked Next");
        List<City> availableCities = new ArrayList<>();
        availableCities.addAll(offeredCities);
            for(int i = 0; i<availableCities.size(); i++){
                for(int j = 0; j<disabledCities.size(); j++){
                    if(availableCities.get(i).getName().equals(disabledCities.get(j))){
                        availableCities.remove(i);
                    }
                }
            }

        if (departureComboBox.getSelectionModel().isEmpty()){
            UserAlert.showWarning("Please select departure destination!");
            return;
        }
        Random random = new Random();
        Integer r = random.nextInt(availableCities.size());
        Route desiredRoute = new Route();
        desiredRoute.setStartDestination(departureComboBox.getSelectionModel().getSelectedItem());
        desiredRoute.setEndDestination(availableCities.get(r));
        desiredRoute.setBlindBookingPrice(Double.parseDouble(priceLabel.getText()));
        try {
            var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            SpringFXMLLoader.FXMLWrapper<Object,CreateBookingController> wrapper =
                fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/createBooking.fxml"),CreateBookingController.class);
            Scene root = new Scene((Parent)wrapper.getLoadedObject());
            CreateBookingController createBookingController = wrapper.getController();
            createBookingController.setBlindBooking(true);
            createBookingController.setSearchRoute(desiredRoute);
            Stage stage = new Stage();
            stage.setScene(root);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            LOG.error("Error:",e);
        }



    }

    @FXML
    public void onImageClick(MouseEvent mouseEvent) {
        LOG.info("User clicked on image");
        if(departureComboBox.getValue() != null){
            ImageView imageView = (ImageView) mouseEvent.getSource();
            int index = 0;
            for(int i = 0; i<imageViews.length; i++){
                if(imageView == imageViews[i]){
                    index = i;
                }
            }
            String city = labels[index].getText();
            Double price = Double.parseDouble(priceLabel.getText());
            if(imageView.getEffect() == greyscale && !city.equals(chosenCity)){
                imageView.setOpacity(1);
                imageView.setEffect(null);
                price -= 5;
                priceLabel.setText(String.valueOf(price));
                disabledCities.remove(city);
                if(disabledCities.isEmpty()){
                    modifyButton.setDisable(false);
                }
            }
            else {
                if (disabledCities.size() < 3) {
                    price += 5;
                    priceLabel.setText(String.valueOf(price));
                    greyscale.setSaturation(-1);
                    imageView.setEffect(greyscale);
                    imageView.setOpacity(0.5);
                    if(!disabledCities.contains(city)){
                        disabledCities.add(city);
                        modifyButton.setDisable(true);
                    }
                }
            }
        } else {
            UserAlert.showWarning("Please select departure location first.");
        }
    }

    @FXML
    public void onModifyClicked(ActionEvent event) {
        LOG.info("Modify button clicked");
        try {
            var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            SpringFXMLLoader.FXMLWrapper<Object,ModifyBlindBookingController> wrapper=
                fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/modifyBlindBooking.fxml"),ModifyBlindBookingController.class);
            Scene root = new Scene((Parent)wrapper.getLoadedObject());
            Stage stage = new Stage();
            stage.setScene(root);
            ModifyBlindBookingController controller = wrapper.getController();
            populateCities();
            checkRoutes();
            controller.setValues(offeredCities, labels, cities);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHiding(event1 -> {
                List<String> list = controller.getLables();
                if(!list.isEmpty()){
                    for(int i = 0; i<labels.length; i++){
                        labels[i].setText(StringUtils.capitalize(list.get(i)));
                    }
                    populateCities();
                    checkRoutes();

                }
            });
            stage.show();
        } catch (IOException e) {
            LOG.error("Error:",e);
        }

    }

    @FXML
    public void onSelectedItem(ActionEvent event) {
        LOG.info("Clicked on an item");
        if (departureComboBox.getValue() == null){
            return;
        }
        selectedCity = departureComboBox.getValue();
        if (selectedCity.getName() == null){
            return;
        }
        for(int i = 0; i<imageViews.length; i++){
                String labelCity = String.valueOf(labels[i].getText());
                if(labelCity.equals(selectedCity.getName())){
                    greyscale.setSaturation(-1);
                    imageViews[i].setEffect(greyscale);
                    imageViews[i].setOpacity(0.5);
                    if(!disabledCities.contains(labelCity)){
                        disabledCities.add(labelCity);
                        modifyButton.setDisable(true);
                    }
                    chosenCity = labelCity;

                }
                else{
                    imageViews[i].setEffect(null);
                    imageViews[i].setOpacity(1);
                    disabledCities.remove(labelCity);
                    priceLabel.setText("50");
                }

        }
    }
}
