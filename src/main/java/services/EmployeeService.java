package services;

import entities.Departement;
import entities.Employees;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.Database;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EmployeeService {

    private Connection cnx;

    public EmployeeService() {
        cnx = Database.getInstance().getCnx();
    }

    public void addEmployee(Employees emp) throws SQLException {
        String sql = "INSERT INTO employees (name, email, phone, role, department_id, password) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setInt(3, emp.getPhone());
            ps.setString(4, emp.getRole());
            ps.setInt(5, emp.getDepartement().getId());
            ps.setString(6, emp.getPassword());

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
        String sql = "UPDATE employees SET name = ?, email = ?, phone = ?, role = ?, department_id = ?, password = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, emp.getName());
            ps.setString(2, emp.getEmail());
            ps.setInt(3, emp.getPhone());
            ps.setString(4, emp.getRole());
            ps.setInt(5, emp.getDepartement().getId());
            ps.setString(6, emp.getPassword());
            ps.setInt(7, emp.getId());

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
                        dep,
                        rs.getString("password")
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
                            dep,
                            rs.getString("password")
                    );
                }
            }
        }
        return null;
    }

    public Map<String, Integer> getEmployeeCountByDepartment() throws SQLException {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT d.name, COUNT(e.id) as employee_count " +
                "FROM departement d LEFT JOIN employees e ON d.id = e.department_id " +
                "GROUP BY d.name";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                stats.put(rs.getString("name"), rs.getInt("employee_count"));
            }
        }

        return stats;
    }

    public boolean validateLogin(String email, String password) throws SQLException {
        String query = "SELECT COUNT(*) FROM employees WHERE email = ? AND password = ?";

        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM employees WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public boolean updatePassword(String email, String newPassword) throws SQLException {
        String sql = "UPDATE employees SET password = ? WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        }
    }
}
