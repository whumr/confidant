package com.confidant.util;

/**
 * Created by Administrator on 2015/8/19.
 */
public class PathUtil {

    public static String getPath(String full_path) {
        if (!Validator.emptyString(full_path)) {
            if (full_path.startsWith("http://") ) {
                int index = full_path.indexOf("/", 7);
                return index >0 ? full_path.substring(index) : "";
            }
        }
        return null;
    }
}
