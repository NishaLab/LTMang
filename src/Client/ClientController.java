/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 * @author LEGION
 */
import DAO.PlayerDAO;
import Model.Player;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Date;

import Model.Question;
import Model.Session;
import Server.CustomServer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import keeptoo.*;

public class ClientController {

    final ClientFrame frame;
    private Socket client;
    private int remotePort;
    private String host;

    private Question question;
    private int timer;
    //    private DataOutputStream dos;
//    private DataInputStream dis;
    private ObjectOutputStream dos;
    private ObjectInputStream dis;
    private String answer;

    public ClientController(ClientFrame frame) {
        this.frame = frame;
        host = "127.0.0.1";
        remotePort = 5056;

    }

    private void connect(String host, int remotePort) {
        try {
            this.client = new Socket(host, remotePort);
//            this.dos = new DataOutputStream(client.getOutputStream());
//            this.dis = new DataInputStream(client.getInputStream());
            this.dos = new ObjectOutputStream(client.getOutputStream());
            this.dis = new ObjectInputStream(client.getInputStream());
            Player player = new Player();
            player.setAddress(this.client.getLocalAddress().toString());
            player.setName(this.frame.getNameField().getText());
            this.frame.getNameField().setEditable(false);
            PlayerDAO pd = new PlayerDAO();
            player = pd.createPlayerIfNotExist(player);
            this.dos.writeObject(player);
            this.dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connected");
    }

    public void init() {
        setButtonAction();
    }

    private void startClient() {
        new Thread() {
            @Override
            public void run() {
                connect(host, remotePort);
                runClient();
            }

        }.start();
    }

    void setButtonAction() {

        KButton aBtt = frame.getaBtt();
        KButton bBtt = frame.getbBtt();
        KButton cBtt = frame.getcBtt();
        KButton dBtt = frame.getdBtt();
        KButton startBtn = frame.getExportBtt();

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Play btn");
                startClient();
                frame.getQuestion().setText("Connecting to server, Please Wait...");
            }
        });
        aBtt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked A");
                try {
                    answer = "1";
                    dos.writeUTF("1");
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
                    answer = "2";
                    dos.writeUTF("2");
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
                    answer = "3";
                    dos.writeUTF("3");
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
                    answer = "4";
                    dos.writeUTF("4");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void startTimeCountdown(int time) throws InterruptedException, IOException, ExecutionException {
        int timeLeft = time;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                for (int i = time; i >= 0; i--) {
                    publish(i);
                    Thread.sleep(1000);
                    //System.out.println(i);
                }
                return "Time's up.";
            }

            @Override
            protected void process(List chunks) {
                int val = (int) chunks.get(chunks.size() - 1);

                frame.getCounter().setText(String.valueOf(val));
            }

            @Override
            protected void done() {
                try {
                    String statusMsg = (String) get();
                    System.out.println(statusMsg);
                    frame.getCounter().setText(statusMsg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void runClient() {
        System.out.println("run");
        while (true) {
            answer = null;

            try {
                Object obj = dis.readObject();
                if ((String) obj == "Game Over") {

                } else {
                    question = (Question) obj;
                    timer = dis.readInt();
                    System.out.println(timer);
                    System.out.println(question);
                    startTimeCountdown(timer);

                    frame.getQuestion().setText(question.getTitle() + ": " + question.getQuestionContent());
                    frame.getaBtt().setText("A. " + question.getAnswerA());
                    frame.getbBtt().setText("B. " + question.getAnswerB());
                    frame.getcBtt().setText("C. " + question.getAnswerC());
                    frame.getdBtt().setText("D. " + question.getAnswerD());

                    //Wait for server to send 'Time out' message
                    String timeout = dis.readUTF();

                    if (answer == null) {
                        answer = "No answer";
                        dos.writeUTF(answer);
                        dos.flush();
                    }
                    System.out.println(timeout + ", " + answer);
                }

            } catch (SocketException e) {
                frame.getQuestion().setText("Server is not available.");
                System.out.println("Server is not available.");
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            dis.close();
            dos.close();
        } catch (IOException e) {
            System.out.println(client.getPort() + " disconnected");
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
    public void startVictoryScreen(Session s){
        
    }
}
