package com.confidant.common;

/**
 * Created by Administrator on 2015/2/9.
 */
public class Constants {

    public static class ErrorMsg {
        public static class Common {
            public static String IllegalArgument = "illegal arguments";
            public static String FileTooLarge = "file too large";
            public static String NotLogin = "not login";
        }
        public static class Member {
            public static String AccountExists = "账号已存在";
            public static String NotExists = "member not exists";
            public static String LoginFail = "用户名或密码错误";
        }
    }

    public static class Keys {
        public static class Session {
            public static String KeyMember = "MEMBER";
        }
    }

    public static class Path {
        public static String Img = "/imgs/";
        public static String UserImg = Img + "users/";
    }
}
