package pitscript;

import java.awt.Color;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

public class GUI extends JButton {
    public static JTextArea text = new JTextArea("No operation performed",1000,1000);
    public static JFrame frame;
    public static JButton transferButton = new GUI("Transfer Files");
    public static JButton deleteButton = new JButton("Delete Files");
    public static JScrollPane scroll = new JScrollPane(text);
    public static JButton endButton = new JButton("End program");

    public GUI(String buttonText) {
        super(buttonText);
    }

    public static void writeToText(String message) {
        text.setEditable(true);
        text.setText(message);
        text.setEditable(false);
        text.updateUI();
    }

    public static void transferButtonPressed() {
        if (!App.TransferFiles()) {
            transferButton.setBackground(Color.RED);
        }
        else{
            transferButton.setBackground(Color.GREEN);
        }
    }

    public static void deleteButtonPressed(){
        if (!App.deleteFiles()) {
            deleteButton.setBackground(Color.RED);
        }
        else{
            deleteButton.setBackground(Color.GREEN);
        }
    }

    public static void MakeGUI() {
        frame = new JFrame();
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(transferButton);
        frame.add(deleteButton);
        frame.add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        transferButton.setBounds(0, 0, 145, 145);
        deleteButton.setBounds(241,0,145,145);
        transferButton.addActionListener(e -> GUI.transferButtonPressed());
        deleteButton.addActionListener(e -> GUI.deleteButtonPressed());
        text.setEditable(false);
        text.setLineWrap(true);
        scroll.setBounds(0, 145, 750, 500);
        frame.setVisible(true);
    }
}