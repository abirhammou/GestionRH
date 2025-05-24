package entities;


import javafx.beans.property.SimpleStringProperty;

public class Post {
    private int id;
    private String content;
    private int employeeId;


    public Post(int id, int employeeId, String content) {
        this.id= id;
        this.content = content;
        this.employeeId=employeeId;

    }
    public Post(String content) {

        this.content = content;

    }

    public Post(String content, int employeeId) {

        this.content = content;
        this.employeeId = employeeId;
    }

    public Post(int id, String content) {
        this.id=id;
        this.content = content;

    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
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
                ", employeeId=" + employeeId +
                ", content='" + content + '\'' +


                '}';
    }




}
