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

public class InputView extends JFrame {
    //JTextField Background Color
    public Color backgroundColor = new Color(174,224,255);
    public Color lightBackgroundColor = new Color(211,211,211);
    public Font font = new Font("TimesRoman", Font.BOLD,20);
    // Section Level TextFields
    public JLabel generalInputs = new JLabel("General Inputs");
    public JLabel financingInputs = new JLabel("Financing Inputs");
    public JLabel simulationInputs = new JLabel("Simulation Inputs");

    //general info
    public JLabel projectName = new JLabel("Project Name");
    public JTextField projectNameInput = new JTextField("",15);
    public JLabel purchasePrice = new JLabel("Purchase Price ($)");
    public JTextField purchasePriceInput = new JTextField("",15);
    public JLabel resalePrice = new JLabel("Projected Resale Price ($)");
    public JTextField resalePriceInput = new JTextField("",15);
    public JLabel downPaymentPercentage = new JLabel("Down Payment %");
    public JTextField downPaymentPercentageInput = new JTextField();
    public JLabel downPaymentAmount = new JLabel("Down Payment Amount ($)"); // set as non editable. will be calculated
    public JTextField downPaymentAmountInput = new JTextField();
    public JLabel projectedCapex = new JLabel("Projected Capital Expenditure (PCE)");
    public JTextField projectedCapexInput = new JTextField();
    public JLabel projectedCapexDownPayment = new JLabel("PCE Down Payment %");
    public JTextField projectedCapexDownPaymentInput = new JTextField();
    // financing information
    public JLabel loanAmount = new JLabel("Loan Amount Required ($)"); //set as non editable will be calculated by taking (purchased price + capex) - down payment
    public JTextField loanAmountInput = new JTextField();
    public JLabel loanRate = new JLabel("Loan Interest Rate %");
    public JTextField loanRateInput = new JTextField();
    public JLabel loanDuration = new JLabel("Loan Duration in Years");
    public JTextField loanDurationInput = new JTextField();
    public JLabel numbLoanPaymentsPerYear = new JLabel("# of Loan Payments per Year");
    public JTextField numbLoanPaymentsPerYearInput = new JTextField();
    // other expenses information
    public JLabel monthlyTaxes = new JLabel("Monthly Real-Estate Taxes");
    public JTextField monthlyTaxesInput = new JTextField();
    public JLabel monthlyInsuranceCosts = new JLabel("Monthly Insurance Costs");
    public JTextField monthlyInsuranceCostsInput = new JTextField();
    public JLabel monthlyOtherExpenses = new JLabel("Other Monthly Expenses");
    public JTextField monthlyOtherExpensesInput = new JTextField();

    // Simulation Inputs
    public JLabel resalePriceMean = new JLabel("Resale Price Mean");
    public JTextField resalePriceMeanInput = new JTextField("",15);
    public JLabel resalePriceSTD = new JLabel("Resale Price Standard Deviation");
    public JTextField resalePriceSTDInput = new JTextField("",15);
    public JLabel numberSimulations = new JLabel("Number of Simulations");
    public JTextField numberSimulationsInput = new JTextField("",15);
    public JLabel assumedProbabilityDistribution = new JLabel("Assumed Probability Distribution");
    public JTextField assumedProbabilityDistributionInput = new JTextField("Gaussian",15);

    //submit Button
    public JButton runAnalysis = new JButton("Run Analysis!");
    public JButton clearInputs = new JButton("Clear Inputs");
    public JButton loadSampleInputs = new JButton("Load Sample Inputs");
    //private double userInputs [] = new double[14];
    private String stringUserInputs [] = new String [14];
    private BackEndCalculations backEndCalculations = new BackEndCalculations();

    //Results View SetUp
    private DefaultTableModel model = new DefaultTableModel();
    private JTable results = new JTable(model);
    private JScrollPane scrollPane = new JScrollPane(results);
    private JLabel resultsMessage = new JLabel("Results will be visible once an analysis is Executed!");

    //Simulation Results View SetUp
    private DefaultTableModel modelSimulation = new DefaultTableModel();
    private JTable simulationResults = new JTable(modelSimulation);
    private JScrollPane scrollPaneSimulation = new JScrollPane(simulationResults);
    private JLabel simulationResultsMessage = new JLabel("Results will be visible once an analysis is Executed!");




