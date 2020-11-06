/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

/**
 *
 * @author LEGION
 */
import Model.Player;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.Date;

import Model.Question;
import static Server.CustomServer.port;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import keeptoo.*;

public class ServerFrameController {

    private ServerFrame frame;

    public ServerFrameController(ServerFrame frame) {
        this.frame = frame;
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        setStartBttAction();
        setPauseBttAction();
        setNextBttAction();
    }
    
    private void startCustomServer() {
        new Thread() {
            @Override
            public void run() {
                new CustomServer(frame).start();
            }
            
        }.start();
    }

    public void setStartBttAction() {
        KButton start = frame.getMainBtt();
//        ActionListener startAction = new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                try {
//                    cs.start();
//
//                } catch (IOException ioException) {
//                    ioException.printStackTrace();
//                } catch (InterruptedException interruptedException) {
//                    interruptedException.printStackTrace();
//                }
//            }
//        };
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startCustomServer();
            }
        });
    }

    public void setNextBttAction() {
        KButton next = frame.getNextBtt();
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    public void setPauseBttAction() {
        KButton pause = frame.getPauseBtt();
        pause.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    public void setQuestionPanel(Question question) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void setCounter(int counter) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void addPlayerToTable(Player player) {
        DefaultTableModel dptb = (DefaultTableModel) this.frame.getPlayerTable().getModel();
        Object[] tmp;
        tmp = new Object[]{player.getId(), player.getAddress()};
        dptb.addRow(tmp);

    }

    public void run() {
        // call ham main cua CustomServer tai day
    }
}
