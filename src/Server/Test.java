/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.*;
import DAO.*;
import java.util.*;

/**
 *
 * @author LEGION
 */
public class Test {

    public static void main(String[] args) {
        GameDAO gd = new GameDAO();
        Game game = new Game();
        Player player = new Player(1, "123456");
        //add question 
        Question question1 = new Question(1, "title1", "123", "!23", "!23", "!23", "!23", 1);
        PlayedQuestion pq1 = new PlayedQuestion();
        pq1.setChosenAnswer(1);
        pq1.setIsCorrect(true);
        pq1.setQuestion(question1);
        pq1.setTime(10);

        Question question2 = new Question(1, "title2", "asdas", "!asdasd", "!asdads", "!asd", "!23", 3);
        PlayedQuestion pq2 = new PlayedQuestion();
        pq2.setChosenAnswer(1);
        pq2.setIsCorrect(true);
        pq2.setQuestion(question2);
        pq2.setTime(10);

        Question question3 = new Question(1, "title3", "asd", "!assd", "!asd", "!asdasd", "!assd", 2);
        PlayedQuestion pq3 = new PlayedQuestion();
        pq3.setChosenAnswer(1);
        pq3.setIsCorrect(true);
        pq3.setQuestion(question3);
        pq3.setTime(10);

        Session session = new Session();
        session.setPlayer(player);
        ArrayList<PlayedQuestion> q = new ArrayList<>();
        q.add(pq1);
        q.add(pq2);
        q.add(pq3);

        session.setQuestion(q);
        game.setPlayDate(new Date());
        ArrayList<Session> ss = new ArrayList<>();
        ss.add(session);
        game.setSessions(ss);
        gd.saveGame(game);
    }
}
