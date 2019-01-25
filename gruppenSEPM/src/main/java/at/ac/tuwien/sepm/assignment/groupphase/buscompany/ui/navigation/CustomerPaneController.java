package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.navigation;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Customer;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.CustomerService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UpdateCustomerController;
import at.ac.tuwien.sepm.assignment.groupphase.exception.CustomerException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import at.ac.tuwien.sepm.assignment.groupphase.validator.UIValidator;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.List;

@Controller
public class CustomerPaneController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfLastName;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField lastNameTextField;

    @FXML
    private TextField socialTextField;

    @FXML
    private DatePicker birthdayTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private TableView<Customer> customers;

    @FXML
    private TableColumn<Customer, String> nameColumn;

    @FXML
    private TableColumn<Customer, String> lastNameColumn;

    @FXML
    private TableColumn<Customer, LocalDate> birthdayColumn;

    @FXML
    private TableColumn<Customer, String> socialNumberColumn;

    @FXML
    private TableColumn<Customer, String> emailColumn;

    private UIValidator validator;
    private CustomerService customerService;
    private ApplicationContext context;
    private SpringFXMLLoader loader;

    @Autowired
    public CustomerPaneController(ApplicationContext context, SpringFXMLLoader loader, UIValidator validator, CustomerService customerService) {
        this.context = context;
        this.validator = validator;
        this.customerService = customerService;
        this.loader = loader;
    }

    @FXML
    private void initialize(){
        customers.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        birthdayColumn.setCellValueFactory(new PropertyValueFactory<>("birthday"));
        socialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("socialNumber"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        loadCustomerTable();
    }

    private void loadCustomerTable(){
        this.customers.getItems().clear();
        try {
            customers.setItems(FXCollections.observableArrayList(customerService.loadCustomers()));
        } catch (ServiceException e) {
            LOG.debug(e.toString());
            UserAlert.showWarning(e.getMessage());
        }


    }


    @FXML
    void onSave(ActionEvent event) {
        LOG.info("Save button clicked");
        String name = nameTextField.getText();
        String lastName = lastNameTextField.getText();
        String email = emailTextField.getText();
        String socialNumber = socialTextField.getText();
        LocalDate birthday = birthdayTextField.getValue();
        try{
            validator.validateCustomer(name, lastName, email, socialNumber, birthday);
            Customer customer = new Customer(name, lastName, socialNumber, email, birthday);
            customerService.add(customer);
            loadCustomerTable();
            nameTextField.clear();
            lastNameTextField.clear();
            emailTextField.clear();
            socialTextField.clear();
            birthdayTextField.setValue(null);
        } catch (CustomerException | ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showWarning(e.getMessage());
        }
    }

    @FXML
    void onSearch(ActionEvent event) {
        LOG.info("Search button clicked");
        Customer c = new Customer();
        if(tfName.getText().trim().isEmpty() && tfLastName.getText().trim().isEmpty()){
            try {
                customers.getItems().clear();
                customers.getItems().addAll(customerService.loadCustomers());
                return;
            } catch (ServiceException e) {
                UserAlert.showWarning(e.getMessage());
            }
        }
        if(!tfName.getText().trim().isEmpty()){
            c.setFirstName(tfName.getText().trim());
        }

        if(!tfLastName.getText().trim().isEmpty()){
            c.setLastName(tfLastName.getText().trim());
        }
        try {
            List<Customer> temp = customerService.filterCustomers(c);
            customers.getItems().clear();
            customers.getItems().addAll(temp);
        } catch (ServiceException e) {
            LOG.error(e.toString());
            UserAlert.showError(e.getMessage());
        }
    }

    public void onModify(ActionEvent event) {
        LOG.info("Modify button clicked");
        List<Customer> customerList = customers.getSelectionModel().getSelectedItems();
        if(customerList.isEmpty()){
            UserAlert.showWarning("Nothing selected.");
        }
        else if(customerList.size() > 1){
            UserAlert.showWarning("Can't modify more customers at once");
            return;
        }
        else{
            try {
                var fxmlLoader = context.getBean(SpringFXMLLoader.class);
                SpringFXMLLoader.FXMLWrapper<Object, UpdateCustomerController> wrapper
                    = fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/updateCustomerPrompt.fxml"), UpdateCustomerController.class);
                Scene root = new Scene((Parent) wrapper.getLoadedObject());
                Stage stage = new Stage();
                stage.setScene(root);
                UpdateCustomerController controller = wrapper.getController();
                controller.initialize(customerList.get(0));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setOnHiding(event1 -> {
                    loadCustomerTable();
                });
                stage.showAndWait();
            } catch (IOException e) {
                LOG.error("Error:",e);
                UserAlert.showError("Oooops, something went wrong. Could not open requested scene.");
            }
        }
    }

    public void onDelete(ActionEvent event) {
        LOG.info("Delete button clicked");
        List<Customer> customerList = customers.getSelectionModel().getSelectedItems();
        if(customerList.isEmpty()){
            UserAlert.showWarning("Nothing selected");
        }
        else{
            if(UserAlert.showConfirmation("Are you sure you want to delete selected customers?")){
                try {
                    for (Customer c:
                        customerList) {

                        customerService.delete(c);
                    }
                    UserAlert.showWarning("Selected customer(s) successfully deleted.");
                    loadCustomerTable();
                }catch (ServiceException e) {
                    LOG.error("Error:",e);
                    UserAlert.showWarning(e.getMessage());
                }
            }
        }
    }

    public void openUpdate(MouseEvent mouseEvent) {
        LOG.info("User double-clicked on customer");
        if(mouseEvent.getClickCount() == 2){
            Customer c = customers.getSelectionModel().getSelectedItem();
            if(c != null){
                try {
                    var fxmlLoader = context.getBean(SpringFXMLLoader.class);
                    SpringFXMLLoader.FXMLWrapper<Object, UpdateCustomerController> wrapper
                        = fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/updateCustomerPrompt.fxml"), UpdateCustomerController.class);
                    Scene root = new Scene((Parent) wrapper.getLoadedObject());
                    Stage stage = new Stage();
                    stage.setScene(root);
                    UpdateCustomerController controller = wrapper.getController();
                    controller.initialize(c);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setOnHiding(event1 -> {
                        loadCustomerTable();
                    });
                    stage.showAndWait();
                } catch (IOException e) {
                    LOG.error("Error:",e);
                    UserAlert.showError("Oooops, something went wrong. Could not open requested scene.");
                }

            }
        }
    }
}
