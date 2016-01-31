package com.careerbuilder.search.semantic;

import java.util.Arrays;

public class Utility {

    public static int getArgMax(double[] array) {
        int argMax = 0;
        double max = -1e100;
        for(int i = 0; i < array.length; i++)
        {
            if(array[i] >= max)
            {
                argMax = i;
                max = array[i];
            }
        }
        return argMax;
    }

    public static int booleansToInt(boolean [] booleans)
    {
        int intValue = 0;
        for(int i =0; i < booleans.length; i++)
        {
            if(booleans[i])
            {
                intValue += (1 << i);
            }
        }
        return intValue;
    }

    public static void intToBooleans(int intValue, boolean [] booleans)
    {
        Arrays.fill(booleans, false);
        for(int i =0; i < booleans.length; i++)
        {
            if(((intValue >> i) & 1) == 1)
            {
                booleans[i] = true;
            }
        }
    }

    public static String joinString(String [] list, String conjunction)
    {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : list)
        {
            if (first)
                first = false;
            else
                sb.append(conjunction);
            sb.append(item);
        }
        return sb.toString();
    }
}
