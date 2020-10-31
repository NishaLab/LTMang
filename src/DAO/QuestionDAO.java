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
public class QuestionDAO extends DAO {

    public ArrayList<Question> getAllQuestion() {
        ArrayList<Question> res = new ArrayList<>();
        String sql = "select * from tblquestion";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Question question = new Question();
                question.setId(rs.getInt("id"));
                question.setTitle(rs.getString("title"));
                question.setQuestionContent(rs.getString("content"));
                question.setAnswerA(rs.getString("answerA"));
                question.setAnswerB(rs.getString("answerB"));
                question.setAnswerC(rs.getString("answerC"));
                question.setAnswerD(rs.getString("answerD"));
                question.setCorrectAnswer(rs.getInt("answer"));
                res.add(question);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    public Question getQuestionById(int id) {
        Question question = new Question();
        String sql = "select * from tblquestion where id = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                question.setId(rs.getInt("id"));
                question.setAnswerA(rs.getString("answerA"));
                question.setAnswerB(rs.getString("answerB"));
                question.setAnswerC(rs.getString("answerC"));
                question.setAnswerD(rs.getString("answerD"));
                question.setCorrectAnswer(rs.getInt("answer"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return question;
    }
}
