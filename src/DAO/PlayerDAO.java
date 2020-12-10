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

    public void updatePlayerName(Player player) {
        String sql = "UPDATE `tblplayer` SET `name` = '?' WHERE (`id` = ?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, player.getName());
            ps.setInt(2, player.getId());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Player getPlayerByAddress(String address) {
        String sql = "Select * from tblplayer WHERE address = ?";
        Player player = new Player();
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, address);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                player.setId(rs.getInt("id"));
                player.setName(rs.getString("name"));
                player.setAddress(rs.getString("address"));
            }
//            System.out.println("player in query: " + player);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return player;
    }

    public boolean createPlayer(Player player) {
        try {
            String sql = "INSERT INTO tblPlayer(name, address) VALUES(?,?)";
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, player.getName());
            ps.setString(2, player.getAddress());
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            if(generatedKeys.next()){
                player.setId(generatedKeys.getInt(1));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
