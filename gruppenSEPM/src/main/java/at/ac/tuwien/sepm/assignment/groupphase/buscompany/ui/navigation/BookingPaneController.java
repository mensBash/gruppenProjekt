package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.navigation;


import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Booking;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking.BookingDetailsController;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.LocalDateStringConverter;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
public class BookingPaneController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private TableView<Booking> activeBookings;
    LocalDateStringConverter converter = new LocalDateStringConverter();
    @FXML
    private TableColumn<Booking, Long> bookingNr;
    @FXML
    private TableColumn<Booking, Booking.Status> status;
    @FXML
    private TableColumn<Booking, Double> totalSum;

    @FXML
    private TextField tfbookingNr;
    @FXML
    private Button newBookingButton;

    @FXML
    private Button cancelButton;
    @FXML
    private Button blindBookingButton;

    private BookingService bookingService;

    private final ApplicationContext context;
    @FXML
    private TableColumn<Booking, String> timeOfBooking;

    private List<Booking> bookingList = new ArrayList<>();
    private List<Long> bookingNumbers = new ArrayList<>();

    @Autowired
    public BookingPaneController(BookingService bookingService, ApplicationContext context) {
        this.bookingService = bookingService;
        this.context = context;
    }

    public void initialize() {
        bookingNumbers.clear();
        bookingList.clear();
        activeBookings.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        bookingNr.setCellValueFactory(new PropertyValueFactory<>("bookingNr"));
        status.setCellValueFactory(new PropertyValueFactory<>("status"));
        totalSum.setCellValueFactory(new PropertyValueFactory<>("price"));
        timeOfBooking.setCellValueFactory(foo -> new SimpleObjectProperty<>(foo.getValue().getTimeOfBooking().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))));
        updateBookingTable();
        try {
            bookingList = bookingService.loadBookings();
            for(Booking b : bookingList){
                bookingNumbers.add(b.getBookingNr());
            }
            TextFields.bindAutoCompletion(tfbookingNr, bookingNumbers);
        } catch (ServiceException e) {
            LOG.error("Error: ", e);

        }

    }

    private void updateBookingTable() {
        this.activeBookings.getItems().clear();
        try {
            activeBookings.setItems(FXCollections.observableArrayList(bookingService.loadBookings()));
        } catch (ServiceException e) {
            LOG.error("Error:",e);
        }
    }


    public void onTableClicked(MouseEvent event) {
        LOG.info("User clicked on table");
        if (event.getClickCount() == 2) {
            Booking selectedItem = activeBookings.getSelectionModel().getSelectedItem();
            if (selectedItem == null) {
                LOG.warn("UI LAYER: User double clicked on table! Nothing was selected in table!");
                return;
            }
            LOG.info("UI LAYER: User double clicked on table row {} !", selectedItem);
            final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            try {
                SpringFXMLLoader.FXMLWrapper<Object, BookingDetailsController> wrapper
                    = fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/bookingDetails.fxml"), BookingDetailsController.class);
                Scene root = new Scene((Parent) wrapper.getLoadedObject());
                Stage stage = new Stage();
                stage.setScene(root);
                BookingDetailsController controller = wrapper.getController();
                controller.initialize(selectedItem);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                stage.toFront();
            } catch (IOException e) {
                UserAlert.showWarning("Failed to display booking");
                LOG.error("Error: ",e);
            }
        }
    }


    public void onNewBooking() {
        try {
            var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            Scene root = new Scene((Parent) fxmlLoader.load(
                getClass().getResourceAsStream("/fxml/createBooking.fxml")));
            Stage stage = new Stage();
            stage.setScene(root);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            initialize();
        } catch (IOException e) {
            LOG.error("Error:",e);
        }

    }

    public void onCancel() {
        LOG.info("Cancel button clicked");
        Booking booking = this.activeBookings.getSelectionModel().getSelectedItem();
        if(booking == null){
            UserAlert.showWarning("Nothing selected!");
            return;
        }
        LocalDateTime departure = booking.getEntries().get(0).getRoute().getTimetable().getDepartureTime();
        if(LocalDateTime.now().isBefore(departure.minusDays(1))) {
            if(UserAlert.showConfirmation("Are you sure you want to cancel selected booking?\nCanceled booking will be removed from database.")){
                try {
                    bookingService.cancel(booking);
                } catch (ServiceException e) {
                    LOG.error("Error:", e);
                    UserAlert.showError(e.getMessage());
                }
            }
        }else{
           UserAlert.showInfo("You cannot cancel the trip  in less than 24 hours before departure!");
           return;
        }
        updateBookingTable();

    }


    @FXML
    public void onBlindBookingClicked(ActionEvent actionEvent) {
        LOG.info("User clicked on Blind booking");
        try {
            var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            Scene root = new Scene((Parent) fxmlLoader.load(
                getClass().getResourceAsStream("/fxml/bbooking.fxml")));
            Stage stage = new Stage();
            stage.setScene(root);
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            stage.setOnHiding(event -> {
                updateBookingTable();
            });
        } catch (IOException e) {
            UserAlert.showWarning("Oooops, something went wrong. Unable to open blind booking");
            LOG.error("Error:",e);
        }
    }

    public void onSearch(){
        if(tfbookingNr.getText().trim().isEmpty()){
            updateBookingTable();
            return;
        }else {
            try {
                List<Booking> temp = bookingService.filterBookings(tfbookingNr.getText().trim());
                activeBookings.getItems().clear();
                activeBookings.getItems().addAll(temp);
            } catch (ServiceException e) {
                LOG.error("Error: ", e);
                UserAlert.showWarning(e.getMessage());
            }
        }
    }

}
