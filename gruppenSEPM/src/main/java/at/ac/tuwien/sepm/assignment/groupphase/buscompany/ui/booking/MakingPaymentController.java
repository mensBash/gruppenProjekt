package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;


import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Booking;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.GeneratePDFService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.MakingPaymentService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PaymentValidatorException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.PrintFailedException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;


@Controller
//@Scope(BeanDefinition.SCOPE_PROTOTYPE) // used to create new obj each time.
public class MakingPaymentController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FXML
    private TextField input;
    @FXML
    private Label priceLabel;
    @FXML
    private Label iban;
    @FXML
    private Button cancel;
    @FXML
    private ComboBox<String> paymentComboBox;

    private MakingPaymentService service;
    private BookingService bookingService;
    private GeneratePDFService generatePDFService;

    private Booking booking;

    private Boolean onCancel = true;

    @Autowired
    public MakingPaymentController(MakingPaymentService service, BookingService bookingService, GeneratePDFService generatePDFService) {
        this.service = service;
        this.bookingService = bookingService;
        this.generatePDFService = generatePDFService;
    }


    public void initialize(Booking booking) {
        this.booking = booking;
        try {
            this.priceLabel.setText(bookingService.calculatePrice(booking) + " EUR");
        } catch (ServiceException e) {
            UserAlert.showWarning(e.getMessage());
        }
        this.paymentComboBox.getItems().addAll("Cash", "IBAN/Credit Card Number");
        this.paymentComboBox.setValue("Cash");
    }

    public void onGenerateClicked() {
        LOG.info("Generate ticket button clicked!");
        try {
            if (!paymentComboBox.getValue().equals("Cash")) {
                service.checkPayment(input.getText());
                LOG.info("Payment successful.");
                UserAlert.showConfirmation("Payment successful. The ticket will be saved in: " + System.getProperty("user.home") + "/busFirmaTickets");

            } else {
                LOG.info("Payment successful");
                //UserAlert.showConfirmation("Payment successful. the Ticket will be saved in: " + Paths.get(System.getProperty("user.home") + "/busFirmaTickets"));
            }
            booking.setStatus(Booking.Status.COMPLETED);
            booking.setBookingNr(System.currentTimeMillis());
            this.generatePDFService.generatePDF(booking);
            this.generatePDFService.printTicket();
            bookingService.createBooking(booking);
            bookingService.createBookingEntry(booking);
            Stage stage = (Stage) cancel.getScene().getWindow();
            onCancel = false;
            stage.close();
        } catch (ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showWarning(e.getMessage());
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        } catch (PaymentValidatorException | PrintFailedException e) {
            LOG.error("Error: ",e);
            UserAlert.showWarning(e.getMessage());
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        }
    }

    public void selectionListener() {
        String selection = paymentComboBox.getValue();
        if (selection.startsWith("IBAN")) {
            iban.setVisible(true);
            input.setVisible(true);
        } else {
            iban.setVisible(false);
            input.setVisible(false);
        }
    }

    public void onCancelClicked() {
        LOG.info("Cancel button clicked!");
        onCancel = true;
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    public boolean onCancel() {
        return onCancel;
    }

    public void setOnCancel(boolean var) {
        onCancel = var;
    }
}
