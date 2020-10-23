/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import DAO.*;
import Model.*;

/**
 *
 * @author LEGION
 */
public class ClientHandler extends Thread {

    final DataOutputStream dos;
    final DataInputStream dis;
    final Socket client;
    private ArrayList<Question> question;

    public ClientHandler(DataOutputStream dos, DataInputStream dis, Socket client) {
        this.dos = dos;
        this.dis = dis;
        this.client = client;
    }
    
    
    @Override
    public void run() {
        String received;
        String reponse = "You have choosed ";
        System.out.println("Here");
        while (true) {
            try {
                dos.writeUTF("Hello");
                received = dis.readUTF();
                System.out.println("Here4");

                if (received.compareTo("Q") == 0) {
                    System.out.println("Closing");
                    this.client.close();
                    System.out.println("Closed");
                    break;
                } else {
                    switch (received) {
                        case "A":
                            System.out.println("Received A");
                            dos.writeUTF(reponse + received);
                            break;
                        case "B":
                            System.out.println("Received B");
                            dos.writeUTF(reponse + received);
                            break;
                        case "C":
                            System.out.println("Received C");

                            dos.writeUTF(reponse + received);
                            break;
                        case "D":
                            System.out.println("Received D");

                            dos.writeUTF(reponse + received);
                            break;
                        default:
                            System.out.println(received);
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            try {
//                dis.close();
//                dos.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
    }

}
