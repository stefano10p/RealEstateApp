/**
 * Class to handle computations.
 * User inputs will be taken from
 * InputView, results will be computed and
 * sent back to the front end. The InputView Front end
 * will then handle the displaying of the results.
 */

public class BackEndCalculations {
    private double purchasePrice;
    private double projectedResalePrice;
    private double downPaymentPercent;
    private double downPaymentAmount;
    private double projectedCapitalExpenditure;
    private double projectedCapitalExpenditureDownPaymentPercentage;
    private double projectedCapitalExpenditureDownPaymentAmount;
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
    private double [] cumulativeTotalPayments; // cumulativePrincipalPayments + cumulative interestPayments

    private Amortization amortizationCalculator;

    private double [] cumulativeCapitalInvested; // this includes cumulative financing + cumu insurance
    // plus cum taxes plus cum Other + down payment amount + Capex
    private double [] netSale;
    private double [] netProfit;

    private double sentinelValue = -1.0; // we assign this value in the userInputs array when an input is invalid
    private final int MAX_INTEREST_RATE = 50;
    private final int MAX_LOAN_DURATION = 50;
    private final int MIN_LOAN_DURATION = 5;
    private final int MAX_NUMB_LOAN_PAYMENTS = 50;
    private final int MIN_NUMB_LOAN_PAYMENTS = 12;
    private final int MIN_NUMBER_SIMULATIONS = 100;
    private final int MAX_NUMBER_SIMULATIONS = 1_000_000;
    private String inputErrors = "";
    private double userInputs [] = new double[14];
    private String userInputsNames [] = {"Purchase Price","Projected Resale Price",
            "Down Payment","Projected Capital Expenditure",
            "Loan Interest Rate","Loan Duration","# of Loan Payments",
            "Monthly Real Estate Taxes","Monthly Insurance Costs",
            "Other Monthly Expenses","Resale Price Mean",
            "Resale Price Standard Deviation","Number of Simulations","PCE Down Payment %"};


    /**
     * Method to receive text inputs as passed
     * by user in Front end, check for validity and
     * set inputs to a dat structure we can use for analysis
     */
    public void feedStringUserInputs( String [] stringUserInputs){
        inputErrors = ""; // we reset this to original state everytime it is called
        int numberOfInputs = stringUserInputs.length;
        for  (int i=0; i<numberOfInputs; i++){
            System.out.println("Checking input index: " + i + " Value: " + stringUserInputs[i]);
            checkUserInput(stringUserInputs[i],i);
        }
    }

    /**
     * This method will set All private members to the
     * values passed by the users in the front end. This
     * will occure only once the inputs have been fully
     * validated
     */
    public void setAllInputs(){
        this.purchasePrice = this.userInputs[0];
        this.projectedResalePrice = this.userInputs[1];
        this.downPaymentPercent = this.userInputs[2];
        this.downPaymentAmount = this.downPaymentPercent/100 * this.purchasePrice;
        this.projectedCapitalExpenditure = this.userInputs[3];
        this.loanInterestRate = this.userInputs[4];
        this.loanDuration = this.userInputs[5];
        this.numberLoanPaymentsPerYear = this.userInputs[6];
        this.monthlyRealEstateTaxes = this.userInputs[7];
        this.monthlyInsuranceCosts = this.userInputs[8];
        this.otherMonthlyExpenses = this.userInputs[9];
        this.resalePriceMean = this.userInputs[10];
        this.resalePriceSTD = this.userInputs[11];
        this.numberSimulations = this.userInputs[12];
        this.projectedCapitalExpenditureDownPaymentPercentage = this.userInputs[13];
        this.totalNumberOfPayments =(int)(this.numberLoanPaymentsPerYear * this.loanDuration);
        this.cumulativeTaxPayments = new double [this.totalNumberOfPayments];
        this.cumulativeOtherPayments = new double [this.totalNumberOfPayments];
        this.cumulativeInsurancePayments = new double [this.totalNumberOfPayments];
        this.cumulativeCapitalInvested = new double [this.totalNumberOfPayments];
        this.netSale = new double [this.totalNumberOfPayments];
        this.netProfit = new double [this.totalNumberOfPayments];

        this.projectedCapitalExpenditureDownPaymentAmount = this.projectedCapitalExpenditureDownPaymentPercentage/100
                * this.projectedCapitalExpenditure;

        this.loanAmount = (this.purchasePrice + this.projectedCapitalExpenditure)
                - (this.downPaymentAmount + this.projectedCapitalExpenditureDownPaymentAmount);
        this.initialInvestmentAmount = this.downPaymentAmount
                + this.projectedCapitalExpenditureDownPaymentAmount;
    }

    /**
     * Driver Method that computes the final Results of the Entire Analysis.
     * The netProfit values for each time period are computed here.
     */
    public void performAnalysis(){
        computeCumulativeExpenses();
        computeCumulativeCapitalInvested();
        computeNetSaleAndNetProfit();
    }

    /**
     * This method will compute cumulative Taxes,Insurance and Other
     * expenses
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
     * The amortization schedule will get computed and expenses from the
     * computeCumulativeExpenses method will be added
     *
     */

