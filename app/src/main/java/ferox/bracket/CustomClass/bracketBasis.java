package ferox.bracket.CustomClass;

import java.util.Arrays;

public class bracketBasis {

    public static void main(String[] args) {
        bracketBasis basis = new bracketBasis();


        int[] arr = new int[32];
        System.out.println(Arrays.toString(basis.populateArray(arr, arr.length)));

    }

    //makes array of t
    public int[] populateArray(int[] arr, int partition) {

        if (partition != 1) {
            //calls itself until the array = {1} is return
            int[] split = populateArray(arr, partition / 2);
            int[] tmp = new int[split.length * 2];
            int[] doublesplit = new int[split.length];

            //creates an array that is equal to split except there is a space between each element
            // if split is equal to [1,2] then tmp is [1,0,2,0]
            for (int i = 0; i < split.length; i++) {
                tmp[i * 2] = split[i];
            }
            //makes array equal to next split.length integers after split.length
            //if array is [1,2], doublesplit is [3,4]
            for (int i = 0; i < doublesplit.length; i++) {
                doublesplit[i] = i + split.length + 1;
            }


            int[] result = matchLowHigh(tmp, doublesplit);
            return result;


        } else if (partition == 1) {
            int[] i = {1};
            return i;
        } else {
            int[] i = {1};
            return i;
        }

    }

    //takes two arrays and will return an array of numbers from 1..n arranged in the order of
    //seeding in a competitive bracket
    // 1..4 will return [1,4,2,3], 1..8 will return [1,8,4,5,2,7,3,6] etc...
    public int[] matchLowHigh(int[] low, int[] high) {


        int lower = 1;
        int upper = high[high.length - 1];
        while (lower < upper) {
            for (int i = 0; i < low.length; i++) {
                if (low[i] == lower) {
                    low[i + 1] = upper;
                }
            }
            ++lower;
            --upper;
        }
        return low;
    }


}
