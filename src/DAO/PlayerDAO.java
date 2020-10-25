/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.*;
import java.util.ArrayList;
import java.sql.*;

/**
 *
 * @author LEGION
 */
public class PlayerDAO extends DAO {

    public ArrayList<Player> getAllPlayer() {
        ArrayList<Player> res = new ArrayList<>();
        String sql = "Select * from question";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Player player = new Player();
                player.setId(rs.getInt("id"));
                player.setAddress(rs.getString("address"));
                res.add(player);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
