/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

/**
 *
 * @author LEGION
 */
import Model.*;
import java.util.ArrayList;
import java.sql.*;

public class SessionDAO extends DAO {

    public ArrayList<Session> getAllSessionByGameId(int id) {
        ArrayList<Session> res = new ArrayList<>();
        QuestionDAO qd = new QuestionDAO();
        String sessionSql = "SELECT * FROM tblSession WHERE tblGame_id = ?";
        String playerSQL = "SELECT * FROM tblPlayer WHERE id = ?";
        String playedQuestionSQL = "SELECT * FROM tblPlayedQuestion WHERE Session_id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sessionSql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Session session = new Session();
                System.out.println(rs.getInt("id"));
                session.setId(rs.getInt("id"));
                int player_id = rs.getInt("Player_id");
                ps = conn.prepareStatement(playerSQL);
                ps.setInt(1, player_id);
                ResultSet aux = ps.executeQuery();
                if (aux.next()) {
                    Player player = new Player();
                    player.setId(aux.getInt("id"));
                    player.setAddress(aux.getString("address"));
                    session.setPlayer(player);
                }
                ps = conn.prepareStatement(playedQuestionSQL);
                ps.setInt(1, id);
                aux = ps.executeQuery();
                ArrayList<PlayedQuestion> questionList = new ArrayList<>();
                while (aux.next()) {
                    PlayedQuestion pq = new PlayedQuestion();
                    pq.setId(aux.getInt("id"));
                    pq.setIsCorrect(aux.getBoolean("isCorrect"));
                    int question_id = aux.getInt("Question_id");
                    Question question = qd.getQuestionById(question_id);
                    pq.setQuestion(question);
                    questionList.add(pq);
                }
                session.setQuestion(questionList);
                res.add(session);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}