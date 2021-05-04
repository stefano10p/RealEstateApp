/**
 * Class that defines the "input view" UI.
 * Users will interact with this interface to
 * provide our program with the desired inputs
 * @author Stefano Parravano
 */

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.io.*;

public class RealEstateFrontEnd extends JFrame {

    private JLabel [] jLabels; // array to store all JLabels
    private JTextField[] jTextFields; //array to store all JTextFields
    //JButtons that will be accessed in different methods
    // and classes
    private JButton runAnalysis = new JButton("Run Analysis!");
    private JButton clearInputs = new JButton("Clear Inputs and Outputs");
    private JButton loadSampleInputs = new JButton("Load Sample Inputs");
    private JButton writeResOutputToDisk = new JButton("Write Results To File");
    private JButton writeSimOutputToDisk = new JButton("Write Simulation Results To File");
    private JButton help = new JButton("Help");

    //table model to display simualtion and output results
    private DefaultTableModel model = new DefaultTableModel();
    private DefaultTableModel modelSimulation = new DefaultTableModel();
    private final String [] sampleInputs = {"Demo","480,000","610,000",
    "5","12,000","65,000","50","5.181","30","12","200","150","140","610,000",
    "10,500","25,000"};
    private int numberOfParameters = 15; //number of numeric inputs required for analysis
    private String stringUserInputs [] = new String [numberOfParameters];
    private BackEndCalculations backEndCalculations = new BackEndCalculations();
    private String resultsOutput = "";
    private String simulationResultsOutput = "";
    private String infoHelperText = "";

    /**
     * No arg constructor
     */
    public RealEstateFrontEnd(){
        createFrontEndLayOut();
    }

    /**
     * Inner class to Implement "Our Listener" to
     * perform actions when triggered by front end user
     */

