package services;

import entities.Comentaires;
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
        String sql = "INSERT INTO gestionpub (content) VALUES (?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, pos.getContent());


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
        String sql = "SELECT * FROM gestionpub"; // Pas besoin de p.* et alias ici

        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Post pos = new Post(
                        rs.getInt("id"),
                        rs.getString("content")
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





































        public List<String> getCommentairesParPostId(int postId) {
            List<String> commentaires = new ArrayList<>();

            String sql = "SELECT content FROM commentaires WHERE post_id = ?";


            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setInt(1, postId);

                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    commentaires.add(rs.getString("content"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return commentaires;
        }






    public void ajouterCommentaire(int postId, String commentaire) {

        String sql = "INSERT INTO commentaires (content, post_id) VALUES (?, ?)";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, commentaire);
            ps.setInt(2, postId);

            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }






    }
