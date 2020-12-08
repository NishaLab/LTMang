package Server;

import Model.Question;

import java.io.IOException;
import java.io.ObjectOutputStream;

public class QuestionHandler extends Thread{
    private Question question;
    private ObjectOutputStream oos;
    private int questionTimer;
    public QuestionHandler(Question question, ObjectOutputStream oos, int questionTimer) {
        this.question = question;
        this.oos=oos;
        this.questionTimer = questionTimer;
    }

    @Override
    public void run() {
        try {
            oos.writeObject(question);
            oos.flush();
            oos.writeInt(questionTimer);
            oos.flush();

            System.out.println("Send question end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
