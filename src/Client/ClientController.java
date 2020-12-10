/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Client;

/**
 * @author LEGION
 */
import DAO.PlayerDAO;
import Model.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;

import Model.Question;
import Model.Session;
import Server.CustomServer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import keeptoo.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ClientController {

    final ClientFrame frame;
    private Socket client;
    private int remotePort;
    private String host;

    private Question question;
    private int timer;
    
    private ObjectOutputStream dos;
    private ObjectInputStream dis;
    private String answer;

    public ClientController(ClientFrame frame) {
        this.frame = frame;
        host = "127.0.0.1";
//        host = "192.168.43.12";
        remotePort = 5056;

    }

    private void connect(String host, int remotePort) {
        try {
            this.client = new Socket(host, remotePort);
            this.dos = new ObjectOutputStream(client.getOutputStream());
            this.dis = new ObjectInputStream(client.getInputStream());

            String ipAddress = this.client.getRemoteSocketAddress().toString();
            String playerName = this.frame.getNameField().getText();

            this.frame.getNameField().setEditable(false);

            this.dos.writeUTF(ipAddress);
            this.dos.writeUTF(playerName);
            this.dos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("connected");
    }

    public void init() {
        setButtonAction();
    }

    private void startClient() {
        new Thread() {
            @Override
            public void run() {
                connect(host, remotePort);
                runClient();
            }

        }.start();
    }

    void setButtonAction() {

        KButton aBtt = frame.getaBtt();
        KButton bBtt = frame.getbBtt();
        KButton cBtt = frame.getcBtt();
        KButton dBtt = frame.getdBtt();
        KButton startBtn = frame.getExportBtt();

        startBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Play btn");
                startClient();
                frame.getQuestion().setText("Connecting to server, Please Wait...");
            }
        });
        aBtt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked A");
                try {
                    answer = "1";
                    dos.writeUTF("1");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        bBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked B");
                try {
                    answer = "2";
                    dos.writeUTF("2");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }

            }
        });
        cBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked C");
                try {
                    answer = "3";
                    dos.writeUTF("3");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
        dBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Clicked D");
                try {
                    answer = "4";
                    dos.writeUTF("4");
                    dos.flush();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }

    public void startTimeCountdown(int time) throws InterruptedException, IOException, ExecutionException {
        int timeLeft = time;
        SwingWorker worker = new SwingWorker() {
            @Override
            protected String doInBackground() throws Exception {
                for (int i = time; i >= 0; i--) {
                    publish(i);
                    Thread.sleep(1000);
                    //System.out.println(i);
                }
                return "Time's up.";
            }

            @Override
            protected void process(List chunks) {
                int val = (int) chunks.get(chunks.size() - 1);

                frame.getCounter().setText(String.valueOf(val));
            }

            @Override
            protected void done() {
                try {
                    String statusMsg = (String) get();
                    System.out.println(statusMsg);
                    frame.getCounter().setText(statusMsg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    public void runClient() {
        System.out.println("run");
        while (true) {
            answer = null;

            try {
                Object obj = dis.readObject();
                if (obj instanceof String && "Game Over".equals((String) obj)) {
                    System.out.println("read session");
                    Session session = (Session) dis.readObject();
                    startVictoryScreen(session);
                    this.frame.setVisible(false);
                } else {
                    question = (Question) obj;
                    timer = dis.readInt();
                    System.out.println(timer);
                    System.out.println(question);
                    startTimeCountdown(timer);

                    frame.getQuestion().setText(question.getTitle() + ": " + question.getQuestionContent());
                    frame.getaBtt().setText("A. " + question.getAnswerA());
                    frame.getbBtt().setText("B. " + question.getAnswerB());
                    frame.getcBtt().setText("C. " + question.getAnswerC());
                    frame.getdBtt().setText("D. " + question.getAnswerD());
                    //Wait for server to send 'Time out' message
                    String timeout = dis.readUTF();

                    if (answer == null) {
                        answer = "0";
                        dos.writeUTF(answer);
                        dos.flush();
                    }
                    System.out.println(timeout + ", " + answer);
                }

            } catch (SocketException e) {
                frame.getQuestion().setText("Server is not available.");
                System.out.println("Server is not available.");
                break;
            } catch (EOFException e) {
                System.out.println("pause time");
//                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            dis.close();
            dos.close();
        } catch (IOException e) {
            System.out.println(client.getPort() + " disconnected");
        }

    }

    public void startVictoryScreen(Session s) {
        VictoryScreenFrame vsf = new VictoryScreenFrame();
        vsf.setVisible(true);
        KButton mainBtt = vsf.getPlayBtt();
        for (PlayedQuestion playedQuestion : s.getQuestion()) {
            vsf.addPlayedQuestionToTable(playedQuestion);
        }
        mainBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                vsf.dispose();
                frame.setVisible(true);
            }
        });
        KButton exportBtt = vsf.getExportBtt();
        exportBtt.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveHistory(s);
                WriteFileExcel(s);
                JOptionPane.showMessageDialog(null, "Export Successful");

            }
        }
        );
    }

    public String handleAnswer(int answer) {
        switch (answer) {
            case 1:
                return "A";
            case 2:
                return "B";
            case 3:
                return "C";
            default:
                return "D";
        }
    }

    public void saveHistory(Session session) {

        try {
            FileWriter fw = new FileWriter("history.txt");
            String record = "Id: " + session.getPlayer().getId() + " - "
                    + "Address: " + session.getPlayer().getAddress() + "\n";

            ArrayList<PlayedQuestion> listQuestion = session.getQuestion();
            for (PlayedQuestion playedQuestion : listQuestion) {
                record = "Question: " + playedQuestion.getQuestion().getTitle() + " - "
                        + "Chosen answer: " + handleAnswer(playedQuestion.getChosenAnswer()) + " - "
                        + "Correct answer: " + handleAnswer(playedQuestion.getQuestion().getCorrectAnswer()) + " - "
                        + "Time: " + playedQuestion.getTime()
                        + "\n";
                System.out.println(record);
                fw.write((String) record);
            }

            fw.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        System.out.println("Success...");
    }

    public void WriteFileExcel(Session session) {
        System.out.println("Create file excel");
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Play History");
        int rowNum = 0;
        Row firstRow = sheet.createRow(rowNum++);
        Cell firstCell = firstRow.createCell(0);
        firstCell.setCellValue("List of player");
        List<PlayedQuestion> listOfPlayedQuestion = session.getQuestion();;

        Row header = sheet.createRow(rowNum++);
        Cell cellHeader1 = header.createCell(0);
        cellHeader1.setCellValue("Question");
        Cell cellHeader2 = header.createCell(1);
        cellHeader2.setCellValue("Chosen answer");
        Cell cellHeader3 = header.createCell(2);
        cellHeader3.setCellValue("Correct answer");
        Cell cellHeader4 = header.createCell(3);
        cellHeader4.setCellValue("Is correct");

        for (PlayedQuestion playedQuestion : listOfPlayedQuestion) {
            System.out.println(
                    "Question: " + playedQuestion.getQuestion().getTitle() + " - "
                    + "Chosen answer: " + handleAnswer(playedQuestion.getChosenAnswer()) + " - "
                    + "Correct answer: " + handleAnswer(playedQuestion.getQuestion().getCorrectAnswer()) + " - "
                    + "Time: " + playedQuestion.getTime()
            );
            Row row = sheet.createRow(rowNum++);
            Cell cell1 = row.createCell(0);
            cell1.setCellValue(playedQuestion.getQuestion().getTitle());
            Cell cell2 = row.createCell(1);
            cell2.setCellValue(handleAnswer(playedQuestion.getChosenAnswer()));
            Cell cell3 = row.createCell(2);
            cell3.setCellValue(handleAnswer(playedQuestion.getQuestion().getCorrectAnswer()));
            Cell cell4 = row.createCell(3);
            cell4.setCellValue((handleAnswer(playedQuestion.getChosenAnswer()) == null ? handleAnswer(playedQuestion.getQuestion().getCorrectAnswer()) == null : handleAnswer(playedQuestion.getChosenAnswer()).equals(handleAnswer(playedQuestion.getQuestion().getCorrectAnswer()))));
        }

        try {
            FileOutputStream outputStream = new FileOutputStream("History.xlsx");
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Done");
    }
}
