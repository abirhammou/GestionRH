package entities;

import java.util.Date;

public class Recruitment {
    private int id;
    private String name;
    private Date date;
    private String status;
    private String phone;
    private String email;

    public Recruitment() {}
    public Recruitment(int id, String name, Date date, String status, String phone, String email) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.status = status;
        this.phone = phone;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "Recruitment{" +
                "name='" + name + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
