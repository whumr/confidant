package com.confidant.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2015/8/19.
 */
public class Validator {
    private static Pattern REG_EMAIL = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    private static Pattern REG_PASSWORD = Pattern.compile("\\w{6,30}");

    public static boolean isEmail(String email) {
        if (emptyString(email))
            return false;
        Matcher m = REG_EMAIL.matcher(email);
        return m.matches();
    }

    public static boolean isPassword(String password) {
        if (emptyString(password))
            return false;
        Matcher m = REG_PASSWORD.matcher(password);
        return m.matches();
    }

    public static boolean emptyString(String value) {
        return emptyString(value, true);
    }

    public static boolean emptyString(String value, boolean trim) {
        return value == null || (trim ? value.trim().length() == 0 : value.length() == 0);
    }
}
