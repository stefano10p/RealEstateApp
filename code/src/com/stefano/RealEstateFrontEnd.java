import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

/**
 * Class that defines the "input view" UI.
 * Users will interact with this interface to
 * provide our program with the desired inputs
 */

public class RealEstateFrontEnd extends JFrame {

    private JLabel [] jLabels; // array to store all JLabels
    private JTextField[] jTextFields; //array to store all JTextFields
    //JButtons that will be accessed in different methods
    // and classes
    private JButton runAnalysis = new JButton("Run Analysis!");
    private JButton clearInputs = new JButton("Clear Inputs");
    private JButton loadSampleInputs = new JButton("Load Sample Inputs");
    //table model to display simualtion and output results
    private DefaultTableModel model = new DefaultTableModel();
    private DefaultTableModel modelSimulation = new DefaultTableModel();
    private final String [] sampleInputs = {"Demo","480,000","610,000",
    "5","12,000","65,000","50","5.181","30","12","200","150","140","610,000",
    "10,500","25,000"};
    private int numberOfParameters = 15; //number of numeric inputs required for analysis
    private String stringUserInputs [] = new String [numberOfParameters];
    private BackEndCalculations backEndCalculations = new BackEndCalculations();

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
            }
        }
    }

    /**
     * Helper method to clear all input cells
     * and outputResults. Method does not take
     * inputs and does not return anything
     */
    private void clearUserInputsAndOutput(){
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
     * an array. This array on inputs will get sent
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
     * Helper method to add rows to our Results JTable.
     * It is not necessary to show results for all time periods.
     * Only the time periods where the netProfit is >0 are relevant
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

        for (int j =0; j<stopIndex; j++){
            this.model.addRow(new Object [] {
                    j+1,
                    formatter.format(projectedResalePrice),
                    formatter.format(financingBalance[j]),
                    formatter.format(cumulativeCapex[j]),
                    formatter.format(netSale[j]),
                    formatter.format(netProfit[j])
            });
        }
    }


    /**
     * Helper method to add rows to our SimulationsResults JTable.
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
        // we want to delete results from previous runs first
        int numberOfRows = modelSimulation.getRowCount();
        if (numberOfRows >0){
            for (int k=numberOfRows-1; k>=0; k--) modelSimulation.removeRow(k);
        }

        double meanSimulatedResalePrice = this.backEndCalculations.getSimulatedMeanResalePrice();
        String [] simulatedCIIntervalNetProfit = this.backEndCalculations.getSimulatedConfidenceIntervalNetProfit();
        String [] simulatedProbabilityProjectSuccess = this.backEndCalculations.getSimulatedProbabilityOfSuccess();

        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        for (int j =0; j<stopIndex; j++){
            this.modelSimulation.addRow(new Object [] {
                    j+1,
                    formatter.format(meanSimulatedResalePrice),
                    formatter.format(meanSimulatedNetProfit[j]),
                    simulatedCIIntervalNetProfit[j],
                    simulatedProbabilityProjectSuccess[j]
            });
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
                "Projected Capital Expenditure (PCE)", "PCE Down Payment %",
                "Loan Interest Rate %","Loan Duration in Years",
                "# of Loan Payments per Year","Monthly Real-Estate Taxes",
                "Monthly Insurance Costs","Other Monthly Expenses",
                "Resale Price Mean","Resale Price Standard Deviation",
                "Number of Simulations","Assumed Probability Distribution",
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
     * Helper method to Create FrontEnd layout.
     * The look of our GUI is defined in this method.
     */
    private void createFrontEndLayOut(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initJLabels();
        initJTextFields();
        JTabbedPane tabs;JPanel resultsPanel;
        JPanel simulationResultsPanel;JPanel inputPanel;

        Color backgroundColor = new Color(174,224,255);
        Color lightBackgroundColor = new Color(211,211,211);
        Font font = new Font("TimesRoman", Font.BOLD,20);

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
        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        inputPanel.add(this.jLabels[0],c);
        c.gridx = 2; c.gridy = 1; c.gridwidth = 1;
        this.jTextFields[0].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[0],c);
        //Purchase Price and Projected Resale Price
        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        inputPanel.add(this.jLabels[1],c);
        c.gridx = 2; c.gridy = 2; c.gridwidth = 1;
        this.jTextFields[1].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[1],c);
        c.gridx = 3; c.gridy = 2; c.gridwidth = 2;
        inputPanel.add(this.jLabels[2],c);
        c.gridx = 5; c.gridy = 2; c.gridwidth = 1;
        this.jTextFields[2].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[2],c);
        //Down Payment % and Other Closing Costs
        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        inputPanel.add(this.jLabels[3],c);
        c.gridx = 2; c.gridy = 3; c.gridwidth = 1;
        this.jTextFields[3].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[3],c);
        c.gridx = 3; c.gridy = 3; c.gridwidth = 2;
        inputPanel.add(this.jLabels[4],c);
        c.gridx = 5; c.gridy = 3; c.gridwidth = 2;
        this.jTextFields[4].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[4],c);
        //PCE and PCE %
        c.gridx = 0; c.gridy = 4; c.gridwidth = 2;
        inputPanel.add(this.jLabels[5],c);
        c.gridx = 2; c.gridy = 4; c.gridwidth = 1;
        this.jTextFields[5].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[5],c);
        c.gridx = 3; c.gridy = 4; c.gridwidth = 2;
        inputPanel.add(this.jLabels[6],c);
        c.gridx = 5; c.gridy = 4; c.gridwidth = 1;
        this.jTextFields[6].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[6],c);
        //Financing Inputs JLabel
        c.gridx = 0; c.gridy = 6; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        this.jLabels[18].setFont(font);
        inputPanel.add(this.jLabels[18],c);
        c.insets = new Insets(0,10,0,10);
        c.weightx=0;
        //Loan Interest Rate
        c.gridx = 0; c.gridy = 7; c.gridwidth = 2;
        inputPanel.add(this.jLabels[7],c);
        c.gridx = 2; c.gridy = 7; c.gridwidth = 1;
        this.jTextFields[7].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[7],c);
        //Loan Duration and # of Loan Payments Per Year
        c.gridx = 0; c.gridy = 8; c.gridwidth = 2;
        inputPanel.add(this.jLabels[8],c);
        c.gridx = 2; c.gridy = 8; c.gridwidth = 1;
        this.jTextFields[8].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[8],c);
        c.gridx = 3; c.gridy = 8; c.gridwidth = 2;
        inputPanel.add(this.jLabels[9],c);
        c.gridx = 5; c.gridy = 8; c.gridwidth = 1;
        this.jTextFields[9].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[9],c);
        // Monthly Taxes and Insurance
        c.gridx = 0; c.gridy = 9; c.gridwidth = 2;
        inputPanel.add(this.jLabels[10],c);
        c.gridx = 2; c.gridy = 9; c.gridwidth = 1;
        this.jTextFields[10].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[10],c);
        c.gridx = 3; c.gridy = 9; c.gridwidth = 2;
        inputPanel.add(this.jLabels[11],c);
        c.gridx = 5; c.gridy = 9; c.gridwidth = 1;
        this.jTextFields[11].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[11],c);
        // Other Monthly Expenses
        c.gridx = 0; c.gridy = 10; c.gridwidth = 2;
        inputPanel.add(this.jLabels[12],c);
        c.gridx = 2; c.gridy = 10; c.gridwidth = 1;
        this.jTextFields[12].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[12],c);
        //Simulation Input Header Text
        c.gridx = 0; c.gridy = 11; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        this.jLabels[19].setFont(font);
        inputPanel.add(this.jLabels[19],c);
        c.insets = new Insets(0,10,0,10);
        //Resale Price Mean and Resale Price Standard Deviation
        c.gridx = 0; c.gridy = 12; c.gridwidth = 2;
        inputPanel.add(this.jLabels[13],c);
        c.gridx = 2; c.gridy = 12; c.gridwidth = 1;
        this.jTextFields[13].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[13],c);
        c.gridx = 3; c.gridy = 12; c.gridwidth = 2;
        inputPanel.add(this.jLabels[14],c);
        c.gridx = 5; c.gridy = 12; c.gridwidth = 1;
        this.jTextFields[14].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[14],c);
        //Number of Simulations Assumed Probability Distribution
        c.gridx = 0; c.gridy = 13; c.gridwidth = 2;
        inputPanel.add(this.jLabels[15],c);
        c.gridx = 2; c.gridy = 13; c.gridwidth = 1;
        this.jTextFields[15].setBackground(backgroundColor);
        inputPanel.add(this.jTextFields[15],c);
        c.gridx = 3; c.gridy = 13; c.gridwidth = 2;
        inputPanel.add(this.jLabels[16],c);
        c.gridx = 5; c.gridy = 13; c.gridwidth = 1;
        this.jTextFields[16].setBackground(lightBackgroundColor);
        this.jTextFields[16].setEditable(false);
        inputPanel.add(this.jTextFields[16],c);

        // add buttons
        c.insets = new Insets(40,10,0,10);
        c.gridx=0;c.gridy=14;c.gridwidth=6;
        c.weightx=1.0;
        inputPanel.add(clearInputs,c);
        c.insets = new Insets(0,10,0,10);
        c.gridx=0;c.gridy=15;c.gridwidth=6;
        inputPanel.add(loadSampleInputs,c);
        c.gridx=0;c.gridy=16;c.gridwidth=6;
        inputPanel.add(runAnalysis,c);

        // add listeners on each button
        InputListener inputListener = new InputListener();
        runAnalysis.addActionListener(inputListener);
        clearInputs.addActionListener(inputListener);
        loadSampleInputs.addActionListener(inputListener);


        // Set up of results and sim views
        JTable results = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(results);
        JTable simulationResults = new JTable(modelSimulation);
        JScrollPane scrollPaneSimulation = new JScrollPane(simulationResults);
        // set up the Results view
        model.addColumn("Time Period"); model.addColumn("Resale Price");
        model.addColumn("Financing Balance"); model.addColumn("Cumulative Capital Invested");
        model.addColumn("Net Sale"); model.addColumn("Net Profit");
        results.getColumnModel().getColumn(1).setPreferredWidth(100);
        results.getColumnModel().getColumn(3).setPreferredWidth(140);
        resultsPanel.add(this.jLabels[20],BorderLayout.PAGE_START);
        resultsPanel.add(scrollPane,BorderLayout.CENTER);

        // set up the simulationResults view
        modelSimulation.addColumn("Time Period"); modelSimulation.addColumn("Mean Resale Price");
        modelSimulation.addColumn("Mean Net Profit"); modelSimulation.addColumn("Net Profit 95% CI");
        modelSimulation.addColumn("Probability Of Success");
        simulationResultsPanel.add(this.jLabels[21],BorderLayout.PAGE_START);
        simulationResultsPanel.add(scrollPaneSimulation,BorderLayout.CENTER);

        tabs.add("Inputs",inputPanel);
        tabs.add("Results",resultsPanel);
        tabs.add("Simulation Results",simulationResultsPanel);
        this.add(tabs);
        this.setVisible (true);
    }

}
