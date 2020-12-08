package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ResultHandler extends Thread {
    private ObjectInputStream ois;
    //    private ObjectOutputStream oos;
    private String result;
    private CustomServer mainServer;

    public ResultHandler(ObjectInputStream ois, CustomServer server) {
        this.ois = ois;
        this.mainServer = server;
//        this.oos = oos;
    }

    public String getResult() {
        return result;
    }

    @Override
    public void run() {
        try {
            result = ois.readUTF();
            if (result.equals("-1")) mainServer.setPause(true);

            System.out.println("Receiving result end");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
