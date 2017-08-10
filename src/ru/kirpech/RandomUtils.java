package ru.kirpech;

import java.util.Random;

public class RandomUtils {
    private RandomUtils(){

    }
    private static final Random RAND = new Random();

    public static int nextInt(int bound){
        return RAND.nextInt(bound);
    }

    public static boolean nextBoolean(){
        return RAND.nextBoolean();
    }

    public static int nextInt(int min,int max){
        return RAND.nextInt(max-min)+min;
    }
    public static double nextDouble(){
        return RAND.nextDouble();
    }
}
