package services;

import entities.Departement;
import entities.Employees;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.StringConverter;
import utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeService {

    private Connection cnx;

    public EmployeeService() {
        cnx = Database.getInstance().getCnx();
    }

    public void addEmployee(Employees emp) throws SQLException {
        String sql = "INSERT INTO employees (name, email, phone, role, department_id) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setInt(3, emp.getPhone());
            ps.setString(4, emp.getRole());
            ps.setInt(5, emp.getDepartement().getId());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating employee failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    emp.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void updateEmployee(Employees emp) throws SQLException {
        String sql = "UPDATE employees SET name = ?, email = ?, phone = ?, role = ?, department_id = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setInt(3, emp.getPhone());
            ps.setString(4, emp.getRole());
            ps.setInt(5, emp.getDepartement().getId());
            ps.setInt(6, emp.getId());

            ps.executeUpdate();
        }
    }

    public void deleteEmployee(int id) throws SQLException {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public ObservableList<Employees> getAllEmployees() throws SQLException {
        ObservableList<Employees> employees = FXCollections.observableArrayList();
        String sql = "SELECT e.*, d.name as dep_name FROM employees e " +
                "LEFT JOIN departement d ON e.department_id = d.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Departement dep = new Departement(
                        rs.getInt("department_id"),
                        rs.getString("dep_name"),
                        "", 0, ""
                );

                Employees emp = new Employees(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getInt("phone"),
                        rs.getString("role"),
                        dep
                );
                employees.add(emp);
            }
        }
        return employees;
    }

    public Employees getById(int id) throws SQLException {
        String sql = "SELECT e.*, d.name as dep_name FROM employees e " +
                "LEFT JOIN departement d ON e.department_id = d.id WHERE e.id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Departement dep = new Departement(
                            rs.getInt("department_id"),
                            rs.getString("dep_name"),
                            "", 0, ""
                    );

                    return new Employees(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getInt("phone"),
                            rs.getString("role"),
                            dep
                    );
                }
            }
        }
        return null;
    }
}
