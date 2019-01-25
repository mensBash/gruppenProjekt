package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.navigation;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.TicketCounter;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.StatisticsService;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.DisplayTicketsStatisticsController;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
public class StatisticsPaneController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private ApplicationContext context;
    private StatisticsService statisticsService;
    private List<TicketCounter> numberOfTickets;

    @FXML
    private ChoiceBox choiceBoxStatistics;
    @FXML
    private DatePicker fromDate;
    @FXML
    private DatePicker toDate;
    @FXML
    private Button buttonGenerate;
    @FXML
    private LineChart lineChart;
    @FXML
    private CategoryAxis lineXAxis;
    @FXML
    private NumberAxis lineYAxis;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private Label activeBookingsLabel;
    @FXML
    private Label registeredCustomersLabel;
    @FXML
    private Label bookedSeatsLabel;
    @FXML
    private CategoryAxis xAxis;
    @FXML
    private NumberAxis yAxis;
    @FXML
    private BarChart barChart;
    @FXML
    private Button buttonDetails;
    @FXML
    private PieChart pieChart;
    @FXML
    private Label labelRoute;
    @FXML
    private ChoiceBox<String> choiceBoxRoute;

    ObservableList<String> choiceBoxList = FXCollections.observableArrayList();


    @Autowired
    public StatisticsPaneController(ApplicationContext context, StatisticsService statisticsService){
        this.context=context;
        this.statisticsService = statisticsService;
    }


    public void initialize(){
        progressIndicator.setMaxHeight(240);
        progressIndicator.setMaxWidth(120);
        choiceBoxList.clear();
        choiceBoxList.add("Sold tickets per route");
        choiceBoxList.add("Weekly profit");
        choiceBoxList.add("Sold tickets per weekday");
        choiceBoxStatistics.setItems(choiceBoxList);
        choiceBoxStatistics.setValue(choiceBoxList.get(0));
        fromDate.setValue(LocalDate.now());
        toDate.setValue(LocalDate.now().plusMonths(3));
        initializeChoiceboxRoutes();
        buttonDetails.setDisable(false);
        buttonDetails.setVisible(true);
        try {
            registeredCustomersLabel.setText(String.valueOf(statisticsService.countRegisteredCustomers()));
            bookedSeatsLabel.setText(String.valueOf(statisticsService.calculateCapacityUtilization())+"%");
            activeBookingsLabel.setText(String.valueOf(statisticsService.countActiveBookings()));
            createBarChart(LocalDateTime.of(LocalDate.now(),LocalTime.of(0,0)),LocalDateTime.of(LocalDate.now().plusMonths(3),LocalTime.of(0,0)));
            barChart.setVisible(true);
        } catch (ServiceException e) {
            LOG.error("Error: ", e);
            UserAlert.showError(e.getMessage());
        }
    }

    private void initializeChoiceboxRoutes(){
        List<String> list = new ArrayList<>();
        list.add("All routes");
        for (Route route : statisticsService.getRoutes()){
            list.add(route.getStartDestination().getName() + " - " + route.getEndDestination().getName());
        }
        choiceBoxRoute.setItems(FXCollections.observableArrayList(list));
        choiceBoxRoute.setValue(list.get(0));
    }

    @FXML
    private void onGenerateChart(ActionEvent event) {
        if (!fromDate.getValue().isBefore(toDate.getValue())) {
            UserAlert.showWarning("Please select valid time interval!");
        } else {
            if (choiceBoxStatistics.getValue().equals(choiceBoxList.get(0))) {
                createBarChart(LocalDateTime.of(fromDate.getValue(), LocalTime.of(0, 0)), LocalDateTime.of(toDate.getValue(), LocalTime.of(0, 0)));
                barChart.setVisible(true);
                buttonDetails.setVisible(true);
                buttonDetails.setDisable(false);
                lineChart.setVisible(false);
                pieChart.setVisible(false);
                labelRoute.setVisible(false);
                choiceBoxRoute.setVisible(false);
            } else if (choiceBoxStatistics.getValue().equals(choiceBoxList.get(1))) {
                generateLineChart();
                barChart.setVisible(false);
                buttonDetails.setDisable(true);
                buttonDetails.setVisible(false);
                lineChart.setVisible(true);
                pieChart.setVisible(false);
                labelRoute.setVisible(true);
                choiceBoxRoute.setVisible(true);
            } else if (choiceBoxStatistics.getValue().equals(choiceBoxList.get(2))) {
                generatePieChart();
                buttonDetails.setVisible(false);
                buttonDetails.setDisable(true);
                barChart.setVisible(false);
                lineChart.setVisible(false);
                pieChart.setVisible(true);
                labelRoute.setVisible(false);
                choiceBoxRoute.setVisible(false);
            }
        }

    }

    private void createBarChart(LocalDateTime from, LocalDateTime to){
        LOG.info("Generating BarChart");
        List<String> routes = new ArrayList<>();
        barChart.getData().clear();
        try {
            numberOfTickets = statisticsService.getNumberOfTicketsPerRoute(from, to);
            int upperBound = 0;
            BarChart.Series dataSeries1 = new BarChart.Series();
            BarChart.Series dataSeries2 = new BarChart.Series();
            int length = 0;
            for(TicketCounter t: numberOfTickets){
                if(upperBound < t.getOneWayTicketsCounter() || upperBound < t.getReturnTicketsCounter()){
                    if(t.getOneWayTicketsCounter() < t.getReturnTicketsCounter()){
                        upperBound = t.getReturnTicketsCounter();
                    }else{
                        upperBound = t.getOneWayTicketsCounter();
                    }
                }
                if(length < 5){
                    dataSeries1.getData().add(new BarChart.Data(t.getDepartureCity()+" - "+t.getArrivalCity(), t.getOneWayTicketsCounter()));
                    dataSeries2.getData().add(new BarChart.Data(t.getDepartureCity()+" - "+t.getArrivalCity(),t.getReturnTicketsCounter()));
                    routes.add(t.getDepartureCity()+" - "+t.getArrivalCity());
                    length++;
                }else{
                    break;
                }
            }
            xAxis.getCategories().clear();
            xAxis.setCategories(FXCollections.observableArrayList(routes));
            yAxis.setUpperBound(upperBound + 2);
            dataSeries1.setName("Outward");
            dataSeries2.setName("Return");
            barChart.getData().addAll(dataSeries1, dataSeries2);
        } catch (ServiceException e) {
            LOG.error("Error: ",e);
            UserAlert.showError(e.getMessage());
        }
    }

    public void onShowDetails(ActionEvent event){
        LOG.info("Displaying details about sold tickets per route");
        final var fxmlLoader = context.getBean(SpringFXMLLoader.class);

        try {
            SpringFXMLLoader.FXMLWrapper<Object,DisplayTicketsStatisticsController> wrapper
                = fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/displayTicketsStatisticsDetail.fxml"), DisplayTicketsStatisticsController.class);
            Scene root = new Scene((Parent) wrapper.getLoadedObject());
            Stage stage = new Stage();
            stage.setScene(root);
            DisplayTicketsStatisticsController controller = wrapper.getController();
            controller.initialize(numberOfTickets);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            stage.toFront();
        }catch (IOException e){
            LOG.error("Error: ", e);
            UserAlert.showWarning("Failed to displayed tickets statistics details");
        }
    }

    private void generateLineChart(){
        LOG.info("Generating LineChart");
        lineChart.getData().clear();
        lineXAxis.getCategories().clear();
        List<String> weekList = new ArrayList<>();
        LocalDate dateFrom = fromDate.getValue();
        LocalDate dateTo = toDate.getValue();
        int weekFrom = dateFrom.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        int weekTo = dateTo.get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
        int numberOfWeeks = 0;
        if(weekFrom < weekTo) {
            numberOfWeeks = weekTo - weekFrom;
        }else if (weekFrom > weekTo) {
            int endWeek = LocalDate.of(dateFrom.getYear(), 12, 25).get(WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear());
            numberOfWeeks = (endWeek - weekFrom) + weekTo;
        }
        boolean first = true;
        List<Route> routeList = new ArrayList<>();
        if (choiceBoxRoute.getValue().equals("All routes")){
            routeList.addAll(statisticsService.getRoutes());
        }else {
            String[] split = choiceBoxRoute.getValue().split(" - ");
            Route route = new Route();
            route.setStartDestination(new City(split[0].trim(), ""));
            route.setEndDestination(new City(split[1].trim(), ""));
            routeList.add(route);
        }

        for(Route route : routeList){
            XYChart.Series series =  new XYChart.Series();
            for(int i = 0; i <= numberOfWeeks; i++) {
                try {
                    if (first) {
                        weekList.add("Week " + (weekFrom + i));
                    }
                    if (i == 0) {
                        if (numberOfWeeks == 0){
                            series.getData().add(new XYChart.Data(weekList.get(i), statisticsService.getPriceForRoute(route, dateFrom, dateTo)));
                            if (first) {
                                lineXAxis.setCategories(FXCollections.observableArrayList(weekList));
                                first = false;
                            }
                        }else{
                            series.getData().add(new XYChart.Data(weekList.get(i), statisticsService.getPriceForRoute(route, dateFrom, dateFrom.with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7))));
                        }
                    } else if (i == numberOfWeeks) {
                        series.getData().add(new XYChart.Data(weekList.get(i), statisticsService.getPriceForRoute(route, dateFrom.plusDays(7 * i).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1), dateTo)));
                        if (first) {
                            lineXAxis.setCategories(FXCollections.observableArrayList(weekList));
                            first = false;
                        }
                    } else {
                        series.getData().add(new XYChart.Data(weekList.get(i), statisticsService.getPriceForRoute(route, dateFrom.plusDays(7 * i).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1), dateFrom.plusDays(7 * i).with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 7))));
                    }
                } catch (ServiceException e) {
                    LOG.error("Error: ", e);
                    UserAlert.showError(e.getMessage());
                }
            }
            series.setName(route.getStartDestination() + " - " + route.getEndDestination());
            lineChart.getData().add(series);
        }
    }

    private void generatePieChart(){
        LOG.info("Generating PieChart");
        List<PieChart.Data> pieChartData = new ArrayList<>();
        int[] weeklyData = new int[7];
        for (LocalDate dateFrom = fromDate.getValue(); dateFrom.isBefore(toDate.getValue()); dateFrom = dateFrom.plusDays(1)){
            try {
                weeklyData[dateFrom.getDayOfWeek().getValue() - 1] += statisticsService.getNumberOfBookedSeats(dateFrom);
            } catch (ServiceException e) {
                LOG.error("Error: ", e);
                UserAlert.showError(e.getMessage());
            }
        }
        for (int i = 0; i < 7; i++){
            pieChartData.add(new PieChart.Data(DayOfWeek.of(i + 1).toString() + " - " + weeklyData[i], weeklyData[i]));
        }
        pieChart.setData(FXCollections.observableArrayList(pieChartData));
    }

}
