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
        Question question = new Question(1, "title", "123", "!23", "!23", "!23", "!23", 1);
        PlayedQuestion pq = new PlayedQuestion();
        pq.setChosenAnswer(1);
        pq.setIsCorrect(true);
        pq.setQuestion(question);
        pq.setTime(10);
        Session session = new Session();
        session.setPlayer(player);
        ArrayList<PlayedQuestion> q = new ArrayList<>();
        q.add(pq);
        session.setQuestion(q);
        game.setPlayDate(new Date());
        ArrayList<Session> ss = new ArrayList<>();
        ss.add(session);
        game.setSessions(ss);
        gd.saveGame(game);
    }
}
