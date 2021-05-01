import java.util.Arrays;

public class RecurseTest {

    public static void main (String [] args){
        int [] numbs = {1,2,3,4,5,6};
        findSum(6);
    }

    public static int findSum(int [] numbs,x) {
        int [] numbs = {1,2,3,4,5,6};
        int [] res = new int[6];
        if (x==0){
            res [x] = numbs[x];
            return numbs[x];
        } else {
            System.out.println(Arrays.toString(res));
            res[x] = numbs[x] + numbs[x-1];
            return findSum(numbs[x] + numbs[x-1]);
        }

    }
}
