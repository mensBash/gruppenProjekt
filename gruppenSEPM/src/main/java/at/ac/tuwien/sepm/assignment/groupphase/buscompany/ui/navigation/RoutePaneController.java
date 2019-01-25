package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.navigation;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route.DisplayTimetableController;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route.SimpleRouteCell;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route.ModifyRouteController;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;

import java.lang.invoke.MethodHandles;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
public class RoutePaneController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ApplicationContext context;
    private RouteService service;
    private List<String> cityList;
    @FXML private Button addRoute;
    @FXML private ListView<Route> listView;
    @FXML private TextField tfFrom,tfTo;


    @Autowired
    public RoutePaneController(ApplicationContext context, RouteService service){
        this.service=service;
        this.context=context;
    }


    @FXML
    public void initialize(){
        listView.setCellFactory(param -> new SimpleRouteCell());
        try {
            service.initializeGraph();
            listView.getItems().clear();
            listView.getItems().addAll(service.getAllSimpleRoutes());
            cityList = service.getAllCitiesNames();
        } catch (ServiceException e) {
            LOG.error("Error:",e);
        }
        TextFields.bindAutoCompletion(tfFrom,cityList);
        TextFields.bindAutoCompletion(tfTo,cityList);
    }

    public void changeSceneAddRoute(){
        try {
            var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            Scene root = new Scene((Parent) fxmlLoader.load(
                getClass().getResourceAsStream("/fxml/addRoute.fxml")));
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(root);
            stage.show();
            stage.setOnHiding(event1 -> {
                initialize();
            });
        } catch (IOException e) {
            LOG.error("Error:",e);
        }
    }

    public void changeSceneModifyRoute(ActionEvent event){
        LOG.info("Modify button clicked");
        Route selectedItem = listView.getSelectionModel().getSelectedItem();
        if(selectedItem==null){
            LOG.warn("UI LAYER: Nothing was selected in table!");
            return;
        }
        try {
            var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            SpringFXMLLoader.FXMLWrapper<Object,ModifyRouteController> wrapper;
            wrapper = fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/modifyRoute.fxml"), ModifyRouteController.class);
            Scene root = new Scene((Parent) wrapper.getLoadedObject());
            Stage stage = new Stage();
            stage.setScene(root);
            ModifyRouteController controller = wrapper.getController();
            controller.initializeRoute(selectedItem);
            controller.setCheckFirstTime(false);
            stage.setResizable(false);
            controller.initialize();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(root);
            stage.showAndWait();
            listView.getItems().removeAll(listView.getItems());
            initialize();
        } catch (IOException e) {
            LOG.error("Error:",e);
        }
    }

    public void onTableClicked(MouseEvent event) {
        LOG.info("User clicked on table");
        if (event.getClickCount() == 2) {
            Route selectedItem =listView.getSelectionModel().getSelectedItem();
            if(selectedItem==null){
                LOG.warn("UI LAYER: User double clicked on table !Nothing was selected in table!");
                return;
            }
            LOG.info("UI LAYER: User double clicked on table row {} !" ,selectedItem);
            final var fxmlLoader = context.getBean(SpringFXMLLoader.class);

            try {
                SpringFXMLLoader.FXMLWrapper<Object,DisplayTimetableController> wrapper
                    = fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/displayTimetable.fxml"), DisplayTimetableController.class);
                Scene root = new Scene((Parent) wrapper.getLoadedObject());
                Stage stage = new Stage();
                stage.setScene(root);
                DisplayTimetableController controller = wrapper.getController();
                controller.initialize(selectedItem);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.showAndWait();
                stage.toFront();
            }catch (IOException e){
                LOG.error("Error: ", e);
                UserAlert.showWarning("Failed to displayed timetable");
            }
        }
    }

    public void onSearch(){
        Route r = new Route();
        if(tfTo.getText().trim().isEmpty() && tfFrom.getText().trim().isEmpty()){
            try {
                listView.getItems().clear();
                listView.getItems().addAll(service.getAllSimpleRoutes());
                return;
            } catch (ServiceException e) {
                UserAlert.showWarning(e.getMessage());
            }
        }
        if(!tfFrom.getText().trim().isEmpty()){
            r.setStartDestination(new City(tfFrom.getText().trim(),null));
        }
        if(!tfTo.getText().trim().isEmpty()){
            r.setEndDestination(new City(tfTo.getText().trim(),null));
        }
        try {
            List<Route> temp = service.filterSimpleRoutes(r);
            listView.getItems().clear();
            listView.getItems().addAll(temp);
        } catch (ServiceException e) {
            LOG.error("Error: ", e);
            UserAlert.showWarning(e.getMessage());
        }


    }

    public void deleteRoute() {
        Route selectedItem =listView.getSelectionModel().getSelectedItem();
        if(selectedItem==null){
            UserAlert.showWarning("Nothing selected!");
            LOG.warn("UI LAYER: User double clicked on remove !Nothing was selected in table!");
            return;
        }
        LOG.info("UI LAYER: User clicked on remove {} !" ,selectedItem);
        boolean temp = true;
        boolean sure = UserAlert.showConfirmation("Are you sure,you want to delete route " +selectedItem.getStartDestination().getName() +"-"+selectedItem.getEndDestination().getName()+"?");
        if(!sure){
            return;
        }
        try {
            Map<Timetable, Double> timetables = service.loadTimetable(selectedItem);
            Iterator<Map.Entry<Timetable, Double>> iterator = timetables.entrySet().iterator();
            String message ="Route can not be deleted for date(s): \n";
            String bookingsMsg="Bookings:\n";
            List<Long> nr;
            while(iterator.hasNext()){
                Map.Entry<Timetable, Double> t = iterator.next();
                if(!(nr=service.isBooked(t.getKey())).isEmpty()){
                    bookingsMsg+=nr+"\n";
                    message+=t.getKey().getDepartureTime().format(DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm"))+"\n";
                    temp=false;
                }else{
                    iterator.remove();
                }
            }

            service.updateTimetable(selectedItem,timetables);
            initialize();
            if(temp){
                UserAlert.showInfo("Route is successfully deleted!");
            }else{
                if(UserAlert.showConfirmation(message+"\n In order to delete this route entirely,please cancel trips on those dates!\n" +
                    "Do you want to list all booked trips on those dates?")){
                    UserAlert.showInfo(bookingsMsg);
                }
            }
        } catch (ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showError(e.getMessage());
        }


    }
}
