package com.rectangularchicken.plexus;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A single node in the plexus.
 * @author Santiago Benoit
 */
public class Node implements Serializable {
    
    public Node() {
        connections = new ArrayList<>();
        x = 0;
        y = 0;
        owner = 0;
    }
    
    public Node(double x, double y) {
        connections = new ArrayList<>();
        this.x = x;
        this.y = y;
        owner = 0;
    }
    
    public static void connect(Node n1, Node n2) {
        n1.connect(n2);
        n2.connect(n1);
    }
    
    public static void disconnect(Node n1, Node n2) {
        n1.disconnect(n2);
        n2.disconnect(n1);
    }
    
    public void connect(Node n) {
        if (n != this && !connections.contains(n)) {
            connections.add(n);
        }
    }
    
    public void disconnect(Node n) {
        connections.remove(n);
    }
    
    public void clearConnections() {
        connections.clear();
    }
    
    public void setCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public void setOwner(int player) {
        owner = player;
    }
    
    public static double distance(Node n1, Node n2) {
        double x = n1.x - n2.x;
        double y = n1.y - n2.y;
        return Math.sqrt(x * x + y * y);
    }
    
    public ArrayList<Node> getConnections() {
        return connections;
    }
    
    public double getX() {
        return x;
    }
    
    public double getY() {
        return y;
    }
    
    public int getOwner() {
        return owner;
    }
    
    private final ArrayList<Node> connections;
    private double x, y;
    private int owner;
}