    class InputListener implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (e.getSource()==runAnalysis){
                setStringUserInputs();
                backEndCalculations.feedStringUserInputs(stringUserInputs);
                String inputErrors = backEndCalculations.getInputErrors();
                if (inputErrors.length()==0){
                    // we may proceed to processing
                    backEndCalculations.setAllInputs();
                    backEndCalculations.performAnalysis();
                    addDataToResultsTable();
                    jLabels[20].setText("Analysis COMPLETE!");
                    addDataToSimulationResultsTable();
                    jLabels[21].setText("Simulation Analysis COMPLETE!");
                    JOptionPane.showMessageDialog(null, "Analysis Complete!" +
                            " \nPlease visit the Results and Simulation Results tab to " +
                            "inspect the output.",
                            "Successfully Completed Analysis",JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, inputErrors
                            ,"Input Error Alert",JOptionPane.ERROR_MESSAGE);

                }
            } else if (e.getSource()==clearInputs){
                clearUserInputsAndOutput();
            } else if (e.getSource()==loadSampleInputs){
                loadSampleInputs();
            } else if (e.getSource()==writeResOutputToDisk){
                if (resultsOutput.length()==0){
                    JOptionPane.showMessageDialog(null,
                            "No Results To write! Run anlaysis first."
                            ," Write Results Error Alert",JOptionPane.ERROR_MESSAGE);
                } else {
                    writeOutput(resultsOutput);
                }
            } else if (e.getSource()==writeSimOutputToDisk){
                if (simulationResultsOutput.length()==0){
                    JOptionPane.showMessageDialog(null,
                            "No Simulation Results to write! Run anlaysis first."
                            ," Write Simulation Results Error Alert",JOptionPane.ERROR_MESSAGE);
                } else {
                    writeOutput(simulationResultsOutput);
                }
            } else if (e.getSource()==help){
                // set up the information view
                HelpPopUp helpPopUp = new HelpPopUp();
                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BorderLayout());
                JEditorPane infoText = new JEditorPane();
                infoText.setContentType("text/html");
                infoText.setText(infoHelperText);
                infoText.setEditable(false);
                JScrollPane infoTextScroller = new JScrollPane(infoText);
                infoPanel.add(infoTextScroller);
                helpPopUp.add(infoPanel);
                helpPopUp.setVisible(true);
                helpPopUp.setSize(400,400);
            }
        }
    }

    /**
     * Helper method to clear all input cells
     * and output results. Method does not take
     * inputs and does not return anything
     */
    private void clearUserInputsAndOutput(){
        this.simulationResultsOutput="";
        this.resultsOutput="";
        for (int i=0; i<jTextFields.length-1;i++){
            jTextFields[i].setText("");
        }
        // we want to delete results from previous runs first
        int numberOfRows = model.getRowCount();
        if (numberOfRows >0){
            for (int k=numberOfRows-1; k>=0; k--) model.removeRow(k);
        }
        int numberOfRowsSimulation = modelSimulation.getRowCount();
        if (numberOfRowsSimulation >0){
            for (int z=numberOfRowsSimulation-1; z>=0; z--) modelSimulation.removeRow(z);
        }
        this.jLabels[20].setText("Results will be visible once an analysis is Executed!");
        this.jLabels[21].setText("Results will be visible once an analysis is Executed!");
    }

    /**
     * Helper method to load sample inputs for users to
     * gain familiarity with tool. Method does not take
     * inputs and does not return anything
     */
    private void loadSampleInputs(){
        clearUserInputsAndOutput();
        for (int i=0; i<=numberOfParameters;i++){
            this.jTextFields[i].setText(this.sampleInputs[i]);
        }
    }

    /**
     * Helper method to collect all userInputs into
     * an array. This array of inputs will get sent
     * to the backend for processing. Method does not take
     * inputs and does not return anything
     *
     */
    private void setStringUserInputs(){
        for (int i=1; i<=numberOfParameters; i++){
            this.stringUserInputs[i-1] = jTextFields[i].getText();
        }
    }

    /**
     * Helper method to add rows to our Results JTable
     * and assemple a string with the rusults of the main analysis.
     * It is not necessary to show results for all time periods.
     * Only the time periods where the net profit is >0 are relevant
     * and as such we only display those to the user.
     */
    private void addDataToResultsTable(){
        // first we work out where the negative values start
        int numberOfPeriods = this.backEndCalculations.getTotalNumberOfPayments();
        int stopIndex = numberOfPeriods;
        double [] netProfit = this.backEndCalculations.getNetProfit();
        for (int i=0; i< numberOfPeriods;i++){
            if (netProfit[i]<0){
                stopIndex = i;
                break;
            }
        }
        if(stopIndex==0) stopIndex=1; //in case project is not profitable in first period we show that
        // we want to delete results from previous runs first
        int numberOfRows = model.getRowCount();
        if (numberOfRows >0){
            for (int k=numberOfRows-1; k>=0; k--) model.removeRow(k);
        }

        double projectedResalePrice = this.backEndCalculations.getProjectedResalePrice();
        double [] financingBalance = this.backEndCalculations.getLoanBalances();
        double [] cumulativeCapex = this.backEndCalculations.getCumulativeCapitalInvested();
        double [] netSale = this.backEndCalculations.getNetSale();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setMaximumFractionDigits(0);

        // add resuilts to our table view
        for (int j=0; j<stopIndex; j++){
            this.model.addRow(new Object [] {
                    j+1,
                    formatter.format(projectedResalePrice),
                    formatter.format(financingBalance[j]),
                    formatter.format(cumulativeCapex[j]),
                    formatter.format(netSale[j]),
                    formatter.format(netProfit[j])
            });
        }
        //store results to class member as a String
        // we will use this to write the results to disk
        String [] userInputs = this.backEndCalculations.getUserInputNames();
        this.resultsOutput="";
        this.resultsOutput +="Model Inputs\n" + "Project Name,"
                + "\"" + jTextFields[0].getText()+"\"\n";
        for (int w=0; w<userInputs.length;w++){
            this.resultsOutput += userInputs[w] + ",";
            this.resultsOutput += ("\"" + this.stringUserInputs[w] + "\"\n");
        }
        this.resultsOutput+="\n\n";
        this.resultsOutput+="Time Period,Resale Price,Financing Balance,"
        +"Cumulative Capital Invested,Net Sale,Net Profit\n";
        for (int k=0; k<stopIndex;k++){
            int valToPrint = k+1;
            this.resultsOutput+=""+valToPrint
                    +"," + "\"" + formatter.format(projectedResalePrice) + "\""
                    +"," + "\"" + formatter.format(financingBalance[k]) + "\""
                    +"," + "\"" + formatter.format(cumulativeCapex[k]) + "\""
                    +"," + "\"" + formatter.format(netSale[k]) + "\""
                    +"," + "\"" + formatter.format(netProfit[k]) + "\""
                    +"\n";
        }
    }


    /**
     * Helper method to add rows to our SimulationsResults JTable
     * and assemple a string with the rusults of the simulation analysis.
     * It is not necessary to show results for all time periods.
     * Only the time periods where the Mean Net Profit is >0 are relevant
     * and as such we only display those to the user.
     */
    private void addDataToSimulationResultsTable(){
        // first we work out where the negative values start
        int numberOfPeriods = this.backEndCalculations.getTotalNumberOfPayments();
        int stopIndex = numberOfPeriods;
        double [] meanSimulatedNetProfit = this.backEndCalculations.getSimulatedMeanNetProfits();
        for (int i=0; i< numberOfPeriods;i++){
            if (meanSimulatedNetProfit[i]<0){
                stopIndex = i;
                break;
            }
        }
        if(stopIndex==0) stopIndex=1; //in case project is not profitable in first period we show that
        // we want to delete results from previous runs first
        int numberOfRows = modelSimulation.getRowCount();
        if (numberOfRows >0){
            for (int k=numberOfRows-1; k>=0; k--) modelSimulation.removeRow(k);
        }

        double meanSimulatedResalePrice = this.backEndCalculations.getSimulatedMeanResalePrice();
        String [] simulatedCIIntervalNetProfit = this.backEndCalculations.getSimulatedConfidenceIntervalNetProfit();
        String [] simulatedProbabilityProjectSuccess = this.backEndCalculations.getSimulatedProbabilityOfSuccess();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setMaximumFractionDigits(0);

        for (int j =0; j<stopIndex; j++){
            this.modelSimulation.addRow(new Object [] {
                    j+1,
                    formatter.format(meanSimulatedResalePrice),
                    formatter.format(meanSimulatedNetProfit[j]),
                    simulatedCIIntervalNetProfit[j],
                    simulatedProbabilityProjectSuccess[j]
            });
        }
        //store results to class member as a String
        // we will use this to write the results to disk
        String [] userInputs = this.backEndCalculations.getUserInputNames();
        this.simulationResultsOutput="";
        this.simulationResultsOutput +="Model Inputs\n"
                + "Project Name," + "\"" + jTextFields[0].getText()+"\"\n";
        for (int w=0; w<userInputs.length;w++){
            this.simulationResultsOutput += userInputs[w] + ",";
            this.simulationResultsOutput += ("\"" + this.stringUserInputs[w] + "\"\n");
        }
        this.simulationResultsOutput+="\n\n";
        this.simulationResultsOutput+="Time Period,Mean Resale Price,Mean Net Profit,"
                +"Net Profit 95% CI,Probability Of Success\n";
        for (int k=0; k<stopIndex;k++){
            int valToPrint = k+1;
            this.simulationResultsOutput+=""+valToPrint
                    +"," + "\"" + formatter.format(meanSimulatedResalePrice) + "\""
                    +"," + "\"" + formatter.format(meanSimulatedNetProfit[k]) + "\""
                    +"," + "\"" + simulatedCIIntervalNetProfit[k] + "\""
                    +"," + "\"" + simulatedProbabilityProjectSuccess[k] + "\""
                    +"\n";
        }
    }


    /**
     * Helper method to initialize all Jlabels that will
     * be made visible in the front end view
     */
    private void initJLabels(){
        jLabels = new JLabel[22];
        String [] jLabelsText = {"Project Name","Purchase Price ($)",
                "Projected Resale Price ($)","Down Payment %",
                "Other Costs At Closing ($)",
                "Projected CAPEX ($) (PCE)", "PCE Down Payment %",
                "Loan Interest Rate %","Loan Duration in Years",
                "# of Loan Payments Per Year","Monthly Real-Estate Taxes ($)",
                "Monthly Insurance ($)","Other Monthly Expenses ($)",
                "Resale Price Mean ($)","Resale Price STD ($)",
                "Number of Simulations","Probability Distribution",
                "General Inputs","Financing Inputs",
                "Simulation Inputs","Results will be visible once an analysis is Executed!",
                "Results will be visible once an analysis is Executed!"
        };
        for (int i=0; i<jLabelsText.length;i++){
            jLabels[i]= new JLabel(jLabelsText[i]);
        }

    }

    /**
     * Helper method to initialize all JTextFields that
     * will be made visible in the front end view.
     */
    private void initJTextFields(){
        jTextFields = new JTextField[17];
        for (int i=0; i<jTextFields.length;i++){
            jTextFields[i] = new JTextField("",15);
        }
        jTextFields[16].setText("Gaussian");
    }

    /**
     * Helper method to write results of analysis to disk.
     * @param outPut
     */
    private void writeOutput(String outPut){
        JFileChooser chooser = new JFileChooser();
        chooser.showSaveDialog (null);
        String fileName = chooser.getSelectedFile().getAbsolutePath();
        try {
            PrintWriter outStream =  new PrintWriter (new File (fileName));
            outStream.print (outPut);
            outStream.close ();
            JOptionPane.showMessageDialog (null, "Output was successfully written to disk!");
        } catch (IOException errorFileWrite){
            JOptionPane.showMessageDialog(null, errorFileWrite.getMessage()
                    ,"Input Error Alert",JOptionPane.ERROR_MESSAGE);
            errorFileWrite.printStackTrace();
        }
    }

    /**
     * Helper method to Create FrontEnd layout.
     * The "look" of our GUI is defined in this method. This
     * will get called in the constructor.
     */
    private void createFrontEndLayOut(){
        initJLabels();
        initJTextFields();
        generateInfoText();
        JTabbedPane tabs;JPanel resultsPanel;
        JPanel simulationResultsPanel;JPanel inputPanel;

        JPanel generalInputs = new JPanel();generalInputs.setLayout(new GridLayout(4,4,8,0));
        JPanel financingInputs = new JPanel();financingInputs.setLayout(new GridLayout(4,4,8,0));
        JPanel simulationInputs = new JPanel();simulationInputs.setLayout(new GridLayout(2,4,8,0));

        JLabel emptyLabel0 = new JLabel("");JLabel emptyLabel1 = new JLabel("");
        JLabel emptyLabel2 = new JLabel("");JLabel emptyLabel3 = new JLabel("");
        JTextField emptyTextField = new JTextField("");emptyTextField.setEditable(false);
        // define colors and fonts for key text boxes and headers
        Color backgroundColor = new Color(174,224,255);
        Color lightBackgroundColor = new Color(211,211,211);
        Font font = new Font("TimesRoman", Font.BOLD,20);
        //Our GUI will have three tabs. THese are defined here.
        tabs = new JTabbedPane(); resultsPanel = new JPanel();
        inputPanel = new JPanel(new GridBagLayout());
        simulationResultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());
        simulationResultsPanel.setLayout(new BorderLayout());
        //General Inputs Header Text
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,10,0,10);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
        this.jLabels[17].setFont(font);
        inputPanel.add(this.jLabels[17],c);
        //Project Name
        c.gridx = 0; c.gridy = 1; c.gridwidth = 1;
        generalInputs.add(this.jLabels[0]);
        this.jTextFields[0].setBackground(backgroundColor);
        generalInputs.add(this.jTextFields[0]);
        generalInputs.add(emptyLabel0);
        generalInputs.add(emptyLabel1);

        //Purchase Price and Projected Resale Price
        generalInputs.add(this.jLabels[1]);
        this.jTextFields[1].setBackground(backgroundColor);
        generalInputs.add(this.jTextFields[1]);
        generalInputs.add(this.jLabels[2]);
        this.jTextFields[2].setBackground(backgroundColor);
        generalInputs.add(this.jTextFields[2]);
        //Down Payment % and Other Closing Costs
        generalInputs.add(this.jLabels[3]);
        this.jTextFields[3].setBackground(backgroundColor);
        generalInputs.add(this.jTextFields[3]);
        generalInputs.add(this.jLabels[4]);
        this.jTextFields[4].setBackground(backgroundColor);
        generalInputs.add(this.jTextFields[4]);
        //PCE and PCE %
        generalInputs.add(this.jLabels[5]);
        this.jTextFields[5].setBackground(backgroundColor);
        generalInputs.add(this.jTextFields[5]);
        generalInputs.add(this.jLabels[6]);
        this.jTextFields[6].setBackground(backgroundColor);
        generalInputs.add(this.jTextFields[6]);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 1;
        inputPanel.add(generalInputs,c);

        //Financing Inputs Header
        c.gridx = 0; c.gridy = 3; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        this.jLabels[18].setFont(font);
        inputPanel.add(this.jLabels[18],c);
        c.insets = new Insets(0,10,0,10);
        c.weightx=0;
        //Loan Interest Rate
        financingInputs.add(this.jLabels[7]);
        this.jTextFields[7].setBackground(backgroundColor);
        financingInputs.add(this.jTextFields[7]);
        financingInputs.add(emptyLabel2);
        financingInputs.add(emptyLabel3);
        //Loan Duration and # of Loan Payments Per Year
        financingInputs.add(this.jLabels[8]);
        this.jTextFields[8].setBackground(backgroundColor);
        financingInputs.add(this.jTextFields[8]);
        financingInputs.add(this.jLabels[9]);
        this.jTextFields[9].setBackground(backgroundColor);
        financingInputs.add(this.jTextFields[9]);
        // Monthly Taxes and Insurance
        financingInputs.add(this.jLabels[10]);
        this.jTextFields[10].setBackground(backgroundColor);
        financingInputs.add(this.jTextFields[10]);
        financingInputs.add(this.jLabels[11]);
        this.jTextFields[11].setBackground(backgroundColor);
        financingInputs.add(this.jTextFields[11]);
        // Other Monthly Expenses
        inputPanel.add(this.jLabels[12],c);
        financingInputs.add(this.jLabels[12]);
        this.jTextFields[12].setBackground(backgroundColor);
        financingInputs.add(this.jTextFields[12]);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 1;
        inputPanel.add(financingInputs,c);


        //Simulation Input Header Text
        c.gridx = 0; c.gridy = 5; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        this.jLabels[19].setFont(font);
        inputPanel.add(this.jLabels[19],c);
        c.insets = new Insets(0,10,0,10);
        //Resale Price Mean and Resale Price Standard Deviation
        simulationInputs.add(this.jLabels[13]);
        this.jTextFields[13].setBackground(backgroundColor);
        simulationInputs.add(this.jTextFields[13]);
        simulationInputs.add(this.jLabels[14]);
        this.jTextFields[14].setBackground(backgroundColor);
        simulationInputs.add(this.jTextFields[14]);
        //Number of Simulations Assumed Probability Distribution
        simulationInputs.add(this.jLabels[15]);
        this.jTextFields[15].setBackground(backgroundColor);
        simulationInputs.add(this.jTextFields[15]);
        simulationInputs.add(this.jLabels[16]);
        this.jTextFields[16].setBackground(lightBackgroundColor);
        this.jTextFields[16].setEditable(false);
        simulationInputs.add(this.jTextFields[16]);

        c.gridx = 0; c.gridy = 6; c.gridwidth = 1;
        inputPanel.add(simulationInputs,c);

        // add buttons
        c.insets = new Insets(40,10,0,10);
        c.gridx=0;c.gridy=7;c.gridwidth=1;
        c.weightx=1.0;
        inputPanel.add(clearInputs,c);
        c.insets = new Insets(0,10,0,10);
        c.gridx=0;c.gridy=8;c.gridwidth=1;
        inputPanel.add(loadSampleInputs,c);
        c.gridx=0;c.gridy=9;c.gridwidth=1;
        inputPanel.add(runAnalysis,c);

        c.gridx=0;c.gridy=10;c.gridwidth=1;
        inputPanel.add(help,c);

        // add listeners on each button
        InputListener inputListener = new InputListener();
        runAnalysis.addActionListener(inputListener);
        clearInputs.addActionListener(inputListener);
        loadSampleInputs.addActionListener(inputListener);
        writeResOutputToDisk.addActionListener(inputListener);
        writeSimOutputToDisk.addActionListener(inputListener);

        help.addActionListener(inputListener);

        // Set up of results and sim views
        JTable results = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(results);
        JTable simulationResults = new JTable(modelSimulation);
        JScrollPane scrollPaneSimulation = new JScrollPane(simulationResults);

        Color limeGreen = new Color(50,205,50);
        writeSimOutputToDisk.setBackground(limeGreen);
        writeResOutputToDisk.setBackground(limeGreen);
        // set up the Results view
        model.addColumn("Time Period"); model.addColumn("Resale Price");
        model.addColumn("Financing Balance"); model.addColumn("Capital Invested");
        model.addColumn("Net Sale"); model.addColumn("Net Profit");
        results.getColumnModel().getColumn(0).setPreferredWidth(20);
        results.getColumnModel().getColumn(1).setPreferredWidth(60);
        results.getColumnModel().getColumn(2).setPreferredWidth(75);
        results.getColumnModel().getColumn(4).setPreferredWidth(60);
        results.getColumnModel().getColumn(5).setPreferredWidth(60);
        resultsPanel.add(this.jLabels[20],BorderLayout.PAGE_START);
        resultsPanel.add(scrollPane,BorderLayout.CENTER);
        resultsPanel.add(writeResOutputToDisk,BorderLayout.PAGE_END);

        // set up the simulationResults view
        modelSimulation.addColumn("Time Period"); modelSimulation.addColumn("Mean Resale Price");
        modelSimulation.addColumn("Mean Net Profit"); modelSimulation.addColumn("Net Profit 95% CI");
        modelSimulation.addColumn("Probability Of Success");
        simulationResults.getColumnModel().getColumn(0).setPreferredWidth(5);
        simulationResults.getColumnModel().getColumn(1).setPreferredWidth(60);
        simulationResults.getColumnModel().getColumn(2).setPreferredWidth(55);
        simulationResults.getColumnModel().getColumn(3).setPreferredWidth(90);
        simulationResults.getColumnModel().getColumn(4).setPreferredWidth(50);
        simulationResultsPanel.add(this.jLabels[21],BorderLayout.PAGE_START);
        simulationResultsPanel.add(scrollPaneSimulation,BorderLayout.CENTER);
        simulationResultsPanel.add(writeSimOutputToDisk,BorderLayout.PAGE_END);

        tabs.add("Inputs",inputPanel);
        tabs.add("Results",resultsPanel);
        tabs.add("Simulation Results",simulationResultsPanel);
        this.add(tabs);
    }

    private void generateInfoText(){
        this.infoHelperText+="<header><b>Below is an input dictionary providing insight on the meaning"
                + " of all the inputs to the model.</b><br/><br/></header>";
        String [] descriptions = {
                "Name of Project.<br/>",
                "Price in USD that was initially paid to acquire the asset.<br/>",
                "Projected resale price in USD of asset.<br/>",
                "Percent of purchase Price that was made available in cash at time of purchase."
                        +"This is usually 5%.<br/>",
                "Costs incurred at closing (legal fees, closing costs, etc.).<br/>",
                "Projected Capital Expenditure required to perform renovations." +
                        "Estimate this value conservatively.<br/>",
                "Percent of CAPEX made available in cash at time of purchase.<br/>",
                "Cost of borrowing funds required to complete project.<br/>",
                "The duration period of the financing agreement. Usually this is 30 years or less.<br/>",
                "The amount of loan payments made in a year. Typically this is 12 (once per month).<br/>",
                "Yearly Real Estate taxes divided by 12.<br/>",
                "Monthly costs of insurance premiums.<br/>",
                "Other miscellaneous monthly expenses.<br/>",
                "Desired mean value of simulated projected resale prices.<br/>",
                "Desired Standard Deviation of simulated resale prices.<br/>",
                "Number of simulations to be performed.<br/>"
        };
        for (int i=0; i<descriptions.length;i++){
            this.infoHelperText +="<b>"+jLabels[i].getText()+"</b>"
                    +": " + descriptions[i] + "\n";
        }

    }

}

