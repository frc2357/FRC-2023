package pitscript;

import java.awt.Color;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

public class GUI extends JButton {
    public static JTextArea text = new JTextArea("No operation performed", 1000, 1000);
    public static JFrame frame;
    public static JButton transferButton = new GUI("Transfer Files");
    public static JScrollPane scroll = new JScrollPane(text);
    public static JButton endButton = new JButton("End program");
    public static JButton deleteButton = new JButton("Delete Logs");
    private static boolean transfering = false;
    private static boolean deleting = false;
    private static Vector<String> m_threadMessages = new Vector<String>();

    private static App app = new App();

    public GUI(String buttonText) {
        super(buttonText);
    }

    public static void writeToText(String message) {
        text.append(message+"\n");
        text.updateUI();
    }
    public static void transferButtonPressed() {
        if (!transfering) {
            transfering = true;
            new SwingWorker<Boolean, Object>() {
                public Boolean doInBackground() {
                    transferButton.setBackground(Color.YELLOW);
                    if (!app.MultiThreadTransferFiles()) {
                        transferButton.setBackground(Color.RED);
                        return false;
                    } else {
                        transferButton.setBackground(Color.GREEN);
                        return true;
                    }
                }

                public void done() {
                    transfering = false;
                }
            }.execute();
        }
    }

    public static void neaterSetText(int threadID, String message){
        m_threadMessages.set(threadID, message);
        text.setText(m_threadMessages.toString().replace(",", "\n").replace('[',' ').replace(']', ' '));
        text.updateUI();
    }

    public static void deleteButtonPressed() {
        if (!deleting) {
            deleting = true;
            new SwingWorker<Boolean, Object>() {
                public Boolean doInBackground() {
                    deleteButton.setBackground(Color.YELLOW);
                    if (!App.deleteAllFiles()) {
                        deleteButton.setBackground(Color.RED);
                        return false;
                    } else {
                        deleteButton.setBackground(Color.GREEN);
                        return true;
                    }
                }

                public void done() {
                    deleting = false;
                }
            }.execute();
        }
    }

    public static void killSwitch() {
        System.out.println("program go bye bye because kill switch was used.");
        GUI.writeToText("kill switch was desired, so program has been poofed.");
        System.exit(0);
    }

    public static void MakeGUI() {
        m_threadMessages.setSize(Constants.THREAD_POOL_THREAD_COUNT);
        frame = new JFrame();
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(transferButton);
        frame.add(endButton);
        frame.add(deleteButton);
        frame.add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        transferButton.setBounds(0, 0, 145, 145);
        endButton.setBounds(241, 0, 145, 145);
        deleteButton.setBounds(145, 0, 96, 145);
        transferButton.addActionListener(e -> GUI.transferButtonPressed());
        endButton.addActionListener(e -> GUI.killSwitch());
        deleteButton.addActionListener(e -> GUI.deleteButtonPressed());
        text.setEditable(true);
        text.setLineWrap(true);
        scroll.setBounds(0, 145, 750, 500);
        frame.setVisible(true);
    }
}