package pitscript;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;

public class GUI extends JButton {
    public static JTextArea text = new JTextArea("No operation performed",1000,1000);
    public static JFrame frame;
    public static JButton transferButton = new GUI("Transfer Files");
    public static JScrollPane scroll = new JScrollPane(text);
    public static JButton endButton = new JButton("End program");

    public GUI(String buttonText) {
        super(buttonText);
    }

    public static void writeToText(String message) {
        text.setText(message);
        text.updateUI();
    }

    public static void transferButtonPressed() {
        new SwingWorker<Boolean , Object>(){
            public Boolean doInBackground(){
            transferButton.setBackground(Color.YELLOW);
        if (!App.TransferFiles()) {
            transferButton.setBackground(Color.RED);
            return false;
        }
        else{
            transferButton.setBackground(Color.GREEN);
            return true;
        }
            }
   
            public void done(){

            }
       }.execute();
        
    }


    public static void killSwitch(){
        System.out.println("program go bye bye because kill switch was used.");
        GUI.writeToText("kill switch was desired, so program has been poofed.");
        System.exit(ABORT);
    }

    public static void MakeGUI() {
        frame = new JFrame();
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(transferButton);
        frame.add(endButton);
        frame.add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        transferButton.setBounds(0, 0, 145, 145);
        endButton.setBounds(241,0,145,145);
        transferButton.addActionListener(e -> GUI.transferButtonPressed());
        endButton.addActionListener(e -> GUI.killSwitch());
        text.setEditable(true);
        text.setLineWrap(true);
        scroll.setBounds(0, 145, 750, 500);
        frame.setVisible(true);
    }
}