    private void computeCumulativeCapitalInvested(){
        this.amortizationCalculator = new Amortization(this.loanAmount,this.loanDuration,
                this.numberLoanPaymentsPerYear,this.loanInterestRate);
        this.amortizationCalculator.calculateAmortizationSchedule();
        this.loanBalances = this.amortizationCalculator.getLoanBalances();
        this.cumulativeTotalPayments = this.amortizationCalculator.getCumulativeTotalPayments();

        for (int i=0; i<this.totalNumberOfPayments; i++){
            this.cumulativeCapitalInvested[i] = this.initialInvestmentAmount
                    + this.cumulativeTotalPayments[i]
                    + this.cumulativeTaxPayments[i]
                    + this.cumulativeInsurancePayments[i]
                    + this.cumulativeOtherPayments[i];
        }

    }

    /**
     * This method will compute the netSale and netProfit
     * values for each Period.
     */

    private void computeNetSaleAndNetProfit(){

        for (int i =0; i<this.totalNumberOfPayments; i++){
            this.netSale[i] = this.projectedResalePrice - this.loanBalances[i];
            this.netProfit[i] = this.netSale[i] - this.cumulativeCapitalInvested[i];
        }

    }


    /**
     * Helper method to check a user provided input. Each index corresponds to a
     * pre-defined input. Each input has it's own logic that we must
     * check
     * @param input
     * @param index
     */

    private void checkUserInput(String input, int index){
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
                if (this.userInputs[index]>100){
                    this.inputErrors += this.userInputsNames[index]
                            +" should be <=100%\n";
                }

            } else if (index==3){
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
                    if (this.userInputs[index] + this.userInputs[0] > this.userInputs[1]){
                        this.inputErrors += this.userInputsNames[index]
                                + this.userInputsNames[0] + " should be < than: "
                                + this.userInputsNames[1]+"\n";
                    }
                }
            } else if (index ==4){
                if (this.userInputs[index] > MAX_INTEREST_RATE){
                    this.inputErrors += this.userInputsNames[index]
                            + " is unreasonably high. Please select a reasonable interest rate: "
                            + "< than " + MAX_INTEREST_RATE + "\n";
                }
            } else if (index == 5){
                if (this.userInputs[index] > MAX_LOAN_DURATION || this.userInputs[index] < MIN_LOAN_DURATION){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be > " + MIN_LOAN_DURATION
                            + " and < " + MAX_LOAN_DURATION + "\n";
                } else if (this.userInputs[index] % 1 !=0){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be an integer value. We only consider loans"
                            + " issued in full year increments\n";
                }
            } else if (index==6){
                if (this.userInputs[index]>MAX_NUMB_LOAN_PAYMENTS || this.userInputs[index]<MIN_NUMB_LOAN_PAYMENTS){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be > " + MAX_LOAN_DURATION
                            + " and < " + MIN_LOAN_DURATION + "\n";
                } else if (this.userInputs[index] % 1 !=0){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be an integer value. We only consider payment"
                            + " frequencies that increment by whole numbers\n";
                }
            } else if (index==7 || index ==8 || index ==9){
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
            } else if (index==11){
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
                    double maxVal = this.userInputs[10] + this.userInputs[index]*3;
                    if (maxVal<this.userInputs[0]) {
                        this.inputErrors += this.userInputsNames[index]
                                + "and/or " + this.userInputsNames[10]
                                + " is/are unreasonably high given the projected Resale Price."
                                + " Please input different assumptions for a meaningful analysis\n";
                    }
                }
            } else if (index==12){
                if (this.userInputs[index]>MAX_NUMBER_SIMULATIONS || this.userInputs[index]<MIN_NUMBER_SIMULATIONS){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be > " + MIN_NUMBER_SIMULATIONS + " and"
                            + " < than " + MAX_NUMBER_SIMULATIONS +"\n";
                } else if (this.userInputs[index] % 1 !=0){
                    this.inputErrors += this.userInputsNames[index]
                            + " should be an integer value.\n";
                }
            } else if (index==13){
                if (this.userInputs[0]==this.sentinelValue) {
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[0]
                            + " is invalid\n";
                } else if (this.userInputs[1]==this.sentinelValue){
                    this.inputErrors += "Unable to confirm validity of input: "
                            + this.userInputsNames[index] + ", Cause: " + this.userInputsNames[1]
                            + " is invalid\n";
                } else if (this.userInputs[3] + this.userInputs[0] > this.userInputs[1]) {
                    this.inputErrors += this.userInputsNames[3]
                                + this.userInputsNames[0] + " should be < than: "
                                + this.userInputsNames[1]+ " therefore this input is invalid\n";

                } else if (this.userInputs[index]>100){
                    this.inputErrors += this.userInputsNames[index]
                            +" should be <=100%\n";
                }
            }
        }

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
     */
    public double [] getNetProfit(){
        return this.netProfit;
    }

    /**
     * Getter for loanBalances per period array
     */
    public double [] getLoanBalances (){
        return this.loanBalances;
    }

    /**
     * Getter for cumulativeCapitalInvested per period array
     */
    public double [] getCumulativeCapitalInvested (){
        return this.cumulativeCapitalInvested;
    }
}
