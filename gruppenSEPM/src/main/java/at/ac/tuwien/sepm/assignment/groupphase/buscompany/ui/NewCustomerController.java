package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.CustomerService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.validator.UIValidator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;
import java.time.LocalDate;

@Controller
public class NewCustomerController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private ImageView img;

    @FXML
    private TextField firstNameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField socialNumberTextField;

    @FXML
    private Button cancel;

    private Boolean cancelClicked;

    private Customer customer;

    @FXML
    private DatePicker birthdayPicker;

    private CustomerService customerService;
    private UIValidator validator;

    @Autowired
    public NewCustomerController(CustomerService customerService, UIValidator validator){
        this.customerService = customerService;
        this.validator = validator;
    }

    public void initialize(){
        cancelClicked = false;
        Image image = new Image(String.valueOf(getClass().getClassLoader().getResource("images/customer.png")), 512, 512,true, true);
        img.setImage(image);
        img.setOpacity(0.1);
    }

    @FXML
    void onCancel(ActionEvent event) {
        cancelClicked = true;
        LOG.info("User clicked on cancel, closing stage");
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onSave(ActionEvent event) {
        LOG.info("Save button clicked");
        String name = firstNameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String socialNumber = socialNumberTextField.getText();
        LocalDate birthday = birthdayPicker.getValue();
        try{
            validator.validateCustomer(name, lastName, email, socialNumber, birthday);
            customer = new Customer(name, lastName, socialNumber, email, birthday);
            customerService.add(customer);
            cancelClicked = false;
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        } catch (CustomerException | ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showError(e.getMessage());
        }
    }

    public Customer getCustomer(){
        return customer;
    }

    public Boolean getCancelClicked(){
        return cancelClicked;
    }

}
