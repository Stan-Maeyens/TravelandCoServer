/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelandcoserver;

import java.util.Scanner;

/**
 *
 * @author Stan
 */
public class TravelandCoServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DatabaseDAO dao = new DatabaseDAO();
        
        System.out.println("Server started...");
        ServerThread serverThread = new ServerThread(); //another thread so this one can listen to input
        serverThread.start();
        Scanner sc = new Scanner(System.in);
        while (!sc.nextLine().equals("quit")) { //input "quit" to stop the server

        }
        serverThread.end();
    }
    
}
