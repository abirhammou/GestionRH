package services;

import entities.Departement;
import utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartementService {
    private Connection cnx;

    public DepartementService() {
        cnx = Database.getInstance().getCnx();
    }

    public void ajouter(Departement d) throws SQLException {
        String sql = "insert into departement(name, description, phone, email) " +
                "values('" + d.getName() + "','" + d.getDescription() + "'," +
                d.getPhone() + ",'" + d.getEmail() + "')";
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
    }

    public void modifier(Departement d) throws SQLException {
        String sql = "update departement set name = ?, description = ?, phone = ?, email = ? where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, d.getName());
        ps.setString(2, d.getDescription());
        ps.setInt(3, d.getPhone());
        ps.setString(4, d.getEmail());
        ps.setInt(5, d.getId());
        ps.executeUpdate();
    }

    public void supprimer(Departement d) throws SQLException {
        String sql = "delete from departement where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, d.getId());
        ps.executeUpdate();
    }

    public List<Departement> recuperer() throws SQLException {
        List<Departement> departements = new ArrayList<>();
        String sql = "select * from departement";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Departement d = new Departement(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getInt("phone"),
                    rs.getString("email")
            );
            departements.add(d);
        }
        return departements;
    }

    public Departement getById(int id) throws SQLException {
        String sql = "SELECT * FROM departement WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Departement(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("description"),
                            rs.getInt("phone"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }
}
