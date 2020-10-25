/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import Model.*;
import java.util.*;
import java.sql.*;

/**
 *
 * @author LEGION
 */
public class GameDAO extends DAO {

    public Game getGameById(int id) {
        Game game = new Game();
        SessionDAO sd = new SessionDAO();
        String sql = "SELECT * FROM tblGame WHERE id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                game.setId(rs.getInt("id"));
                game.setPlayDate(rs.getTimestamp("playDate"));
                System.out.println(game.getId());
                ArrayList<Session> sessionList = sd.getAllSessionByGameId(id);
                game.setSessions(sessionList);
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return game;
    }
}
