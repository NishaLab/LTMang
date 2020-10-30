/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import DAO.QuestionDAO;
import Helper.CountdownHelper;
import Model.Question;

import java.io.*;
import java.lang.reflect.Array;
import java.text.*;
import java.util.*;
import java.net.*;

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
    static boolean isOver;
    final static int port = 5056;
    public final static int clientEndPort = 2112;

    final static int timeAnswerQuest = 15; //sec
    public final static int timeConnectToServer = 10; //sec

    private static void questionCountdown(int time) throws InterruptedException, IOException {
        System.out.println("You have 15 seconds to send your answer to server...");
        while (time > 0) {
            System.out.println(time);
            Thread.sleep(1000);
            time--;
        }

        System.out.println("Time up!");
    }

    public static void startTimeCountdown(int time) throws InterruptedException, IOException {
        System.out.println("You have 10 seconds to connect to server...");
        while (time > 0) {
            System.out.println(time);
            Thread.sleep(1000);
            time--;
        }
        isOver = true;
        System.out.println("Server connect closed.");
    }


    public static void main(String[] args) throws IOException {
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
        for (Socket client : clients) {
            dis = new ObjectInputStream(client.getInputStream());
            dos = new ObjectOutputStream(client.getOutputStream());
            inStreamMap.put(client, dis);
            outStreamMap.put(client, dos);
        }

        //Loop through questions list
        for (Question q : question) {

            System.out.println(q);
            System.out.println("Size before clear: " + clientHandlers.size());
            clientHandlers.clear();
            System.out.println("Size after clear: " + clientHandlers.size());

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
                    dos.writeUTF("Time out");
                    dos.flush();

                }
                clientAnswerHandler(q);
                System.out.println("Waiting for 3 seconds to continue...");
                Thread.sleep(3000);


            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (SocketException e) {
                System.out.println("No client");
            }
        }

        System.out.println("Answers: ");
        for (String ans : clientAnswers) {
            System.out.println(ans);
        }


        System.out.println("Game over.");
    }

    //Get all the answers
    private static void clientAnswerHandler(Question q) {
        for (ClientHandler ch : clientHandlers) {

            String ans = null;
            while (ans == null) {
                ans = ch.getResponse();
            }
            String answer = ch.getClient().getPort() + ";" + q.getTitle() + ";" + ans;
            clientAnswers.add(answer);

        }

    }
}
