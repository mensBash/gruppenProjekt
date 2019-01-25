
package at.ac.tuwien.sepm.assignment.groupphase.buscompany.ui.navigation;

import javafx.application.Preloader;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

@Component
public class StartPageController extends Preloader {
    private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    Stage stage;

    private Scene createPreloaderScene() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxml/startPage.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(loader.load());

        } catch (IOException e) {
            LOG.error("Error:",e);
        }
        return scene;
    }

    @Override
    public void start(Stage stage) throws Exception {
        this.stage = stage;
        Scene scene = createPreloaderScene();
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.getIcons().add(new Image(String.valueOf(getClass().getClassLoader().getResource("images/logo1.png"))));
        stage.show();
    }

    @Override
    public void handleProgressNotification(ProgressNotification pn) {
    }

    @Override
    public void handleStateChangeNotification(StateChangeNotification evt) {
        //ignore, hide after application signals it is ready
    }


    @Override
    public void handleApplicationNotification(PreloaderNotification pn) {
        stage.hide();
    }
}
