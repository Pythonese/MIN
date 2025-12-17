package org.example.gui_client;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class LoginController {
    @FXML private VBox loginContainer;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;

    private Class<?> mainApp;

    public void setMainApp(Class<?> mainApp) {
        this.mainApp = mainApp;
    }

    @FXML
    private void initialize() {
        // Add styles programmatically
        loginContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #667eea 0%, #764ba2 100%);");

        // Set button style
        loginButton.setStyle("-fx-background-color: #4a6ee0; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 5; -fx-padding: 10 20;");

        // Add hover effects
        loginButton.setOnMouseEntered(e ->
                loginButton.setStyle("-fx-background-color: #5a7ef0; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;")
        );

        loginButton.setOnMouseExited(e ->
                loginButton.setStyle("-fx-background-color: #4a6ee0; -fx-text-fill: white; -fx-font-weight: bold; " +
                        "-fx-background-radius: 5; -fx-padding: 10 20;")
        );

        // Clear error when user types
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> errorLabel.setText(""));
        passwordField.textProperty().addListener((obs, oldVal, newVal) -> errorLabel.setText(""));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty()) {
            showError("Please enter username");
            return;
        }

        if (password.isEmpty()) {
            showError("Please enter password");
            return;
        }

        // For demo, accept any non-empty credentials
        try {
            Main.showChatPage(username, password);
        } catch (Exception e) {
            showError("Error connecting to server: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setTextFill(Color.RED);
    }
}