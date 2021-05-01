/**
 * Class to compute amortization
 * schedule for financing
 */
public class Amortization {
    private double loanAmount;
    private double loanDuration;
    private double numberLoanPaymentsPerYear;
    private double loanInterestRate;
    private double loanInterestRatePerPeriod;

    private double monthlyPayment;
    private double totalNumberOfPayments;

    private double [] principalPayments;
    private double [] interestPayments;
    private double [] loanBalances;

    private double [] cumulativePrincipalPayments;
    private double [] cumulativeInterestPayments;
    private double [] cumulativeTotalPayments; // cumulativePrincipalPayments + cumulative interestPayments


    /**
     * Constructor.
     */
    public Amortization(double loanAmount, double loanDuration,
                        double numberLoanPaymentsPerYear ,double loanInterestRate){
        this.loanAmount = loanAmount;
        this.loanDuration = loanDuration;
        this.numberLoanPaymentsPerYear = numberLoanPaymentsPerYear;
        this.loanInterestRate = loanInterestRate/100;
        this.totalNumberOfPayments = loanDuration * numberLoanPaymentsPerYear;
        this.loanInterestRatePerPeriod =loanInterestRate/100/numberLoanPaymentsPerYear;
        this.principalPayments = new double [(int)this.totalNumberOfPayments];
        this.interestPayments = new double [(int)this.totalNumberOfPayments];
        this.loanBalances = new double [(int)this.totalNumberOfPayments];
        this.cumulativePrincipalPayments = new double [(int)this.totalNumberOfPayments];
        this.cumulativeInterestPayments = new double [(int)this.totalNumberOfPayments];
        this.cumulativeTotalPayments = new double [(int)this.totalNumberOfPayments];
    }

    /**
     * Helper Method to compute Monthly Payment Amount.
     * This is given by the formula: A = P((r*(1+r)^n)/((1+r)^n)-1)
     */
    private void calculateMonthlyPayment(){
        double numerator = this.loanInterestRatePerPeriod * Math.pow(1+this.loanInterestRatePerPeriod,this.totalNumberOfPayments);
        double denominator = Math.pow(1+this.loanInterestRatePerPeriod,this.totalNumberOfPayments) -1;
        double result = this.loanAmount * (numerator/denominator);
        this.monthlyPayment = result;
        System.out.println("Monthly Payment is: " + result);
    }

    /**
     * This Method will compute the Amortization Schedule
     */
    public void calculateAmortizationSchedule(){
        this.calculateMonthlyPayment();
        double interestPaid;

        interestPaid = this.loanInterestRatePerPeriod * this.loanAmount;
        this.interestPayments[0] = interestPaid;
        this.principalPayments[0] = this.monthlyPayment - interestPaid;
        this.loanBalances [0] = this.loanAmount - this.principalPayments[0];

        for (int i=1; i<this.totalNumberOfPayments; i++){
            interestPaid = this.loanInterestRatePerPeriod * this.loanBalances[i-1];
            this.interestPayments[i] = interestPaid;
            this.principalPayments[i] = this.monthlyPayment - interestPaid;
            this.loanBalances [i] = this.loanBalances [i-1] - this.principalPayments[i];
        }
        this.computeCumulativeInterestAndPrincipal();
    }

    /**
     * This method will compute the cumulative interest
     * and principal payments
     */
    private void computeCumulativeInterestAndPrincipal(){
        this.cumulativeInterestPayments[0] = this.interestPayments[0];
        this.cumulativePrincipalPayments[0] = this.principalPayments[0];
        this.cumulativeTotalPayments[0] = this.interestPayments[0] + this.principalPayments[0];
        for (int i=1; i<this.totalNumberOfPayments; i++){
            this.cumulativePrincipalPayments[i] = this.cumulativePrincipalPayments[i-1]
                    + this.principalPayments[i];
            this.cumulativeInterestPayments[i] = this.cumulativeInterestPayments[i-1]
                    + this.interestPayments[i];

            this.cumulativeTotalPayments[i] = this.cumulativeInterestPayments[i]
                    + this.cumulativePrincipalPayments[i];
            //System.out.println(cumulativePrincipalPayments[i] + " "  +cumulativeInterestPayments[i] + " "
            //        +  " "  +cumulativeTotalPayments[i] + " "  + i);
        }

    }

    /**
     * Getter method to retrieve all Principal Payments
     */
    public double [] getPrincipalPayments(){
        return this.principalPayments;
    }

    /**
     * Getter method to retrieve all Interest Payments
     */
    public double [] getInterestPayments(){
        return this.interestPayments;
    }

    /**
     * Getter method to retrieve Balances for all periods
      */
    public double [] getLoanBalances(){
        return this.loanBalances;
    }

    /**
     * Getter method to retrieve cumulative total
     * payments (principal + interest) for all periods
     */
    public double [] getCumulativeTotalPayments(){
        return this.cumulativeTotalPayments;
    }


}
