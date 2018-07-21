package net.nourepide.nagibSystem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("resource/Main.fxml"));

        Scene scene = new Scene(root, 375, 600);

        primaryStage.setTitle("NagibSystem");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        Platform.runLater(() -> primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("resource/Icon.svg.png"))));
        Platform.runLater(() -> scene.getStylesheets().add(getClass().getResource("resource/Main.css").toExternalForm()));
    }
}
