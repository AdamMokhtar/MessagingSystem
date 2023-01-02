package graduation.client.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Objects;

public class ClientMain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        final Logger logger = LoggerFactory.getLogger(ClientMain.class.getName());

        final String fxmlFileName = "client.fxml";
        URL url  = getClass().getClassLoader().getResource(fxmlFileName );
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            ClientController controller = new ClientController();
            loader.setController(controller);
            Parent root = loader.load();

            primaryStage.setTitle("Approval Client");
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/client.png"))));
            primaryStage.setScene(new Scene(root, 500, 300));
            primaryStage.setOnCloseRequest(t -> {
                logger.info("Closing loan-client .....");
                controller.stop();
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
