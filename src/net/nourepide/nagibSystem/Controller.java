package net.nourepide.nagibSystem;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Random;
import java.util.TimerTask;

public class Controller {
    @FXML
    ScrollPane scrollPane;

    @FXML
    VBox chat;

    @FXML
    TextField text;

    private Button getButton(String text) {
        Button button = new Button(text);

        button.setMaxWidth(333);

        return button;
    }

    private AnchorPane getPlayerMessage() {
        AnchorPane anchorPane = new AnchorPane();
        Button button = getButton(text.getText());
        button.setId("message");
        AnchorPane.setRightAnchor(button, 0.0);
        anchorPane.getChildren().add(button);

        setEffects(button);

        return anchorPane;
    }

    private AnchorPane getBotMessage(String text) {
        String[] phrases = {
                "Слышали о Lorem Ipsum?",
                "Надеюсь тебя всё устраивает",
                "Не оборачивайся",
                "Не лезь она тебя сожрёт!",
                "5 часов на разработку ... вот это-го",
                "Приветствие на Нибирском",
                "А давайте - \"без давайте?\"",
                "Ой, ... извините",
                "Если долго смотреть в декольте ...",
                "Roll 100",
                "Лезь в робота!",
                "Может хотя-бы 3\'чку поставите?",
                "Вот так вот и загнёшся после такого",
                "Хороводят грязь с дождём",
                "Не подсматривай!",
                "Плутон - планета!",
                "С кем не бывает?",
                "Лок'так огар",
                "Это конец? Это конец."
        };

        AnchorPane anchorPane = new AnchorPane();

        Button button = getButton(
                text == null ? phrases[new Random().nextInt(phrases.length)] : text
        );

        anchorPane.getChildren().add(button);

        setEffects(button);

        return anchorPane;
    }

    private void setEffects(Button button) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(button);
        fadeTransition.setDuration(Duration.millis(250));
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        fadeTransition.play();

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(button);
        scaleTransition.setDuration(Duration.millis(250));
        scaleTransition.setByY(0.4);
        scaleTransition.setByX(0.4);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    public void send() {
        if (!text.getText().equals("")) {
            chat.getChildren().add(getPlayerMessage());
            text.setText("");
            scrollPane.setVvalue(1.0);

            new java.util.Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            Platform.runLater(() -> chat.getChildren().add(getBotMessage(null)));
                            scrollPane.setVvalue(1.0);

                        }
                    },
                    1000
            );
        }
    }

    @FXML
    public void initialize() {
        new java.util.Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> chat.getChildren().add(getBotMessage("Вас приветствует чат NagibSystem")));
                        scrollPane.setVvalue(1.0);

                    }
                },
                1000
        );

        new java.util.Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> chat.getChildren().add(getBotMessage("Попробуйте что нибудь написать!")));
                        scrollPane.setVvalue(1.0);

                    }
                },
                2000
        );
    }
}
