package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Entry;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.TicketCounter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;

import java.lang.invoke.MethodHandles;
import java.util.List;

@Controller
public class DisplayTicketsStatisticsController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FXML
    private TableColumn<TicketCounter,String> columnStartDestination, columnEndDestination;
    @FXML
    private TableColumn<TicketCounter,Integer> columnOutward, columnReturn;
    @FXML
    private TableView<TicketCounter> tableTicketStatistics;

    public void initialize(List<TicketCounter> list){
        columnStartDestination.setCellValueFactory(new PropertyValueFactory<>("departureCity"));
        columnEndDestination.setCellValueFactory(new PropertyValueFactory<>("arrivalCity"));
        columnOutward.setCellValueFactory(new PropertyValueFactory<>("oneWayTicketsCounter"));
        columnReturn.setCellValueFactory(new PropertyValueFactory<>("returnTicketsCounter"));
        ObservableList<TicketCounter> data = FXCollections.observableArrayList(list);
        tableTicketStatistics.setItems(data);
    }
}
