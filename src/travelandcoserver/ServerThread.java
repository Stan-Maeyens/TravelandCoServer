/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelandcoserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stan
 */
public class ServerThread extends Thread {

    //uses port 5555
    private static final int PORT = 6666;
    private final AtomicBoolean listening;
    private Selector selector;
    private static final int BUFSIZE = 100;

    public ServerThread() {
        listening = new AtomicBoolean();
    }

    @Override
    public void run() {
        System.out.println("serverthread started");
        listening.set(true);
        try (ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            selector = Selector.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            System.out.println("serversocket registered"); //from now it's possible to accept clients

            while (listening.get()) {
                int ready = selector.selectNow();
                if (ready > 0) {
                    for (SelectionKey key : selector.selectedKeys()) {
                        checkKey(key);
                    }
                }
                selector.selectedKeys().clear();
            }

        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void end() {
        //ends the while loop
        listening.set(false);
    }

    private void checkKey(SelectionKey key) {
        try {
            //accept a new client
            if (key.isAcceptable()) {
                SocketChannel s = ((ServerSocketChannel) key.channel()).accept();
                s.configureBlocking(false);
                s.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
                System.out.println("client accepted");
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);

        }
    }
}
