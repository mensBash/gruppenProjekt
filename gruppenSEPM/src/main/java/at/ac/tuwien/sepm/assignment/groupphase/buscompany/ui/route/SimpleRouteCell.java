package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class SimpleRouteCell extends ListCell<Route> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


    @FXML private HBox root;
    public SimpleRouteCell(){

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listCell.fxml"));

            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }catch(IOException e){
            LOG.error("Error:",e);
        }
    }

    @FXML
    public void initialize(){

    }

    private VBox createConnection(Route r){
        VBox pane = new VBox();
        pane.setPadding(new Insets(0,0 ,10,0));
        pane.setAlignment(Pos.CENTER);
        HBox time = new HBox(20);

        Line line = new Line();
        line.setStroke(Color.valueOf("efaa21"));
        pane.getChildren().add(line);
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(50);
        line.setEndY(0);
        return pane;
    }
    @Override
    protected void updateItem(Route item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty && item!=null){
            root.getChildren().clear();
            root.getChildren().add(createLabelCity(item.getStartDestination()));
            root.getChildren().add(createConnection(item));
            root.getChildren().add(createLabelCity(item.getEndDestination()));
            root.getChildren().add(
                createInfoLabel("Distance: "+item.getDistance()+" km")
            );

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

        }else{
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
    }

    private boolean existTimeTable(List<Route> routes){
        for(Route r : routes){
            if(r.getTimetable() == null){
                return false;
            }
        }
        return true;
    }

    private Label createLabelTime(LocalDateTime time){
        Label label = new Label(time.format(DateTimeFormatter.ofPattern("HH:mm")));
        label.getStyleClass().add("time");
        label.setFont(Font.font(10));
        return label;
    }
    private Label createLabelCity(City city){
        Label label = new Label(StringUtils.capitalize(city.getName()));
        label.setPrefWidth(100);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setAlignment(Pos.CENTER);
        label.getStyleClass().add("city");
        label.setFont(Font.font(16));
        return label;
    }
    private Label createInfoLabel(String text){
        Label label = new Label(text);
        //label.getStyleClass().add("city");
        label.setFont(Font.font(14));
        label.setPadding(new Insets(0,0,0,50));
        return label;
    }
}
