package entities;


import javafx.beans.property.SimpleStringProperty;

public class Post {
    private int id;
    private String content, user_id;


    public Post(int id, String user_id, String content, String commenter) {
        this.id= id;
        this.content = content;


    }
    public Post(String content) {

        this.content = content;



    }

    public Post(String user_id, String content) {

        this.content = content;

    }

    public Post(int id, String content) {
        this.id=id;
        this.content = content;

    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", content='" + content + '\'' +


                '}';
    }




}
