package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.*;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.GeneratePDFService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingDetailsController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FXML
    private Label labelBookingNr;
    @FXML
    private Button cancelButton;
    @FXML
    private Button generateTicket;
    @FXML
    private ImageView imageLogo;
    @FXML
    private TableView<Customer> passengers;
    @FXML
    private TableColumn<Customer, String> firstName;
    @FXML
    private TableColumn<Customer, String> lastName;
    @FXML
    private TableView<RouteEntry> routes;
    @FXML
    private TableColumn<RouteEntry, String> busNumber;
    @FXML
    private TableColumn<RouteEntry, String> from;
    @FXML
    private TableColumn<RouteEntry, String> to;
    @FXML
    private TableColumn<RouteEntry, String> departure;
    @FXML
    private TableColumn<RouteEntry, String> arrival;
    @FXML
    private TableColumn<RouteEntry, String> seatNumber;
    @FXML
    private TextField totalSum;
    @FXML
    private TextField bookingTime;

    private GeneratePDFService generatePDFService;
    private Booking booking;

    @Autowired
    public BookingDetailsController(GeneratePDFService generatePDFService) {
        this.generatePDFService = generatePDFService;
    }

    public void initialize(Booking booking){
        Image img_logo = new Image(String.valueOf(getClass().getClassLoader().getResource("images/logo.png")));
        imageLogo.setImage(img_logo);
        imageLogo.setFitHeight(300);
        imageLogo.setFitWidth(300);
        labelBookingNr.setText(booking.getBookingNr().toString());
        this.booking = booking;

        List<Customer> customers = new ArrayList<>();
        List<RouteEntry> routeList = new ArrayList<>();
        List<Route> routeListHelp = new ArrayList<>();
        for (BookingEntry be : booking.getEntries()){
            if (!customers.contains(be.getCustomer())){
                customers.add(be.getCustomer());
            }
            if(!routeListHelp.contains(be.getRoute())) {
                routeListHelp.add(be.getRoute());
                routeList.add(new RouteEntry(be.getRoute().getBusNumber(),
                    be.getRoute().getStartDestination().getName(),
                    be.getRoute().getEndDestination().getName(),
                    be.getRoute().getTimetable().getDepartureTime(),
                    be.getRoute().getTimetable().getArrivalTime(),
                    be.getSeatNo().toString()
                ));
            }else {
                routeList.get(routeList.size()-1).setSeatNumber(routeList.get(routeList.size()-1).getSeatNumber() + ", " + be.getSeatNo());
            }
        }

        firstName.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        passengers.setItems(FXCollections.observableArrayList(customers));

        busNumber.setCellValueFactory(new PropertyValueFactory<>("busNumber"));
        from.setCellValueFactory(new PropertyValueFactory<>("startDestination"));
        to.setCellValueFactory(new PropertyValueFactory<>("endDestination"));
        departure.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getDeparture().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
        arrival.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getArrival().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
        seatNumber.setCellValueFactory(new PropertyValueFactory<>("seatNumber"));
        routes.setItems(FXCollections.observableArrayList(routeList));

        totalSum.setText(booking.getPrice().toString());
        bookingTime.setText(booking.getTimeOfBooking().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")));
    }

    @FXML
    private void onGenerateTicketClicked(){
        try {
            generatePDFService.generatePDF(booking);
            UserAlert.showInfo("Ticket(s) successfully created!");
        } catch (ServiceException e) {
            LOG.error("Error:", e);
            UserAlert.showWarning(e.getMessage());
        }
    }

    @FXML
    private void clickedCancelButton(ActionEvent e){
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    protected class RouteEntry{
        private String busNumber;
        private String startDestination;
        private String endDestination;
        private LocalDateTime departure;
        private LocalDateTime arrival;
        private String seatNumber;

        private RouteEntry(String busNumber, String startDestination, String endDestination, LocalDateTime departure, LocalDateTime arrival, String seatNumber) {
            this.busNumber = busNumber;
            this.startDestination = startDestination;
            this.endDestination = endDestination;
            this.departure = departure;
            this.arrival = arrival;
            this.seatNumber = seatNumber;
        }

        public String getBusNumber() {
            return busNumber;
        }

        public void setBusNumber(String busNumber) {
            this.busNumber = busNumber;
        }

        public String getStartDestination() {
            return startDestination;
        }

        public void setStartDestination(String startDestination) {
            this.startDestination = startDestination;
        }

        public String getEndDestination() {
            return endDestination;
        }

        public void setEndDestination(String endDestination) {
            this.endDestination = endDestination;
        }

        public LocalDateTime getDeparture() {
            return departure;
        }

        public void setDeparture(LocalDateTime departure) {
            this.departure = departure;
        }

        public LocalDateTime getArrival() {
            return arrival;
        }

        public void setArrival(LocalDateTime arrival) {
            this.arrival = arrival;
        }

        public String getSeatNumber() {
            return seatNumber;
        }

        public void setSeatNumber(String seatNumber) {
            this.seatNumber = seatNumber;
        }
    }

}
