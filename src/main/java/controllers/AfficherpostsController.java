package controllers;
import entities.Post;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import services.PostService;
import java.sql.SQLException;
import java.util.List;
public class AfficherpostsController {


    @FXML
    private ListView<Post> listView;


    @FXML
    void initialize() {
        try {
            PostService ps = new PostService();
            List<Post> postList = ps.recuperer();
            listView.getItems().addAll(postList);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }




}
