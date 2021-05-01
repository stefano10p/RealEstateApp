//package com.stefano;

import javax.swing.*;
import java.awt.*;

/**
 * Front end for RealEstate App that
 * enables investors to perform important
 * computations to evaluate prospective
 * deals
 */

public class RealEstateFrontEnd extends JFrame{
    private InputView inputsPanel;
    // add members for ReulstsView and SimulationResultsView

    /**
     * No arg Constructor to initiate front
     * end view.
     */
    public RealEstateFrontEnd(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // add Tabs and panels for tabs

        //InputView inputsPanel = new InputView();
        this.inputsPanel = new InputView();
        System.out.println(inputsPanel.getBackEndCalculations().getNetSale());
        JPanel resultsPanel = new JPanel(new GridBagLayout());
        JPanel simulationResultsPanel = new JPanel(new GridBagLayout());
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Inputs",inputsPanel);
        tabs.add("Results",resultsPanel);
        tabs.add("Simulation Results",simulationResultsPanel);
        this.add(tabs);
        //this.setVisible(true);
        this.pack();
    }

}