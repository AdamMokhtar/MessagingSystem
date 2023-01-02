package graduation.approval.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

public class ApprovalMain extends Application {

    private static String queueName = null;
    private static String appName = null;

    public static void main(String[] args) {
        if (args.length < 2 ){
            throw new IllegalArgumentException("Arguments are missing. You must provide two arguments: APPROVAL_REQUEST_QUEUE and APPROVAL_NAME");
        }
        if (args[0] == null){
            throw new IllegalArgumentException("Please provide APPROVAL_NAME.");
        }
        if (args[1] == null){
            throw new IllegalArgumentException("Please provide APPROVAL_REQUEST_QUEUE.");
        }

        appName =args[0];
        queueName = args[1];

        launch(args);
    }

    @Override
    public void start(final Stage primaryStage) throws IOException {
        final Logger logger = LoggerFactory.getLogger(ApprovalMain.class);

        final String fxmlFileName = "approval.fxml";
        URL url = getClass().getClassLoader().getResource(fxmlFileName);
        if (url != null) {
            FXMLLoader loader = new FXMLLoader(url);
            ApprovalController controller = new ApprovalController(appName, queueName);
            loader.setController(controller);
            Parent root = loader.load();

            // EXIT this application when this stage is closed
            primaryStage.setOnCloseRequest(t -> {
                logger.info("Closing approval .....");
                controller.stop();
                Platform.exit();
                System.exit(0);
            });
            // set the stage title, icon and size
            primaryStage.setTitle("APPROVAL - " + appName);
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/approval.png"))));
            primaryStage.setScene(new Scene(root, 420, 270));
            // show the stage
            primaryStage.show();


        } else {
            logger.error("Could not load frame from " + fxmlFileName);
        }
    }
}
