/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;

/**
 *
 * @author LEGION
 */
public class PlayedQuestion implements Serializable{
    private int id;
    private boolean isCorrect;
    private Question question;

    public PlayedQuestion() {
    }

    public PlayedQuestion(int id, boolean isCorrect, Question question) {
        this.id = id;
        this.isCorrect = isCorrect;
        this.question = question;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isIsCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @Override
    public String toString() {
        return "PlayedQuestion{" + "id=" + id + ", isCorrect=" + isCorrect + ", question=" + question + '}';
    }
    
}
