package controllers;

import entities.Comentaires;
import entities.Employees;
import entities.Post;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.EmployeeService;
import services.PostService;



import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

    public class GestionK implements Initializable {

    @FXML private AnchorPane home;
    @FXML private AnchorPane contentPane;
    @FXML private AnchorPane Post;
    @FXML private Button home_btn;
    @FXML private Button post_btn;
    @FXML private TextArea postContent;
    @FXML private Button btnAddPost;
    @FXML private VBox postsContainer;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField commentField;
    @FXML private Button btnAddComment;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField roleField;
    @FXML private TextField departmentField;
    @FXML private Label welcomeLabel;
    @FXML private Button modifyBtn;
    @FXML private Button logoutBtn;

    @FXML
    private TextField TSearch;


        @FXML
        private Button btnUploadImage;
        private String imagePath;

    private Employees currentEmployee;
    private boolean isEditMode = false;
    private PostService postService = new PostService();
        private AfficherpostsController afficherpostsController;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        contentPane.setVisible(true);
        home.setVisible(false);
        try {
            loadPosts();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load posts: " + e.getMessage());
        }

        modifyBtn.setOnAction(this::handleModify);
        logoutBtn.setOnAction(this::handleLogout);
        btnUploadImage.setOnAction(this::handleUploadImage);
    }



    private void handleUploadImage(ActionEvent event) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choisir une image");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                imagePath = selectedFile.getAbsolutePath();
                showAlert("Succès", "Image sélectionnée: " + selectedFile.getName());
            }
        }


    public void setEmployeeData(Employees employee) {
        this.currentEmployee = employee;
        nameField.setText(employee.getName());
        emailField.setText(employee.getEmail());
        phoneField.setText(String.valueOf(employee.getPhone()));
        roleField.setText(employee.getRole());
        departmentField.setText(employee.getDepartement().getName());
        welcomeLabel.setText("Welcome, " + employee.getName());
    }

    @FXML
    private void handleModify(ActionEvent event) {
        if (!isEditMode) {
            nameField.setEditable(true);
            phoneField.setEditable(true);
            modifyBtn.setText("Save");
            isEditMode = true;
        } else {
            if (nameField.getText() == null || nameField.getText().trim().isEmpty()) {
                showAlert("Error", "Name field cannot be empty!");
                nameField.setStyle("-fx-border-color: #00a6ff;"); // Optionnel: mettre en évidence le champ
                return;
            }



            try {
                currentEmployee.setName(nameField.getText());
                currentEmployee.setPhone(Integer.parseInt(phoneField.getText()));

                EmployeeService employeeService = new EmployeeService();
                employeeService.updateEmployee(currentEmployee);

                nameField.setEditable(false);
                phoneField.setEditable(false);
                modifyBtn.setText("Modify");
                isEditMode = false;

                showAlert("Success", "Profile updated successfully");
            } catch (NumberFormatException e) {
                showAlert("Error", "Phone number must be a valid number");
            } catch (SQLException e) {
                showAlert("Error", "Failed to update profile: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadPosts() throws SQLException {
        postsContainer.getChildren().clear();
        List<Post> allPosts = postService.getAllPosts();

        for (Post post : allPosts) {
            VBox postCard = createPostCard(post);
            postsContainer.getChildren().add(postCard);
        }
    }

    private void handleUpdatePost(Post post, TextArea contentArea, Button updateBtn) {
        if (contentArea.isEditable()) {
            try {
                String newContent = contentArea.getText().trim();
                if (!newContent.isEmpty()) {
                    post.setContent(newContent);
                    postService.updatePost(post);
                    contentArea.setEditable(false);
                    updateBtn.setText("Edit");
                    showAlert("Success", "Post updated successfully");
                } else {
                    showAlert("Error", "Post content cannot be empty");
                    contentArea.setText(post.getContent());
                    contentArea.setEditable(false);
                    updateBtn.setText("Edit");
                }
            } catch (SQLException e) {
                showAlert("Error", "Failed to update post: " + e.getMessage());
                contentArea.setText(post.getContent());
                contentArea.setEditable(false);
                updateBtn.setText("Edit");
            }
        } else {
            contentArea.setEditable(true);
            updateBtn.setText("Save");
        }
    }

    private VBox createPostCard(Post post) {
        VBox postCard = new VBox(10);
        postCard.setStyle("-fx-background-color: #ffffff; -fx-padding: 15; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5;");

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label username = new Label(getEmployeeName(post.getEmployeeId()));
        username.setStyle("-fx-font-weight: bold; -fx-font-size: 14;");

        TextArea contentArea = new TextArea(post.getContent());
        contentArea.setWrapText(true);
        contentArea.setEditable(false);
        contentArea.setStyle("-fx-background-color: transparent; -fx-border-width: 0;");


        if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
            try {
                Image image = new Image(new File(post.getImagePath()).toURI().toString());
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(300);
                imageView.setPreserveRatio(true);
                postCard.getChildren().add(imageView);
            } catch (Exception e) {
                System.err.println("Error loading image: " + e.getMessage());
            }
        }








        if (currentEmployee != null && post.getEmployeeId() == currentEmployee.getId()) {
            Button updatePostBtn = new Button("Edit");
            updatePostBtn.setStyle("-fx-background-color: #256b47; -fx-text-fill: white;");
            updatePostBtn.setOnAction(e -> handleUpdatePost(post, contentArea, updatePostBtn));

            Button deletePostBtn = new Button("Delete");
            deletePostBtn.setStyle("-fx-background-color: #971e18; -fx-text-fill: white;");
            deletePostBtn.setOnAction(e -> deletePost(post));

            header.getChildren().addAll(username, updatePostBtn, deletePostBtn);
        } else {
            header.getChildren().add(username);
        }

        VBox commentsSection = new VBox(5);
        commentsSection.setStyle("-fx-padding: 10 0 0 0;");
        loadComments(post, commentsSection);

        HBox commentInput = new HBox(10);
        if (currentEmployee != null) {
            TextField newCommentField = new TextField();
            newCommentField.setPromptText("Add a comment...");
            newCommentField.setStyle("-fx-background-color: #f5f5f5; -fx-border-radius: 15; -fx-padding: 5 10;");

            Button addCommentBtn = new Button("Post");
            addCommentBtn.setStyle("-fx-background-color: #104975; -fx-text-fill: white;");
            addCommentBtn.setOnAction(e -> {
                if (!newCommentField.getText().isEmpty()) {
                    try {
                        postService.ajouterCommentaire(post.getId(), currentEmployee.getId(), newCommentField.getText());
                        newCommentField.clear();
                        loadComments(post, commentsSection);
                    } catch (SQLException ex) {
                        showAlert("Error", "Failed to add comment");
                    }
                }
            });

            commentInput.getChildren().addAll(newCommentField, addCommentBtn);
        }

        postCard.getChildren().addAll(header, contentArea, commentsSection, commentInput);
        return postCard;
    }

    private String getEmployeeName(int employeeId) {
        try {
            EmployeeService employeeService = new EmployeeService();
            Employees employee = employeeService.getById(employeeId);
            return employee != null ? employee.getName() : "Unknown";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

    private void loadComments(Post post, VBox commentsSection) {
        commentsSection.getChildren().clear();
        try {
            List<Comentaires> comments = postService.getCommentairesParPostId(post.getId());
            if (comments != null && !comments.isEmpty()) {
                for (Comentaires comment : comments) {
                    HBox commentBox = new HBox(5);
                    commentBox.setAlignment(Pos.CENTER_LEFT);

                    String employeeName = getEmployeeName(comment.getEmployeeId());

                    TextField editCommentField = new TextField(comment.getContent());
                    editCommentField.setVisible(false);
                    editCommentField.setMaxWidth(400);

                    Label commentLabel = new Label(employeeName + ": " + comment.getContent());
                    commentLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                    commentLabel.setWrapText(true);
                    commentLabel.setMaxWidth(400);

                    if (currentEmployee != null && comment.getEmployeeId() == currentEmployee.getId()) {
                        Button editBtn = new Button("Edit");
                        editBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 12;");
                        editBtn.setOnAction(e -> {
                            if (editBtn.getText().equals("Edit")) {
                                // Passer en mode édition
                                commentLabel.setVisible(false);
                                editCommentField.setVisible(true);
                                editCommentField.setText(comment.getContent());
                                editBtn.setText("Save");
                            } else {
                                try {
                                    comment.setContent(editCommentField.getText());
                                    postService.updateComment(comment);
                                    commentLabel.setText(employeeName + ": " + comment.getContent());
                                    commentLabel.setVisible(true);
                                    editCommentField.setVisible(false);
                                    editBtn.setText("Edit");
                                    showAlert("Success", "Comment updated successfully");
                                } catch (SQLException ex) {
                                    showAlert("Error", "Failed to update comment");
                                }
                            }
                        });

                        Button deleteBtn = new Button("Delete");
                        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 12;");
                        deleteBtn.setOnAction(e -> {
                            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmationAlert.setTitle("Delete Comment");
                            confirmationAlert.setHeaderText("Confirm Deletion");
                            confirmationAlert.setContentText("Are you sure you want to delete this comment?");

                            Optional<ButtonType> result = confirmationAlert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                try {
                                    postService.deleteComment(comment.getId());
                                    loadComments(post, commentsSection);
                                    showAlert("Success", "Comment deleted successfully");
                                } catch (SQLException ex) {
                                    showAlert("Error", "Failed to delete comment");
                                }
                            }
                        });

                        HBox buttonBox = new HBox(5, editBtn, deleteBtn);
                        commentBox.getChildren().addAll(commentLabel, editCommentField, buttonBox);
                    } else {
                        commentBox.getChildren().add(commentLabel);
                    }

                    commentsSection.getChildren().add(commentBox);
                }
            } else {
                Label noCommentsLabel = new Label("No comments yet");
                noCommentsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: gray;");
                commentsSection.getChildren().add(noCommentsLabel);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Error loading comments");
            errorLabel.setStyle("-fx-text-fill: red;");
            commentsSection.getChildren().add(errorLabel);
            e.printStackTrace();
        }
    }

    @FXML
    void handleAddPost(ActionEvent event) {
        try {
            String content = postContent.getText().trim();

            if (content.isEmpty()) {
                showAlert("Error", "Post content cannot be empty!");
                return;
            }

            Post newPost = new Post(content, currentEmployee.getId());
            newPost.setImagePath(imagePath);
            postService.addPost(newPost);
            postContent.clear();
            imagePath = null;
            loadPosts();
        } catch (SQLException e) {
            showAlert("Error", "Failed to add post: " + e.getMessage());
        }
    }

    private void deletePost(Post post) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this post?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    postService.deletePost(post.getId());
                    loadPosts();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete post: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void switchForm(ActionEvent event) {
        if (event.getSource() == home_btn) {
            home.setVisible(true);
            contentPane.setVisible(false);
        } else if (event.getSource() == post_btn) {
            contentPane.setVisible(true);
            home.setVisible(false);
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            Stage currentStage = (Stage) logoutBtn.getScene().getWindow();
            currentStage.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("Login");
            loginStage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
            e.printStackTrace();
        }
    }



        public void setAfficherpostsController(AfficherpostsController controller) {
            this.afficherpostsController = controller;
        }

        @FXML
        private void handleSearch(ActionEvent event) {
            try {
                String keyword = TSearch.getText().trim();

                if (keyword.isEmpty()) {
                    loadPosts(); // Recharger tous les posts si la recherche est vide
                    return;
                }

                // Effectuer la recherche
                List<Post> searchResults = postService.searchPosts(keyword);

                // Afficher les résultats
                displaySearchResults(searchResults);

            } catch (SQLException e) {
                showAlert("Erreur", "Une erreur est survenue lors de la recherche : " + e.getMessage());
                e.printStackTrace();
            }
        }

        private void displaySearchResults(List<Post> results) {
            postsContainer.getChildren().clear(); // Vider le conteneur actuel

            if (results.isEmpty()) {
                Label noResults = new Label("Aucun résultat trouvé");
                noResults.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
                postsContainer.getChildren().add(noResults);
                return;
            }

            for (Post post : results) {
                VBox postCard = createPostCard(post); // Utilise votre méthode existante
                postsContainer.getChildren().add(postCard);
            }
        }
    }

















