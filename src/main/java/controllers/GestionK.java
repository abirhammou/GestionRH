package controllers;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.cell.PropertyValueFactory;
import services.PostService;
import entities.Post;
import javafx.fxml.Initializable;

import java.awt.event.MouseEvent;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Pattern;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

public class GestionK implements Initializable {



    @FXML
    private TextField ajtcomm;

    @FXML
    private TableView<Post> P;

    @FXML
    private Button btnAddPost;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnUpdate;

    @FXML
    private TextArea tfComment;


    @FXML
    private AnchorPane home;

    @FXML
    private AnchorPane homeadd;

    @FXML
    private TableColumn<Post, String> publication;

    @FXML
    private Button btnAddComment;

    @FXML
    private Button btnCommenter;


    @FXML
    private TextArea trcontent;

    @FXML
    private TableColumn<Post, String> content_t;

    @FXML
    private Button btnDeleteComment;


    @FXML
    private Button btnUpdateComment;

    @FXML
    private TextField btnSearch;

    @FXML
    private AnchorPane Post;



    @FXML
    private Button home_btn;

    @FXML
    private Button post_btn;

    private ObservableList<Post> postList = FXCollections.observableArrayList();
    PostService postService = new PostService();
    private FilteredList<Post> filteredList;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupP();

    }







    private void setupP() {
        content_t.setCellValueFactory(new PropertyValueFactory<>("content"));


        refreshP();
    }


    private void refreshP() {
        try {
            postList.clear();
            postList.addAll(postService.getAllPosts());
            P.setItems(postList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load post: " + e.getMessage());
        }
    }



    @FXML
    void handleAddPost(ActionEvent event) {

        try {

        String content = trcontent.getText().trim();



            if (content.isEmpty()) {
            showAlert("Error", "All fields are required!");
            return;
        }


            Post newpost = new Post(content);

            postService.addPost(newpost);



            showAlert("Succès", "Publication ajoutée avec succès !");
            clearForm();
            refreshTable();
        } catch (SQLException e) {
            showAlert("Erreur base de données", "Erreur lors de l'ajout de la publication : " + e.getMessage());
        }
    }


    private void refreshTable() {
        try {
            postList.clear();
            postList.addAll(postService.recuperer());
            P.setItems(postList);
        } catch (SQLException e) {
            showAlert("Error", "Failed to load post: " + e.getMessage());
        }
    }







    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        trcontent.clear();

    }

    @FXML
    void handleDeletePost(ActionEvent event) {

        Post selected = P.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a post to delete.");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this Post?");

        if (confirmation.showAndWait().get() == ButtonType.OK) {
            try {
                postService.deletePost(selected.getId());
                showAlert("Success", "Post deleted successfully!");
                refreshP();
                clearPostForm();
            } catch (SQLException e) {
                showAlert("Error", "Error deleting post: " + e.getMessage());
            }
        }





    }

    private void clearPostForm() {
        trcontent.clear();







    }
    @FXML
    void handleUpdatePost(ActionEvent event) {

            Post selected = P.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showAlert("Warning", "Please select a post to update.");
                return;
            }

            try {
                String content = trcontent.getText().trim();

                if (content.isEmpty()) {
                    showAlert("Error", "All fields are required!");
                    return;
                }

                selected.setContent(content);


                postService.updatePost(selected);
                showAlert("Success", "Post updated successfully!");
                refreshP();
                clearPostForm();
            } catch (SQLException e) {
                showAlert("Error", "Error updating post: " + e.getMessage());
            }
        }





    @FXML
    private void handlePClick() {
        Post selected = P.getSelectionModel().getSelectedItem();
        if (selected != null) {
            trcontent.setText(selected.getContent());

        }
    }





    @FXML
    public void onbtnCommenterClicked(ActionEvent actionEvent) {
        Post selectedPost = P.getSelectionModel().getSelectedItem();
        if (selectedPost != null) {
            // Récupérer les commentaires liés
            List<String> commentaires = postService.getCommentairesParPostId(selectedPost.getId());

            // Construire le texte à afficher
            StringBuilder sb = new StringBuilder();
            sb.append("📄 Contenu du post :\n");
            sb.append(selectedPost.getContent()).append("\n\n");

            sb.append("💬 Commentaires :\n");
            for (String commentaire : commentaires) {
                sb.append("- ").append(commentaire).append("\n");
            }

            // Affichage dans le TextArea
            tfComment.setText(sb.toString());

        }
    }

    @FXML

  //  public void onbtnAddCommentClicked(ActionEvent actionEvent) {


   //     Post selectedPost = P.getSelectionModel().getSelectedItem();
     //   String ajtcommer = ajtcomm.getText();

       // if (selectedPost != null && !ajtcommer.isEmpty()) {
         //   postService.ajouterCommentaire(selectedPost.getId(), ajtcommer);
           // tfComment.clear();

            // Recharger les commentaires dans le TextArea
            //List<String> commentaires = postService.getCommentairesParPostId(selectedPost.getId());
            //StringBuilder sb = new StringBuilder();
            //for (String commentaire : commentaires) {
              //  sb.append("- ").append(commentaire).append("\n");
            //}
            //tfComment.setText(sb.toString());
        //}



    public void onbtnAddCommentClicked(ActionEvent actionEvent) {
        Post selectedPost = P.getSelectionModel().getSelectedItem();
        String ajtcommer = ajtcomm.getText(); // champ de saisie

        if (selectedPost != null && !ajtcommer.isEmpty()) {
            // Ajouter le commentaire à la base
            postService.ajouterCommentaire(selectedPost.getId(), ajtcommer);
            ajtcomm.clear(); // On vide le champ de saisie

            // Recharger les commentaires dans le TextArea tfComment
            List<String> commentaires = postService.getCommentairesParPostId(selectedPost.getId());
            StringBuilder sb = new StringBuilder();
            sb.append("📄 Contenu du post :\n");
            sb.append(selectedPost.getContent()).append("\n\n");

            sb.append("💬 Commentaires :\n");
            for (String commentaire : commentaires) {
                sb.append("- ").append(commentaire).append("\n");
            }

            tfComment.setText(sb.toString()); // <-- Affichage dans le TextArea
        }
    }










    @FXML
    private void deleteSelectedText(ActionEvent event) {
        String selected = tfComment.getSelectedText();
        if (!selected.isEmpty()) {
            String original = tfComment.getText();
            String updated = original.replaceFirst(Pattern.quote(selected), "");
            tfComment.setText(updated);
        } else {
            System.out.println("Aucun texte sélectionné !");
        }
    }






    @FXML
    private void updateSelectedComment() {
        String selectedText = tfComment.getSelectedText();
        String updatedText = ajtcomm.getText();

        if (!selectedText.isEmpty() && !updatedText.isEmpty()) {
            String fullText = tfComment.getText();
            // Remplace la première occurrence du texte sélectionné par le texte mis à jour
            String newFullText = fullText.replaceFirst(Pattern.quote(selectedText), updatedText);
            tfComment.setText(newFullText);
            ajtcomm.clear();
        } else {
            System.out.println("Sélection ou texte de remplacement manquant !");
        }
    }







    @FXML
    private void searchComment() {
        String keyword = btnSearch.getText().trim();
        String fullText = tfComment.getText();

        if (!keyword.isEmpty()) {
            int index = fullText.indexOf(keyword);
            if (index >= 0) {
                // Sélectionne le texte trouvé
                tfComment.selectRange(index, index + keyword.length());
            } else {
                System.out.println("Mot-clé non trouvé !");
            }
        }
    }







    public void switchForm(ActionEvent event) {

        if (event.getSource() == home_btn) {
            Post.setVisible(false);
            home.setVisible(true);

        } else if (event.getSource() == post_btn) {
            Post.setVisible(true);
            home.setVisible(false);
        }


    }




}





