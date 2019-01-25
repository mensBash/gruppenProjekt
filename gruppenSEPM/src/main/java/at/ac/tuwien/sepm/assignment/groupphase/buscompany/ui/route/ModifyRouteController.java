package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Entry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.BookingService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.TextFieldListener;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import at.ac.tuwien.sepm.assignment.groupphase.exception.InvalidRouteException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
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
public class ModifyRouteController extends AddOrModifyController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private boolean checkFirstTime = false;

    @Autowired
    private ApplicationContext context;
    private RouteService routeService;
    private BookingService bookingService;

    @Autowired
    public ModifyRouteController(RouteService routeService, BookingService bookingService) {
        super(routeService, bookingService);
        this.routeService = routeService;
        this.bookingService = bookingService;
    }

    @Override
    public void initialize() {
        super.initialize();
        mainLabel.setText("Modify route");
        nextButton.setDisable(false);
    }

    @Override
    protected void addListeners(){
        map.clear();
        String regexDouble = "^?([0-9]+(?:[\\.][0-9]*)?|\\.[0-9]+)$";
        String regex = "^[a-zA-Z0-9_ ][a-zA-Z0-9_ ]*$";
        fieldDistance.textProperty().addListener(new TextFieldListener(fieldDistance, regexDouble, false, map, nextButton));
        fieldBusNr.textProperty().addListener(new TextFieldListener(fieldBusNr, regex, false, map, nextButton));
    }

    @Override
    public void next(){
        super.next();
    }

    @Override
    protected void loadScene(String scene, Button button) throws IOException {
        String helpScene = "";
        if (scene.equals("/fxml/nextScene.fxml")){
            helpScene = "/fxml/nextSceneModify.fxml";
        }else if (scene.equals("/fxml/addRoute.fxml")){
            helpScene = "/fxml/modifyRoute.fxml";
        }
        Stage stage = (Stage) button.getScene().getWindow();
        final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
        root = new Scene((Parent)fxmlLoader.load(
            getClass().getResourceAsStream(helpScene)));
        stage.setScene(root);
    }

    @Override
    protected boolean addRoutes(Route route) throws ServiceException, InvalidRouteException {
        if (!timetableList.isEmpty()) {
            routeService.updateTimetable(route, timetableList);
            routeService.updateRoute(route);
            return true;
        }else {
            if (UserAlert.showConfirmation("The route will be deleted, because the timetable list is empty")){
                routeService.updateTimetable(route, timetableList);
                return true;
            }else {
                return false;
            }
        }
    }

    @Override
    public void onRemoveClicked(ActionEvent event) {
        LOG.info("Remove button clicked");
        List<Entry> selected = tvTimetable.getSelectionModel().getSelectedItems();
        List<Timetable> timetables = new ArrayList<>();
        for (Entry e: selected) {
            Timetable timetable = new Timetable(LocalDateTime.parse(e.getDepTime(),DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                LocalDateTime.parse(e.getArrTime(), DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")));
            timetable.setId(e.getRid());
            timetables.add(timetable);
        }
        LOG.debug("list: " + timetables);
        List<Entry> toBeRemoved = new ArrayList<>();
        try {
            boolean temp =true;
            String msg="Route can not be deleted for date(s): \n";
            String bookingsMsg="Bookings:";
            for (Timetable timetable: timetables) {
                List<Long> nr ;
                if (!(nr=routeService.isBooked(timetable)).isEmpty()){
                    temp=false;
                    bookingsMsg+=nr+"\n";
                    msg+=timetable.getDepartureTime().format(DateTimeFormatter.ofPattern("dd.MM.yyy HH:mm"))+"\n";
                }else {
                    for (Entry e: selected) {
                        if (e.getRid().equals(timetable.getId())){
                            toBeRemoved.add(e);
                            break;
                        }
                    }
                    timetableList.remove(timetable);
                }

                if (timetableList.isEmpty()){
                    removeButton.setVisible(false);
                }
            }
            if(!temp){
                if(UserAlert.showConfirmation(msg+"\n In order to delete this route entirely,please cancel trips on those dates!\n" +
                    "Do you want to list all booked trips on those dates?")){
                    UserAlert.showInfo(bookingsMsg);
                }
            }
            LOG.debug("removing from table: " + toBeRemoved);
            tvTimetable.getItems().removeAll(toBeRemoved);
        } catch (ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showWarning("No item is selected!");
        }

    }

    public void initializeRoute(Route route){
        if (!checkFirstTime) {
            try {
                timetableList.clear();
                timetableList.putAll(routeService.loadTimetable(route));
            } catch (ServiceException e) {
                UserAlert.showError(e.getMessage());
                LOG.error("Error:",e);
            }
            fieldBusNr.setText(route.getBusNumber());
            fieldStartDestCity.setText(route.getStartDestination().getName());
            fieldStartDestCity.setDisable(true);
            fieldStartDestCountry.setText(route.getStartDestination().getCountry());
            fieldStartDestCountry.setDisable(true);
            fieldEndDestCity.setText(route.getEndDestination().getName());
            fieldEndDestCity.setDisable(true);
            fieldEndDestCountry.setText(route.getEndDestination().getCountry());
            fieldEndDestCountry.setDisable(true);
            fieldDistance.setText(route.getDistance().toString());
            checkFirstTime = true;
        }
    }

    public void setCheckFirstTime(boolean checkFirstTime) {
        this.checkFirstTime = checkFirstTime;
    }
}
