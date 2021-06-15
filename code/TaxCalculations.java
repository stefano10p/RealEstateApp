/**
 * Class to handle tax exposure calculations.
 * Informations is up to date as of 5/27/2021
 */
import java.util.Arrays;

class TaxCalculations{
    private double regularIncome;
    //private double additionalIncome;
    private double [] taxBracketPercentages = {.10,.12,.22,.24,.32,.35,.37};
    private double [] taxBracketThresholdsLower = {0,9_876, 40_126, 85_526, 163_301, 207_351, 518_401};
    private double [] taxBracketThresholdsUpper = {9_875,40_125, 85_525, 163_300, 207_350, 518_400,-1};
    private double [] taxBracketThresholdsDelta = new double [taxBracketThresholdsLower.length];

    private double [] taxBracketLongTermPercentages = {.10,.15,.20};
    private double [] taxBracketLongTermThresholdsLower = {0,40_001, 441_451};
    private double [] taxBracketLongTermThresholdsUpper = {40_000,441_450, -1};
    private double [] taxBracketLongTermThresholdsDelta = new double [taxBracketLongTermThresholdsLower.length];

    /**
     * Constructor
     */
    public TaxCalculations(double regularIncome){
        this.regularIncome = regularIncome;
        for (int j=0; j<this.taxBracketThresholdsUpper.length;j++){
            this.taxBracketThresholdsDelta[j] = taxBracketThresholdsUpper[j]
                    - taxBracketThresholdsLower[j] + 1;
        }

        for (int j=0; j<this.taxBracketLongTermThresholdsUpper.length;j++){
            this.taxBracketLongTermThresholdsDelta[j] = taxBracketLongTermThresholdsUpper[j]
                    - taxBracketLongTermThresholdsLower[j] + 1;
        }
        System.out.println();


    }

    /**
     * Method to compute tax exposure given base income
     * plus additional income from project
     * @return total tax exposure given tax bracket and
     */
    public double calculateShortTermInvestmentTaxExposure(double investmentIncome){
        double totalIncome = this.regularIncome + investmentIncome;
        int numberBrackets = taxBracketThresholdsLower.length;
        if (totalIncome>taxBracketThresholdsLower[numberBrackets-1]){
            taxBracketThresholdsUpper[numberBrackets-1] = totalIncome;
            this.taxBracketThresholdsDelta[numberBrackets-1] = taxBracketThresholdsUpper[numberBrackets-1]
                    - taxBracketThresholdsLower[numberBrackets-1];
        }
        System.out.println(Arrays.toString(taxBracketThresholdsDelta));
        System.out.println(Arrays.toString(taxBracketThresholdsUpper));
        System.out.println(Arrays.toString(taxBracketThresholdsLower));
        double totalTaxExposure = calculateTaxExposure(totalIncome);
        System.out.println("Total Tax Exposure:" + totalTaxExposure);
        double regularIncomeTaxExposure = calculateTaxExposure(this.regularIncome);
        System.out.println("Regular Income Tax Exposure:" + regularIncomeTaxExposure);
        return totalTaxExposure - regularIncomeTaxExposure;

    }
    
    // SHORT TERM EXPOSURE LOGIC
    private double calculateTaxExposure(double totalIncome){
        if ( totalIncome>=taxBracketThresholdsLower[0] && totalIncome <= taxBracketThresholdsUpper[0] ){
            return totalIncome * taxBracketPercentages[0] ;
        } else if ( totalIncome>=taxBracketThresholdsLower[1] && totalIncome <= taxBracketThresholdsUpper[1]){
            return individualBracketExposure(0) + (totalIncome - taxBracketThresholdsLower[1]) * taxBracketPercentages[1] ;
        } else if (totalIncome>=taxBracketThresholdsLower[2] && totalIncome <= taxBracketThresholdsUpper[2]){
            return individualBracketExposure(1) + (totalIncome - taxBracketThresholdsLower[2]) * taxBracketPercentages[2] ;
        } else if (totalIncome>=taxBracketThresholdsLower[3] && totalIncome <= taxBracketThresholdsUpper[3]){
            return individualBracketExposure(2) + (totalIncome - taxBracketThresholdsLower[3]) * taxBracketPercentages[3] ;
        } else if (totalIncome>=taxBracketThresholdsLower[4] && totalIncome <= taxBracketThresholdsUpper[4]){
            return individualBracketExposure(3) + (totalIncome - taxBracketThresholdsLower[4]) * taxBracketPercentages[4] ;
        } else if (totalIncome>=taxBracketThresholdsLower[5] && totalIncome <= taxBracketThresholdsUpper[5]){
            return individualBracketExposure(4) + (totalIncome - taxBracketThresholdsLower[5]) * taxBracketPercentages[5] ;
        } else if (totalIncome>=taxBracketThresholdsLower[6] && totalIncome <= taxBracketThresholdsUpper[6]){
            return individualBracketExposure(5) + (totalIncome - taxBracketThresholdsLower[6]) * taxBracketPercentages[6] ;
        } else {
            return -1;
        }
    }

