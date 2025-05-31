package services;

import entities.Recruitment;
import utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RecruitmentService {
    private Connection cnx;

    public RecruitmentService() {
        cnx = Database.getInstance().getCnx();
    }

    public void ajouter(Recruitment d) throws SQLException {
        String sql = "insert into recruitment values (?, ?, ?, ?, ?)";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, d.getName());
        ps.setDate(2, (Date) d.getDate());
        ps.setString(3, d.getStatus());
        ps.setString(4, d.getPhone());
        ps.setString(5, d.getEmail());
        ps.executeUpdate();
    }

    public void modifier(Recruitment d) throws SQLException {
        String sql = "update recruitment set name = ?, date = ?, status = ?, phone = ?, email = ? where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, d.getName());
        ps.setDate(2, (Date) d.getDate());
        ps.setString(3, d.getStatus());
        ps.setString(4, d.getPhone());
        ps.setString(5, d.getEmail());
        ps.setInt(6, d.getId());
        ps.executeUpdate();
    }

    public void supprimer(Recruitment d) throws SQLException {
        String sql = "delete from recruitment where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, d.getId());
        ps.executeUpdate();
    }

    public List<Recruitment> recuperer() throws SQLException {
        List<Recruitment> recruitments = new ArrayList<>();
        String sql = "select * from recruitment";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()) {
            Recruitment d = new Recruitment(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getDate("date"),
                    rs.getString("status"),
                    rs.getString("phone"),
                    rs.getString("email")
            );
            recruitments.add(d);
        }
        return recruitments;
    }

    public Recruitment getById(int id) throws SQLException {
        String sql = "SELECT * FROM recruitment WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Recruitment(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDate("date"),
                            rs.getString("status"),
                            rs.getString("phone"),
                            rs.getString("email")
                    );
                }
            }
        }
        return null;
    }
}
