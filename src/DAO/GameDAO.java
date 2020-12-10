/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DAO.conn;
import Model.*;
import java.util.ArrayList;
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

    public boolean saveGame(Game game) {
        SessionDAO sd = new SessionDAO();
        String sql = "INSERT INTO tblgame(playDate) VALUES(?)";
        PlayedQuestionDAO pqd = new PlayedQuestionDAO();
        String sessionSQL = "INSERT INTO tblsession(Player_id, tblGame_id) VALUES(?,?)";
        String playedQuestionSQL = "INSERT INTO tblplayedquestion(isCorrect, Question_id, Session_id, time, chosenAnswer) VALUES(?,?,?,?,?)";
        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            java.sql.Timestamp sqldate = new java.sql.Timestamp(game.getPlayDate().getTime());
            ps.setTimestamp(1, sqldate);
            ps.executeUpdate();
            ResultSet generatedKeys = ps.getGeneratedKeys();
            System.out.println("Pass create Game");
            if (generatedKeys.next()) {
                game.setId(generatedKeys.getInt(1));
                for (Session session : game.getSessions()) {
                    ps = conn.prepareStatement(sessionSQL, Statement.RETURN_GENERATED_KEYS);
                    ps.setInt(1, session.getPlayer().getId());
                    ps.setInt(2, game.getId());
                    ps.executeUpdate();
                    generatedKeys = ps.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        session.setId(generatedKeys.getInt(1));
                        for (PlayedQuestion playedQuestion : session.getQuestion()) {
                            ps = conn.prepareStatement(playedQuestionSQL);
                            ps.setBoolean(1, playedQuestion.isIsCorrect());
                            ps.setInt(2, playedQuestion.getQuestion().getId());
                            ps.setInt(3, session.getId());
                            ps.setInt(4, playedQuestion.getTime());
                            ps.setInt(5, playedQuestion.getChosenAnswer());
                            ps.executeUpdate();
                        }
                    }
                }
            }
            conn.commit();
        } catch (Exception e) {
            try {
                conn.rollback();
            } catch (Exception f) {
                f.printStackTrace();
                return false;
            }
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
