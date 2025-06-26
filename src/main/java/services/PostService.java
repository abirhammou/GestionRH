package services;

import entities.Comentaires;
import entities.Departement;
import entities.Employees;
import entities.Post;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import utils.Database;

public class PostService {
    private int id;
    private String userId;
    private String content;
    private String commenter;


    private Connection cnx;

    public PostService(){

        cnx = Database.getInstance().getCnx();

    }



    public void addPost(Post pos) throws SQLException {
        String sql = "INSERT INTO gestionpub (content, employee_id, image_path) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pos.getContent());
            ps.setInt(2, pos.getEmployeeId());
            ps.setString(3, pos.getImagePath());

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating post failed, no rows affected.");
            }

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pos.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    public ObservableList<Post> searchPosts(String keyword) throws SQLException {
        ObservableList<Post> posts = FXCollections.observableArrayList();
        String sql = "SELECT p.*, p.content FROM gestionpub p " +
                "LEFT JOIN employees e ON p.employee_id = e.id " +
                "WHERE p.content LIKE ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Post pos = new Post(
                            rs.getInt("id"),
                            rs.getInt("employee_id"),
                            rs.getString("content"),
                            rs.getString("image_path")

                    );
                    posts.add(pos);
                }
            }
        }
        return posts;
    }

    public void updatePost(Post pos) throws SQLException {
        String sql = "UPDATE gestionpub SET content = ?  WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, pos.getContent());
            ps.setInt(2, pos.getId());
            ps.executeUpdate();
        }
    }




    public void deletePost(int id) throws SQLException {
        String sql = "DELETE FROM gestionpub WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }



    public ObservableList<Post> getAllPosts() throws SQLException {
        ObservableList<Post> posts = FXCollections.observableArrayList();
        String sql = "SELECT p.*, e.name as employee_name FROM gestionpub p " +
                "LEFT JOIN employees e ON p.employee_id = e.id";

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Post pos = new Post(
                        rs.getInt("id"),
                        rs.getInt("employee_id"),
                        rs.getString("content"),
                        rs.getString("image_path")

                );
                posts.add(pos);
            }
        }
        return posts;
    }












    public Post getById(int id) throws SQLException {
        String sql = "SELECT p.* FROM gestionpub p WHERE p.id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Post(
                            rs.getInt("id"),
                            rs.getString("content")
                    );
                }
            }
        }

        return null;
    }







    public List<Post> recuperer() throws SQLException {
        List<Post> posts = new ArrayList<>();
        String sql = "select * from gestionpub";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()){
            int id = rs.getInt("id");
            String content = rs.getString("content");

            Post p = new Post(id, content);
            posts.add(p);
        }

        return posts;
    }


    public List<Comentaires> getCommentairesParPostId(int postId) throws SQLException {
        List<Comentaires> commentaires = new ArrayList<>();
        String sql = "SELECT c.*, e.name as employee_name FROM commentaires c " +
                "LEFT JOIN employees e ON c.employee_id = e.id WHERE c.post_id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Comentaires comment = new Comentaires(
                        rs.getInt("id"),
                        rs.getInt("post_id"),
                        rs.getInt("employee_id"),
                        rs.getString("content")
                );
                commentaires.add(comment);
            }
        }
        return commentaires;
    }


    public void ajouterCommentaire(int postId, int employeeId, String commentaire) throws SQLException {
        String sql = "INSERT INTO commentaires (content, post_id, employee_id) VALUES (?, ?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, commentaire);
            ps.setInt(2, postId);
            ps.setInt(3, employeeId);
            ps.executeUpdate();
        }
    }



    public void deleteComment(int commentId) throws SQLException {
        String sql = "DELETE FROM commentaires WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            ps.executeUpdate();
        }
    }

    public Employees getEmployeeByEmail(String email) throws SQLException {
        String sql = "SELECT e.*, d.name as dep_name FROM employees e " +
                "LEFT JOIN departement d ON e.department_id = d.id WHERE e.email = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);

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

    public void updateComment(Comentaires comment) throws SQLException {
        String sql = "UPDATE commentaires SET content = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, comment.getContent());
            ps.setInt(2, comment.getId());
            ps.executeUpdate();
        }
    }

}
