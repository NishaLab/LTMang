/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import DAO.*;
import Model.*;

/**
 * @author LEGION
 */
public class ClientHandler extends Thread {

    private ObjectOutputStream dos;
    private ObjectInputStream dis;
    private Socket client;
    private Question question;
    private String response;
    private int timer;
    private CustomServer mainServer;

    public ClientHandler(ObjectOutputStream dos, ObjectInputStream dis, Socket client, Question question, int timer, CustomServer mainServer) {
        this.dos = dos;
        this.dis = dis;
        this.client = client;
        this.question = question;
        this.timer = timer;
        this.mainServer = mainServer;
    }

    public ObjectOutputStream getDos() {
        return dos;
    }

    public ObjectInputStream getDis() {
        return dis;
    }

    public Socket getClient() {
        return client;
    }


    public String getResponse() {
        return response;
    }


    public ClientHandler(ObjectOutputStream dos, ObjectInputStream dis, Socket client) {
        this.dos = dos;
        this.dis = dis;
        this.client = client;
    }

    public ClientHandler(ObjectOutputStream dos, ObjectInputStream dis, Socket client, Question question) {
        this.dos = dos;
        this.dis = dis;
        this.client = client;
        this.question = question;
    }

    public ClientHandler(ObjectOutputStream dos, ObjectInputStream dis, Socket client, Question question, int timer) {
        this.dos = dos;
        this.dis = dis;
        this.client = client;
        this.question = question;
        this.timer = timer;
    }


    public void setQuestion(Question question) {
        this.question = question;
    }


    @Override
    public void run() {

        //            dos.writeObject(question);
//            dos.flush();
//            dos.writeInt(timer);
//            dos.flush();
        new QuestionHandler(question, dos, timer).start();
        ResultHandler resultHandler = new ResultHandler(dis, mainServer);
        resultHandler.start();
        try {
            resultHandler.join();
            response = resultHandler.getResult();
            System.out.println("response: " + response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        System.out.println(question.getTitle() + " " + response);


        System.out.println("Thread's life end.");

//        while (true) {
//            questionTime = System.currentTimeMillis();
//            System.out.println("Here");
//
//            QuestionDAO questionDAO = new QuestionDAO();
//            question = questionDAO.getAllQuestion();
//            try {
//                dos.writeInt(question.size());
//                dos.flush();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            for (Question q : question) {
//                try {
////                        dos.writeUTF(q.getQuestionContent());
////                        dos.writeUTF(q.getAnswerA());
////                        dos.writeUTF(q.getAnswerB());
////                        dos.writeUTF(q.getAnswerC());
////                        dos.writeUTF(q.getAnswerD());
//
//                        //Thread.sleep(5000);
//                    dos.writeObject(q);
//                    dos.flush();
//
//                    timeCounter();
//
//
//                    String ans = dis.readUTF();
//
//                    System.out.println(ans);
////                    if (ans == null) {
////                        dos.writeBoolean(isTimedout);
////                        System.out.println("Time out.");
////                    } else {
////                        System.out.println(ans);
////                    }
//                    System.out.println("DONE");
//                } catch (Exception e) {
//                    System.out.println(e.getMessage());
//                }
//
//            }
//
//        }
//    }

    }
}


