/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package travelandcoserver;

import data.User;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Stan
 */
public class ServerThread extends Thread {

    private Properties props;
    private static final int PORT = 6666;
    private final AtomicBoolean listening;
    private Selector selector;
    private static final int BUFSIZE = 1024;
    private DatabaseDAO dao;

    public ServerThread() {
        props = getProperties();
        dao = new DatabaseDAO();
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
                int ready = selector.select(1000);
                if (ready > 0) {
                    for (SelectionKey key : selector.selectedKeys()) {
                        checkKey(key);
                    }
                }
                selector.selectedKeys().clear();
            }
            serverSocketChannel.close();
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
                s.register(selector, SelectionKey.OP_READ);
                System.out.println("client accepted");
            } else if (key.isReadable()) {
                SocketChannel s = (SocketChannel) key.channel();
                ByteBuffer buf = ByteBuffer.allocate(BUFSIZE);
                int bytesRead = s.read(buf);
                if (bytesRead > 0) {
                    buf.flip();
                    List<String> outgoing = handleIncoming(new String(buf.array()).trim());
                    if (!outgoing.isEmpty()) {
                        handleOutgoing(s, outgoing);
                    }
                }
                else{
                    s.close();
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);

        }
    }

    private List<String> handleIncoming(String message) {
        System.out.println("message received: " + message);
        String[] partsIn = message.split(":");
        List<String> partsOut = new ArrayList<>();
        if (partsIn[0].equals(props.getProperty("get_user"))) {
            User user = dao.getUser(partsIn[1]);
            if (user != null) {
                partsOut.add(user.getEmail());
                partsOut.add(user.getPasswd());
                partsOut.add(user.getName());
            } else {
                partsOut.add("NOT FOUND");
            }
        } else if (partsIn[0].equals(props.getProperty("add_user"))) {
            try {
                if (dao.addUser(partsIn[1], partsIn[2], partsIn[3])) {
                    partsOut.add("true");
                } else {
                    partsOut.add("false");
                }
            } catch (ArrayIndexOutOfBoundsException ex) {
                System.err.println("array out of bounds (add_user)");
                partsOut.add("false");
            }
        }
        return partsOut;
    }

    private void handleOutgoing(SocketChannel s, List<String> parts) {
        String message = parts.get(0);
        for (int i = 1; i < parts.size(); i++) {
            message += ":" + parts.get(i);
        }
        try {
            ByteBuffer buf = ByteBuffer.allocate(BUFSIZE);
            buf.clear();
            buf.put(message.getBytes());
            buf.flip();
            s.write(buf);
            System.out.println("message sent: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties getProperties() {
        Properties props = new Properties();
        try {
            FileInputStream in = new FileInputStream("src/resources/commands.properties");
            props.load(in);
            in.close();

        } catch (IOException ex) {
            Logger.getLogger(DatabaseDAO.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return props;
    }
}
