package com.chatroom.util;

public final class RedisKeyUtil {

    private RedisKeyUtil() {
    }

    public static final String USER_PREFIX = "user:";
    public static final String USER_BY_USERNAME = "user:byUsername";
    public static final String USER_ID_COUNTER = "user:idCounter";

    public static final String SESSION_PREFIX = "session:";

    public static final String ONLINE_USERS_SET = "online:users";
    public static final String USER_STATUS_PREFIX = "user:status:";

    public static final String ROOM_MESSAGES_PREFIX = "room:";
    public static final String ROOM_MESSAGES_SUFFIX = ":messages";
    public static final String ROOM_META_SUFFIX = ":meta";

    public static final String MUTE_PREFIX = "mute:";
    public static final String KICK_PREFIX = "kick:";

    public static String userKey(String userId) {
        return USER_PREFIX + userId;
    }

    public static String sessionKey(String token) {
        return SESSION_PREFIX + token;
    }

    public static String userStatusKey(String userId) {
        return USER_STATUS_PREFIX + userId;
    }

    public static String roomMessagesKey(String roomId) {
        return ROOM_MESSAGES_PREFIX + roomId + ROOM_MESSAGES_SUFFIX;
    }

    public static String roomMetaKey(String roomId) {
        return ROOM_MESSAGES_PREFIX + roomId + ROOM_META_SUFFIX;
    }

    public static String muteKey(String roomId, String userId) {
        return MUTE_PREFIX + roomId + ":" + userId;
    }

    public static String kickKey(String roomId, String userId) {
        return KICK_PREFIX + roomId + ":" + userId;
    }
}
