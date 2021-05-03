/**
 * Class to handle computations.
 * User inputs will be taken from
 * Front end, results will be computed and
 * sent back to the front end. The RealEstateFrontEnd class
 * will then handle the displaying of the results and this class
 * will handle all computations ("Business-Logic").
 * @author Stefano Parravano
 */

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Random;

public class BackEndCalculations {
    private double projectedResalePrice;
    private double loanAmount;
    private double loanInterestRate;
    private double loanDuration;
    private double numberLoanPaymentsPerYear;
    private double monthlyRealEstateTaxes;
    private double monthlyInsuranceCosts;
    private double otherMonthlyExpenses;
    private double resalePriceMean;
    private double resalePriceSTD;
    private double numberSimulations;
    private double initialInvestmentAmount;
    private int totalNumberOfPayments;
    private double [] cumulativeTaxPayments;
    private double [] cumulativeInsurancePayments;
    private double [] cumulativeOtherPayments;
    private double [] loanBalances;
    //Simulation members
    private double simulatedMeanResalePrice;
    private double [] simulatedMeanNetProfits; //array encoding the net Profit means --> one value for each time period
    private String [] simulatedConfidenceIntervalNetProfit; //array encoding the 95th CI for Simulated NetProfit Values
    private String [] simulatedProbabilityOfSuccess; //array encoding the probability of success for each time period
    private double [] cumulativeCapitalInvested; // this includes cumulative financing + cumu insurance
    // plus cum taxes plus cum Other + down payment amount + Capex
    private double [] netSale;
    private double [] netProfit;
    private static final double sentinelValue = -1.0; // we assign this value in the userInputs array when an input is invalid
    private  static final int MAX_INTEREST_RATE = 50;
    private  static final int MAX_LOAN_DURATION = 50;
    private  static final int MIN_LOAN_DURATION = 5;
    private  static final int MAX_NUMB_LOAN_PAYMENTS = 50;
    private  static final int MIN_NUMB_LOAN_PAYMENTS = 12;
    private  static final int MIN_NUMBER_SIMULATIONS = 100;
    private  static final int MAX_NUMBER_SIMULATIONS = 35_000;
    private String inputErrors = "";
    private double userInputs [] = new double[15];
    private String userInputsNames [] = {"Purchase Price","Projected Resale Price ($)",
            "Down Payment %","Other Costs At Closing ($)",
            "Projected Capital Expenditure ($) (PCE)","PCE Down Payment %",
            "Loan Interest Rate %","Loan Duration","# of Loan Payments per Year",
            "Monthly Real Estate Taxes ($)","Monthly Insurance Costs ($)",
            "Other Monthly Expenses ($)","Resale Price Mean ($)",
            "Resale Price Standard Deviation ($)","Number of Simulations"};


    /**
     * Method to receive text inputs as passed
     * by user in Front end, check for validity and
     * set inputs to a dats structure (the this.userInputs array)
     * we can use for analysis
     */
    public void feedStringUserInputs( String [] stringUserInputs){
        inputErrors = ""; // we reset this to original state every time it is called
        int numberOfInputs = stringUserInputs.length;
        for  (int i=0; i<=numberOfInputs-1; i++){
            checkAndSetUserInputs(stringUserInputs[i],i);
        }
    }

    /**
     * This method will set All private members to the
     * values passed by the users in the front end. This
     * will occur only once the inputs have been fully
     * validated
     */
    public void setAllInputs(){
        double purchasePrice = this.userInputs[0];
        this.projectedResalePrice = this.userInputs[1];
        double downPaymentPercent = this.userInputs[2];
        double downPaymentAmount = downPaymentPercent/100 * purchasePrice;
        double otherClosingCosts = this.userInputs[3];
        double projectedCapitalExpenditure = this.userInputs[4];
        double projectedCapitalExpenditureDownPaymentPercentage = this.userInputs[5];
        this.loanInterestRate = this.userInputs[6];
        this.loanDuration = this.userInputs[7];
        this.numberLoanPaymentsPerYear = this.userInputs[8];
        this.monthlyRealEstateTaxes = this.userInputs[9];
        this.monthlyInsuranceCosts = this.userInputs[10];
        this.otherMonthlyExpenses = this.userInputs[11];
        this.resalePriceMean = this.userInputs[12];
        this.resalePriceSTD = this.userInputs[13];
        this.numberSimulations = this.userInputs[14];
        this.totalNumberOfPayments =(int)(this.numberLoanPaymentsPerYear * this.loanDuration);
        this.cumulativeTaxPayments = new double [this.totalNumberOfPayments];
        this.cumulativeOtherPayments = new double [this.totalNumberOfPayments];
        this.cumulativeInsurancePayments = new double [this.totalNumberOfPayments];
        this.cumulativeCapitalInvested = new double [this.totalNumberOfPayments];
        this.netSale = new double [this.totalNumberOfPayments];
        this.netProfit = new double [this.totalNumberOfPayments];

        double projectedCapitalExpenditureDownPaymentAmount = projectedCapitalExpenditureDownPaymentPercentage/100
                * projectedCapitalExpenditure;

        this.loanAmount = (purchasePrice + projectedCapitalExpenditure)
                - (downPaymentAmount + projectedCapitalExpenditureDownPaymentAmount);
        this.initialInvestmentAmount = downPaymentAmount
                + otherClosingCosts
                + projectedCapitalExpenditureDownPaymentAmount;
    }

