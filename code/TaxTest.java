class TaxTest {

    public static void main (String [] args){
        TaxCalculations test = new TaxCalculations(100_000);
        System.out.println("Test: " + test.calculateLongTermInvestmentTaxExposure(740_251));
        
        TaxCalculations test2 = new TaxCalculations(101_000);
        System.out.println("Test2 :" + test2.calculateLongTermInvestmentTaxExposure(900_462));
    }
}