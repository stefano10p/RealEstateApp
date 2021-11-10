/**
 * Class that defines a welcome
 * view that will be displayed
 * when app launches
 */
import javax.swing.*;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class WelcomeView extends JFrame{
    private JButton close = new JButton("To close window please read message and click here");
    private JFrame mainFrame = this;

    public WelcomeView(){
        JPanel mainPage = new JPanel ();
        JPanel outperPage = new JPanel();
        outperPage.setLayout(new BorderLayout());
        BoxLayout boxlayout = new BoxLayout(mainPage,BoxLayout.Y_AXIS);
        mainPage.setLayout(boxlayout);
        Font font = new Font("TimesRoman", Font.BOLD,15);
        Color darkGreen = new Color(0,100,0);

        String information = "RealEstateProjectionsApp is an interactive "
                +"tool for\nanalyzing prospective real estate \"flip\" "
                +"investments.\nPlease insert the parameters of your project "
                +"in the\n\"Inputs\" tab and inspect the results in the \"Results\""
                +" and\n\"Simulation Results\" tabs.";
        String warning = "Stefano Parravano is not a registered investment,\n"
                +" legal or tax advisor or a broker/dealer. All investment\n"
                +" /financial insights made available by this software are\n"
                +" from the personal research and experience of the author\n"
                +" and are intended as educational material only. Although\n"
                +" best efforts are made to ensure that all information is\n"
                +" accurate and up to date, occasionally unintended errors\n"
                +" may occur.";
        String copyRight = "RealEstateProjectionsApp 2021.05\n"
                +"Copyright 2021, Stefano Parravano";


        Border border = BorderFactory.createLineBorder(Color.BLACK, 3);

        JTextArea infoText = new JTextArea(information);
        infoText.setFont(font);
        infoText.setForeground(darkGreen);

        infoText.setBorder(border);
        JTextArea warningText = new JTextArea(warning);
        warningText.setFont(font);
        warningText.setForeground(Color.red);
        warningText.setBorder(border);
        JTextArea copyRightText = new JTextArea(copyRight);
        copyRightText.setFont(font);
        copyRightText.setForeground(Color.black);
        copyRightText.setBorder(border);
        infoText.setEditable(false);
        copyRightText.setEditable(false);
        warningText.setEditable(false);

        mainPage.add(copyRightText);
        mainPage.add(infoText);
        mainPage.add(warningText);
        outperPage.add(mainPage,BorderLayout.CENTER);
        outperPage.add(close,BorderLayout.PAGE_END);

        WelcomeListener welcomeListener = new WelcomeListener();
        close.addActionListener(welcomeListener);
        this.add(outperPage);
    }

    class WelcomeListener implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (e.getSource()==close){
                mainFrame.dispose();
            }
        }
    }

}