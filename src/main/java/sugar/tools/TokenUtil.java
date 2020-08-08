package sugar.tools;


import sugar.bean.User;
import sugar.exception.ServiceException;

/**
 * @author sugar
 * 2019/10/31
 * 18:48
 * 身份令牌工具类
 */

public class TokenUtil {

    /**
     * 加密秘钥
     */
    private final static String KEY = "swpuKerno";

    /**
     * 返回给前端的token长度
     */
    private final static int LENGTH = 40;

    /**
     * 生成令牌
     *
     * @param user    用户信息
     * @param timeout 过期时间(s)
     */
    public static String create(User user, Integer timeout) throws Exception {
        String token = DesUtil.encrypt(JsonUtil.objToStr(user), KEY);
        LocalCache localCache = new LocalCache();
        String userToken = token.substring(0, LENGTH);
        localCache.putValue(userToken, token, timeout);
        return userToken;
    }

    /**
     * 判断令牌是否过期
     *
     * @param userToken 用户令牌
     */
    public static Boolean isExpire(String userToken) {
        LocalCache localCache = new LocalCache();
        return localCache.getValue(userToken) == null;
    }

    /**
     * 根据令牌获取存入的用户信息
     *
     * @param userToken 用户令牌
     */
    public static User getUser(String userToken) throws Exception {
        if (!isExpire(userToken)) {
            LocalCache localCache = new LocalCache();
            String enToken = (String) localCache.getValue(userToken);
            String token = DesUtil.decrypt(enToken, KEY);
            return (User) JsonUtil.strToObj(token, User.class);
        }

        throw new ServiceException("token is expire", 500);

    }

    /**
     * 主动销毁令牌
     * @param token
     */
    public static void destroyToken(String token){
        LocalCache localCache = new LocalCache();
        localCache.putValue(token,"",0);
    }
}
