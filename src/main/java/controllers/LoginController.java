package controllers;

import entities.Employees;
import entities.Login;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import services.EmployeeService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Random;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private AnchorPane employeePane;
    @FXML private AnchorPane adminPane;
    @FXML private AnchorPane loginPane;
    @FXML private AnchorPane rhPage;
    @FXML private AnchorPane rhPane;
    @FXML private TextField adminEmailField;
    @FXML private PasswordField adminPasswordField;
    @FXML private Button logoutButton;
    @FXML private AnchorPane forgotPasswordPane;
    @FXML private TextField forgotEmailField;
    @FXML private Button resetPasswordButton;
    @FXML private TextField rhEmailField;
    @FXML private PasswordField rhPasswordField;

    private EmployeeService employeeService = new EmployeeService();

    @FXML
    private void showEmployeeForm() {
        employeePane.setVisible(true);
        adminPane.setVisible(false);
        rhPane.setVisible(false);
    }

    @FXML
    private void showAdminForm() {
        adminPane.setVisible(true);
        employeePane.setVisible(false);
        rhPane.setVisible(false);
    }

    @FXML
    private void showRHForm() {
        rhPane.setVisible(true);
        adminPane.setVisible(false);
        employeePane.setVisible(false);
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password are required!");
            return;
        }

        try {
            if (employeeService.validateLogin(email, password)) {
                // Get the employee data
                Employees employee = employeeService.getEmployeeByEmail(email);
                if (employee != null) {
                    openGestionPubView(employee);
                } else {
                    showAlert("Error", "Failed to load employee data!");
                }
            } else {
                showAlert("Error", "Invalid email or password!");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error during login: " + e.getMessage());
        }
    }

    private void openGestionPubView(Employees employee) {
        try {
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestion1_pub.fxml"));
            Parent root = loader.load();

            // Pass employee data to the GestionK controller
            GestionK controller = loader.getController();
            controller.setEmployeeData(employee);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Publication Management");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open publication management view: " + e.getMessage());
        }
    }

    private void openGestionPubView() {
        try {
            Stage currentStage = (Stage) emailField.getScene().getWindow();
            currentStage.close();

            // Load the gestion1_pub.fxml file
            Parent root = FXMLLoader.load(getClass().getResource("/gestion1_pub.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Publication Management");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open publication management view: " + e.getMessage());
        }
    }

    private void showHomePane() {
        loginPane.setVisible(false);
        employeePane.setVisible(false);
        adminPane.setVisible(false);
        rhPage.setVisible(true);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleAdminLogin() {
        String email = adminEmailField.getText().trim();
        String password = adminPasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password are required!");
            return;
        }


        if (email.equals("admin@gmail.com") && password.equals("admin123")) {
            openDepartementView();
        } else {
            showAlert("Error", "Invalid admin credentials!");
        }
    }

    private void openDepartementView() {
        try {
            Stage currentStage = (Stage) adminEmailField.getScene().getWindow();
            currentStage.close();

            Parent root = FXMLLoader.load(getClass().getResource("/Departement.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Department Management");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open department view: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            loginPane.setVisible(true);
            employeePane.setVisible(false);
            adminPane.setVisible(false);
            rhPage.setVisible(false);

            emailField.clear();
            passwordField.clear();
            adminEmailField.clear();
            adminPasswordField.clear();

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to logout");
            alert.setContentText("Error during logout: " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void showForgotPassword() {
        forgotPasswordPane.setVisible(true);
        employeePane.setVisible(false);
        adminPane.setVisible(false);
        forgotEmailField.clear();
    }

    @FXML
    private void handleResetPassword() {
        String email = forgotEmailField.getText().trim();

        if (email.isEmpty()) {
            showAlert("Error", "Please enter your email address");
            return;
        }

        try {
            if (!employeeService.emailExists(email)) {
                showAlert("Error", "No account found with this email");
                return;
            }

            String tempPassword = generateTempPassword();

            boolean updated = employeeService.updatePassword(email, tempPassword);

            if (updated) {
                showAlert("Password Reset",
                        "Your temporary password is: " + tempPassword +
                                "\nPlease change it after logging in.");

                forgotPasswordPane.setVisible(false);
                showEmployeeForm();
                forgotEmailField.clear();
            } else {
                showAlert("Error", "Failed to update password");
            }

        } catch (SQLException e) {
            showAlert("Error", "Failed to reset password: " + e.getMessage());
        }
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }

        return sb.toString();
    }

    @FXML
    private void handleRHLogin() {
        String email = rhEmailField.getText().trim();
        String password = rhPasswordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Email and password are required!");
            return;
        }

        try {
            if (employeeService.validateHRLogin(email, password)) {
                Employees employee = employeeService.getEmployeeByEmail(email);
                if (employee != null) {
                    openRHView(employee);
                } else {
                    showAlert("Error", "Failed to load HR data!");
                }
            } else {
                showAlert("Error", "Invalid HR credentials or not authorized!");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Error during HR login: " + e.getMessage());
        }
    }

    private void openRHView(Employees employee) {

        loginPane.setVisible(false);
        employeePane.setVisible(false);
        adminPane.setVisible(false);
        rhPane.setVisible(false);
        forgotPasswordPane.setVisible(false);

        rhPage.setVisible(true);

    }


}