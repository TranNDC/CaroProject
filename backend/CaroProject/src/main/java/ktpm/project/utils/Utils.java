package ktpm.project.utils;

import java.util.Random;

public class Utils {
    public static String randomImage(Integer n){
        Random random = new Random();
        return String.valueOf((random.nextInt(n)+1));
    }
}
