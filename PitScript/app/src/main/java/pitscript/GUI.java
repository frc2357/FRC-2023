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
    public static JButton button = new GUI("Transfer Files");
    public static JScrollPane scroll = new JScrollPane(text);
    public static JButton endButton = new JButton("End program");

    public GUI(String buttonText) {
        super(buttonText);
    }

    public static void writeToText(String message) {
        text.setEditable(true);
        text.setText(message);
        text.setEditable(false);
    }

    public static void buttonPressed() {
        if (App.TransferFiles()) {
            button.setBackground(Color.RED);
        }
        else{
            button.setBackground(Color.GREEN);
        }
    }
    public static void killProgram(){
        System.exit(0);
    }

    public static void MakeGUI() {
        frame = new JFrame();
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);
        frame.add(button);
        frame.add(endButton);
        frame.add(scroll);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        button.setBounds(0, 0, 145, 145);
        endButton.setBounds(241, 0, 145, 145);
        button.addActionListener(e -> GUI.buttonPressed());
        endButton.addActionListener(e -> GUI.killProgram());
        text.setEditable(false);
        text.setLineWrap(true);
        scroll.setBounds(0, 145, 750, 500);
        frame.setVisible(true);
    }
}