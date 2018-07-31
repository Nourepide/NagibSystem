package net.nourepide.nagibSystem.frontend;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load root
        Parent root = FXMLLoader.load(getClass().getResource("resource/Main.fxml"));
        Scene scene = new Scene(root);

        // Anti GTK broken application
        Platform.runLater(() -> {
            // Set icon
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resource/Icon.png")));

            // Set stylesheets
            scene.getStylesheets().add(getClass().getResource("resource/Main.css").toExternalForm());

            // Another settings
            primaryStage.setTitle("NagibSystem");
            primaryStage.setResizable(false);
            primaryStage.setOnCloseRequest(event -> Platform.exit());
        });

        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
