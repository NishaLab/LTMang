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
import java.text.SimpleDateFormat;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import keeptoo.*;

public class ServerFrameController implements Runnable {

    private ServerFrame frame;

    public ServerFrameController(ServerFrame frame) {
        this.frame = frame;
        try {
            CustomServer cs = new CustomServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void init() {
        setStartBttAction();
        setPauseBttAction();
        setNextBttAction();
    }

    public void setStartBttAction() {
        KButton start = frame.getMainBtt();
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    public void setNextBttAction() {
        KButton next = frame.getMainBtt();
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    public void setPauseBttAction() {
        KButton pause = frame.getMainBtt();
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
