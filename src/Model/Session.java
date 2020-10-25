/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

/**
 *
 * @author LEGION
 */
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class Session implements Serializable{
    private int id;
    private ArrayList<PlayedQuestion> question;
    private Player player;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<PlayedQuestion> getQuestion() {
        return question;
    }

    public void setQuestion(ArrayList<PlayedQuestion> question) {
        this.question = question;
    }



    public Session(int id, ArrayList<PlayedQuestion> question, Player player) {
        this.id = id;
        this.question = question;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }



    public Session() {
    }

    @Override
    public String toString() {
        return "Session{" + "id=" + id + ", question=" + question + ", player=" + player + '}';
    }


    
    
}
