/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Server;

import Model.*;
import DAO.*;
import java.util.*;

/**
 *
 * @author LEGION
 */
public class Test {

    public static void main(String[] args) {
        GameDAO gd = new GameDAO();
        Game game = new Game();
        try {
            game = gd.getGameById(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Session sess : game.getSessions()) {
            System.out.println(sess);
        }
        System.out.println(game);
    }
}
