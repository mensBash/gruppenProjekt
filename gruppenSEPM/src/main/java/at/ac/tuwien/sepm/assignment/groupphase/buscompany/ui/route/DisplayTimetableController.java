package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Entry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Timetable;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

import java.lang.invoke.MethodHandles;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class DisplayTimetableController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FXML
    private TableColumn<Entry,Double> columnPrice;
    @FXML
    private TableColumn<Entry,String> columnDeparture, columnArrival;
    @FXML
    private TableView<Entry> tableTimetable;
    @FXML
    private Label labelFrom;
    @FXML
    private Label labelTo;

    private RouteService routeService;

    @Autowired
    public DisplayTimetableController(RouteService routeService) {
        this.routeService = routeService;
    }

    public void initialize(Route route){
        labelFrom.setText(StringUtils.capitalize(route.getStartDestination().getName()));
        labelTo.setText(StringUtils.capitalize(route.getEndDestination().getName()));
        columnDeparture.setCellValueFactory(new PropertyValueFactory<>("depTime"));
        columnArrival.setCellValueFactory(new PropertyValueFactory<>("arrTime"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        List<Entry> timetableList = new ArrayList<>();
        try {
            for(Map.Entry<Timetable,Double> e:routeService.loadTimetable(route).entrySet()){
                timetableList.add(new Entry(e.getKey().getId(), e.getKey().getDepartureTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"))
                    ,e.getKey().getArrivalTime().format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm")),
                    e.getValue()));

            }
            ObservableList<Entry> data = FXCollections.observableArrayList(timetableList);
            tableTimetable.setItems(data);
        } catch (ServiceException e) {
            UserAlert.showError(e.getMessage());
            LOG.error("Error:",e);
        }
    }


}
