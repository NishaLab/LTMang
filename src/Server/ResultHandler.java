package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ResultHandler extends Thread {
    private ObjectInputStream ois;
    //    private ObjectOutputStream oos;
    private String result;
    private CustomServer mainServer;
    private boolean isOver;

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
        while (true) {
            String temp = null;
            try {
                temp = ois.readUTF();
//                System.out.println("temp: " + temp);
                if (temp.equals("-1")) {
                    mainServer.setPause(true);
                    continue;
                } else if(temp.equals("over")) {
//                    System.out.println("over: " + result);
                    break;
                }
                result = temp;

                System.out.println("Receiving result end");

            } catch (IOException e) {
//                e.printStackTrace();
                System.out.println("...");
                break;
            }
        }

    }

}
