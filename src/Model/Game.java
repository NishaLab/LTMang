/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author LEGION
 */
public class Game implements Serializable {

    private int id;
    private ArrayList<Session> sessions;
    private Date playDate;

    public Game() {
    }

    public Game(int id, ArrayList<Session> sessions, Date playDate) {
        this.id = id;
        this.sessions = sessions;
        this.playDate = playDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<Session> getSessions() {
        return sessions;
    }

    public void setSessions(ArrayList<Session> sessions) {
        this.sessions = sessions;
    }

    public Date getPlayDate() {
        return playDate;
    }

    public void setPlayDate(Date playDate) {
        this.playDate = playDate;
    }

    @Override
    public String toString() {
        return "Game{" + "id=" + id + ", sessions=" + sessions + ", playDate=" + playDate + '}';
    }
    
}
