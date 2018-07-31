package net.nourepide.nagibSystem.frontend;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {
    @FXML
    StackPane stackPane;

    @FXML
    VBox chat;

    @FXML
    VBox cap;

    @FXML
    Node firstNicknameElement;

    @FXML
    Node secondNicknameElement;

    @FXML
    Node thirdNicknameElement;

    @FXML
    TextField textNickname;

    @FXML
    Text warningNickname;

    @FXML
    ScrollPane scrollPane;

    @FXML
    TextField text;

    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;

    @FXML
    public void initialize() {
        try {
            socket = new Socket("localhost", 7820);
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());

            Platform.runLater(this::initAppearance);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void verificationNickname() throws IOException {
        if (!textNickname.getText().equals("")) {
            output.writeUTF("!nickname" + " " + textNickname.getText());

            while (true) {
                String message = input.readUTF();

                if (message.contains("!nickname")) {
                    String[] strings = message.split(" ", 2);
                    if (strings[1].equals("confirmed")) {
                        appearence(cap, 500, false).setOnFinished(event -> cap.setPickOnBounds(false));

                        Thread thread = new Thread(this::checkInput);
                        thread.setDaemon(true);
                        thread.start();

                        Thread thread2 = new Thread(this::checkConsole);
                        thread2.setDaemon(true);
                        thread2.start();

                        break;
                    } else if (strings[1].equals("denied")) {
                        appearence(warningNickname, 500, true);

                        break;
                    }
                }
            }
        }
    }

    private void checkInput() {
        while (true) {
            try {
                String message = input.readUTF();

                if (input.available() == 0) {
                    System.out.println(message);

                    String[] strings = message.split(" ", 2);
                    String name = strings[0];
                    String userMessage = strings[1];

                    Platform.runLater(() -> addMessage(MESSAGE_TYPE.USER, userMessage, name));
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }

        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkConsole() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String message = scanner.nextLine();

            if (!message.equals("")) {
                Platform.runLater(() -> addMessage(MESSAGE_TYPE.PLAYER, message));

                try {
                    output.writeUTF(message);
                } catch (IOException e) {
                    System.out.println("Server closed");
                    break;
                }
            }
        }
    }

    public void sendPlayerMessage() {
        String message = text.getText();

        if (!message.equals("")) {
            addMessage(MESSAGE_TYPE.PLAYER, message);
            try {
                output.writeUTF(message);
            } catch (IOException e) {
                System.out.println("Server closed");
            }
            text.setText("");
        }
    }

    private void addMessage(MESSAGE_TYPE messageType, String message, String name) {
        if (messageType.equals(MESSAGE_TYPE.USER)) {
            AnchorPane userMessage = getUserMessage(name, message);

            setEffects(userMessage.getChildren().get(0));

            chat.getChildren().add(userMessage);

            new Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            scrollPane.setVvalue(1.0);
                        }
                    },
                    20
            );
        }
    }

    private void addMessage(MESSAGE_TYPE messageType, String message) {
        if (messageType.equals(MESSAGE_TYPE.PLAYER)) {
            AnchorPane playerMessage = getPlayerMessage(message);

            setEffects(playerMessage.getChildren().get(0));

            chat.getChildren().add(playerMessage);

            new java.util.Timer().schedule(
                    new TimerTask() {
                        @Override
                        public void run() {
                            scrollPane.setVvalue(1.0);
                        }
                    },
                    20
            );
        }
    }

    private void setEffects(Node node) {
        appearence(node, 250, true);

        ScaleTransition scaleTransition = new ScaleTransition();
        scaleTransition.setNode(node);
        scaleTransition.setDuration(Duration.millis(250.0));
        scaleTransition.setByY(0.4);
        scaleTransition.setByX(0.4);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    private AnchorPane getPlayerMessage(String message) {
        AnchorPane anchorPane = new AnchorPane();
        Button buttonMessage = new Button(message);

        buttonMessage.setId("message");
        buttonMessage.setMaxWidth(333.0);
        buttonMessage.setWrapText(true);

        AnchorPane.setRightAnchor(buttonMessage, 0.0);
        anchorPane.getChildren().add(buttonMessage);

        return anchorPane;
    }

    private AnchorPane getUserMessage(String userName, String userMessage) {
        AnchorPane anchorPane = new AnchorPane();
        HBox hBox = new HBox();
        Button buttonName = new Button(userName);
        Button buttonMessage = new Button(userMessage);


        buttonName.setEllipsisString(userName);
        buttonName.setId("messageName");

        buttonMessage.setId("messageText");
        buttonMessage.setWrapText(true);

        hBox.getChildren().addAll(buttonName, buttonMessage);

        hBox.setMaxWidth(333);
        anchorPane.getChildren().add(hBox);

        return anchorPane;
    }

    private FadeTransition appearence(Node node, double duration, Boolean effect) {
        FadeTransition fadeTransition = new FadeTransition();
        fadeTransition.setNode(node);
        fadeTransition.setDuration(Duration.millis(duration));

        if (effect) {
            fadeTransition.setFromValue(0);
            fadeTransition.setToValue(1);
        } else {
            fadeTransition.setFromValue(1);
            fadeTransition.setToValue(0);
        }

        fadeTransition.play();

        return fadeTransition;
    }

    private void initAppearance() {
        new Timer().schedule(timerTask(() -> appearence(firstNicknameElement, 1000, true)), 1000);

        new Timer().schedule(timerTask(() -> appearence(secondNicknameElement, 1000, true)), 2000);

        new Timer().schedule(timerTask(() -> appearence(thirdNicknameElement, 1000, true)), 3000);
    }

    private TimerTask timerTask(Runnable runnable) {
        return new TimerTask() {
            @Override
            public void run() {
                runnable.run();
            }
        };
    }

    enum MESSAGE_TYPE {
        PLAYER,
        USER
    }
}