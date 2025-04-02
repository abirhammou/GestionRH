package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final String url = "jdbc:mysql://localhost:3306/gestionrh";
    private final String username = "root";
    private final String password = "";
    private Connection cnx;

    private static Database instance;

    private Database(){
        try {
            cnx = DriverManager.getConnection(url, username, password);
            System.out.println("Connexion établie");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public static Database getInstance(){
        if(instance == null)
            instance = new Database();
        return instance;
    }

    public Connection getCnx() {
        return cnx;
    }
}
