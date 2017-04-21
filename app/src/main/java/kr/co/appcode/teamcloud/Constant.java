package kr.co.appcode.teamcloud;

/**
 * Created by Chad on 2017-04-20.
 */

public class Constant {
    public static final int JOIN_COMPLETE = 1;
    public static final int JOIN_EMPTY_EMAIL = 2;
    public static final int JOIN_DUPLICATE_EMAIL = 3;
    public static final int JOIN_EMPTY_PASSWORD = 4;
    public static final int JOIN_EMPTY_NICKNAME = 5;
    public static final int JOIN_DUPLICATE_NICKNAME = 6;
    public static final int JOIN_EMPTY_NAME = 7;
    public static final int JOIN_QUERY_ERROR = -1;

    public static final int LOGIN_ERROR = 2;
    public static final int LOGIN_EMPTY_ID = 3;
    public static final int LOGIN_EMPTY_PASSWORD = 4;

    public static final int DUPLICATED = 2;
    public static final int NOT_DUPLICATED = 1;

    public static final int MODE_CHECK_NICKNAME = 2;
    public static final int MODE_AUTH_EMAIL = 3;
    public static final int MODE_JOIN_SUBMIT = 4;

    public static final int MAIL_SEND_COMPLETE = 1;
    public static final int MAIL_SEND_ERROR = 2;
}