    /**
     * No arg constructor
     */
    public InputView(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JTabbedPane tabs = new JTabbedPane();
        //JPanel simulationResultsPanel = new JPanel(new GridBagLayout());
        //JPanel resultsPanel = new JPanel(new GridBagLayout());
        JPanel resultsPanel = new JPanel();
        resultsPanel.setLayout(new BorderLayout());

        JPanel simulationResultsPanel = new JPanel();
        simulationResultsPanel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;

        c.insets = new Insets(0,10,0,10);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
        generalInputs.setFont(font);
        inputPanel.add(generalInputs,c);


        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        inputPanel.add(projectName,c);
        c.gridx = 2; c.gridy = 1; c.gridwidth = 1;
        projectNameInput.setBackground(backgroundColor);
        inputPanel.add(projectNameInput,c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        inputPanel.add(purchasePrice,c);
        c.gridx = 2; c.gridy = 2; c.gridwidth = 1;
        purchasePriceInput.setBackground(backgroundColor);
        inputPanel.add(purchasePriceInput,c);
        c.gridx = 3; c.gridy = 2; c.gridwidth = 2;
        inputPanel.add(resalePrice,c);
        c.gridx = 5; c.gridy = 2; c.gridwidth = 1;
        resalePriceInput.setBackground(backgroundColor);
        inputPanel.add(resalePriceInput,c);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        inputPanel.add(downPaymentPercentage,c);
        c.gridx = 2; c.gridy = 3; c.gridwidth = 1;
        downPaymentPercentageInput.setBackground(backgroundColor);
        inputPanel.add(downPaymentPercentageInput,c);
        c.gridx = 3; c.gridy = 3; c.gridwidth = 2;
        inputPanel.add(downPaymentAmount,c);
        c.gridx = 5; c.gridy = 3; c.gridwidth = 1;
        downPaymentAmountInput.setBackground(lightBackgroundColor);
        downPaymentAmountInput.setEditable(false);
        inputPanel.add(downPaymentAmountInput,c);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 2;
        inputPanel.add(projectedCapex,c);
        c.gridx = 2; c.gridy = 4; c.gridwidth = 1;
        projectedCapexInput.setBackground(backgroundColor);
        inputPanel.add(projectedCapexInput,c);
        c.gridx = 3; c.gridy = 4; c.gridwidth = 2;
        inputPanel.add(projectedCapexDownPayment,c);
        c.gridx = 5; c.gridy = 4; c.gridwidth = 1;
        projectedCapexDownPaymentInput.setBackground(backgroundColor);
        inputPanel.add(projectedCapexDownPaymentInput,c);

        c.gridx = 0; c.gridy = 6; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        financingInputs.setFont(font);
        inputPanel.add(financingInputs,c);
        c.insets = new Insets(0,10,0,10);
        c.weightx=0;

        c.gridx = 0; c.gridy = 7; c.gridwidth = 2;
        inputPanel.add(loanAmount,c);
        c.gridx = 2; c.gridy = 7; c.gridwidth = 1;
        loanAmountInput.setEditable(false);
        loanAmountInput.setBackground(lightBackgroundColor);
        inputPanel.add(loanAmountInput,c);
        c.gridx = 3; c.gridy = 7; c.gridwidth = 2;
        inputPanel.add(loanRate,c);
        c.gridx = 5; c.gridy = 7; c.gridwidth = 1;
        loanRateInput.setBackground(backgroundColor);
        inputPanel.add(loanRateInput,c);

        c.gridx = 0; c.gridy = 8; c.gridwidth = 2;
        inputPanel.add(loanDuration,c);
        c.gridx = 2; c.gridy = 8; c.gridwidth = 1;
        loanDurationInput.setBackground(backgroundColor);
        inputPanel.add(loanDurationInput,c);
        c.gridx = 3; c.gridy = 8; c.gridwidth = 2;
        inputPanel.add(numbLoanPaymentsPerYear,c);
        c.gridx = 5; c.gridy = 8; c.gridwidth = 1;
        numbLoanPaymentsPerYearInput.setBackground(backgroundColor);
        inputPanel.add(numbLoanPaymentsPerYearInput,c);

        c.gridx = 0; c.gridy = 9; c.gridwidth = 2;
        inputPanel.add(monthlyTaxes,c);
        c.gridx = 2; c.gridy = 9; c.gridwidth = 1;
        monthlyTaxesInput.setBackground(backgroundColor);
        inputPanel.add(monthlyTaxesInput,c);
        c.gridx = 3; c.gridy = 9; c.gridwidth = 2;
        inputPanel.add(monthlyInsuranceCosts,c);
        c.gridx = 5; c.gridy = 9; c.gridwidth = 1;
        monthlyInsuranceCostsInput.setBackground(backgroundColor);
        inputPanel.add(monthlyInsuranceCostsInput,c);

        c.gridx = 0; c.gridy = 10; c.gridwidth = 2;
        inputPanel.add(monthlyOtherExpenses,c);
        c.gridx = 2; c.gridy = 10; c.gridwidth = 1;
        monthlyOtherExpensesInput.setBackground(backgroundColor);
        inputPanel.add(monthlyOtherExpensesInput,c);

        c.gridx = 0; c.gridy = 11; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        simulationInputs.setFont(font);
        inputPanel.add(simulationInputs,c);
        c.insets = new Insets(0,10,0,10);

        c.gridx = 0; c.gridy = 12; c.gridwidth = 2;
        inputPanel.add(resalePriceMean,c);
        c.gridx = 2; c.gridy = 12; c.gridwidth = 1;
        resalePriceMeanInput.setBackground(backgroundColor);
        inputPanel.add(resalePriceMeanInput,c);
        c.gridx = 3; c.gridy = 12; c.gridwidth = 2;
        inputPanel.add(resalePriceSTD,c);
        c.gridx = 5; c.gridy = 12; c.gridwidth = 1;
        resalePriceSTDInput.setBackground(backgroundColor);
        inputPanel.add(resalePriceSTDInput,c);

        c.gridx = 0; c.gridy = 13; c.gridwidth = 2;
        inputPanel.add(numberSimulations,c);
        c.gridx = 2; c.gridy = 13; c.gridwidth = 1;
        numberSimulationsInput.setBackground(backgroundColor);
        inputPanel.add(numberSimulationsInput,c);
        c.gridx = 3; c.gridy = 13; c.gridwidth = 2;
        inputPanel.add(assumedProbabilityDistribution,c);
        c.gridx = 5; c.gridy = 13; c.gridwidth = 1;
        assumedProbabilityDistributionInput.setBackground(lightBackgroundColor);
        inputPanel.add(assumedProbabilityDistributionInput,c);

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

        // set up the Results view
        model.addColumn("Time Period"); model.addColumn("Resale Price");
        model.addColumn("Financing Balance"); model.addColumn("Cumulative Capital Invested");
        model.addColumn("Net Sale"); model.addColumn("Net Profit");
        results.getColumnModel().getColumn(1).setPreferredWidth(100);
        results.getColumnModel().getColumn(3).setPreferredWidth(140);
        resultsPanel.add(resultsMessage,BorderLayout.PAGE_START);
        resultsPanel.add(scrollPane,BorderLayout.CENTER);

        // set up the simulationResults view
        modelSimulation.addColumn("Time Period"); modelSimulation.addColumn("Mean Resale Price");
        modelSimulation.addColumn("Mean Net Profit"); modelSimulation.addColumn("5th Percentile Net Profit");
        modelSimulation.addColumn("95th Percentile Net Profit");
        simulationResultsPanel.add(simulationResultsMessage,BorderLayout.PAGE_START);
        simulationResultsPanel.add(scrollPaneSimulation,BorderLayout.CENTER);

        tabs.add("Inputs",inputPanel);
        tabs.add("Results",resultsPanel);
        tabs.add("Simulation Results",simulationResultsPanel);
        this.add(tabs);
        this.setVisible (true);
    }

    class InputListener implements ActionListener{
        public void actionPerformed (ActionEvent e){
            if (e.getSource()==runAnalysis){
                System.out.println("Run Analysis was pressed");
                setStringUserInputs();
                backEndCalculations.feedStringUserInputs(getStringUserInputs());
                String inputErrors = backEndCalculations.getInputErrors();
                if (inputErrors.length()==0){
                    // we may proceed to processing
                    System.out.println("NO INPUT ERRORS!");
                    backEndCalculations.setAllInputs();
                    backEndCalculations.performAnalysis();
                    addDataToResultsTable();
                    resultsMessage.setText("Analysis COMPLETE!");
                    // TO DO: PERFORM SIMULATION ANALUSIS
                    // ADD RESULTS TO SIMUALTION RESULTS TABLE

                } else {
                    System.out.println("we have errors: " + inputErrors);
                    // pop up showing a detailed description of erros that
                    // need to be corrected
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
     * and outputResults
     */
    private void clearUserInputsAndOutput(){
        purchasePriceInput.setText("");resalePriceInput.setText("");
        downPaymentPercentageInput.setText("");projectedCapexInput.setText("");
        loanRateInput.setText("");loanDurationInput.setText("");
        numbLoanPaymentsPerYearInput.setText("");monthlyTaxesInput.setText("");
        monthlyInsuranceCostsInput.setText("");resalePriceMeanInput.setText("");
        resalePriceSTDInput.setText(""); monthlyOtherExpensesInput.setText("");
        numberSimulationsInput.setText("");projectedCapexDownPaymentInput.setText("");

        // we want to delete results from previous runs first
        int numberOfRows = model.getRowCount();
        if (numberOfRows >0){
            for (int k=numberOfRows-1; k>=0; k--) model.removeRow(k);
        }

        int numberOfRowsSimulation = modelSimulation.getRowCount();
        if (numberOfRowsSimulation >0){
            for (int z=numberOfRowsSimulation-1; z>=0; z--) modelSimulation.removeRow(z);
        }
        resultsMessage.setText("Results will be visible once an analysis is Executed!");
        simulationResultsMessage.setText("Results will be visible once an analysis is Executed!");
    }

    /**
     * Helper method to load sample inputs
     */
    private void loadSampleInputs(){
        clearUserInputsAndOutput();
        purchasePriceInput.setText("480,000");resalePriceInput.setText("610,000");
        downPaymentPercentageInput.setText("5");projectedCapexInput.setText("65,000");
        loanRateInput.setText("5.181");loanDurationInput.setText("30");
        numbLoanPaymentsPerYearInput.setText("12");monthlyTaxesInput.setText("200");
        monthlyInsuranceCostsInput.setText("150");resalePriceMeanInput.setText("610,000");
        resalePriceSTDInput.setText("2,300"); monthlyOtherExpensesInput.setText("140");
        numberSimulationsInput.setText("50,000");projectedCapexDownPaymentInput.setText("50");
    }

    /**
     * Helper method to collect all userInputs into
     * an array
     *
     */
    private void setStringUserInputs(){
        this.stringUserInputs[0] = purchasePriceInput.getText();
        this.stringUserInputs[1] = resalePriceInput.getText();
        this.stringUserInputs[2] = downPaymentPercentageInput.getText();
        this.stringUserInputs[3] = projectedCapexInput.getText();
        this.stringUserInputs[4] = loanRateInput.getText();
        this.stringUserInputs[5] = loanDurationInput.getText();
        this.stringUserInputs[6] = numbLoanPaymentsPerYearInput.getText();
        this.stringUserInputs[7] = monthlyTaxesInput.getText();
        this.stringUserInputs[8] = monthlyInsuranceCostsInput.getText();
        this.stringUserInputs[9] = monthlyOtherExpensesInput.getText();
        this.stringUserInputs[10] = resalePriceMeanInput.getText();
        this.stringUserInputs[11] = resalePriceSTDInput.getText();
        this.stringUserInputs[12] = numberSimulationsInput.getText();
        this.stringUserInputs[13] = projectedCapexDownPaymentInput.getText();
    }

    /**
     * Helper method to add rows to our Results JTable.
     * It is not necessary to show results for all time periods.
     * Only the time periods where the netProfit is >0 are relevant
     * and as such we only display those to the user
     */
    private void addDataToResultsTable(){
        // first we work out where the negative values start
        int numberOfPeriods = this.backEndCalculations.getTotalNumberOfPayments();
        int stopIndex = numberOfPeriods;
        double [] netProft = this.backEndCalculations.getNetProfit();
        for (int i=0; i< numberOfPeriods;i++){
            if (netProft[i]<0){
                stopIndex = i;
                break;
            }
        }
        System.out.println("STOPINDEX found at:" + stopIndex);

        // we want to delete results from previous runs first
        int numberOfRows = model.getRowCount();
        if (numberOfRows >0){
            for (int k=numberOfRows-1; k>=0; k--) model.removeRow(k);
        }

        double projectedResalePrice = this.backEndCalculations.getProjectedResalePrice();
        double [] financingBalance = this.backEndCalculations.getLoanBalances();
        double [] cumulativeCapex = this.backEndCalculations.getCumulativeCapitalInvested();
        double [] netSale = this.backEndCalculations.getNetSale();
        double [] netProfit = this.backEndCalculations.getNetProfit();

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

    public String[] getStringUserInputs() {
        return stringUserInputs;
    }

}
