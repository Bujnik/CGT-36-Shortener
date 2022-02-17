package main;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Helper {
    /**
     * Returns 130 bit random string
     * based on alphanumeric characters (10 numeric + 26 letters)
     */
    public static String generateRandomString(){
        return new BigInteger(130, new SecureRandom()).toString(36);
    }

    public static void printMessage(String message){
        System.out.println(message);
    }
}
