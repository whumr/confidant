package com.confidant.common;

/**
 * Created by Administrator on 2015/2/9.
 */
public class Constants {

    public static class ErrorMsg {
        public static class Common {
            public static String IllegalArgument = "illegal arguments";
            public static String FileTooLarge = "file too large";
        }
        public static class User {
            public static String UserExists = "user exists";
        }
    }

    public static class Keys {
        public static class Session {
            public static String KeyUser = "USER";
        }
    }

    public static class Path {
        public static String Img = "/imgs/";
        public static String UserImg = Img + "users/";
    }
}
