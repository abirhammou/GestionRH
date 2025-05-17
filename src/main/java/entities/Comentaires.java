package entities;

public class Comentaires {


    int id;

    public int getPost_id() {
        return post_id;
    }

    public void setPost_id(int post_id) {
        this.post_id = post_id;
    }

    int post_id;
    String content;


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
                ", content='" + content + '\'' +
                '}';
    }




}


