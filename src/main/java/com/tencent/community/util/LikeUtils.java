package com.tencent.community.util;



public class LikeUtils {

    private static String KEY_PREFIX = ":";

    private static String LIKE_KEY_PREFIX = "like";

    private static String  LIKE_USER = "like:user";

    // 用户关注的实体key followee:user:id:entitytype --> entityid;
    private static String FOLLOWEE_KEY_PREFIX = "followee:user";
    // 实体的粉丝数/被关注数 follower:entityType:entityid --> userid;
    private static String FOLLOWER_KEY_PREFIX = "follower";

    // 优化验证码访问key,从redis中进行验证码存取，未登录状态获取不到用户id，利用临时凭证进行记录，帮将其以cookie形式发送给客户端
    private static String KAPTCHA_KEY = "kaptcha";

    // 优化登录凭证访问，存入redis库中，登录凭证为一个对象  减少数据库操作，重构查找数据库的操作
    private static String TICKET_KEY = "ticket";

    // 优化获取用户信息，使用户信息作为缓存，存入redis库
    private static String USER_KEY = "user";

    private static final String PREFIX_UV = "uv";
    private static final String PREFIX_DAU = "dau";

    private static final String PREFIX_POST = "post";


    // 生成想要的redis key
    public static String getKey(int entityType, int entityId){
        String key = LIKE_KEY_PREFIX + KEY_PREFIX +entityType + KEY_PREFIX + entityId;
        return key;
    }

    // 生成用户统计赞的key
    public static String getLikeUserKey(int userId){
        String userKey = LIKE_USER + KEY_PREFIX + userId;
        return userKey;
    }

    // 生成用户关注的key
    public static String getFolloweeKey(int userId, int entityType){
        String followeeKey = FOLLOWEE_KEY_PREFIX + KEY_PREFIX + userId + KEY_PREFIX + entityType;
        return followeeKey;
    }

    // 生成实体被关注的key
    public static String getFollowerKey(int entityType, int entityId){
        String followerKey = FOLLOWER_KEY_PREFIX + KEY_PREFIX + entityType + KEY_PREFIX + entityId;
        return followerKey;
    }

    // 生成kaptchakey
    public static String getKaptchaKey(String randomString){
        String kaptchKey = KAPTCHA_KEY + KEY_PREFIX + randomString;
        return kaptchKey;
    }

    // 生成登录凭证key
    public static String getLoginTicketKey(String randomString){
        String ticketKey = TICKET_KEY + KEY_PREFIX + randomString;
        return ticketKey;
    }

    // 生成user key
    public static String getUserKey(int userid){
        String userKey = USER_KEY + KEY_PREFIX + userid;
        return userKey;
    }

    // 单日UV
    public static String getUVKey(String date) {
        return PREFIX_UV + KEY_PREFIX + date;
    }

    // 区间UV
    public static String getUVKey(String startDate, String endDate) {
        return PREFIX_UV + KEY_PREFIX + startDate + KEY_PREFIX + endDate;
    }

    // 单日活跃用户
    public static String getDAUKey(String date) {
        return PREFIX_DAU + KEY_PREFIX + date;
    }

    // 区间活跃用户
    public static String getDAUKey(String startDate, String endDate) {
        return PREFIX_DAU + KEY_PREFIX + startDate + KEY_PREFIX + endDate;
    }

    // 帖子分数
    public static String getPostScoreKey() {
        return PREFIX_POST + KEY_PREFIX + "score";
    }
}
