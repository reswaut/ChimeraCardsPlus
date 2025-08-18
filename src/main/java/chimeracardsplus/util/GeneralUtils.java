package chimeracardsplus.util;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GeneralUtils {
    public static String arrToString(Object[] arr) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        int length = arr.length;
        return IntStream.range(0, length - 1).mapToObj(i -> arr[i] + ", ").collect(Collectors.joining("", "", String.valueOf(arr[arr.length - 1])));
    }

    public static String removePrefix(String id) {
        return id.substring(id.indexOf(':') + 1);
    }
}
