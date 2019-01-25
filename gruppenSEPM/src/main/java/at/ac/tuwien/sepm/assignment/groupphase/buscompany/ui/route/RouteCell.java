package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.route;

import at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.UserAlert;
import at.ac.tuwien.sepm.assignment.groupphase.exception.ServiceException;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.City;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.dto.Route;
import at.ac.tuwien.sepm.assignment.groupphase.buscompany.service.RouteService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RouteCell extends ListCell<List<Route>> {

    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private RouteService service;

    @FXML private HBox root;

    public RouteCell(RouteService service){
        this.service=service;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/listCell.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }catch(IOException e){
            UserAlert.showError("List of items couldn't be shown!");
            LOG.error("Error:",e);
        }
    }

    private VBox createConnection(Route r){
        VBox pane = new VBox();
        pane.setPadding(new Insets(0,0 ,10,0));
        pane.setAlignment(Pos.CENTER);
        HBox time = new HBox(20);
        Line line = new Line();
        line.setStroke(Color.valueOf("efaa21"));
        Label dep= createLabelTime(r.getTimetable().getDepartureTime());
        Label arr= createLabelTime(r.getTimetable().getArrivalTime());
        time.getChildren().addAll(dep,arr);
        pane.getChildren().add(time);
        pane.getChildren().add(line);
        Label date = new Label(r.getTimetable().getDepartureTime().format(DateTimeFormatter.ofPattern("dd.MM.YYYY")));
        date.setFont(Font.font(10));
        pane.getChildren().add(date);
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(50);
        line.setEndY(0);
        return pane;
    }
    @Override
    protected void updateItem(List<Route> item, boolean empty) {
        super.updateItem(item, empty);
        if(!empty && existTimeTable(item)){
            root.getChildren().clear();

            try {
                VBox box =new VBox(5);
                box.setAlignment(Pos.CENTER_LEFT);
                box.getChildren().addAll(
                    createAdditionalInfo("Total price",service.calculatePrice(item)+""),
                    createAdditionalInfo("Total distance",service.calculateDistance(item)+"")
                );
                root.getChildren().add(box);
            } catch (ServiceException e) {
                LOG.error("Error:",e);
            }
            for(int i = 0 ; i < item.size();i++){
                if(i==0)
                    root.getChildren().add(createLabelCity(item.get(i).getStartDestination()));
                root.getChildren().add(createConnection(item.get(i)));
                root.getChildren().add(createLabelCity(item.get(i).getEndDestination()));
            }


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
        label.getStyleClass().add("city");
        label.setFont(Font.font(16));
        return label;
    }

    private VBox createAdditionalInfo(String top ,String bottom){
        Label lblTop = new Label(top);
        lblTop.setFont(Font.font(10));
        Label lblBottom = new Label(bottom);
        lblBottom.setStyle("-fx-font-weight: bold");
        lblBottom.setFont(Font.font(9));
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(0,0,0,20));
        box.getChildren().addAll(lblTop,lblBottom);
        return box;
    }
}
