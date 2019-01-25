package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.booking;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.FareItem;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
import at.ac.tuwien.sepm.assignment.groupphase.exception.NotFoundException;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.util.SpringFXMLLoader;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class FareFinderMainController {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @FXML
    private Label lblCity;
    @FXML
    private BorderPane root;
    @FXML
    private ListView<Route> listView;

    private RouteService routeService;
    private ApplicationContext context;

    private Stage bookingStage;


    @Autowired
    public FareFinderMainController(RouteService routeService,ApplicationContext context){
        this.routeService=routeService;
        this.context=context;
    }

    public void initialize(){
        //listView=new ListView<>();
        listView.setId("fareList");
        listView.setCellFactory(lv -> {
            ListCell<Route> cell = new ListCell<Route>() {
                @Override
                protected void updateItem(Route item, boolean empty) {
                    super.updateItem(item, empty);
                    if(item!=null && !empty){
                        GridPane gridPane = new GridPane();
                        gridPane.getColumnConstraints().addAll( new ColumnConstraints( 100 ) , new ColumnConstraints( 100 ), new ColumnConstraints( 170 ),new ColumnConstraints(30) );
                        gridPane.getStyleClass().add("box");
                        Button btn = new Button(">");
                        btn.setPadding(new Insets(10,0,0,0));
                        btn.setOnAction(e->{
                            loadScene(item);
                        });
                        btn.setId("fareSelect");
                        Label from =createLabel("from "+item.getPrice()+" Eur","lvLbl");
                        from.setPadding(new Insets(0,0,0,40));
                        gridPane.add(createLabel(item.getEndDestination().getName(),"lvLbl"),0,0);
                        gridPane.add(createLabel(item.getDepartureDate().getMonth().toString(),"lvLbl"),1,0);
                        gridPane.add(from,2,0);
                        gridPane.add(btn,3,0);
                        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                        setGraphic(gridPane);
                    }else{
                        setContentDisplay(ContentDisplay.TEXT_ONLY);
                        setText(null);
                    }

                }
            };
            cell.setOnMouseClicked(e -> {
                if (!cell.isEmpty()) {
                    e.consume();
                }
            });
            return cell;
        });

    }


    public void init (Map<FareItem,Route>r,Stage bookingStage) {
        this.bookingStage=bookingStage;
        listView.getItems().clear();
        listView.getItems().addAll(r.values());
        for(Route route : r.values()){
            lblCity.setText(route.getStartDestination().getName()+" - ");
            break;
        }

    }

   private void loadScene(Route r) {

       try {
           final var fxmlLoader = context.getBean(SpringFXMLLoader.class);
           SpringFXMLLoader.FXMLWrapper<Object,FareFinderChartController> wrapper=
               fxmlLoader.loadAndWrap(getClass().getResourceAsStream("/fxml/fareFinderChart.fxml"),FareFinderChartController.class);
           Scene root = new Scene((Parent)wrapper.getLoadedObject());
           FareFinderChartController controller = wrapper.getController();
           Stage stage = (Stage) listView.getScene().getWindow();
           controller.init(r,stage.getScene(),bookingStage);
           stage.setOnHiding(e->{controller.initSelected();});
           stage.setScene(root);
       } catch (IOException e) {
          LOG.error("Error:",e);
          UserAlert.showWarning("Ooops , window couldn't be loaded!");
       }



    }


    private Label createLabel(String text , String styleClass){
        Label l = new Label(text);
        l.getStyleClass().add(styleClass);
        return l;
    }
}
