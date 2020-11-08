/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Model;

import java.io.Serializable;

/**
 *
 * @author LEGION
 */
public class Player implements Serializable{
    private int id;
    private String name,address;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Player{" + "id=" + id + ", name=" + name + ", address=" + address + '}';
    }



    public Player() {
    }

    public Player(int id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }


    
}
