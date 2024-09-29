package org.backend.utils;

/**
 * 作为存入redis中的数据的前缀，因为此时需要区分不同的存的数据的类型
 */
public class Const {
    public static final String JWT_BLACK_LIST = "jwt:blacklist";
    public static final int CORS_ORDER = -102;

    public static final String VERIFY_EMAIL_LIMIT = "verify:email:limit:";
    // 存数据的前缀
    public static final String VERIFY_EMAIL_DATA = "verify:email:data:";

}