    /**
     * Driver Method that computes the final Results of the Entire Analysis.
     * The netProfit values for each time period are computed here.
     */
    public void performAnalysis(){
        computeCumulativeExpenses();
        computeCumulativeCapitalInvested();
        computeNetSaleAndNetProfit();
        performSimulation();
    }

    /**
     * This method will compute cumulative Taxes,Insurance and Other
     * expenses for each time period adn store the results to the
     * appropriate private members: cumulativeTaxPayments,cumulativeOtherPayments
     * cumulativeInsurancePayments.
     */
    private void computeCumulativeExpenses(){
        for (int i = 1; i<= this.totalNumberOfPayments; i++){
            this.cumulativeTaxPayments[i-1] = this.monthlyRealEstateTaxes * i;
            this.cumulativeOtherPayments[i-1] = this.otherMonthlyExpenses * i;
            this.cumulativeInsurancePayments[i-1] = this.monthlyInsuranceCosts * i;
        }

    }

    /**
     * This method will compute the cumulative Capital Invested.
     * The amortization schedule will also get computed and expenses from the
     * computeCumulativeExpenses method will be added to the total out
     * of pocket cumulative laon payments (principal + interest). The results
     * will be stored to the appropriate private members: cumulativeTotalPayments
     * cumulativeCapitalInvested
     *
     */

    private void computeCumulativeCapitalInvested(){
        Amortization amortizationCalculator = new Amortization(this.loanAmount,this.loanDuration,
                this.numberLoanPaymentsPerYear,this.loanInterestRate);
        amortizationCalculator.calculateAmortizationSchedule();
        this.loanBalances = amortizationCalculator.getLoanBalances();
        double [] cumulativeTotalPayments = amortizationCalculator.getCumulativeTotalPayments();

        for (int i=0; i<this.totalNumberOfPayments; i++){
            this.cumulativeCapitalInvested[i] = this.initialInvestmentAmount
                    + cumulativeTotalPayments[i]
                    + this.cumulativeTaxPayments[i]
                    + this.cumulativeInsurancePayments[i]
                    + this.cumulativeOtherPayments[i];
        }

    }

    /**
     * This helper method will compute the netSale and netProfit
     * values for each Period and store them to the appropriate private
     * members: netSale and netProfit.
     */

    private void computeNetSaleAndNetProfit(){
        for (int i =0; i<this.totalNumberOfPayments; i++){
            this.netSale[i] = this.projectedResalePrice - this.loanBalances[i];
            this.netProfit[i] = this.netSale[i] - this.cumulativeCapitalInvested[i];
        }
    }

    /**
     * This method will run our simulation and compute statistics of
     * interest and store them to the appropriate private members
     * (simulatedMeanNetProfits,simulatedConfidenceIntervalNetProfit,simulatedProbabilityOfSuccess).
     * A distribution of resale values will be computed based on the parameters
     * set by the front end user. This distribution will be used to create
     * a distribution of possible outcomes for each time period. The distribution
     * of outcomes will be used to show a 95% CI of possible future states
     * as well as the probability of success (defined as net profit >0) for
     * each future time period.
     */
    private void performSimulation(){
        double [] simulatedResalePrices = new double [(int)this.numberSimulations];
        Random randomGenerator = new Random();
        double randomValue;
        for (int i=0; i<this.numberSimulations; i++){
            randomValue = randomGenerator.nextGaussian()
                    * this.resalePriceSTD + this.resalePriceMean;
            simulatedResalePrices[i] = randomValue;
        }
        double [] [] netProfitSimulationResults = new double [this.totalNumberOfPayments][(int)this.numberSimulations];
        double newNetSale; // new netSale value as a function of the simulated resale Price
        double newNetProfit; // new netProfit as a function of the simulated resale Price

        for (int j=0; j<this.totalNumberOfPayments; j++){
            for (int k=0; k<this.numberSimulations; k++){
                newNetSale = simulatedResalePrices[k] - this.loanBalances[j];
                newNetProfit = newNetSale - this.cumulativeCapitalInvested[j];
                netProfitSimulationResults[j][k]=newNetProfit;
            }

        }
        this.simulatedMeanResalePrice = MathHelper.computeMean(simulatedResalePrices);
        this.simulatedMeanNetProfits = new double [this.totalNumberOfPayments];
        this.simulatedConfidenceIntervalNetProfit = new String [this.totalNumberOfPayments];
        this.simulatedProbabilityOfSuccess = new String [this.totalNumberOfPayments];

        for (int x=0; x<this.totalNumberOfPayments; x++){
            double [] tempArray = netProfitSimulationResults[x];
            this.simulatedMeanNetProfits[x] = MathHelper.computeMean(tempArray);
            this.simulatedConfidenceIntervalNetProfit[x] = MathHelper.compute95CI(tempArray);
            this.simulatedProbabilityOfSuccess[x] = MathHelper.computeProbabilityProjectSuccess(tempArray);
        }

    }



