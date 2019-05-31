package sugar.tools;
/*
 *@auther suger
 *2019
 *3:12
 */

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class encryption {

    /**
     * 获取加密后的内容
     */
    public static String getAfterData(String waitEncode) throws Exception {
        MessageDigest md5=MessageDigest.getInstance("md5");
        byte[] bytes = md5.digest(waitEncode.getBytes());
        BASE64Encoder encoder=new BASE64Encoder();
        return encoder.encode(bytes);
    }
}
