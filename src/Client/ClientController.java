/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 * @author LEGION
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Date;

import Model.Question;
import keeptoo.*;

public class ClientController implements Runnable {

    final ClientFrame frame;
    private Socket client;

    private Question question;
    //    private DataOutputStream dos;
//    private DataInputStream dis;
    private ObjectOutputStream dos;
    private ObjectInputStream dis;
    private String answer;

    public ClientController(ClientFrame frame) {
        this.frame = frame;
        try {
            this.client = new Socket("127.0.0.1", 5056);
//            this.dos = new DataOutputStream(client.getOutputStream());
//            this.dis = new DataInputStream(client.getInputStream());
            this.dos = new ObjectOutputStream(client.getOutputStream());
            this.dis = new ObjectInputStream(client.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        setButtonAction();
    }

    void setButtonAction() {

        KButton aBtt = frame.getaBtt();
        KButton bBtt = frame.getbBtt();
        KButton cBtt = frame.getcBtt();
        KButton dBtt = frame.getdBtt();
        aBtt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked A");
                try {
                    answer = "A";
                    dos.writeUTF("A");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        bBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked B");
                try {
                    answer = "B";
                    dos.writeUTF("B");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });
        cBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked C");
                try {
                    answer = "C";
                    dos.writeUTF("C");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        dBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked D");
                try {
                    answer = "D";
                    dos.writeUTF("D");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void run() {
        while (true) {
            answer = null;

            try {
                question = (Question) dis.readObject();
                System.out.println(question);

                frame.getQuestion().setText(question.getTitle() + ": " + question.getQuestionContent());
                frame.getaBtt().setText("A. " + question.getAnswerA());
                frame.getbBtt().setText("B. " + question.getAnswerB());
                frame.getcBtt().setText("C. " + question.getAnswerC());
                frame.getdBtt().setText("D. " + question.getAnswerD());

                //Wait for server to send 'Time out' message
                String timeout = dis.readUTF();


                System.out.println(timeout + ", " + answer);
                if (answer == null) {
                    answer = "No answer";
                    dos.writeUTF(answer);
                    dos.flush();
                }


            } catch (SocketException e) {
                System.out.println("Server is not available.");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

//        try {
//            sizeOfQuestionList = dis.readInt();
//            System.out.println(sizeOfQuestionList);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        while (true) {
//            if (sizeOfQuestionList == 0) {
//                break;
//            }
//            isTimedout = false;
//            try {
//                //String questionContent = dis.readUTF();
//                Question question = (Question) dis.readObject();
//                String questionContent = question.getQuestionContent();
//                frame.getQuestion().setText(question.getTitle() + ": " + questionContent);
//                String answerA = question.getAnswerA();
//                String answerB = question.getAnswerB();
//                String answerC = question.getAnswerC();
//                String answerD = question.getAnswerD();
//                start = new Date();
//                System.out.println(questionContent);
////                String answerA = dis.readUTF();
////                String answerB = dis.readUTF();
////                String answerC = dis.readUTF();
////                String answerD = dis.readUTF();
//                Thread.sleep(15000);
//
//
//                dos.writeUTF("No answer");
//                dos.flush();
//                System.out.println(question.getTitle() + ": \n"
//                        + " A. " + answerA + " B. " + answerB
//                        + " C. " + answerC + " D. " + answerD);
//                sizeOfQuestionList--;
//            } catch (Exception e) {
//                System.out.println(e.getMessage());
//                try {
//
//                    this.client.close();
//
//                } catch (Exception f) {
//                }
//            }
//        }
//        try {
//            dis.close();
//            dos.close();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
