package main.tests;

import main.Helper;
import main.Shortener;
import main.strategy.HashBiMapStorageStrategy;
import main.strategy.HashMapStorageStrategy;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * We're going to write another test that verifies that using HashBiMapStorageStrategy
 * to get the identifier for a string can be faster than using HashMapStorageStrategy.
 */
public class SpeedTest {
    @Test
    public void testHashMapStorage(){
        Shortener shortener1 = new Shortener(new HashMapStorageStrategy());
        Shortener shortener2 = new Shortener(new HashBiMapStorageStrategy());

        Set<String> origStrings = new HashSet<>();
        for (int i = 0; i < 10000; i++) origStrings.add(Helper.generateRandomString());

        Set<Long> ids1 = new HashSet<>();
        Set<Long> ids2 = new HashSet<>();

        long time1 = getTimeToGetIds(shortener1,origStrings,ids1);
        long time2 = getTimeToGetIds(shortener2,origStrings,ids2);

        //We check if HashMap is slower than HashBiMap
        Assert.assertTrue(time1 > time2);

        Set<String> strings1 = new HashSet<>();
        Set<String> strings2 = new HashSet<>();

        time1 = getTimeToGetStrings(shortener1, ids1, strings1);
        time2 = getTimeToGetStrings(shortener2, ids2, strings2);

        Assert.assertEquals(time1, time2, 30);
    }



    public long getTimeToGetIds(Shortener shortener, Set<String> strings, Set<Long> ids){
        long start = new Date().getTime();
        for (String s : strings) ids.add(shortener.getId(s));
        long end = new Date().getTime();

        return end - start;
    }

    public long getTimeToGetStrings(Shortener shortener, Set<Long> ids, Set<String> strings){
        long start = new Date().getTime();
        for (Long id : ids) strings.add(shortener.getString(id));
        long end = new Date().getTime();

        return end - start;
    }

}
