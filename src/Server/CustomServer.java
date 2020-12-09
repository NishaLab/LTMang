/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import DAO.*;
import Helper.CountdownHelper;
import Model.*;
import Server.ServerFrameController;

import javax.print.attribute.standard.PDLOverrideSupported;
import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.net.*;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

/**
 * @author LEGION
 */
public class CustomServer {

    //List clients' thread
    private static ArrayList<ClientHandler> clientHandlers;
    //List clients' answers
    private static ArrayList<String> clientAnswers;
    //List clients
    private static ArrayList<Socket> clients;
    //List session
    private static ArrayList<Session> sessions;
    //Map oos to client
    private static HashMap<Socket, ObjectOutputStream> outStreamMap;
    //Map ois to client
    private static HashMap<Socket, ObjectInputStream> inStreamMap;
    //Map session to client
    private static HashMap<Socket, Session> sessionMap;
    //
    private HashMap<Socket, ClientHandler> clientThreadMap;
    //
    private ObjectInputStream dis;
    private ObjectOutputStream dos;
    //check if time to connect to server is over
    private static boolean isOver;
    final static int port = 5056;
    private final ServerFrame frame;
    private boolean isPause;

    final static int timeAnswerQuest = 15; //sec
    public final static int timeConnectToServer = 10; //sec

    public CustomServer(ServerFrame frame) {
        this.frame = frame;
    }

    private void questionCountdown(int time, Question q) throws InterruptedException, IOException {
//        System.out.println("You have 15 seconds to send your answer to server...");
        while (time > 0) {
//            System.out.println("pause?" + isPause);
            if (!isPause) {
                frame.getCounter().setText(Integer.toString(time));
                Thread.sleep(1000);
                time--;
            } else {
//                System.out.println("pause");
                for (Socket client : clients) {
//                    System.out.println("state: " + clientThreadMap.get(client).getState());
                    clientThreadMap.get(client).getDos().writeUTF("Pause");
                    clientThreadMap.get(client).getDos().flush();
                }
                Thread.sleep(10000);
                for (ClientHandler clientHandler : clientHandlers) {
                    clientHandler.getDos().writeObject(q);
                    clientHandler.getDos().writeInt(0);
                    clientHandler.getDos().flush();
                }
                isPause = false;

            }
        }

//        System.out.println("Time up!");
        frame.getQuestion().setText("Time up");
    }

    public void setPause(boolean isPause) {
        this.isPause = isPause;
    }



