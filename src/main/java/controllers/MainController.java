package controllers;

import entities.Departement;
import entities.Employees;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import services.DepartementService;
import services.EmployeeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML private AnchorPane homePane;
    @FXML private AnchorPane departmentsPane;
    @FXML private AnchorPane employeesPane;

    @FXML private TableColumn<Departement, String> departmentDescription;
    @FXML private TableColumn<Departement, String> departmentPhone;
    @FXML private TableColumn<Departement, String> departmentEmail;
    @FXML private TableColumn<Departement, Integer> departmentID;
    @FXML private TableColumn<Departement, String> departmentName;
    @FXML private Button department_add_btn;
    @FXML private Button department_delete_btn;
    @FXML private TextField descriptionTF;
    @FXML private TextField emailTF;
    @FXML private TextField nameTF;
    @FXML private TextField phoneTF;
    @FXML private TableView<Departement> table_view;
    @FXML private Button logoutButton;

    @FXML private TableColumn<Employees, Integer> employeeID;
    @FXML private TableColumn<Employees, String> employeeName;
    @FXML private TableColumn<Employees, String> employeeEmail;
    @FXML private TableColumn<Employees, Integer> employeePhone;
    @FXML private TableColumn<Employees, String> employeeRole;
    @FXML private TableColumn<Employees, String> employeeDepartement;
    @FXML private TableColumn<Employees, String> employeePassword;
    @FXML private TextField employeeNameTF;
    @FXML private TextField employeeEmailTF;
    @FXML private TextField employeePhoneTF;
    @FXML private TextField employeeRoleTF;
    @FXML private TextField employeePasswordTF;
    @FXML private ComboBox<Departement> departmentComboBox;
    @FXML private TableView<Employees> EmployeeTable;

    @FXML private Label employeeCountLabel;
    @FXML private Label departmentCountLabel;
    @FXML private LineChart<String, Number> departmentChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;

    private EmployeeService employeeService = new EmployeeService();
    private ObservableList<Employees> employeeList = FXCollections.observableArrayList();
    private DepartementService departementService = new DepartementService();
    private ObservableList<Departement> departementList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupDepartmentTable();
        setupEmployeeTable();
        setupDepartmentComboBox();
        showHome();
        updateCountLabels();
        updateDepartmentChart();
    }

    private void setupDepartmentTable() {
        departmentName.setCellValueFactory(new PropertyValueFactory<>("name"));
        departmentDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        departmentPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        departmentEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        refreshTable();
    }

    private void setupEmployeeTable() {
        employeeName.setCellValueFactory(new PropertyValueFactory<>("name"));
        employeeEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        employeePhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        employeeRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        employeePassword.setCellValueFactory(new PropertyValueFactory<>("password"));
        employeeDepartement.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getDepartement().getName()));
        refreshEmployeeTable();
    }

    @FXML
    private void showHome() {
        homePane.setVisible(true);
        departmentsPane.setVisible(false);
        employeesPane.setVisible(false);
        updateCountLabels();
    }

    @FXML
    private void showDepartments() {
        homePane.setVisible(false);
        departmentsPane.setVisible(true);
        employeesPane.setVisible(false);
        refreshTable();
    }

    @FXML
    private void showEmployees() {
        homePane.setVisible(false);
        departmentsPane.setVisible(false);
        employeesPane.setVisible(true);
        refreshEmployeeTable();
    }

    @FXML
    private void handleAddDepartment(ActionEvent event) {
        try {
            String name = nameTF.getText().trim();
            String description = descriptionTF.getText().trim();
            String phone = phoneTF.getText().trim();
            String email = emailTF.getText().trim();

            if (name.isEmpty()) {
                showAlert("Error", "Name is required!");
                return;
            }

            Departement newDept = new Departement(name, description, Integer.parseInt(phone), email);
            departementService.ajouter(newDept);
            showAlert("Success", "Department added successfully!");
            clearForm();
            refreshTable();
            updateCountLabels();
            updateDepartmentChart();
        } catch (SQLException e) {
            showAlert("Database Error", "Error adding department: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "Phone must be a valid number");
        }
    }

    @FXML
    private void handleDeleteDepartment(ActionEvent event) {
        Departement selected = table_view.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a department to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this department?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                departementService.supprimer(selected);
                showAlert("Success", "Department deleted successfully!");
                refreshTable();
                clearForm();
                updateCountLabels();
                updateDepartmentChart();
            } catch (SQLException e) {
                showAlert("Error", "Error deleting department: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleUpdateDepartment(ActionEvent event) {
        Departement selected = table_view.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a department to update.");
            return;
        }

        try {
            String name = nameTF.getText().trim();
            String description = descriptionTF.getText().trim();
            String phoneStr = phoneTF.getText().trim();
            String email = emailTF.getText().trim();

            if (name.isEmpty() || phoneStr.isEmpty() || email.isEmpty()) {
                showAlert("Error", "Name, phone and email are required!");
                return;
            }

            if (!phoneStr.matches("\\d{8}")) {
                showAlert("Error", "Phone number must contain exactly 8 digits.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                showAlert("Error", "Email must contain '@' and '.'");
                return;
            }

            int phone = Integer.parseInt(phoneStr);
            selected.setName(name);
            selected.setDescription(description);
            selected.setPhone(phone);
            selected.setEmail(email);

            departementService.modifier(selected);
            showAlert("Success", "Department updated successfully!");
            refreshTable();
            clearForm();
            updateDepartmentChart();
        } catch (SQLException e) {
            showAlert("Error", "Error updating department: " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Error", "Phone number must be a valid number.");
        }
    }

    @FXML
    private void handleTableClick() {
        Departement selected = table_view.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nameTF.setText(selected.getName());
            descriptionTF.setText(selected.getDescription());
            phoneTF.setText(String.valueOf(selected.getPhone()));
            emailTF.setText(selected.getEmail());
        }
    }

    public static boolean isValidPassword(String password) {
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._-])[A-Za-z\\d@$!%*?&._-]{8,}$";
        return password.matches(regex);
    }

    @FXML
    private void handleAddEmployee(ActionEvent event) {
        try {
            String name = employeeNameTF.getText().trim();
            String email = employeeEmailTF.getText().trim();
            String phone = employeePhoneTF.getText().trim();
            String role = employeeRoleTF.getText().trim();
            String password = employeePasswordTF.getText().trim();
            Departement department = departmentComboBox.getValue();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty() || password.isEmpty() || department == null) {
                showAlert("Erreur", "Tous les champs sont obligatoires !");
                return;
            }

            if (!phone.matches("\\d{8}")) {
                showAlert("Erreur", "Le numéro de téléphone doit contenir exactement 8 chiffres.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                showAlert("Erreur", "L'adresse e-mail doit contenir '@' et '.'");
                return;
            }

            if (!isValidPassword(password)) {
                showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.");
                return;
            }

            for (Employees emp : employeeService.getAllEmployees()) {
                if (emp.getEmail().equalsIgnoreCase(email)) {
                    showAlert("Erreur", "Cette adresse e-mail est déjà utilisée.");
                    return;
                }
            }

            Employees newEmployee = new Employees(0, name, email, Integer.parseInt(phone), role, department, password);
            employeeService.addEmployee(newEmployee);
            showAlert("Succès", "Employé ajouté avec succès !");
            clearEmployeeForm();
            refreshEmployeeTable();
            updateCountLabels();
            updateDepartmentChart();
        } catch (SQLException e) {
            showAlert("Erreur Base de Données", "Erreur lors de l'ajout : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le téléphone doit être un nombre valide.");
        }
    }

    @FXML
    private void handleUpdateEmployee(ActionEvent event) {
        Employees selected = EmployeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Attention", "Veuillez sélectionner un employé à modifier.");
            return;
        }

        try {
            String name = employeeNameTF.getText().trim();
            String email = employeeEmailTF.getText().trim();
            String phone = employeePhoneTF.getText().trim();
            String role = employeeRoleTF.getText().trim();
            String password = employeePasswordTF.getText().trim();
            Departement department = departmentComboBox.getValue();

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || role.isEmpty() || password.isEmpty() || department == null) {
                showAlert("Erreur", "Tous les champs sont obligatoires !");
                return;
            }

            if (!phone.matches("\\d{8}")) {
                showAlert("Erreur", "Le numéro de téléphone doit contenir exactement 8 chiffres.");
                return;
            }

            if (!email.contains("@") || !email.contains(".")) {
                showAlert("Erreur", "L'adresse e-mail doit contenir '@' et '.'");
                return;
            }

            if (!isValidPassword(password)) {
                showAlert("Erreur", "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial.");
                return;
            }

            for (Employees emp : employeeService.getAllEmployees()) {
                if (emp.getEmail().equalsIgnoreCase(email) && emp.getId() != selected.getId()) {
                    showAlert("Erreur", "Cette adresse e-mail est déjà utilisée par un autre employé.");
                    return;
                }
            }

            selected.setName(name);
            selected.setEmail(email);
            selected.setPhone(Integer.parseInt(phone));
            selected.setRole(role);
            selected.setPassword(password);
            selected.setDepartement(department);

            employeeService.updateEmployee(selected);
            showAlert("Succès", "Employé modifié avec succès !");
            refreshEmployeeTable();
            clearEmployeeForm();
            updateDepartmentChart();
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la mise à jour : " + e.getMessage());
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le téléphone doit être un nombre valide.");
        }
    }

    @FXML
    private void handleDeleteEmployee(ActionEvent event) {
        Employees selected = EmployeeTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an employee to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this employee?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                employeeService.deleteEmployee(selected.getId());
                showAlert("Success", "Employee deleted successfully!");
                refreshEmployeeTable();
                clearEmployeeForm();
                updateCountLabels();
                updateDepartmentChart();
            } catch (SQLException e) {
                showAlert("Error", "Error deleting employee: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleEmployeeTableClick() {
        Employees selected = EmployeeTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            employeeNameTF.setText(selected.getName());
            employeeEmailTF.setText(selected.getEmail());
            employeePhoneTF.setText(String.valueOf(selected.getPhone()));
            employeeRoleTF.setText(selected.getRole());
            employeePasswordTF.setText(selected.getPassword());
            departmentComboBox.setValue(selected.getDepartement());
        }
    }

    private void refreshTable() {
        try {
            departementList.clear();
            departementList.addAll(departementService.recuperer());
            table_view.setItems(departementList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load departments: " + e.getMessage());
        }
    }

    private void refreshEmployeeTable() {
        try {
            employeeList.clear();
            employeeList.addAll(employeeService.getAllEmployees());
            EmployeeTable.setItems(employeeList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load employees: " + e.getMessage());
        }
    }

    private void clearForm() {
        nameTF.clear();
        descriptionTF.clear();
        phoneTF.clear();
        emailTF.clear();
    }

    private void clearEmployeeForm() {
        employeeNameTF.clear();
        employeeEmailTF.clear();
        employeePhoneTF.clear();
        employeeRoleTF.clear();
        employeePasswordTF.clear();
    }

    private void setupDepartmentComboBox() {
        try {
            departementList = FXCollections.observableArrayList(departementService.recuperer());
            departmentComboBox.setItems(departementList);
            departmentComboBox.setConverter(new StringConverter<Departement>() {
                @Override
                public String toString(Departement departement) {
                    return departement != null ? departement.getName() : "";
                }

                @Override
                public Departement fromString(String string) {
                    return departementList.stream()
                            .filter(dep -> dep.getName().equals(string))
                            .findFirst()
                            .orElse(null);
                }
            });
            if (!departementList.isEmpty()) {
                departmentComboBox.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load departments: " + e.getMessage());
        }
    }

    private void updateCountLabels() {
        try {
            int employeeCount = employeeService.getAllEmployees().size();
            int departmentCount = departementService.recuperer().size();

            employeeCountLabel.setText(String.valueOf(employeeCount));
            departmentCountLabel.setText(String.valueOf(departmentCount));
            updateDepartmentChart();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load counts: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void updateDepartmentChart() {
        try {
            Map<String, Integer> stats = employeeService.getEmployeeCountByDepartment();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Employees by department");

            departmentChart.getData().clear();

            for (Map.Entry<String, Integer> entry : stats.entrySet()) {
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
            }

            departmentChart.getData().add(series);

            departmentChart.setLegendVisible(true);
            departmentChart.setCreateSymbols(true);
            departmentChart.setAnimated(true);

        } catch (SQLException e) {
            showAlert("Error", "Failed to load department statistics: " + e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Stage currentStage = (Stage) logoutButton.getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();

            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to logout");
            alert.setContentText("Could not load the login view: " + e.getMessage());
            alert.showAndWait();
        }
    }

}
