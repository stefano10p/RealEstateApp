/**
 * This class contains a number of static methods
 * that will allow us to perform statistical
 * calculations on arrays holding net profit values
 * from out simulations.
 * @author Stefano Parravano
 */

import java.text.NumberFormat;
import java.util.Arrays;

public class MathHelper {

    /**
     * Method to compute mean of an array
     * @param inputArray holding all net profit outcomes for a given time period
     * @return the mean net profit value
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
     * @param inputArray holding all net profit outcomes for a given time period
     * @return the standard deviation of our net profit values
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
     * @oaram inputArray holding all net profit outcomes for a given time period
     * @return formatted tring encoding upper and lower bound of 95% CI of our net profit values
     */
    public static String compute95CI( double [] inputArray){
        double [] inputArrayClone = inputArray.clone();
        Arrays.sort(inputArrayClone);
        int lowerBound = (int) Math.ceil(0.025 * inputArrayClone.length);
        int upperBound = (int) Math.ceil(0.975 * inputArrayClone.length);
        double lowerVal = inputArrayClone[lowerBound];
        double upperVal = inputArrayClone[upperBound];
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        formatter.setMaximumFractionDigits(0);

        String Result = "[" + formatter.format(lowerVal)
                + ", " + formatter.format(upperVal) + "]";
        return Result;
    }

    /**
     * Method to compute probability of Project Success
     * for a given Time Period. A success is defined as an outcome
     * in which the net profit > 0. P(Success) = # of net profit >0 / total trials.
     * @param inputArray holding all net profit outcomes for a given time period
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
