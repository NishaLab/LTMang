/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DAO;

import static DAO.DAO.conn;
import Model.*;
import java.sql.*;

/**
 *
 * @author LEGION
 */
public class PlayedQuestionDAO extends DAO {

    public boolean savePlayedQuestion(PlayedQuestion question, int session) {
        String sql = "INSERT INTO tblplayedquestion(isCorrect, Question_id, Session_id, time, chosenAnswer) VALUES(?,?,?,?,?)";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBoolean(1, question.isIsCorrect());
            ps.setInt(2, question.getQuestion().getId());
            ps.setInt(3, session);
            ps.setInt(4, question.getTime());
            ps.setInt(5, question.getChosenAnswer());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public PlayedQuestion getPlayedQuestionById(int id) {
        PlayedQuestion question = new PlayedQuestion();
        QuestionDAO qd = new QuestionDAO();
        String sql = "select * from tblplayedquestion where id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                question.setId(rs.getInt("id"));
                question.setIsCorrect(rs.getBoolean("isCorrect"));
                int question_id = rs.getInt("Question_id");
                Question tmp = qd.getQuestionById(question_id);
                question.setQuestion(tmp);
                question.setChosenAnswer(rs.getInt("chosenAnswer"));
                question.setTime(rs.getInt("time"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return question;
    }
}
