/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import java.io.*;
import java.text.*;
import java.util.*;
import java.net.*;

/**
 *
 * @author LEGION
 */
public class CustomServer {

    public static void main(String[] args) throws IOException {
        ServerSocket ss = new ServerSocket(5056);
//        ss.setSoTimeout(10000);
        while (true) {
            Socket client = null;
            try {
                client = ss.accept();
                System.out.println(client);
                DataInputStream dis = new DataInputStream(client.getInputStream());
                DataOutputStream dos = new DataOutputStream(client.getOutputStream());
                System.out.println("Assigning new thread for this client");
                Thread t = new ClientHandler(dos, dis, client);
                t.start();
            } catch (SocketTimeoutException e) {
                client.close();
                e.printStackTrace();
            } catch (Exception f) {
                client.close();
                f.printStackTrace();
            }
        }
    }
}
