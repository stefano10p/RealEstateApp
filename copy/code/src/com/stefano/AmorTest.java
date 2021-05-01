import java.util.Arrays;

public class AmorTest {

    public static void main(String [] args){
        Amortization test = new Amortization(456000,30,12,5.181);
        test.calculateAmortizationSchedule();

        System.out.println("Here are the results:");
        System.out.println("Principal Payments" + Arrays.toString(test.getPrincipalPayments()));
        System.out.println("Interest Payments" + Arrays.toString(test.getInterestPayments()));
        System.out.println("LoanBalances" + Arrays.toString(test.getLoanBalances()));
        System.out.println("CUM TOTAL PAYMENTS" + Arrays.toString(test.getCumulativeTotalPayments()));

    }


}
