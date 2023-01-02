package graduation.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.JMSException;
import java.io.IOException;
import java.net.URL;

import static javafx.application.Application.launch;

public class GraduationMain extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        final Logger logger = LoggerFactory.getLogger(GraduationMain.class.getName());

        final String fxmlFileName = "graduation_hub.fxml";
        URL url  = getClass().getClassLoader().getResource(fxmlFileName);
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            GraduationController controller = new GraduationController();
            loader.setController(controller);
            Parent root = loader.load();

            primaryStage.setTitle("Graduation Hub");
            //primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/client.png")));
            primaryStage.setScene(new Scene(root, 500, 300));
            primaryStage.setOnCloseRequest(t -> {
                logger.info("Closing graduation-hub .....");
                try {
                    controller.stop();
                } catch (JMSException e) {
                    e.printStackTrace();
                }
                Platform.exit();
                System.exit(0);
            });


            primaryStage.show();
        }else {
            logger.error("Could not load frame from "+fxmlFileName);
        }
    }
    public static void main(String[] args) {
        launch(args);
    }
}
