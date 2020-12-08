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
import Model.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

import Model.Question;
import Model.Session;
import Server.CustomServer;

import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import keeptoo.*;

public class ClientController {

    final ClientFrame frame;
    private Socket client;
    private int remotePort;
    private String host;
    private int pauseRemaining;

    private Question question;
    private int timer;

    private ObjectOutputStream dos;
    private ObjectInputStream dis;
    private String answer;
    private boolean isPause;

    public ClientController(ClientFrame frame) {
        this.frame = frame;
        host = "127.0.0.1";
//        host = "192.168.43.12";
        remotePort = 5056;

    }

    private void connect(String host, int remotePort) {
        try {
            pauseRemaining = 5;
            this.client = new Socket(host, remotePort);
            this.dos = new ObjectOutputStream(client.getOutputStream());
            this.dis = new ObjectInputStream(client.getInputStream());

            String ipAddress = this.client.getRemoteSocketAddress().toString();
            String playerName = this.frame.getNameField().getText();

            this.frame.getNameField().setEditable(false);

            this.dos.writeUTF(ipAddress);
            this.dos.writeUTF(playerName);
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
        KButton pauseBtn = frame.getPlayBtt();

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Play btn");
                startClient();
                frame.getQuestion().setText("Connected to server, waiting for the game to start...");
            }
        });

        pauseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (pauseRemaining>0) {
                    try {
                        dos.writeUTF("-1");
                        dos.flush();
                        isPause = true;
                        pauseRemaining--;
                        frame.getQuestion().setText("Game pause! You have " + pauseRemaining + " pause requests remaining.");
                    } catch (IOException ex) {

                    }
                } else {
                    frame.getQuestion().setText("You have no more chance to pause!");
                    frame.getQuestion().setText(question.getTitle() + ": " + question.getQuestionContent());
                }


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

    public void startTimeCountdown(int time, Question q) throws InterruptedException, IOException, ExecutionException {
        int timeLeft = time;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                for (int i = time; i >= 0; i--) {
                    System.out.println(isPause);
                    if (!isPause) {
                        publish(i);
                        Thread.sleep(1000);
                    } else {
                        System.out.println("pause");
                        Thread.sleep(10000);
                        frame.getQuestion().setText(q.getTitle() + ": " + q.getQuestionContent());
                        isPause = false;
                    }

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
            isPause = false;
            try {
                Object obj = dis.readObject();
                if (obj instanceof String && "Game Over".equals((String) obj)) {
                    System.out.println("read session");
                    Session session = (Session) dis.readObject();
                    startVictoryScreen(session);
                    this.frame.setVisible(false);
                } else {
                    question = (Question) obj;
                    timer = dis.readInt();
                    System.out.println(timer);
                    System.out.println(question);
                    startTimeCountdown(timer, question);

                    frame.getQuestion().setText(question.getTitle() + ": " + question.getQuestionContent());
                    frame.getaBtt().setText("A. " + question.getAnswerA());
                    frame.getbBtt().setText("B. " + question.getAnswerB());
                    frame.getcBtt().setText("C. " + question.getAnswerC());
                    frame.getdBtt().setText("D. " + question.getAnswerD());
                    //Wait for server to send 'Time out' message
                    String timeout = dis.readUTF();
                    System.out.println("timeout: " + timeout);
                    if (timeout.equals("Pause")) {
//                        System.out.println("pause is real");
//                        frame.getQuestion().setText("Game Pause!");
                        isPause = true;
//                        Thread.sleep(10000);
                        dis.readUTF();
                        continue;
                    }

                    if (answer == null) {
                        System.out.println("go here");
                        answer = "0";
                        dos.writeUTF(answer);
                        dos.flush();
                    }
                    System.out.println(timeout + ", " + answer);
                }

            } catch (SocketException e) {
                frame.getQuestion().setText("Server is not available.");
                System.out.println("Server is not available.");
                break;
            } catch (EOFException e) {
                System.out.println("pause time");
//                e.printStackTrace();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            dis.close();
            dos.close();
        } catch (IOException e) {
            System.out.println(client.getPort() + " disconnected");
        }

    }

    public void startVictoryScreen(Session s) {
        VictoryScreenFrame vsf = new VictoryScreenFrame();
        vsf.setVisible(true);
        KButton mainBtt = vsf.getPlayBtt();
        for (PlayedQuestion playedQuestion : s.getQuestion()) {
            vsf.addPlayedQuestionToTable(playedQuestion);
        }
        mainBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vsf.dispose();
                frame.setVisible(true);
            }
        });
        KButton exportBtt = vsf.getExportBtt();
        exportBtt.addActionListener(new ActionListener() {
                                        @Override
                                        public void actionPerformed(ActionEvent e) {
                                            saveHistory(s);
                                            JOptionPane.showMessageDialog(null, "Export Successful");

                                        }
                                    }
        );
    }

    public String handleAnswer(int answer) {
        switch (answer) {
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            default:
                return "D";
        }
    }

    public void saveHistory(Session session) {

        try {
            FileWriter fw = new FileWriter("history.txt");
            String record = "Id: " + session.getPlayer().getId() + " - "
                    + "Address: " + session.getPlayer().getAddress() + "\n";

            ArrayList<PlayedQuestion> listQuestion = session.getQuestion();
            for (PlayedQuestion playedQuestion : listQuestion) {
                record = "Question: " + playedQuestion.getQuestion().getTitle() + " - "
                        + "Chosen answer: " + handleAnswer(playedQuestion.getChosenAnswer()) + " - "
                        + "Correct answer: " + handleAnswer(playedQuestion.getQuestion().getCorrectAnswer()) + " - "
                        + "Time: " + playedQuestion.getTime()
                        + "\n";
                System.out.println(record);
                fw.write((String) record);
            }

            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Success...");
    }
}
