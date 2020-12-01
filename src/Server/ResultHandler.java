package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ResultHandler extends Thread{
    private ObjectInputStream ois;
//    private ObjectOutputStream oos;
    private String result;

    public ResultHandler(ObjectInputStream ois) {
        this.ois = ois;
//        this.oos = oos;
    }

    public String getResult() {
        return result;
    }

    @Override
    public void run() {
        try {
            result = ois.readUTF();

            System.out.println("Receiving result end");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
