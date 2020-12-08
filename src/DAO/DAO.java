/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author LEGION
 */
public class DAO {

    public static Connection conn;

    public DAO() {
        String url = "jdbc:mysql://localhost:3306/ltm?useSSL=false";
        String username = "root";
        String password = "12022020";
        try {
            conn = DriverManager.getConnection(url, username, password);
//            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