    private double individualBracketExposure(int bracket){
        if (bracket==0){
            return taxBracketThresholdsDelta[0] * taxBracketPercentages[0];
        } else if (bracket==1){
            return taxBracketThresholdsDelta[0] * taxBracketPercentages[0]
                    + taxBracketThresholdsDelta[1] * taxBracketPercentages[1];
        } else if (bracket==2){
            return taxBracketThresholdsDelta[0] * taxBracketPercentages[0]
                    + taxBracketThresholdsDelta[1] * taxBracketPercentages[1]
                    + taxBracketThresholdsDelta[2] * taxBracketPercentages[2];
        } else if (bracket==3){
            return taxBracketThresholdsDelta[0] * taxBracketPercentages[0]
                    + taxBracketThresholdsDelta[1] * taxBracketPercentages[1]
                    + taxBracketThresholdsDelta[2] * taxBracketPercentages[2]
                    + taxBracketThresholdsDelta[3] * taxBracketPercentages[3];
        } else if (bracket==4){
            return taxBracketThresholdsDelta[0] * taxBracketPercentages[0]
                    + taxBracketThresholdsDelta[1] * taxBracketPercentages[1]
                    + taxBracketThresholdsDelta[2] * taxBracketPercentages[2]
                    + taxBracketThresholdsDelta[3] * taxBracketPercentages[3]
                    + taxBracketThresholdsDelta[4] * taxBracketPercentages[4];
        } else if (bracket==5){
            return taxBracketThresholdsDelta[0] * taxBracketPercentages[0]
                    + taxBracketThresholdsDelta[1] * taxBracketPercentages[1]
                    + taxBracketThresholdsDelta[2] * taxBracketPercentages[2]
                    + taxBracketThresholdsDelta[3] * taxBracketPercentages[3]
                    + taxBracketThresholdsDelta[4] * taxBracketPercentages[4]
                    + taxBracketThresholdsDelta[5] * taxBracketPercentages[5];
        } else {
            return -1; // this can't happen
        }
    }

    //Long Term Capital Gains

    private double calculateLongTermTaxExposure(double investmentIncome){
        if (investmentIncome>=taxBracketLongTermThresholdsLower[0] && investmentIncome <= taxBracketLongTermThresholdsUpper[0] ){
            return investmentIncome * taxBracketLongTermPercentages[0] ;
        } else if (investmentIncome>=taxBracketLongTermThresholdsLower[1] && investmentIncome <= taxBracketLongTermThresholdsUpper[1]){
            return individualLongTermBracketExposure(0) + (investmentIncome - taxBracketLongTermThresholdsLower[1]) * taxBracketLongTermPercentages[1] ;
        } else if (investmentIncome>=taxBracketLongTermThresholdsLower[2] && investmentIncome <= taxBracketLongTermThresholdsUpper[2]){
            return individualLongTermBracketExposure(1) + (investmentIncome - taxBracketLongTermThresholdsLower[2]) * taxBracketLongTermPercentages[2] ;
        } else {
            return -1;
        }
    }

    private double individualLongTermBracketExposure(int bracket){
        if (bracket==0){
            System.out.println("Here?");
            return taxBracketLongTermThresholdsDelta[0] * taxBracketLongTermPercentages[0]; //TODO come back here
        } else if (bracket==1){
            return taxBracketLongTermThresholdsDelta[0] * taxBracketLongTermPercentages[0]
                    + taxBracketLongTermThresholdsDelta[1] * taxBracketLongTermPercentages[1];
        } else {
            return -1; // this can't happen
        }
    }


    public double calculateLongTermInvestmentTaxExposure (double investmentIncome){
        int numberBrackets = taxBracketLongTermThresholdsLower.length;
        if (investmentIncome>taxBracketLongTermThresholdsLower[numberBrackets-1]){
            taxBracketLongTermThresholdsLower[numberBrackets-1] = investmentIncome;
            this.taxBracketLongTermThresholdsDelta[numberBrackets-1] = taxBracketLongTermThresholdsUpper[numberBrackets-1]
                    - taxBracketLongTermThresholdsLower[numberBrackets-1];
        }
        System.out.println(Arrays.toString(taxBracketLongTermThresholdsDelta));
        System.out.println(Arrays.toString(taxBracketLongTermThresholdsUpper));
        System.out.println(Arrays.toString(taxBracketLongTermThresholdsLower));
        double totalLongTermTaxExposure = calculateLongTermTaxExposure(investmentIncome);
        return totalLongTermTaxExposure;
    }

    public static void main(String [] args){
        TaxCalculations test = new TaxCalculations(265_000);
        double taxExposure = test.calculateShortTermInvestmentTaxExposure(350_000);
        System.out.println("Short Term: " + taxExposure);
        double longTermTest = test.calculateLongTermInvestmentTaxExposure(85_000);
        System.out.println("Long Term: " + longTermTest);
    }

}