package entities;

public class Comentaires {


    private int id;
    private int post_id;
    private int employeeId;
    private String content;



    public Comentaires(int id, int post_id, int employeeId, String content) {
        this.id = id;
        this.post_id = post_id;
        this.employeeId = employeeId;
        this.content = content;
    }

    public Comentaires(int post_id, int employeeId, String content) {
        this.post_id = post_id;
        this.employeeId = employeeId;
        this.content = content;
    }


    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }


    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
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


    public Comentaires(int id, String content) {
    }


    @Override
    public String toString() {
        return "Comentaires{" +
                "id=" + id +
                ", post_id=" + post_id +
                ", employeeId=" + employeeId +
                ", content='" + content + '\'' +
                '}';
    }
}
