package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.CustomerService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.NewCustomerController;
import at.ac.tuwien.sepm.assignment.groupphase.exception.NoCustomersException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route.RouteCell;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.*;

@Controller
public class CreateBookingController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FXML
    private RadioButton cbWithoutDate;
    @FXML
    private RadioButton cbWithDate;
    @FXML
    private TableColumn<Customer,String> fnameColumn,lnameColumn;
    @FXML
    public TableView<Customer> personTable;
    @FXML
    private TextField fromTextfield, toTextfield, firstnameTf, lastnameTf;
    @FXML
    private DatePicker departureDatepicker, returnDatepicker;
    @FXML
    private Group returnGroup;
    @FXML
    private CheckBox returnCheckbox, transferCheckbox;
    @FXML
    private Button makePaymentButton, cancelButton, searchButton, seatButton;
    @FXML
    private ToggleButton cheapestButton, fastestButton, shortestButton;
    @FXML
    private ListView<List<Route>> listView,listViewReturn;
    @FXML
    private Label lblNormalRoute,lblReturnRoute;
    @FXML
    private Pane normalPane,returnPane;
    @FXML
    private Button addPerson;
    @FXML
    private Button removePerson;
    @FXML
    private TextField socialNumberTextField;
    @FXML
    private CheckBox premiumCheckBox;

    private CustomerService customerService;

    private Customer x;

    private Route fareRoute;
    private Route returnFareRoute;

    @Autowired
    public CreateBookingController(BookingService bookingService, RouteService routeService, ApplicationContext context, CustomerService customerService) {
        this.bookingService = bookingService;
        this.routeService = routeService;
        this.context = context;
        this.customerService = customerService;
        this.booking= new Booking(null,null,0.0);
    }

    private BookingService bookingService;
    private List<String> cityList;
    private RouteService routeService;
    private ApplicationContext context;
    private Booking booking ;
    private boolean blindBooking = false;

    private List<Customer> customerList;
    private List<String> customerSVNR;

    public Route getSearchRoute() {
        return searchRoute;
    }

    public void setSearchRoute(Route searchRoute) {
        if (blindBooking){
            fromTextfield.setText(searchRoute.getStartDestination().toString());
            toTextfield.setText(searchRoute.getEndDestination().toString());
            cbWithoutDate.setVisible(false);
        }
        this.searchRoute = searchRoute;
    }

    private Route searchRoute = new Route();

    public void setBlindBooking(boolean blindBooking) {
        premiumCheckBox.setDisable(true);
        this.blindBooking = blindBooking;
    }

    @FXML
    public void initialize(){
        blindBooking = false;
        ToggleGroup g = new ToggleGroup();
        cbWithDate.setToggleGroup(g);
        cbWithoutDate.setToggleGroup(g);
        cbWithDate.setVisible(false);
        cbWithoutDate.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && newValue){
                returnCheckbox.setDisable(true);
                returnCheckbox.setSelected(false);
                returnPane.setVisible(false);
                departureDatepicker.setValue(null);
            }else {
                returnCheckbox.setDisable(false);
                if(returnCheckbox.isSelected()){
                    returnPane.setVisible(true);
                }

            }
        });
        departureDatepicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                cbWithDate.setSelected(true);
            }else {
                cbWithDate.setSelected(false);
            }
        });

        toTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null && newValue.trim().equals("Anywhere")){
                returnCheckbox.setDisable(true);
            }else {
                returnCheckbox.setDisable(false);
            }
        });


        listView.setCellFactory(param -> new RouteCell(routeService));
        listViewReturn.setCellFactory(param -> new RouteCell(routeService));

        setNormalRouteVisible(false);
        setReturnRouteVisible(false);

        try {
            routeService.initializeGraph();
        } catch (ServiceException e) {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText(e.getMessage());
            a.showAndWait();
        }
        searchButton.setDisable(true);
        returnGroup.setVisible(false);

        BooleanBinding disableSeatBtn = Bindings.isEmpty(listView.getSelectionModel().getSelectedItems()).or(
            Bindings.isEmpty(personTable.getItems()));
        returnCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (returnGroup.isVisible()){
                returnGroup.setVisible(false);
            }
            else returnGroup.setVisible(true);
            seatButton.disableProperty().bind(disableSeatBtn.or(new SimpleBooleanProperty(newValue).and(Bindings.isEmpty(listViewReturn.getSelectionModel().getSelectedItems()))));
        });

        premiumCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (socialNumberTextField.isVisible()){
                socialNumberTextField.setVisible(false);
            }
            else socialNumberTextField.setVisible(true);
        });

        seatButton.disableProperty().bind(disableSeatBtn);
        makePaymentButton.setDisable(true);
        personTable.setEditable(true);
        personTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        fnameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lnameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        updateListOfCities();
        TextFields.bindAutoCompletion(fromTextfield,cityList);
        cityList.add("Anywhere");
        TextFields.bindAutoCompletion(toTextfield,cityList);
        toTextfield.setText("Anywhere");
        bindSearch();
        cheapestButton.setSelected(false);
        fastestButton.setSelected(false);
        shortestButton.setSelected(false);


        try {
            customerList = customerService.loadCustomers();
            List<String> customerNames = new ArrayList<>();
            List<String> customerLastNames = new ArrayList<>();
            customerSVNR = new ArrayList<>();
            for(Customer c : customerList){
                customerNames.add(c.getFirstName());
                customerLastNames.add(c.getLastName());
                customerSVNR.add(c.getSocialNumber());
            }
            bindAutoCompletion(socialNumberTextField, firstnameTf, lastnameTf);
            TextFields.bindAutoCompletion(firstnameTf, customerNames);
            TextFields.bindAutoCompletion(lastnameTf, customerLastNames);
            TextFields.bindAutoCompletion(socialNumberTextField, customerSVNR);
        } catch (ServiceException e) {
           LOG.error("Error", e);
           UserAlert.showError(e.getMessage());
        }
    }

    private void bindAutoCompletion(TextField socialNumberTextField, TextField textField, TextField lname){
        TextFields.bindAutoCompletion(socialNumberTextField, customerSVNR).setOnAutoCompleted(event -> {
            if(customerSVNR.contains(socialNumberTextField.getText())) {
                for (Customer c : customerList) {
                    if(c.getSocialNumber().equals(socialNumberTextField.getText())){
                        textField.setText(c.getFirstName());
                        lname.setText(c.getLastName());
                    }
                }
            }
        });
    }


    private void bindSearch() {
        departureDatepicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
        returnDatepicker.setDayCellFactory(picker -> new DateCell() {
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0 );
            }
        });
        BooleanBinding bb = new BooleanBinding() {
            {
                super.bind(fromTextfield.textProperty(),
                    toTextfield.textProperty(),
                    departureDatepicker.valueProperty(),
                    returnCheckbox.selectedProperty(),
                    returnDatepicker.valueProperty(),
                    cbWithoutDate.selectedProperty());
            }
            @Override
            protected boolean computeValue() {
                return (fromTextfield.getText().isEmpty()
                    || toTextfield.getText().isEmpty()
                    || (departureDatepicker.getValue() == null && !cbWithoutDate.isSelected())
                    || (returnCheckbox.isSelected() && returnDatepicker.getValue() == null));
            }
        };
        searchButton.disableProperty().bind(bb);
        fastestButton.disableProperty().bind(bb);
        shortestButton.disableProperty().bind(bb);
        cheapestButton.disableProperty().bind(bb);
    }

    private void searchMethod(String sort){
        lblNormalRoute.setText("");
        lblReturnRoute.setText("");
        listView.getItems().clear();
        listViewReturn.getItems().clear();
        Route searchRoute = new Route();
        searchRoute.setIncludeReturn(false);
        searchRoute.setWithTransfer(false);
        City fromCity = new City();
        City toCity = new City();
        fromCity.setName(fromTextfield.getText());
        toCity.setName(toTextfield.getText());
        searchRoute.setStartDestination(fromCity);
        searchRoute.setEndDestination(toCity);
        setNormalRouteVisible(true);
        setReturnRouteVisible(false);
        lblNormalRoute.setText(searchRoute.getStartDestination().getName()+" - "+searchRoute.getEndDestination().getName());
        boolean returnTicket= false;
        if (returnCheckbox.isSelected()) {
            searchRoute.setReturnDate(returnDatepicker.getValue());
            lblReturnRoute.setText(searchRoute.getEndDestination().getName()+" - "+searchRoute.getStartDestination().getName());
            searchRoute.setIncludeReturn(true);
            setReturnRouteVisible(true);
            returnTicket=true;
        }
        if (transferCheckbox.isSelected()){
            searchRoute.setWithTransfer(true);
        }
        searchRoute.setDepartureDate(departureDatepicker.getValue());
        //LOG.debug(departureDatepicker.getValue()+"");
        List<List<Route>> result = new ArrayList<>();
        List<List<Route>> resultPointer;
        try {
            resultPointer = routeService.searchRoutes(searchRoute,false, sort,true);
            LOG.debug(blindBooking + "");

            for (List<Route> routes:resultPointer) {
                List<Route> copyList = new ArrayList<>();
                for (Route r:routes){
                    Route route = new Route(r);
                    copyList.add(route);
                    LOG.debug(route.getPrice().toString());
                }
                result.add(copyList);
            }
            if(blindBooking){
                checkIfBlindBooking(result);
            }
            LOG.debug(result.toString());
            listView.getItems().addAll(result);
            if(returnTicket){
                result = routeService.searchRoutes(searchRoute,true, sort,true);
                if(blindBooking){
                    checkIfBlindBooking(result);
                }
                listViewReturn.getItems().addAll(result);
                lblReturnRoute.setText(searchRoute.getEndDestination().getName()+" - "+searchRoute.getStartDestination().getName());
            }
            lblNormalRoute.setText(searchRoute.getStartDestination().getName()+" - "+searchRoute.getEndDestination().getName());
                selectFareRoute();


        } catch (ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showError(e.getMessage());
        }

    }

    private void checkIfBlindBooking(List<List<Route>> result) {
        for (List<Route> routes:result) {
            for (Route r:routes){
                if (routes.size() == 1) {
                    r.setPrice(searchRoute.getBlindBookingPrice());
                }
                else {
                    if (routes.size()>1){
                        if (r == routes.get(0)){
                            r.setPrice(searchRoute.getBlindBookingPrice());
                        }
                        else {
                            r.setPrice(0.0);
                        }
                    }
                }
            }
        }
    }

    @FXML
    private void onSearchRoutesClicked() {
        cheapestButton.setSelected(false);
        fastestButton.setSelected(false);
        shortestButton.setSelected(false);
        if(cbWithoutDate.isSelected() || toTextfield.getText().equals("Anywhere")){
            findFares();
        }else{
            searchMethod("default");
        }

    }

    private void findFares() {

        try {
            City end = (toTextfield.getText().equals("Anywhere"))?null:new City(toTextfield.getText(),null);
            LocalDate departure = (cbWithoutDate.isSelected())?LocalDate.now(): departureDatepicker.getValue();
            Map<FareItem,Route>  r = routeService.findFares(new City(fromTextfield.getText(),null),end,departure);

            final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            SpringFXMLLoader.FXMLWrapper<Object,FareFinderMainController> wrapper=
                fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/fareFinderMain.fxml"),FareFinderMainController.class);
            Scene root = new Scene((Parent)wrapper.getLoadedObject());
            FareFinderMainController controller = wrapper.getController();

            Stage stage = new Stage();
            controller.init(r,(Stage) toTextfield.getScene().getWindow());
            stage.setScene(root);
            stage.showAndWait();

        } catch (ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showError(e.getMessage());
        } catch (NotFoundException e) {
            UserAlert.showInfo(e.getMessage());
        }catch (IOException e) {
            LOG.error("Error:",e);
            UserAlert.showError("Couldn't load scene!");
        }

    }

    @FXML
    private void onCheapest(){
        cheapestButton.setSelected(true);
        fastestButton.setSelected(false);
        shortestButton.setSelected(false);
        searchMethod("cheapest");
    }

    @FXML
    private void onFastest(){
        cheapestButton.setSelected(false);
        fastestButton.setSelected(true);
        shortestButton.setSelected(false);
        searchMethod("fastest");
    }

    @FXML
    private void onShortest(){
        cheapestButton.setSelected(false);
        fastestButton.setSelected(false);
        shortestButton.setSelected(true);
        searchMethod("shortest");
    }

    /*
        Every time new route is being created, if there are new cities we want
        them also to be offered in new bookings
     */
    private void updateListOfCities(){
        try {
            cityList = routeService.getAllCitiesNames();
        } catch (ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showError(e.getMessage());
        }
    }
    @FXML
    private void addPersonClicked() {
        Customer c;
        boolean checker = true;
        if (blindBooking && personTable.getItems().size() == 2){
            UserAlert.showWarning("When creating new blind booking, maximum 2 Persons can be added!");
            return;
        }
        if (firstnameTf.getText().isEmpty() || lastnameTf.getText().isEmpty()){
            UserAlert.showWarning("Please provide customer name!");
            LOG.info("Not all fields provided!");
        }
        else{
            if(premiumCheckBox.isSelected()){
                if(socialNumberTextField.getText().isEmpty()){
                    UserAlert.showWarning("Please provide social security number.");
                    return;
                }
                else{
                    c = checkPremium(firstnameTf.getText(), lastnameTf.getText(), socialNumberTextField.getText());
                    if(c != null){

                        personTable.getItems().add(c);
                        premiumCheckBox.setSelected(false);
                        socialNumberTextField.clear();
                        LOG.info("Person added for buying ticket: " + c.toString());
                    }
                    else{
                        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                        alert.setTitle("Registration?");
                        alert.setContentText("It seems like entered user is not premium.\nWould you like to become premium?");
                        ButtonType yes = new ButtonType("Yes");
                        ButtonType no = new ButtonType("No");
                        alert.getButtonTypes().setAll(yes, no);
                        Optional<ButtonType> option = alert.showAndWait();
                        if(option.get() == yes){
                            LOG.debug("Opening scene for premium user registration.");
                            try {
                                var fxmlLoader = context.getBean(SpringFXMLLoader.class);
                                SpringFXMLLoader.FXMLWrapper<Object, NewCustomerController> wrapper = null;
                                wrapper = fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/newCustomerPrompt.fxml"), NewCustomerController.class);
                                Scene root = new Scene((Parent) wrapper.getLoadedObject());
                                Stage stage = new Stage();
                                stage.setScene(root);
                                NewCustomerController controller = wrapper.getController();
                                stage.initModality(Modality.APPLICATION_MODAL);
                                stage.show();
                                stage.setOnHiding(new EventHandler<WindowEvent>() {
                                    @Override
                                    public void handle(WindowEvent event) {
                                        boolean cancelClicked = controller.getCancelClicked();
                                        if(cancelClicked){
                                            premiumCheckBox.setSelected(false);
                                            socialNumberTextField.clear();
                                        }
                                        else{
                                            x = controller.getCustomer();
                                            personTable.getItems().add(x);
                                        }
                                    }
                                });
                                premiumCheckBox.setSelected(false);
                                socialNumberTextField.clear();

                            } catch (IOException e) {
                                UserAlert.showError("Ooooops, something went wrong. Scene could not be loaded.");
                                return;
                            }
                        }else{
                            checker = false;
                            premiumCheckBox.setSelected(false);
                            socialNumberTextField.clear();
                        }
                    }
                }
            }
            else{
                c = new Customer();
                if (!firstnameTf.getText().matches("[a-zA-Z]+") || !lastnameTf.getText().matches("[a-zA-Z]+")){
                    LOG.error("Invalid customer data!");
                    UserAlert.showWarning("Please give valid customer data!");
                    return;
                }
                c.setFirstName(firstnameTf.getText());
                c.setLastName(lastnameTf.getText());
                personTable.getItems().add(c);
                premiumCheckBox.setSelected(false);
            }
            if(checker){
                firstnameTf.clear();
                lastnameTf.clear();
            }
        }
    }
    @FXML
    private void removePersonClicked(){
        List<Customer> customerList = personTable.getSelectionModel().getSelectedItems();
        personTable.getItems().removeAll(customerList);
        LOG.info("Removing person(s) from buying ticket");
    }
    @FXML
    private void onSeatButtonClicked(){
        LOG.info("Seat button clicked");
        booking.getEntries().clear();
        if (listView.getSelectionModel().getSelectedItems().isEmpty()){
            LOG.error("No route selected!");
            UserAlert.showWarning("Please select one route/row!");
            return;
        }
        if(returnCheckbox.isSelected() && listViewReturn.getSelectionModel().getSelectedItems().isEmpty()){
            LOG.error("No return route is selected!");
            UserAlert.showWarning("Please select one return route/row!");
            return;
        }
        if(personTable.getItems().isEmpty()){
            LOG.error("No passenger is added!");
            UserAlert.showWarning("Please add at least one passenger!");
            return;
        }
        if(returnCheckbox.isSelected()){
            try {
                if(!bookingService.isReturnValid(listView.getSelectionModel().getSelectedItem().get(0),
                    listViewReturn.getSelectionModel().getSelectedItem().get(0))){
                    UserAlert.showWarning("Return trip must be after !");
                    return;
                }
            } catch (ServiceException e) {
                UserAlert.showWarning(e.getMessage());
                return;
            }
        }
        Map<Customer,Integer> selectedPassengers = new HashMap<>();
        for (Customer customer: personTable.getItems()) {
            selectedPassengers.put(customer,null);
        }
        try {
            final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            SpringFXMLLoader.FXMLWrapper<Object,SeatController> wrapper=
                fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/seatBooking.fxml"),SeatController.class);
            Scene root = new Scene((Parent)wrapper.getLoadedObject());
            Stage stage = new Stage();
            stage.setScene(root);
            SeatController controller = wrapper.getController();
            List<Route> listofRoutes = listView.getSelectionModel().getSelectedItem();
            List<Route> listReturnRoutes = listViewReturn.getSelectionModel().getSelectedItem();
            initSeatsController(stage,listofRoutes,controller,selectedPassengers);
            if(returnCheckbox.isSelected()){
               initSeatsController(stage,listReturnRoutes,controller,selectedPassengers);
            }
        } catch (IOException e) {
            LOG.error("Error:",e);
            UserAlert.showWarning("Error by loading new scene!");
        } catch (NoCustomersException e) {
            LOG.error("Error:",e);
        }

    }

    @FXML
    private void onMakePaymentClicked(){
        LOG.info("Make payment clicked");
        try {
            final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            SpringFXMLLoader.FXMLWrapper<Object,MakingPaymentController> wrapper=
                fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/makePayment.fxml"),MakingPaymentController.class);
            Scene root = new Scene((Parent)wrapper.getLoadedObject());
            Stage stage = new Stage();
            stage.setScene(root);
            MakingPaymentController controller = wrapper.getController();
            controller.initialize(booking);
            stage.setOnCloseRequest(event -> {
                Stage stage1 = (Stage) cancelButton.getScene().getWindow();
                if(!controller.onCancel()){
                    stage1.close();
                }
            });
            stage.setOnHiding(event -> {
                Stage stage1 = (Stage) cancelButton.getScene().getWindow();
                if(!controller.onCancel()){
                    stage1.close();
                }
            });
            stage.show();

        } catch (IOException e) {
            LOG.error("Error:",e);
            UserAlert.showError("Error by loading scene!");
        }

    }

    @FXML
    private void onCancel(){
        LOG.info("Cancel button clicked, closing stage");
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private Customer checkPremium(String name, String lastName, String socialNumber){
        Customer c = null;
        try {
            c = customerService.checkPremium(name, lastName, socialNumber);
        } catch (ServiceException e) {
            LOG.error("Error:",e);
        }
        return c;
    }
    private void setReturnRouteVisible(boolean visible){
        returnPane.setVisible(visible);
    }
    private void setNormalRouteVisible(boolean visible){
        normalPane.setVisible(visible);
    }


    public void setUpFareFinder(Route route,Route returnFareRoute) throws ServiceException {
        fareRoute=route;
        this.returnFareRoute=returnFareRoute;
        if(route!=null){
            toTextfield.setText(route.getEndDestination().getName());
            fromTextfield.setText(route.getStartDestination().getName());
            transferCheckbox.setSelected(true);
            departureDatepicker.setValue(route.getDepartureDate());
            onCheapest();

        }
        if(returnFareRoute!=null){
            returnCheckbox.setSelected(true);
            returnDatepicker.setValue(returnFareRoute.getDepartureDate());
            onCheapest();
        }

    }

    private void scrollToFare(ListView<List<Route>> listView ,Route fareRoute) throws ServiceException {
        int i = 0;
        for(List<Route> item : listView.getItems()){
            if(routeService.calculatePrice(item) == fareRoute.getPrice()){
                i = listView.getItems().indexOf(item);
                break;
            }
        }
        listView.getSelectionModel().select(i);
        listView.getFocusModel().focus(i);
        listView.scrollTo(i);
    }

    private void selectFareRoute() throws ServiceException {
        if(fareRoute!=null){
            scrollToFare(listView,fareRoute);
        }

        if(returnFareRoute!=null){
            scrollToFare(listViewReturn,returnFareRoute);
        }

    }

    private void initSeatsController(Stage stage,List<Route> listofRoutes,SeatController controller,Map<Customer,Integer> selectedPassengers) throws NoCustomersException {
        for(Route r : listofRoutes){
            stage.setOnHiding(event -> {
                Map<Customer, Integer> map = controller.getSeatMap();
                for (Map.Entry<Customer, Integer> entry : map.entrySet()){
                    booking.getEntries().add(new BookingEntry(entry.getKey(),entry.getValue(),r));
                }
            });
            if(controller.onCancel()){
                makePaymentButton.setDisable(true);
                controller.setCancel(false);
                break;
            }
            else {
                controller.initialize(selectedPassengers, r);
                stage.showAndWait();
                stage.toFront();
                makePaymentButton.setDisable(false);
            }
        }
    }
}