    public void startTimeCountdown(int time) throws InterruptedException, IOException, ExecutionException {
        SwingWorker worker = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                for (int i = time; i >= 0; i--) {
                    publish(i);
                    Thread.sleep(1000);
                    //System.out.println(i);
                }

                isOver = true;
                new Socket(InetAddress.getLocalHost(), port);
                return "Connection is closed.";
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
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void getConnections(ServerSocket ss) throws IOException {

        frame.getQuestion().setText("Game start on port " + port);

        Socket client = null;
        client = ss.accept();

        clients.add(client);
        System.out.println("Client " + client.getPort() + " connected to server.");

    }

    public void start() {
        try {
            //System.out.println("custom server: " + Thread.currentThread().getName());
            //Init
            PlayerDAO playerDAO = new PlayerDAO();
            GameDAO gd = new GameDAO();
            clients = new ArrayList<>();
            clientHandlers = new ArrayList<>();
            clientAnswers = new ArrayList<>();
            inStreamMap = new HashMap<>();
            outStreamMap = new HashMap<>();
            sessionMap = new HashMap<>();
            sessions = new ArrayList<>();
            clientThreadMap = new HashMap<>();

            QuestionDAO questionDAO = new QuestionDAO();
            ArrayList<Question> question = questionDAO.getAllQuestion();

            ServerSocket ss = null;

            try {
                ss = new ServerSocket(port);
            } catch (IOException ex) {
                Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            startTimeCountdown(timeConnectToServer);
//            System.out.println(isOver);

            while (!isOver) {
//                System.out.println(isOver);
                try {
                    getConnections(ss);
                } catch (IOException ex) {
                    Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //remove the last element
            if (clients.size() > 0) {
                clients.remove(clients.size() - 1);
            }

            frame.getQuestion().setText("Let's start!");
            Thread.sleep(2000);

            //check and return if no client
            if (clients.size() == 0) {
                System.out.println("No player.");
                frame.getQuestion().setText("No player");
                return;
            }
            //Inputstreams, outputstreams' initialization
            for (Socket client : clients) {
                try {
                    dis = new ObjectInputStream(client.getInputStream());
                    dos = new ObjectOutputStream(client.getOutputStream());
                    Player player = createPlayer(dis, playerDAO);
//                    System.out.println("player " + player);
                    try {
//                        player = (Player) dis.readObject();
                        frame.addPlayerToTable(player);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    System.out.println(player);
                    inStreamMap.put(client, dis);
                    outStreamMap.put(client, dos);
                    Session tmp = new Session();
                    ArrayList<PlayedQuestion> pq = new ArrayList<>();
                    tmp.setPlayer(player);
                    tmp.setQuestion(pq);
                    sessionMap.put(client, tmp);
                } catch (IOException ex) {
                    Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            Game game = new Game();
            game.setPlayDate(new Date());

            //Loop through questions list
            for (Question q : question) {

//                System.out.println(q);
                frame.getQuestion().setText(q.getTitle());
                //System.out.println("Size before clear: " + clientHandlers.size());
                clientHandlers.clear();
                //System.out.println("Size after clear: " + clientHandlers.size());
                clientThreadMap.clear();

                for (Socket client : clients) {
//                    System.out.println(client);
                    dis = inStreamMap.get(client);
                    dos = outStreamMap.get(client);
                    ClientHandler thread = new ClientHandler(dos, dis, client, q, timeAnswerQuest, this);
                    clientHandlers.add(thread);
                    clientThreadMap.put(client, thread);
                }

                for (ClientHandler ch : clientHandlers) {
                    ch.start();
                }
//                System.out.println("Get ready to answer " + q.getTitle() + "...");

                try {
                    questionCountdown(timeAnswerQuest, q);
                    for (ClientHandler ch : clientHandlers) {
                        dos = ch.getDos();
                        //                    System.out.println(ch.getClient().getPort() + ": " + ch.getResponse());
                        //                    System.out.println(ch.getClient().getPort() + ": " + ch.getState());
                        if (ch.getResponse() == null && ch.getState() == Thread.State.TERMINATED) {
                            //Client closed app
                            Socket clientToBeRemoved = ch.getClient();
                            clients.remove(clientToBeRemoved);
                            inStreamMap.remove(clientToBeRemoved);
                            continue;
                        }
                        dos.writeUTF("Time out");
                        //System.out.println(ch.getDis());
                        dos.flush();

                    }
                    //System.out.println("hi");
                    clientAnswerHandler(q);
                    frame.getQuestion().setText("Get ready for next question...");
                    Thread.sleep(3000);
//                    System.out.println("Size: " + clientAnswers.size());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    System.out.println("No more client");
                    frame.getQuestion().setText("No more client");
                } catch (IOException ex) {
                    Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

//            System.out.println("Answers: ");
//            String answer = "";
//            for (String ans : clientAnswers) {
//                answer += ans + "\n";
//                System.out.println(ans);
//            }
//            frame.getQuestion().setText(answer + "\nGame over.");
            for (Socket client : clients) {
                sessions.add(sessionMap.get(client));
            }
//            System.out.println("Sessions: " + sessions);
            game.setSessions(sessions);
            for (Socket client : clients) {
                dos = outStreamMap.get(client);
                dos.writeObject("Game Over");
                dos.flush();
                dos.writeObject(sessionMap.get(client));
                dos.flush();
                System.out.println("send Session" + sessionMap.get(client));
            }
            gd.saveGame(game);
            frame.getQuestion().setText("Game Over!");
            reset();
        } catch (InterruptedException ex) {
            Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Get all the answers
    private static void clientAnswerHandler(Question q) {
        for (ClientHandler ch : clientHandlers) {

            String ans = ch.getResponse();
            String finalAnswer = ch.getClient().getPort() + ";" + q.getTitle() + ";";
            PlayedQuestion pq = new PlayedQuestion();
            pq.setQuestion(q);
//            System.out.println(ch.getState());

            while (ans == null) {
                ans = ch.getResponse();
            }

//            System.out.println("answer get: " + ans);
            if (ans != null) {
                pq.setChosenAnswer(Integer.parseInt(ans));
                if (pq.getQuestion().getCorrectAnswer() == pq.getChosenAnswer()) {
                    pq.setIsCorrect(true);
                } else {
                    pq.setIsCorrect(false);
                }
                finalAnswer += ans;
                clientAnswers.add(finalAnswer);
            }
            sessionMap.get(ch.getClient()).getQuestion().add(pq);

        }

    }

    public void reset() {
        clients = new ArrayList<>();
        clientHandlers = new ArrayList<>();
        clientAnswers = new ArrayList<>();
        inStreamMap = new HashMap<>();
        outStreamMap = new HashMap<>();
        sessionMap = new HashMap<>();
        sessions = new ArrayList<>();
        frame.getQuestion().setText("");
        DefaultTableModel dtm = (DefaultTableModel) frame.getPlayerTable().getModel();
        dtm.setRowCount(0);
        frame.getMainBtt().setVisible(true);
    }

    private Player createPlayer(ObjectInputStream ois, PlayerDAO playerDAO) throws IOException {
        String ipAddress = ois.readUTF();
        String playerName = ois.readUTF();
//        System.out.println(ipAddress + "      " + playerName);

        Player player = playerDAO.getPlayerByAddress(ipAddress);
//        System.out.println(player);
        if (player.getId() == 0) {
            player.setName(playerName);
            player.setAddress((ipAddress));
            playerDAO.createPlayer(player);

        } else {
            if (player.getName().compareTo(playerName) != 0) {
                player.setName(playerName);
                playerDAO.updatePlayerName(player);
            }

        }

        player = playerDAO.getPlayerByAddress(ipAddress);

        return player;
    }

}