    /**
     * Method to check a user provided input. Each index corresponds to a
     * pre-defined input. Each input has it's own logic that we must
     * check. This method will check and set the values in the
     * userInput instance variable
     * @param input passed from user
     * @param index location of input in userInput array
     */

    private void checkAndSetUserInputs(String input, int index){
        input = input.replaceAll(",","");
        boolean passNumericTest = false;
        double value;
        try{
            value = Double.parseDouble(input);
            if (value>=0){
                passNumericTest = true;
                this.userInputs[index] = value;
            } else {
                this.inputErrors+="Error with input: " + this.userInputsNames[index]
                        + ", Cause of Error: Negative Numeric Value Provided\n";
                this.userInputs[index] = this.sentinelValue;
            }
        } catch (Exception e){
            this.inputErrors += "Error with input: " + this.userInputsNames[index]
            + ", Cause of Error: Non-Numeric Input Provided\n";
            this.userInputs[index] = this.sentinelValue;
        }

        if (passNumericTest){
            // now we check for conditions specific to each input
            if (index==1){
                if (this.userInputs[0]==this.sentinelValue){
                    // checking Projected Resale Price input
                    // Purchase Price is not valid. We can't determine
                    // if Projected Resale Price is Valid. Analysis
                    // only makes sense if Resale Price is > purchase Price
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[0]
                            + " is invalid\n";
                } else {
                    if (this.userInputs[index] <= this.userInputs[index-1]){
                        this.inputErrors += this.userInputsNames[index]
                                +" should be > than: " + this.userInputsNames[index-1] + "\n";
                    }
                }
            } else if (index ==2){
                // checking Down Payment Percentage input
                if (this.userInputs[index]>100){
                    this.inputErrors += this.userInputsNames[index]
                            +" should be <=100%\n";
                }

            } else if(index==3){
                // checking Other costs at closing input. Closing costs plus purchase price
                // should be less than projected resale price
                if (this.userInputs[0]==this.sentinelValue) {
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[0]
                            + " is invalid\n";
                } else if (this.userInputs[1]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[1]
                            + " is invalid\n";
                } else {
                    if (this.userInputs[index] + this.userInputs[0] > this.userInputs[1]){
                        this.inputErrors += this.userInputsNames[index] + " plus "
                                + this.userInputsNames[0] + " should be < than: "
                                + this.userInputsNames[1]+"\n";
                    }
                }

            } else if (index==4){
                // checking PCE input
                // PCE + Purchase Price should be < resale price
                if (this.userInputs[0]==this.sentinelValue) {
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[0]
                            + " is invalid\n";
                } else if (this.userInputs[1]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[1]
                            + " is invalid\n";
                } else {
                    if (this.userInputs[index] + this.userInputs[0] + this.userInputs[3] > this.userInputs[1]){
                        this.inputErrors += this.userInputsNames[index] + " plus "
                                + this.userInputsNames[0] + " plus " + this.userInputsNames[3]  + " should be < than: "
                                + this.userInputsNames[1]+"\n";
                    }
                }

            } else if(index==5){
                //checking PCE Down Payment %
                if (this.userInputs[0]==this.sentinelValue) {
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[0]
                            + " is invalid\n";
                } else if (this.userInputs[1]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[1]
                            + " is invalid\n";
                } else if (this.userInputs[index]>100){
                    this.inputErrors += this.userInputsNames[index]
                            +" should be <=100%\n";
                }

            } else if (index ==6){
                // checking loan interest rate
                if (this.userInputs[index] > MAX_INTEREST_RATE){
                    this.inputErrors += this.userInputsNames[index]
                            + " is unreasonably high. Please select a reasonable interest rate: "
                            + "< than " + MAX_INTEREST_RATE + "\n";
                }
            } else if (index == 7){
                if (this.userInputs[index] > MAX_LOAN_DURATION || this.userInputs[index] < MIN_LOAN_DURATION){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be > " + MIN_LOAN_DURATION
                            + " and < " + MAX_LOAN_DURATION + "\n";
                } else if (this.userInputs[index] % 1 !=0){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be an integer value. We only consider loans"
                            + " issued in full year increments\n";
                }
            } else if (index==8){
                if (this.userInputs[index]>MAX_NUMB_LOAN_PAYMENTS || this.userInputs[index]<MIN_NUMB_LOAN_PAYMENTS){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be > " + MIN_NUMB_LOAN_PAYMENTS
                            + " and < " + MAX_NUMB_LOAN_PAYMENTS + "\n";
                } else if (this.userInputs[index] % 1 !=0){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be an integer value. We only consider payment"
                            + " frequencies that increment by whole numbers\n";
                }
            } else if (index==9 || index ==10 || index ==11){
                if (this.userInputs[0]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[0]
                            + " is invalid\n";
                } else if (this.userInputs[1]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[1]
                            + " is invalid\n";
                } else {
                    if (this.userInputs[index]*24+userInputs[0]>this.userInputs[1]){
                        this.inputErrors += this.userInputsNames[index]
                                + " is unreasonably high given the projected Resale Price."
                                + " Please input different assumptions for a meaningful analysis\n";
                    }
                }
            } else if (index==13){
                if (this.userInputs[0]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[0]
                            + " is invalid\n";
                } else if (this.userInputs[1]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[1]
                            + " is invalid\n";
                } else if (this.userInputs[10]==this.sentinelValue) {
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[1]
                            + " is invalid\n";

                } else {
                    double maxVal = this.userInputs[12] + this.userInputs[index]*3;
                    if (maxVal<this.userInputs[0]) {
                        this.inputErrors += this.userInputsNames[index]
                                + "and/or " + this.userInputsNames[10]
                                + " is/are unreasonably high given the projected Resale Price."
                                + " Please input different assumptions for a meaningful analysis\n";
                    }
                }
            } else if (index==14){
                if (this.userInputs[index]>MAX_NUMBER_SIMULATIONS || this.userInputs[index]<MIN_NUMBER_SIMULATIONS){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be > " + MIN_NUMBER_SIMULATIONS + " and"
                            + " < than " + MAX_NUMBER_SIMULATIONS +"\n";
                } else if (this.userInputs[index] % 1 !=0){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be an integer value.\n";
                }
            }
        }

    }

