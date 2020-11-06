/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import DAO.QuestionDAO;
import Helper.CountdownHelper;
import Model.Question;

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

    //Map oos to client
    private static HashMap<Socket, ObjectOutputStream> outStreamMap;
    //Map ois to client
    private static HashMap<Socket, ObjectInputStream> inStreamMap;

    //check if time to connect to server is over
    private static boolean isOver;
    final static int port = 5056;
    public final static int clientEndPort = 2113;
    private final ServerFrame frame;
    private ActionListener countdown;

    final static int timeAnswerQuest = 15; //sec
    public final static int timeConnectToServer = 10; //sec

    public CustomServer(ServerFrame frame) {
        this.frame = frame;
    }

    private void questionCountdown(int time) throws InterruptedException, IOException {
        System.out.println("You have 15 seconds to send your answer to server...");

        while (time > 0) {
            System.out.println(time);
            frame.getCounter().setText(Integer.toString(time));

            Thread.sleep(1000);
            time--;
        }

        System.out.println("Time up!");
        frame.getQuestion().setText("Time up");
    }

    public void setOver(boolean over) {
        isOver = over;
    }

    private int timeLeft;

    public void startTimeCountdown(int time) throws InterruptedException, IOException, ExecutionException {
        timeLeft = time;
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
                    System.out.println(statusMsg);
                    frame.getQuestion().setText(statusMsg);
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
            ObjectInputStream dis;
            ObjectOutputStream dos;
            clients = new ArrayList<>();
            clientHandlers = new ArrayList<>();
            clientAnswers = new ArrayList<>();
            inStreamMap = new HashMap<>();
            outStreamMap = new HashMap<>();
            //        ServerSocket ss = null;
            //
            //        ss = new ServerSocket(port);
            //        frame.getQuestion().setText("Game start on port " + port);

            QuestionDAO questionDAO = new QuestionDAO();
            ArrayList<Question> question = questionDAO.getAllQuestion();

            ServerSocket ss = null;

            try {
                ss = new ServerSocket(port);
            } catch (IOException ex) {
                Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
            }

            startTimeCountdown(timeConnectToServer);

            while (!isOver) {
                //System.out.println(isOver);
                try {
                    getConnections(ss);
                } catch (IOException ex) {
                    Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //System.out.println("hello");
            //remove the last element
            if (clients.size() > 0) {
                clients.remove(clients.size() - 1);
            }

            frame.getQuestion().setText("Let's start!");
            //System.out.println("Server connect closed.");

            //        //Just receive client in 10s
            //        while (true) {
            //            if (isOver) break;
            //            Socket client = null;
            //            client = ss.accept();
            //            if (client.getPort() != clientEndPort) {
            //                clients.add(client);
            //                System.out.println("Client " + client.getPort() + " connected to server.");
            //            }
            //        }
            //Inputstreams, outputstreams' initialization
            if (clients.size() == 0) {
                System.out.println("No player.");
                frame.getQuestion().setText("No player");
                return;
            }
            for (Socket client : clients) {
                try {
                    dis = new ObjectInputStream(client.getInputStream());
                    dos = new ObjectOutputStream(client.getOutputStream());
                    inStreamMap.put(client, dis);
                    outStreamMap.put(client, dos);
                } catch (IOException ex) {
                    Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            //Loop through questions list
            for (Question q : question) {

                System.out.println(q);
                frame.getQuestion().setText(q.getTitle());
                //System.out.println("Size before clear: " + clientHandlers.size());
                clientHandlers.clear();
                //System.out.println("Size after clear: " + clientHandlers.size());

                for (Socket client : clients) {
                    System.out.println(client);
                    dis = inStreamMap.get(client);
                    dos = outStreamMap.get(client);
                    ClientHandler thread = new ClientHandler(dos, dis, client, q);
                    clientHandlers.add(thread);
                }

                for (ClientHandler ch : clientHandlers) {
                    ch.start();
                }
                System.out.println("Get ready to answer " + q.getTitle() + "...");

                try {
                    questionCountdown(timeAnswerQuest);
                    for (ClientHandler ch : clientHandlers) {
                        dos = ch.getDos();
                        //                    System.out.println(ch.getClient().getPort() + ": " + ch.getResponse());
                        //                    System.out.println(ch.getClient().getPort() + ": " + ch.getState());
                        if (ch.getResponse() == null && ch.getState() == Thread.State.TERMINATED) {
                            Socket toBeRemoved = ch.getClient();
                            clients.remove(toBeRemoved);
                            inStreamMap.remove(toBeRemoved);
                            continue;
                        }
                        dos.writeUTF("Time out");
                        //System.out.println(ch.getDis());
                        dos.flush();

                    }
                    //System.out.println("hi");
                    clientAnswerHandler(q);
                    System.out.println("Waiting for 3 seconds to continue...");
                    System.out.println("Size: " + clientAnswers.size());

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (SocketException e) {
                    System.out.println("No more client");
                    frame.getQuestion().setText("No more client");
                } catch (IOException ex) {
                    Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            System.out.println("Answers: ");
            String answer = "";
            for (String ans : clientAnswers) {
                answer+=ans + "\n";
                System.out.println(ans);
            }
            frame.getQuestion().setText(answer + "\nGame over.");
            System.out.println("Game over.");
        } catch (InterruptedException ex) {
            Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(CustomServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*public static void main(String[] args) throws IOException {
        //Init
        ObjectInputStream dis;
        ObjectOutputStream dos;
        clients = new ArrayList<>();
        clientHandlers = new ArrayList<>();
        clientAnswers = new ArrayList<>();
        inStreamMap = new HashMap<>();
        outStreamMap = new HashMap<>();
        ServerSocket ss = null;

        ss = new ServerSocket(port);

        QuestionDAO questionDAO = new QuestionDAO();
        ArrayList<Question> question = questionDAO.getAllQuestion();

        Thread countdownHelper = new CountdownHelper(port);
        countdownHelper.start();

        //Just receive client in 10s
        while (true) {
            if (isOver) break;
            Socket client = null;
            client = ss.accept();
            if (client.getPort() != clientEndPort) {
                clients.add(client);
                System.out.println("Client " + client.getPort() + " connected to server.");
            }
        }

        //Inputstreams, outputstreams' initialization
        if (clients.size() == 0) {
            System.out.println("No player.");
            return;
        }
        for (Socket client : clients) {
            dis = new ObjectInputStream(client.getInputStream());
            dos = new ObjectOutputStream(client.getOutputStream());
            inStreamMap.put(client, dis);
            outStreamMap.put(client, dos);
        }

        //Loop through questions list
        for (Question q : question) {

            System.out.println(q);
            //System.out.println("Size before clear: " + clientHandlers.size());
            clientHandlers.clear();
            //System.out.println("Size after clear: " + clientHandlers.size());

            for (Socket client : clients) {
                System.out.println(client);
                dis = inStreamMap.get(client);
                dos = outStreamMap.get(client);
                ClientHandler thread = new ClientHandler(dos, dis, client, q);
                clientHandlers.add(thread);
            }


            for (ClientHandler ch : clientHandlers) {
                ch.start();
            }
            System.out.println("Get ready to answer " + q.getTitle() + "...");

            try {
                questionCountdown(timeAnswerQuest);
                for (ClientHandler ch : clientHandlers) {
                    dos = ch.getDos();
//                    System.out.println(ch.getClient().getPort() + ": " + ch.getResponse());
//                    System.out.println(ch.getClient().getPort() + ": " + ch.getState());
                    if (ch.getResponse() == null && ch.getState() == Thread.State.TERMINATED) {
                        Socket toBeRemoved = ch.getClient();
                        clients.remove(toBeRemoved);
                        inStreamMap.remove(toBeRemoved);
                        continue;
                    }
                    dos.writeUTF("Time out");
                    //System.out.println(ch.getDis());
                    dos.flush();


                }
                //System.out.println("hi");
                clientAnswerHandler(q);
                System.out.println("Waiting for 3 seconds to continue...");
                System.out.println("Size: " + clientAnswers.size());
                Thread.sleep(3000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                System.out.println("No more client");
            }
        }

        System.out.println("Answers: ");
        for (String ans : clientAnswers) {
            System.out.println(ans);
        }


        System.out.println("Game over.");
    } */
    //Get all the answers
    private static void clientAnswerHandler(Question q) {
        for (ClientHandler ch : clientHandlers) {

            String ans = ch.getResponse();
            String finalAnswer = ch.getClient().getPort() + ";" + q.getTitle() + ";";

            if (ch.getState() == Thread.State.RUNNABLE) {
                //System.out.println(ch.getClient().getPort() + "; " + ch.getState());
                while (ans == null) {
                    ans = ch.getResponse();
                }
            }
            if (ans != null) {
                finalAnswer += ans;
                clientAnswers.add(finalAnswer);
            }

        }

    }

}
