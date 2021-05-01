import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that defines the "input view" UI.
 * Users will interact with this interface to
 * provide our program with the desired inputs
 */

public class InputView extends JPanel {
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
    private double userInputs [] = new double[14];
    private String stringUserInputs [] = new String [14];
    private BackEndCalculations backEndCalculations = new BackEndCalculations();


    /**
     * No arg constructor
     */
    public InputView(){
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        c.fill = GridBagConstraints.HORIZONTAL;

        c.insets = new Insets(0,10,0,10);
        c.gridx = 0; c.gridy = 0; c.gridwidth = 1;
        generalInputs.setFont(font);
        this.add(generalInputs,c);


        c.gridx = 0; c.gridy = 1; c.gridwidth = 2;
        this.add(projectName,c);
        c.gridx = 2; c.gridy = 1; c.gridwidth = 1;
        projectNameInput.setBackground(backgroundColor);
        this.add(projectNameInput,c);

        c.gridx = 0; c.gridy = 2; c.gridwidth = 2;
        this.add(purchasePrice,c);
        c.gridx = 2; c.gridy = 2; c.gridwidth = 1;
        purchasePriceInput.setBackground(backgroundColor);
        this.add(purchasePriceInput,c);
        c.gridx = 3; c.gridy = 2; c.gridwidth = 2;
        this.add(resalePrice,c);
        c.gridx = 5; c.gridy = 2; c.gridwidth = 1;
        resalePriceInput.setBackground(backgroundColor);
        this.add(resalePriceInput,c);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2;
        this.add(downPaymentPercentage,c);
        c.gridx = 2; c.gridy = 3; c.gridwidth = 1;
        downPaymentPercentageInput.setBackground(backgroundColor);
        this.add(downPaymentPercentageInput,c);
        c.gridx = 3; c.gridy = 3; c.gridwidth = 2;
        this.add(downPaymentAmount,c);
        c.gridx = 5; c.gridy = 3; c.gridwidth = 1;
        downPaymentAmountInput.setBackground(lightBackgroundColor);
        downPaymentAmountInput.setEditable(false);
        this.add(downPaymentAmountInput,c);

        c.gridx = 0; c.gridy = 4; c.gridwidth = 2;
        this.add(projectedCapex,c);
        c.gridx = 2; c.gridy = 4; c.gridwidth = 1;
        projectedCapexInput.setBackground(backgroundColor);
        this.add(projectedCapexInput,c);
        c.gridx = 3; c.gridy = 4; c.gridwidth = 2;
        this.add(projectedCapexDownPayment,c);
        c.gridx = 5; c.gridy = 4; c.gridwidth = 1;
        projectedCapexDownPaymentInput.setBackground(backgroundColor);
        this.add(projectedCapexDownPaymentInput,c);

        c.gridx = 0; c.gridy = 6; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        financingInputs.setFont(font);
        this.add(financingInputs,c);
        c.insets = new Insets(0,10,0,10);
        c.weightx=0;

        c.gridx = 0; c.gridy = 7; c.gridwidth = 2;
        this.add(loanAmount,c);
        c.gridx = 2; c.gridy = 7; c.gridwidth = 1;
        loanAmountInput.setEditable(false);
        loanAmountInput.setBackground(lightBackgroundColor);
        this.add(loanAmountInput,c);
        c.gridx = 3; c.gridy = 7; c.gridwidth = 2;
        this.add(loanRate,c);
        c.gridx = 5; c.gridy = 7; c.gridwidth = 1;
        loanRateInput.setBackground(backgroundColor);
        this.add(loanRateInput,c);

        c.gridx = 0; c.gridy = 8; c.gridwidth = 2;
        this.add(loanDuration,c);
        c.gridx = 2; c.gridy = 8; c.gridwidth = 1;
        loanDurationInput.setBackground(backgroundColor);
        this.add(loanDurationInput,c);
        c.gridx = 3; c.gridy = 8; c.gridwidth = 2;
        this.add(numbLoanPaymentsPerYear,c);
        c.gridx = 5; c.gridy = 8; c.gridwidth = 1;
        numbLoanPaymentsPerYearInput.setBackground(backgroundColor);
        this.add(numbLoanPaymentsPerYearInput,c);

        c.gridx = 0; c.gridy = 9; c.gridwidth = 2;
        this.add(monthlyTaxes,c);
        c.gridx = 2; c.gridy = 9; c.gridwidth = 1;
        monthlyTaxesInput.setBackground(backgroundColor);
        this.add(monthlyTaxesInput,c);
        c.gridx = 3; c.gridy = 9; c.gridwidth = 2;
        this.add(monthlyInsuranceCosts,c);
        c.gridx = 5; c.gridy = 9; c.gridwidth = 1;
        monthlyInsuranceCostsInput.setBackground(backgroundColor);
        this.add(monthlyInsuranceCostsInput,c);

        c.gridx = 0; c.gridy = 10; c.gridwidth = 2;
        this.add(monthlyOtherExpenses,c);
        c.gridx = 2; c.gridy = 10; c.gridwidth = 1;
        monthlyOtherExpensesInput.setBackground(backgroundColor);
        this.add(monthlyOtherExpensesInput,c);

        c.gridx = 0; c.gridy = 11; c.gridwidth = 1;
        c.insets = new Insets(40,10,0,10);
        simulationInputs.setFont(font);
        this.add(simulationInputs,c);
        c.insets = new Insets(0,10,0,10);

        c.gridx = 0; c.gridy = 12; c.gridwidth = 2;
        this.add(resalePriceMean,c);
        c.gridx = 2; c.gridy = 12; c.gridwidth = 1;
        resalePriceMeanInput.setBackground(backgroundColor);
        this.add(resalePriceMeanInput,c);
        c.gridx = 3; c.gridy = 12; c.gridwidth = 2;
        this.add(resalePriceSTD,c);
        c.gridx = 5; c.gridy = 12; c.gridwidth = 1;
        resalePriceSTDInput.setBackground(backgroundColor);
        this.add(resalePriceSTDInput,c);

        c.gridx = 0; c.gridy = 13; c.gridwidth = 2;
        this.add(numberSimulations,c);
        c.gridx = 2; c.gridy = 13; c.gridwidth = 1;
        numberSimulationsInput.setBackground(backgroundColor);
        this.add(numberSimulationsInput,c);
        c.gridx = 3; c.gridy = 13; c.gridwidth = 2;
        this.add(assumedProbabilityDistribution,c);
        c.gridx = 5; c.gridy = 13; c.gridwidth = 1;
        assumedProbabilityDistributionInput.setBackground(lightBackgroundColor);
        this.add(assumedProbabilityDistributionInput,c);

        c.insets = new Insets(40,10,0,10);
        c.gridx=0;c.gridy=14;c.gridwidth=6;
        c.weightx=1.0;
        this.add(clearInputs,c);
        c.insets = new Insets(0,10,0,10);
        c.gridx=0;c.gridy=15;c.gridwidth=6;
        this.add(loadSampleInputs,c);
        c.gridx=0;c.gridy=16;c.gridwidth=6;
        this.add(runAnalysis,c);

        // add listeners on each button
        InputListener inputListener = new InputListener();
        runAnalysis.addActionListener(inputListener);
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
                } else {
                    System.out.println("we have errors: " + inputErrors);
                    // pop up showing a detailed description of erros that
                    // need to be corrected
                }
                // then add code to perform processing if inputs are good
                // once processing is complete results and simulations results will be stored to private members in this class
                // the RealEstateFrontEnd will then get these results and use them to set values in the Results and Simulation Results UIs
            }
        }
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

    public String[] getStringUserInputs() {
        return stringUserInputs;
    }

    /**
     * Getter method to access the
     * backEndCalculations object which stores all
     * the results of our analysis
     */
    public BackEndCalculations getBackEndCalculations(){
        return this.backEndCalculations;
    }
}