    /**
     * Getter for the names of the user inputs
     * @return String UserInput names
     */
    public String [] getUserInputNames() {
        return userInputsNames;
    }

    /**
     * Getter for input Errors
     * @return String UserInput errors
     */
    public String getInputErrors() {
        return inputErrors;
    }

    /**
     * Getter for netSale per period array
     */
    public double [] getNetSale (){
        return this.netSale;
    }

    /**
     * Getter for netProfit per period array
     * @return double [] array of net profit results for
     *                  each period
     */
    public double [] getNetProfit(){
        return this.netProfit;
    }

    /**
     * Getter for loanBalances per period array
     * @return double [] array of loan balances for each period
     */
    public double [] getLoanBalances (){
        return this.loanBalances;
    }

    /**
     * Getter for cumulativeCapitalInvested per period array
     * @ return double [] array of cumulative capital invested
     *                  for each period
     */
    public double [] getCumulativeCapitalInvested (){
        return this.cumulativeCapitalInvested;
    }

    /**
     * Getter for totalNumberOfPayments
     * @return total payments over course of financing period
     */
    public int getTotalNumberOfPayments(){
        return this.totalNumberOfPayments;
    }

    /**
     * Getter for projectedResalePrice
     * @return projected resale price
     */
    public double getProjectedResalePrice(){
        return this.projectedResalePrice;
    }

    /**
     * Getter for simulatedMeanResalePrice
     * @return mean resale price from simulated distribution
     */
    public double getSimulatedMeanResalePrice(){
        return this.simulatedMeanResalePrice;
    }

    /**
     * Getter for simulatedMeanNetProfits
     * @return mean net profits from simulation
     */
    public double [] getSimulatedMeanNetProfits(){
        return this.simulatedMeanNetProfits;
    }

    /**
     * Getter for simulatedConfidenceIntervalNetProfit
     * @ return formatted string encoding 95% CI of net profit for each
     *                     period from simulation
     */
    public String [] getSimulatedConfidenceIntervalNetProfit(){
        return this.simulatedConfidenceIntervalNetProfit;
    }

    /**
     * Getter for simulatedProbabilityOfSuccess
     * @return formatted string encoding probability of
     *          success (net profit >0) for each time period
     */
    public String [] getSimulatedProbabilityOfSuccess(){
        return this.simulatedProbabilityOfSuccess;
    }

}
