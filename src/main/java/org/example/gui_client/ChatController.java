package org.example.gui_client;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.IOException;

class MessageBubble extends HBox {
    public final int id;
    MessageBubble(int id) {
        super();
        this.id = id;
    }
}

public class ChatController {
    @FXML private VBox chatContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox messageArea;
    @FXML private TextField messageInput;
    @FXML private Button sendButton;
    @FXML private Label chatTitle;
    @FXML private Label onlineCountLabel;

    private MessageAPI messageAPI;
    private String username;
    private int onlineCount = 1;
//    private int firstMessageID = 2;

    public void setMessageAPI(MessageAPI messageAPI) {
        this.messageAPI = messageAPI;
    }

    public void setUsername(String username) {
        this.username = username;
        chatTitle.setText("Global Chat - Welcome, " + username);
        updateOnlineCount();
    }

    @FXML
    private void initialize() {
        // Set styles programmatically
        chatContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Style for scroll pane
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scrollPane.setFitToWidth(true);
        scrollPane.setOnScroll((event -> {
            if (event.getDeltaY() > 0) {
                if (messageArea.getChildren().isEmpty()) {
                    messageAPI.sendMessageCommandMessage(Header.Type.LOAD_SOME_MESSAGES, new int[]{1});
                    System.out.println("Sent load some messages command");
                } else {
                    try {
                        messageAPI.sendMessageCommandMessage(Header.Type.LOAD_SOME_MESSAGES, new int[]{1, ((MessageBubble) messageArea.getChildren().getFirst()).id});
//                        firstMessageID = ((MessageBubble) messageArea.getChildren().getFirst()).id;
                        System.out.println("Sent load some messages command");
                    } catch (Exception e) {
                        System.out.println("Error sending: " + e.getMessage());
                        try {
                            messageAPI.close();
                            messageAPI.connect();
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            } else if (event.getDeltaY() < 0) {
                try {
//                    firstMessageID = ((MessageBubble) messageArea.getChildren().getFirst()).id + 1;
                    messageAPI.sendMessageCommandMessage(Header.Type.LOAD_SOME_MESSAGES, new int[]{1});
                    System.out.println("Sent load some messages command");
                } catch (Exception e) {
                    System.out.println("Error sending: " + e.getMessage());
                    try {
                        messageAPI.close();
                        messageAPI.connect();
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        }));

        // Style for message area
        messageArea.setStyle("-fx-background-color: transparent;");
        messageArea.setPadding(new Insets(10));

        // Style for message input
        messageInput.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 10 20; " +
                "-fx-font-size: 14px;");

        // Style for send button
        sendButton.setStyle("-fx-background-color: #4a6ee0; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 20; " +
                "-fx-padding: 10 25; " +
                "-fx-font-size: 14px;");

        sendButton.setOnMouseEntered(e ->
                sendButton.setStyle("-fx-background-color: #5a7ef0; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 10 25; " +
                        "-fx-font-size: 14px;")
        );

        sendButton.setOnMouseExited(e ->
                sendButton.setStyle("-fx-background-color: #4a6ee0; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 20; " +
                        "-fx-padding: 10 25; " +
                        "-fx-font-size: 14px;")
        );

        // Send message on Enter key
        messageInput.setOnAction(e -> sendMessage());
    }

    public void startMessageListener() {
        new Thread(() -> {
            try {
                Thread.sleep(1000); // Wait for authentication

                // Send initial message
                Platform.runLater(() -> {
                    displaySystemMessage("Connected to global chat. Welcome!");
                });

                // Listen for incoming messages
                while (true) {
                    try {
//                        new Thread(() -> {
//                            try {
//                                Thread.sleep(1000);
//                                if (messageArea.getChildren().isEmpty()) {
//                                    messageAPI.sendMessageCommandMessage(Header.Type.LOAD_SOME_MESSAGES, new int[]{1});
//                                    System.out.println("Sent load some messages command");
//                                } else {
//                                    messageAPI.sendMessageCommandMessage(Header.Type.LOAD_SOME_MESSAGES, new int[]{1, ((MessageBubble) messageArea.getChildren().getFirst()).id});
//                                    System.out.println("Sent load some messages command");
//                                }
//                            } catch (InterruptedException e) {
//                                throw new RuntimeException(e);
//                            }
//                        }).start();
                        Header header = messageAPI.receiveHeader();
                        if (header.type() == Header.Type.TEXT) {
                            SendTextPayload payload = (SendTextPayload) messageAPI.receivePayload(header);
                            System.out.println("Received message: " + payload.getText());

                            // Update UI with received message
                            Platform.runLater(() -> {
                                int i = payload.getText().indexOf(" ");
                                if (messageArea.getChildren().isEmpty()) {
                                    displayMessage(payload.getText().substring(0, i), payload.getText().substring(i + 1), payload.getMessageIDs()[1]);
                                } else {
                                    if (payload.getMessageIDs()[1] < ((MessageBubble) messageArea.getChildren().getFirst()).id) {
                                        displayPreviousMessage(payload.getText().substring(0, i), payload.getText().substring(i + 1), payload.getMessageIDs()[1]);
                                    } else if (payload.getMessageIDs()[1] > ((MessageBubble) messageArea.getChildren().getLast()).id) {
                                        displayMessage(payload.getText().substring(0, i), payload.getText().substring(i + 1), payload.getMessageIDs()[1]);
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            displaySystemMessage("Connection lost. Please restart the app.");
                        });
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
//        try (ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor()) {
//
//            scheduler.scheduleAtFixedRate(() -> {
//                // UI elements must be accessed on the JavaFX Application Thread
//                Platform.runLater(() -> {
//                    int firstMessageId = ((MessageBubble) messageArea.getChildren().getFirst()).id;
//
//                    // Move the network call back to a background thread if it's blocking
//                    CompletableFuture.runAsync(() -> {
//                        messageAPI.sendMessageCommandMessage(Header.Type.LOAD_SOME_MESSAGES, new int[]{1, firstMessageId});
//                    });
//                });
//            }, 0, 1000, TimeUnit.MILLISECONDS);
//        }
    }

    @FXML
    private void sendMessage() {
        String text = messageInput.getText();
        if (!text.isEmpty()) {
            // Display sent message
//            displayMessage("You", text);
            messageInput.clear();

            // Send via MessageAPI in background thread
            new Thread(() -> {
                try {
                    messageAPI.sendSendTextMessage(new int[] {1}, text);
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        displaySystemMessage("Failed to send message: " + e.getMessage());
                    });
                }
            }).start();
        }
    }

    private void displayPreviousMessage(String sender, String text, int id) {
        HBox messageBubble = createMessageBubble(sender, text, id, sender.equals("You"));
        messageArea.getChildren().addFirst(messageBubble);

        // Scroll to bottom
//        Platform.runLater(() -> {
//            scrollPane.setVvalue(1.0);
//        });
    }

    private void displayMessage(String sender, String text, int id) {
        HBox messageBubble = createMessageBubble(sender, text, id, sender.equals("You"));
        messageArea.getChildren().add(messageBubble);

        // Scroll to bottom
        Platform.runLater(() -> {
            scrollPane.setVvalue(1.0);
        });
    }

    private void displaySystemMessage(String text) {
//        Label systemMessage = new Label(text);
//        systemMessage.setStyle("-fx-background-color: rgba(0, 0, 0, 0.3); " +
//                "-fx-text-fill: white; " +
//                "-fx-padding: 5 15; " +
//                "-fx-background-radius: 15;");
//        systemMessage.setFont(Font.font("Arial", FontWeight.NORMAL, 12));
//        systemMessage.setMaxWidth(400);
//        systemMessage.setWrapText(true);
//        systemMessage.setAlignment(Pos.CENTER);
//
//        HBox container = new HBox();
//        container.setAlignment(Pos.CENTER);
//        container.getChildren().add(systemMessage);
//
//        messageArea.getChildren().add(container);
//
//        Platform.runLater(() -> {
//            scrollPane.setVvalue(1.0);
//        });
    }

    private MessageBubble createMessageBubble(String sender, String text, int id, boolean isSent) {
        MessageBubble container = new MessageBubble(id);
        container.setMaxWidth(500);

        VBox bubble = new VBox(5);
        bubble.setPadding(new Insets(10, 15, 10, 15));
        bubble.setMaxWidth(400);

        if (!isSent) {
            Label senderLabel = new Label(sender);
            senderLabel.setTextFill(Color.LIGHTBLUE);
            senderLabel.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            bubble.getChildren().add(senderLabel);
        }

        Label messageLabel = new Label(text);
        messageLabel.setTextFill(isSent ? Color.WHITE : Color.WHITE);
        messageLabel.setWrapText(true);
        messageLabel.setFont(Font.font("Arial", 14));

        Label timeLabel = new Label(java.time.LocalTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm")));
        timeLabel.setTextFill(Color.LIGHTGRAY);
        timeLabel.setFont(Font.font("Arial", 10));
        timeLabel.setAlignment(Pos.BOTTOM_RIGHT);

        VBox contentBox = new VBox(5, messageLabel, timeLabel);
        bubble.getChildren().add(contentBox);

        // Set bubble style
        if (isSent) {
            bubble.setStyle("-fx-background-color: #4a6ee0; " +
                    "-fx-background-radius: 20 20 5 20;");
            container.setAlignment(Pos.CENTER_RIGHT);
            HBox.setMargin(bubble, new Insets(5, 0, 5, 100));
        } else {
            bubble.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); " +
                    "-fx-background-radius: 20 20 20 5;");
            container.setAlignment(Pos.CENTER_LEFT);
            HBox.setMargin(bubble, new Insets(5, 100, 5, 0));
        }

        container.getChildren().add(bubble);
        HBox.setHgrow(bubble, Priority.NEVER);

        return container;
    }

    private void updateOnlineCount() {
        onlineCountLabel.setText("Online: " + onlineCount + " user(s)");
    }

    @FXML
    private void handleLogout() {
        try {
            Main.showLoginPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}