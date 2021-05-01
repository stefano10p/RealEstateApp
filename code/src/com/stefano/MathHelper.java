import java.text.NumberFormat;
import java.util.Arrays;

/**
 * This class contains a number of static methods
 * that will allow us to perform statistical
 * calculations on arrays
 */

public class MathHelper {

    /**
     * Method to compute mean of an array
     * @param inputArray
     * @return double the mean of thee array
     */
    public static double computeMean( double [] inputArray){

        double sum=0;
        for (int i=0; i<inputArray.length; i++){
            sum += inputArray[i];
        }
        return sum/inputArray.length;
    }

    /**
     * Method to compute standard deviations of values in an array
     * @param inputArray
     * @return double the standard deviation
     */
    public static double computeStandardDeviation(double [] inputArray){
        double mean = MathHelper.computeMean(inputArray);
        double sumSquares = 0;
        for (int i=0; i<inputArray.length;i++){
            sumSquares += Math.pow((inputArray[i] - mean),2);
        }
        double meanSumSquares = sumSquares/inputArray.length;
        double result = Math.pow(meanSumSquares,0.5);
        return result;
    }

    /**
     * Method to compute 95% CI of an array.
     * @oaram inputArray
     * @return String Indicating upper and lower bound of 95% CI
     */
    public static String compute95CI( double [] inputArray){
        double [] inputArrayClone = inputArray.clone();
        Arrays.sort(inputArrayClone);
        int lowerBound = (int) Math.ceil(0.025 * inputArrayClone.length);
        int upperBound = (int) Math.ceil(0.975 * inputArrayClone.length);
        double lowerVal = inputArrayClone[lowerBound];
        double upperVal = inputArrayClone[upperBound];
        NumberFormat formatter = NumberFormat.getCurrencyInstance();

        String Result = "[" + formatter.format(lowerVal)
                + ", " + formatter.format(upperVal) + "]";
        return Result;
    }

    /**
     * Method to compute probability of Project Success
     * for a given Time Period
     * @param inputArray
     * @return Formatted string encoding probability of project Success
     */
    public static String computeProbabilityProjectSuccess( double [] inputArray){
        double successCount = 0;
        for (int i=0; i<inputArray.length;i++){
            if (inputArray[i]>0) successCount++;
        }
        double result = successCount/inputArray.length;
        NumberFormat percentFormatter;
        percentFormatter = NumberFormat.getPercentInstance();
        String percentResult = percentFormatter.format(result);
        return percentResult;
    }
}
