package com.tencent.community.util;

public interface CommunityConstant {

    /*
        激活状态 常量
     */
    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_FAILURE = 1;
    int ACTIVATION_REPEAT = 2;

    /*
        凭证有效时长常量
     */
    int DEFAULT_EXPIRED_SECONDS= 3600 * 12;
    int LONG_EXPIRED_SECONDS = 3600 * 24 * 30;

    /*
        帖子类型 常量
        1，表示帖子评论
        2， 表示评论的回复
        3. 表示用户实体
     */
    int ENTITY_TYPE_COMMENT = 1;
    int ENTITY_TYPE_REPLY = 2;
    int ENTITY_TYPE_USER = 3;

    /*
        topic 类型
     */
    String TOPIC_COMMENT = "comment";
    String TOPIC_LIKE = "like";
    String TOPIC_FOLLOW = "follow";
    String TOPIC_PUBLISH = "publish";
    String TOPIC_DELETE = "delete";
    String TOPIC_SHARE = "share";
    /*
        系统用户id
     */
    int SYSTEM_USER_ID = 1;

    /*
        用户权限
     */
    String AUTHORITY_USER = "user";
    String AUTHORITY_ADMIN = "admin";
    String AUTHORITY_MODERATOR = "moderator";
}
