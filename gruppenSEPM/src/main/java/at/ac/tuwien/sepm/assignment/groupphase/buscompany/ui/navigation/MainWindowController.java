package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.navigation;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.Optional;


@Controller
public class MainWindowController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private ApplicationContext context;

    @FXML
    private ImageView imageLogo;
    @FXML
    private ImageView bookingIcon;
    @FXML
    private ImageView customerIcon;
    @FXML
    private ImageView routeIcon;
    @FXML
    private ImageView statistics_icon;
    @FXML
    private HBox bookingHbox;
    @FXML
    private HBox customerHbox;
    @FXML
    private HBox routeHbox;
    @FXML
    private HBox statisticsHbox;

    @FXML
    private BorderPane mainPane;

    private RouteService routeService;

    private static final String RESOURCE_MAIN_CSS = "style/sepm.css";
    private static final String RESOURCE_HELP_CSS = "style/hboxStyle.css ";

    @Autowired
    public MainWindowController(RouteService routeService) {
        this.routeService = routeService;
    }

    @FXML
    private void initialize(){
        Image img_logo = new Image(String.valueOf(getClass().getClassLoader().getResource("images/logo1.png")));
        imageLogo.setImage(img_logo);
        Image img_booking = new Image(String.valueOf(getClass().getClassLoader().getResource("images/booking.png")));
        bookingIcon.setImage(img_booking);
        Image img_route = new Image(String.valueOf(getClass().getClassLoader().getResource("images/route.png")));
        routeIcon.setImage(img_route);
        Image img_customer = new Image(String.valueOf(getClass().getClassLoader().getResource("images/customers.png")));
        customerIcon.setImage(img_customer);
        Image img_statistics = new Image(String.valueOf(getClass().getClassLoader().getResource("images/statistics.png")));
        statistics_icon.setImage(img_statistics);
        bookingHbox.getStylesheets().clear();
        bookingHbox.getStylesheets().add(RESOURCE_HELP_CSS);
        mainPane.setCenter(loadContent("/fxml/bookingPane.fxml"));

        try {
            routeService.initializeGraph();
        } catch (ServiceException e) {
            LOG.error("Error:",e);
        }
    }

    public void onBookingClicked(){
        LOG.info("User clicked on bookings in sidebar");
        mainPane.setCenter(loadContent("/fxml/bookingPane.fxml"));
        setActive(bookingHbox, customerHbox, routeHbox, statisticsHbox);
    }

    public void onCustomerClicked(){
        LOG.info("User clicked on customers in sidebar");
        mainPane.setCenter(loadContent("/fxml/customerPane.fxml"));
        setActive(customerHbox, bookingHbox, routeHbox, statisticsHbox);
    }

    public void onRouteClicked(){
       LOG.info("User clicked on routes in sidebar");
       mainPane.setCenter(loadContent("/fxml/routePane.fxml"));
       setActive(routeHbox, customerHbox, bookingHbox, statisticsHbox);
    }

    public void onStatisticsClicked(){
        LOG.info("User clicked on statistics in sidebar");
        mainPane.setCenter(loadContent("/fxml/statisticsPane.fxml"));
        setActive(statisticsHbox, customerHbox, bookingHbox, routeHbox);
    }

    public void exit(){
        LOG.info("User clicked Exit");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType yes = new ButtonType("Yes");
        ButtonType no = new ButtonType("No");
        alert.getButtonTypes().setAll(yes, no);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get()==yes) {
            Platform.exit();
        }
    }

    private Node loadContent(String path){
        Node parent = null;
        var fxmlLoader = context.getBean(SpringFXMLLoader.class);
        try {
            parent = (Node) fxmlLoader.load(
                getClass().getResourceAsStream(path));
        } catch (IOException e) {
            LOG.error("Error:",e);
        }
        return parent;
    }

    private void setActive(HBox active, HBox inactive1, HBox inactive2, HBox inactive3){
        active.getStylesheets().clear();
        active.getStylesheets().add(RESOURCE_HELP_CSS);
        inactive1.getStylesheets().clear();
        inactive1.getStylesheets().add(RESOURCE_MAIN_CSS);
        inactive2.getStylesheets().clear();
        inactive2.getStylesheets().add(RESOURCE_MAIN_CSS);
        inactive3.getStylesheets().clear();
        inactive3.getStylesheets().add(RESOURCE_MAIN_CSS);
    }


}
