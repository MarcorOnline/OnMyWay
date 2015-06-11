package com.onmyway.utils;

/**
 * Created by Marco on 20/05/2015.
 */
public class StringUtils {
    public static boolean isNullOrWhiteSpaces(String string)
    {
        return string == null || string.trim().isEmpty();
    }
}
