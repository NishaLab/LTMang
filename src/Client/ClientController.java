/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 *
 * @author LEGION
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import keeptoo.*;

public class ClientController implements Runnable {

    final ClientFrame frame;
    private Socket client;
    private DataOutputStream dos;
    private DataInputStream dis;

    public ClientController(ClientFrame frame) {
        this.frame = frame;
        try {
            this.client = new Socket("127.0.0.1", 5056);
            this.dos = new DataOutputStream(client.getOutputStream());
            this.dis = new DataInputStream(client.getInputStream());
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
                try {
                    System.out.println("Clicked A");
                    dos.writeUTF("A");
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
        bBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("Clicked B");
                    dos.writeUTF("B");
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
        cBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("Clicked C");
                    dos.writeUTF("C");
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
        dBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    System.out.println("Clicked D");
                    dos.writeUTF("D");
                } catch (Exception f) {
                    f.printStackTrace();
                }
            }
        });
    }

    public void run() {
        while (true) {
            try {
                String received = this.dis.readUTF();
                System.out.println(received);
                this.frame.getQuestion().setText(received);
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    this.client.close();
                } catch (Exception f) {
                }
            }
        }
    }
}
