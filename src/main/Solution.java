package main;

import main.strategy.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Solution {
    public static void main(String[] args) {
        testStrategy(new HashMapStorageStrategy(), 10000);
        System.out.println("-----------------------");
        testStrategy(new OurHashMapStorageStrategy(), 10000);
        System.out.println("-----------------------");
        testStrategy(new OurHashBiMapStorageStrategy(), 10000);
        System.out.println("-----------------------");
        testStrategy(new HashBiMapStorageStrategy(), 10000);
        System.out.println("-----------------------");
        testStrategy(new DualHashBidiMapStorageStrategy(), 10000);
    }

    /**
     * These methods are meant for testing storage strategies
     * In normal circumstances I would mark these methods private,
     * but CG validator does want these methods to be public
     */
    public static Set<Long> getIds(Shortener shortener, Set<String> strings){
        Set<Long> ids = new HashSet<>();
        for (String s : strings) ids.add(shortener.getId(s));
        return ids;
    }

    public static Set<String> getStrings(Shortener shortener, Set<Long> keys) {
        Set<String> strings = new HashSet<>();
        for (Long l : keys) strings.add(shortener.getString(l));
        return strings;
    }

    public static void testStrategy(StorageStrategy strategy, long elementsNumber){
        //Display name of the strategy class
        Helper.printMessage(strategy.getClass().getSimpleName());
        //Populate test set with elementsNumber elements
        Set<String> testSet = new HashSet<>();
        for (long i = 0; i < elementsNumber; i++) {
            testSet.add(Helper.generateRandomString());
        }
        //Create Shortener object
        Shortener shortener = new Shortener(strategy);

        //Measure time of getIds method
        long startTime = new Date().getTime();
        Set<Long> ids = getIds(shortener, testSet);
        long endTime = new Date().getTime();
        //Difference in ms
        long diff = (endTime - startTime);
        System.out.printf("getIds method took: %d ms\n", diff);

        //Measure time of getStrings method
        startTime = new Date().getTime();
        Set<String> strings = getStrings(shortener, ids);
        endTime = new Date().getTime();
        diff = endTime - startTime;
        System.out.printf("getStrings method took: %d ms\n", diff);

        //Check if String sets are the same
        System.out.println(strings.equals(testSet) ? "The test passed." : "The test failed.");


    }


}