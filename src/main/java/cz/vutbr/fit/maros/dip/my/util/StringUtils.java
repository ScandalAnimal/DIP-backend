package cz.vutbr.fit.maros.dip.my.util;

public class StringUtils {

    public static String stringifyJSONObject(String obj) {
        String withoutFirst = obj.substring(1);
        return withoutFirst.substring(0, withoutFirst.length() - 1) + "\n";
    }
}
