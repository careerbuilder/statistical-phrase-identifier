package com.careerbuilder.search.semantic;

import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(JMockit.class)
public class UtilityTest {

    @Test
    public void getArgMax_DoubleArray() {
        double [] testCase = new double[] {0.0, -99999, -1, 1, 45, 1e10, 5, 354};
        int expected = 5;

        int actual = Utility.getArgMax(testCase);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void getArgMax_Duplicates() {
        double [] testCase = new double[] {0.0, 1e10, -99999, 1e10, 1, 45, 1e10, 5, 354};
        int expected = 6;

        int actual = Utility.getArgMax(testCase);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void intToBooleans_0(){
        int testInt = 0;
        boolean [] actual = new boolean[2];
        boolean [] expected = new boolean[2];
        Arrays.fill(expected, false);

        Utility.intToBooleans(testInt, actual);

        for(int i = 0; i < actual.length; ++i) {
            Assert.assertEquals(actual[i], expected[i]);
        }
    }

    @Test
    public void intToBooleans_1(){
        int testInt = 1;
        boolean [] actual = new boolean[2];
        boolean [] expected = new boolean[] { true, false };

        Utility.intToBooleans(testInt, actual);

        for(int i = 0; i < actual.length; ++i) {
            Assert.assertEquals(actual[i], expected[i]);
        }
    }


    @Test
    public void intToBooleans_2(){
        int testInt = 2;
        boolean [] actual = new boolean[2];
        boolean [] expected = new boolean[] { false, true};

        Utility.intToBooleans(testInt, actual);

        for(int i = 0; i < actual.length; ++i) {
            Assert.assertEquals(actual[i], expected[i]);
        }
    }


    @Test
    public void intToBooleans_3(){
        int testInt = 3;
        boolean [] actual = new boolean[2];
        boolean [] expected = new boolean[] { true, true};

        Utility.intToBooleans(testInt, actual);

        for(int i = 0; i < actual.length; ++i) {
            Assert.assertEquals(actual[i], expected[i]);
        }
    }

    @Test
    public void intToBooleans_1538(){
        int testInt = 1538;
        boolean [] actual = new boolean[12];
        boolean [] expected = new boolean[] { false, true, false, false, false, false, false, false, false, true, true, false};

        Utility.intToBooleans(testInt, actual);

        for(int i = 0; i < actual.length; ++i) {
            Assert.assertEquals(actual[i], expected[i]);
        }
    }

    @Test
    public void intToBooleans_clearInput(){
        int testInt = 1538;
        boolean [] actual = new boolean[12];
        Arrays.fill(actual, true);
        boolean [] expected = new boolean[] { false, true, false, false, false, false, false, false, false, true, true, false};

        Utility.intToBooleans(testInt, actual);

        for(int i = 0; i < actual.length; ++i) {
            Assert.assertEquals(actual[i], expected[i]);
        }
    }

    @Test
    public void booleansToInt_127(){
        boolean [] testCase = new boolean[7];
        Arrays.fill(testCase, true);
        int expected = 127;
        int actual = Utility.booleansToInt(testCase);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void booleansToInt_1538(){
        boolean [] testCase = new boolean[] { false, true, false, false, false, false, false, false, false, true, true, false};
        int expected = 1538;
        int actual = Utility.booleansToInt(testCase);

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void joinString(){
        String [] array = new String[] {"associate", "sales", "manager"};
        String expected = "associate sales manager";
        String actual = Utility.joinString(array, " ");
        Assert.assertTrue(expected.compareTo(actual) == 0);
    }

    @Test
    public void joinString_oneTerm(){
        String [] array = new String[] {"sales"};
        String expected = "sales";
        String actual = Utility.joinString(array, " ");
        Assert.assertTrue(expected.compareTo(actual) == 0);
    }

    @Test
    public void joinString_noTerms(){
        String [] array = new String[] {""};
        String expected = "";
        String actual = Utility.joinString(array, " ");
        Assert.assertTrue(expected.compareTo(actual) == 0);
    }
}
