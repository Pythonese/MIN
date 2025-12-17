package org.example.gui_client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.security.InvalidParameterException;

public class Main extends Application {
    private static MessageAPI messageAPI;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        showLoginPage();
        primaryStage.show();
    }

    public static void showLoginPage() throws Exception {
        FXMLLoader loader = new FXMLLoader(Main1.class.getResource("login.fxml"));
        Parent root = loader.load();
        LoginController controller = loader.getController();
        controller.setMainApp(Main1.class);

        primaryStage.setTitle("Secure Messenger - Login");
        primaryStage.setScene(new Scene(root, 400, 500));
        primaryStage.setResizable(false);
    }

    public static void showChatPage(String username, String password) throws Exception {
        // Initialize MessageAPI
        messageAPI = new MessageAPI(new byte[] {127, 0, 0, 1}, 8080);

        messageAPI.connect();
        messageAPI.sendKeyExchangeMessage();
        messageAPI.receiveKeyExchangeMessage();
        messageAPI.sendAuthMessage(username, password);
        Header header = messageAPI.receiveHeader();
        SendTextPayload payload = (SendTextPayload) messageAPI.receivePayload(header);
        if (payload.getText().equals("WRONG")) {
            throw new InvalidParameterException("Invalid password");
        } else {
            System.out.println("Authentication successful");
        }

        FXMLLoader loader = new FXMLLoader(Main1.class.getResource("chat.fxml"));
        Parent root = loader.load();
        ChatController controller = loader.getController();
        controller.setMessageAPI(messageAPI);
        controller.setUsername(username);

        primaryStage.setTitle("Global Chat - " + username);
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.setResizable(true);

        // Start listening for messages
        controller.startMessageListener();
    }

    public static void main(String[] args) {
        launch(args);
    }
}