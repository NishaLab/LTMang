package Helper;

import Server.CustomServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class CountdownHelper extends Thread {
    private int remotePort;

    public CountdownHelper(int remotePort) {
        this.remotePort = remotePort;
    }

    @Override
    public void run() {
        try {
            CustomServer.startTimeCountdown(CustomServer.timeConnectToServer);
            //A distinguished cheating :D
            Socket clientEnd = new Socket(InetAddress.getLocalHost(), remotePort, InetAddress.getLocalHost(), CustomServer.clientEndPort);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
