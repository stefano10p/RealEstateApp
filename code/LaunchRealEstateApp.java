/**
 * Class to Launch Application
 */
import java.awt.*;
import javax.swing.*;

public class LaunchRealEstateApp {
    public static void main(String [] args){
        RealEstateFrontEnd launchApp = new RealEstateFrontEnd();
        launchApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        launchApp.setVisible(true);
        launchApp.setSize(750,630);
        launchApp.setTitle("RealEstateProjectionsApp Copyright 2021, Stefano Parravano");

        WelcomeView launchWelcomePage = new WelcomeView();
        launchWelcomePage.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        launchWelcomePage.setVisible(true);
        launchWelcomePage.setSize(400,400);
        launchWelcomePage.setTitle("RealEstateProjectionsApp -- Welcome");
    }
}
