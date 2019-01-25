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
import java.time.format.DateTimeFormatter;

@Controller
public class UpdateCustomerController {

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

    private Customer updateCustomer;

    @FXML
    private DatePicker datePicker;

    private CustomerService customerService;

    private UIValidator uiValidator;
    private String svnr;

    @Autowired
    public UpdateCustomerController(CustomerService customerService, UIValidator uiValidator){
        this.customerService = customerService;
        this.uiValidator = uiValidator;
    }

    public void initialize(Customer c){
        this.updateCustomer = c;
        svnr = c.getSocialNumber();
        Image image = new Image(String.valueOf(getClass().getClassLoader().getResource("images/customer.png")), 512, 512,true, true);
        img.setImage(image);
        img.setOpacity(0.1);
        firstNameTextField.setText(c.getFirstName());
        lastNameTextField.setText(c.getLastName());
        emailTextField.setText(c.getEmail());
        socialNumberTextField.setText(c.getSocialNumber());
        datePicker.setValue(c.getBirthday());
    }

    @FXML
    void onCancel(ActionEvent event) {
        LOG.info("User clicked on cancel, closing stage");
        Stage stage = (Stage) cancel.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onSave(ActionEvent event) {
        try {
            uiValidator.validateCustomer(firstNameTextField.getText(), lastNameTextField.getText(), emailTextField.getText(), socialNumberTextField.getText(), datePicker.getValue());
            updateCustomer.setFirstName(firstNameTextField.getText());
            updateCustomer.setLastName(lastNameTextField.getText());
            updateCustomer.setEmail(emailTextField.getText());
            updateCustomer.setSocialNumber(socialNumberTextField.getText());
            updateCustomer.setBirthday(datePicker.getValue());
            customerService.update(updateCustomer, svnr);
            UserAlert.showInfo("Customer successfully updated");
            Stage stage = (Stage) cancel.getScene().getWindow();
            stage.close();
        }catch (ServiceException | CustomerException e){
            LOG.error("Error:",e);
            UserAlert.showError(e.getMessage());
        }

    }

}
