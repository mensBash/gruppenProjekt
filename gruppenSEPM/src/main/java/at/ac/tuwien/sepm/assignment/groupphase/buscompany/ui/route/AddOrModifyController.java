package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Entry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.TextFieldListener;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidInputException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidRouteException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import at.ac.tuwien.sepm.assignment.groupphase.validator.UIValidator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
public class AddOrModifyController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public TextField fieldBusNr;
    public TextField fieldStartDestCity;
    public TextField fieldEndDestCity;
    public TextField fieldStartDestCountry;
    public TextField fieldEndDestCountry;
    public TextField fieldPrice;
    public TextField fieldDistance;
    public DatePicker datepickerDeparture;
    public TextField fieldDepartureHours;
    public DatePicker datepickerArrival;
    public TextField fieldArrivalHours;
    public TextField fieldDepartureMin;
    public TextField fieldArrivalMin;
    public ImageView imageIcon;
    public ImageView imageLogo;
    public Button cancelButton;
    public Button nextButton;
    public Button finishButton;
    public Button removeButton;
    public Label mainLabel;
    public Button backButton;
    public TableView<Entry> tvTimetable;
    public TableColumn<Entry, String> departureColumn;
    public TableColumn<Entry, String> arrivalColumn;
    public TableColumn<Entry, Double> priceColumn;
    public Label departureCityLabel;
    public Label arrivalCityLabel;

    public ComboBox<String> everyDays;
    public ComboBox<Integer> inNextNumber;
    public ComboBox<String> inNextMonths;
    public Button clearLeft;
    public Button clearRight;

    protected Map<TextField, Boolean> map = new HashMap<TextField, Boolean>();

    private RouteService routeService;
    private BookingService bookingService;

    protected Scene root;

    private Stage stage;

    protected Map<Timetable, Double> timetableList = new HashMap<>();
    private List<String> citiesName = null;
    private List<String> countries = null;
    private List<String> buses = null;
    private List<City> cities = null;



    @Autowired
    private ApplicationContext context;

    @Autowired
    private UIValidator uiValidator;

    private String routeNr;
    private String startDestinationCity;
    private String endDestinationCity;
    private String startDestinationCountry;
    private String endDestinationCountry;
    private String price;
    private String distance;
    private Boolean graphinit = true;

    @Autowired
    public AddOrModifyController(RouteService routeService, BookingService bookingService) {
        this.routeService = routeService;
        this.bookingService = bookingService;
    }

    @FXML
    public void onClick() {
        LOG.info("Button add route clicked");
        next();
    }

    public void initialize() {
        try {
            if(graphinit){
                routeService.initializeGraph();
                graphinit = false;
            }
        } catch (ServiceException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
        Image img_info = new Image(String.valueOf(getClass().getClassLoader().getResource("images/icon_info.png")));
        imageIcon.setImage(img_info);
        Image img_logo = new Image(String.valueOf(getClass().getClassLoader().getResource("images/logo.png")));
        imageLogo.setImage(img_logo);
        imageLogo.setFitHeight(300);
        imageLogo.setFitWidth(300);
        addListeners();
        nextButton.setDisable(true);
        updateDestinationsData();
        bindAutoCompletion(fieldStartDestCity, fieldStartDestCountry);
        bindAutoCompletion(fieldEndDestCity, fieldEndDestCountry);

        //eventHandlingDestinations();

        TextFields.bindAutoCompletion(fieldStartDestCountry,countries);
        TextFields.bindAutoCompletion(fieldEndDestCountry,countries);
        TextFields.bindAutoCompletion(fieldBusNr,buses);
    }

    private void bindAutoCompletion(TextField field_end_dest_city, TextField field_end_dest_country) {
        TextFields.bindAutoCompletion(field_end_dest_city, citiesName).setOnAutoCompleted(event -> {
            if (citiesName.contains(field_end_dest_city.getText())) {
                for (City c : cities) {
                    if (c.getName().equals(field_end_dest_city.getText())) {
                        field_end_dest_country.setText(c.getCountry());
                    }
                }
            }
        });
    }

    private void eventHandlingDestinations() {

        EventHandler<KeyEvent> keyHandler = event -> {
            if (citiesName.contains(fieldStartDestCity.getText())) {
                for (City c : cities) {
                    if (c.getName().equals(fieldStartDestCity.getText())) {
                        fieldStartDestCountry.setText(c.getCountry());
                    }
                }
            }
            if (!citiesName.contains(fieldStartDestCity.getText())) {
                fieldStartDestCountry.setText("");
            }
            if (citiesName.contains(fieldEndDestCity.getText())) {
                for (City c : cities) {
                    if (c.getName().equals(fieldEndDestCity.getText())) {
                        fieldEndDestCountry.setText(c.getCountry());
                    }
                }
            }
            if (!citiesName.contains(fieldEndDestCity.getText())) {
                fieldEndDestCountry.setText("");
            }
        };
        fieldStartDestCity.addEventHandler(KeyEvent.ANY,keyHandler);
        fieldEndDestCity.addEventHandler(KeyEvent.ANY,keyHandler);

    }

    private void updateDestinationsData() {
        try {
            cities = routeService.getAllCities();
            citiesName = routeService.getAllCitiesNames();
            countries = routeService.getAllCountriesName();
            buses = routeService.getAllBuses();
        } catch (ServiceException e) {
            UserAlert.showError(e.getMessage());
            LOG.error(e.getMessage(),e);
        }
    }

    protected void addListeners() {
        map.clear();
        String regexDouble = "^?([0-9]+(?:[\\.][0-9]*)?|\\.[0-9]+)$";
        String regexLetters = "^[a-zA-Z_ ]*$";
        String regex = "^[a-zA-Z0-9_ ]*$";
        fieldDistance.textProperty().addListener(new TextFieldListener(fieldDistance, regexDouble, true, map, nextButton));
        fieldStartDestCity.textProperty().addListener(new TextFieldListener(fieldStartDestCity, regexLetters, true, map, nextButton));
        fieldStartDestCountry.textProperty().addListener(new TextFieldListener(fieldStartDestCountry, regexLetters, true, map, nextButton));
        fieldEndDestCity.textProperty().addListener(new TextFieldListener(fieldEndDestCity, regexLetters, true, map, nextButton));
        fieldEndDestCountry.textProperty().addListener(new TextFieldListener(fieldEndDestCountry, regexLetters, true, map, nextButton));
        fieldBusNr.textProperty().addListener(new TextFieldListener(fieldBusNr, regex, true, map, nextButton));
    }

    public void clickedCancelButton(ActionEvent actionEvent) {
        LOG.info("Button cancel clicked");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    protected void loadScene(String scene, Button button) throws IOException {
        Stage stage = (Stage) button.getScene().getWindow();
        final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
        root = new Scene((Parent) fxmlLoader.load(
            getClass().getResourceAsStream(scene)));
        stage.setScene(root);
    }

    public void next() {
        LOG.info("Switching to the next scene");
        try {
            loadScene("/fxml/nextScene.fxml", cancelButton);
        } catch (IOException e) {
            UserAlert.showWarning("Ooops! Something went wrong while loading scene\"");
        }
        tvTimetable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        departureColumn.setCellValueFactory(new PropertyValueFactory<>("depTime"));
        arrivalColumn.setCellValueFactory(new PropertyValueFactory<>("arrTime"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        initializeTableView();
        if (timetableList.isEmpty()){
            removeButton.setVisible(false);
        }else {
            removeButton.setVisible(true);
        }
        everyDays.setItems(FXCollections.observableArrayList("day", "7 days"));
        inNextNumber.setItems(FXCollections.observableArrayList(1,2,3,4,5,6));
        inNextMonths.setItems(FXCollections.observableArrayList("week(s)","month(s)"));
        clearLeft.setOnAction(event -> {
            everyDays.valueProperty().setValue(null);
        });
        clearRight.setOnAction((event -> {
            inNextMonths.valueProperty().setValue(null);
            inNextNumber.valueProperty().setValue(null);
        }));
        routeNr = fieldBusNr.getText();
        startDestinationCity = fieldStartDestCity.getText();
        endDestinationCity = fieldEndDestCity.getText();
        startDestinationCountry = fieldStartDestCountry.getText();
        endDestinationCountry = fieldEndDestCountry.getText();
        price = fieldPrice.getText();
        distance = fieldDistance.getText();
        departureCityLabel.setText(StringUtils.capitalize(startDestinationCity)+", "+ startDestinationCountry);
        arrivalCityLabel.setText(StringUtils.capitalize(endDestinationCity)+", "+ endDestinationCountry);
    }


    public void onAddClicked(ActionEvent event){
        LOG.info("Trying to add new timetable");
        boolean addPeriod = false;
        if (everyDays.getSelectionModel().isEmpty()){
            if (!inNextNumber.getSelectionModel().isEmpty() || !inNextMonths.getSelectionModel().isEmpty()){
                UserAlert.showWarning("Not all values given for specific period!");
                LOG.warn("Not all values given for specific period");
                return;
            }
        }
        if (!everyDays.getSelectionModel().isEmpty()){
            if (inNextNumber.getSelectionModel().isEmpty() || inNextMonths.getSelectionModel().isEmpty()){
                UserAlert.showWarning("Not all values given for specific period!");
                LOG.warn("Not all values given for specific period");
                return;
            }
            addPeriod = true;
        }
        LOG.info("Button add timetable clicked");
        LocalDate departure_date = datepickerDeparture.getValue();
        LocalDate arrival_date = datepickerArrival.getValue();
        String departure_hours = fieldDepartureHours.getText();
        String arrival_hours = fieldArrivalHours.getText();
        String departure_min = fieldDepartureMin.getText();
        String arrival_min = fieldArrivalMin.getText();
        List<Timetable> timetables;
        try {
            if (addPeriod){
                timetables = calculatePeriod();
                for (Timetable timetable:timetables) {
                    //routeService.checkOverlap(timetable, timetableList.keySet());
                    timetableList.put(timetable, uiValidator.validatePrice(fieldPrice.getText()));
                }
            }
            else {
                Timetable timetable = uiValidator.validateTime(departure_date, arrival_date, departure_hours, arrival_hours, departure_min, arrival_min);
                if (timetable != null) {
                    //routeService.checkOverlap(timetable, timetableList.keySet());
                    timetableList.put(timetable, uiValidator.validatePrice(fieldPrice.getText()));

                }
            }
            datepickerDeparture.setValue(null);
            datepickerArrival.setValue(null);
            fieldDepartureHours.clear();
            fieldArrivalHours.clear();
            fieldDepartureMin.clear();
            fieldArrivalMin.clear();
            initializeTableView();
            removeButton.setVisible(true);
            LOG.info("Timetable successfully added.");
        } catch (InvalidInputException e) {
            UserAlert.showError(e.getMessage());
            LOG.error("Error:",e);
        }
    }

    private List<Timetable> calculatePeriod() {
        LocalDate departure_date = datepickerDeparture.getValue();
        LocalDate arrival_date = datepickerArrival.getValue();
        String departure_hours = fieldDepartureHours.getText();
        String arrival_hours = fieldArrivalHours.getText();
        String departure_min = fieldDepartureMin.getText();
        String arrival_min = fieldArrivalMin.getText();
        int everyDay = 7;
        Timetable timetable;
        List<Timetable> result = new ArrayList<>();
        LocalDate endDate;
        if (inNextMonths.getValue().equals("week(s)")){
            endDate = departure_date.plusWeeks(inNextNumber.getValue());
        }
        else {
            endDate = departure_date.plusMonths(inNextNumber.getValue());
        }
        if (everyDays.getValue().equals("day")){
            everyDay = 1;
        }
        try {
            while (departure_date.isBefore(endDate)) {
                timetable = uiValidator.validateTime(departure_date, arrival_date, departure_hours, arrival_hours, departure_min, arrival_min);
                result.add(timetable);
                departure_date = departure_date.plusDays(everyDay);
                arrival_date = arrival_date.plusDays(everyDay);
            }
        }catch (InvalidInputException e) {
            UserAlert.showError(e.getMessage());
            LOG.error("Error:",e);
        }
        return result;

    }

    private void initializeTableView(){
        List<Entry> entryList = new ArrayList<>();
        for(Map.Entry<Timetable, Double> e:timetableList.entrySet()){
            entryList.add(new Entry(e.getKey().getId(), e.getKey().getDepartureTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
                ,e.getKey().getArrivalTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                e.getValue()));

        }
        ObservableList<Entry> data = FXCollections.observableArrayList(entryList);
        tvTimetable.setItems(data);
    }

    public void onRemoveClicked(ActionEvent event){
        LOG.info("Remove button clicked");
        List<Entry> selected = tvTimetable.getSelectionModel().getSelectedItems();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm");
        tvTimetable.getItems().removeAll(selected);
        for (Entry e:selected) {
            timetableList.remove(new Timetable(LocalDateTime.parse(e.getDepTime(),df), LocalDateTime.parse(e.getArrTime(),df)));
        }
        if (timetableList.isEmpty()){
            removeButton.setVisible(false);
        }
    }

    public void onFinishClicked(ActionEvent event){
        LOG.info("Finish button clicked. Adding route");
        try {
            Route route = uiValidator.validateUIRoute(routeNr, startDestinationCity, endDestinationCity, startDestinationCountry, endDestinationCountry, "1", distance);
            if(route != null) {
                if (addRoutes(route)){
                    timetableList.clear();
                    Stage stage = (Stage) backButton.getScene().getWindow();
                    stage.close();
                }
            }
        } catch (ServiceException | InvalidRouteException e) {
            UserAlert.showError(e.getMessage());
            LOG.error("Error:",e);
        }catch (InvalidInputException e){
            LOG.warn(e.getMessage());
            UserAlert.showWarning("Wrong Input! "+e.getMessage());
        }

    }

    protected boolean addRoutes(Route route) throws InvalidRouteException, ServiceException {
        if (!timetableList.isEmpty()) {
            for (Map.Entry<Timetable, Double> entry : timetableList.entrySet()) {
                route.setTimetable(entry.getKey());
                route.setPrice(entry.getValue());
                routeService.addRoute(route);
                LOG.info("Route successfully created");
            }
            UserAlert.showInfo("Route successfully created");
            return true;
        }else {
            return UserAlert.showConfirmation("Route will not be saved because the list of timetables is empty");
        }

    }

    public void onBackClicked(){
        LOG.info("Going back to the previous scene");
        try {
            loadScene("/fxml/addRoute.fxml", finishButton);
            fieldBusNr.setText(routeNr);
            fieldDistance.setText(distance);
            fieldPrice.setText(price);
            fieldEndDestCountry.setText(endDestinationCountry);
            fieldEndDestCity.setText(endDestinationCity);
            fieldStartDestCity.setText(startDestinationCity);
            fieldStartDestCountry.setText(startDestinationCountry);
        }catch (IOException e) {
            UserAlert.showError("Ooops! Something went wrong while loading scene");
        }
    }
}
