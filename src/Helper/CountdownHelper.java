package Helper;

import Server.CustomServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class CountdownHelper extends Thread {
    private int remotePort;
    private CustomServer server;

    public CountdownHelper(int remotePort, CustomServer server) {
        this.remotePort = remotePort;
        this.server = server;
    }

//    public CountdownHelper(int remotePort) {
//        this.remotePort = remotePort;
//    }

    @Override
    public void run() {
        try {

                //A distinguished cheating :D
                Socket clientEnd = new Socket(InetAddress.getLocalHost(), remotePort);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
