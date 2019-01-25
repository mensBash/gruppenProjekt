package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.util.Map;

@Controller
public class FareFinderChartController {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FXML
    private Button btnReturn;
    @FXML
    private HBox bottom;
    @FXML
    private BorderPane root;
    private BarChart<String,Number> firstChart;
    private BarChart<String,Number> secondChart;
    private Route searchRoute;
    private RouteService routeService;
    private XYChart.Data<String,Number> selected;
    private XYChart.Data<String,Number> secondSelected;
    private Scene backScene;
    private Stage bookingStage;
    private static final String STYLE_ONE = "-fx-background-color: efaa21;";
    private static final String STYLE_TWO = "-fx-background-color: CFFFB0;";

    private ApplicationContext context;

    @Autowired
    public FareFinderChartController(RouteService routeService,ApplicationContext context) {
        this.routeService = routeService;
        this.context=context;
    }

    public void initialize(){

    }

    public void init(Route r, Scene scene,Stage bookingStage){
        this.bookingStage=bookingStage;
        this.backScene=scene;
        this.searchRoute=r;
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Date");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Price");

        CategoryAxis x1Axis = new CategoryAxis();
        x1Axis.setLabel("Date");

        NumberAxis y1Axis = new NumberAxis();
        y1Axis.setLabel("Price");

        secondChart = new BarChart<>(x1Axis, y1Axis);
        firstChart = new BarChart<>(xAxis, yAxis);

        root.setCenter(firstChart);
        createSeries(r, firstChart,false);
    }

    private void createSeries(Route r,BarChart<String,Number> barChart, boolean returnRoute){
        XYChart.Series<String,Number> dataSeries1 = new XYChart.Series<>();
        dataSeries1.setName(r.getStartDestination().getName()+"-"+r.getEndDestination().getName()+"("+r.getDepartureDate().getMonth().toString()+")");


        try {
            Map<LocalDate,Route> res = routeService.getRoutesForMonth(r);

            for(Map.Entry<LocalDate,Route> e : res.entrySet()){

                XYChart.Data<String,Number> data =new XYChart.Data<>(e.getKey().getDayOfMonth()+"."+e.getKey().getMonth(), e.getValue().getPrice(),e.getValue());
                dataSeries1.getData().add(data);

            }
        } catch (ServiceException e) {
            LOG.error("Error:",e);
            UserAlert.showError(e.getMessage());
        }

        barChart.getData().add(dataSeries1);
        for (XYChart.Series<String,Number> serie: barChart.getData()){
            for (XYChart.Data<String, Number> item: serie.getData()){
                item.getNode().setStyle(STYLE_ONE);
                item.getNode().setOnMousePressed(e->{
                    if(!returnRoute){
                        if(selected!=null){
                            selected.getNode().setStyle(STYLE_ONE);
                        }
                        selected=item;
                        selected.getNode().setStyle(STYLE_TWO);
                    }else{
                        if(secondSelected!=null){
                            secondSelected.getNode().setStyle(STYLE_ONE);
                        }
                        secondSelected=item;
                        secondSelected.getNode().setStyle(STYLE_TWO);
                    }


                });
                Tooltip tooltip = new Tooltip(((Route)item.getExtraValue()).getPrice()+" Eur");
                tooltip.setShowDelay(Duration.ZERO);
                Tooltip.install(item.getNode(),tooltip);
            }
        }
    }

    public void onBook() {
        if(selected==null ){
            UserAlert.showWarning("Please select one item!");
            return;
        }

        try{
            this.bookingStage.close();
            final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
            SpringFXMLLoader.FXMLWrapper<Object,CreateBookingController> wrapper=
                fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/createBooking.fxml"),CreateBookingController.class);
            Scene root = new Scene((Parent)wrapper.getLoadedObject());
            Stage stage = new Stage();
            stage.setScene(root);
            CreateBookingController controller = wrapper.getController();
            controller.setUpFareFinder((Route)selected.getExtraValue(),(secondSelected==null)?null:(Route)secondSelected.getExtraValue());
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
            selected=null;
            secondSelected=null;
            ((Stage)this.firstChart.getScene().getWindow()).close();
        }catch (IOException e){
            UserAlert.showError("Oops couldn't load booking window.");
            LOG.error("Error:",e);
        }catch (ServiceException e){
            UserAlert.showError(e.getMessage());
            LOG.error("Error:",e);
        }

    }

    public Route getSelected(){
        if(selected!=null){
            return (Route) selected.getExtraValue();
        }else{
            return null;
        }
    }

    public void onBack() {
        Stage stage = (Stage) root.getScene().getWindow();
        stage.setScene(backScene);
    }

    public void onReturn(ActionEvent actionEvent) {


        VBox box = new VBox(20);
        box.getChildren().addAll(firstChart,secondChart);
        root.setCenter(box);
        Route r = new Route(searchRoute.getEndDestination(),searchRoute.getStartDestination(),"",searchRoute.getPrice(),53,0.0);
        r.setDepartureDate(searchRoute.getDepartureDate());
        r.setWithTransfer(true);
        createSeries(r,secondChart,true);
        btnReturn.setVisible(false);
    }

    public void initSelected(){
        selected=null;
        secondSelected=null;
    }
}
