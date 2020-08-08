package sugar;
/*
 *@auther suger
 *2019
 *3:02
 */

import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;

public class testMd5 {

    @Test
    public void test1() throws Exception {
        MessageDigest md=MessageDigest.getInstance("md5");
        String testStr="2019614";
        byte[] bytes = md.digest(testStr.getBytes());
        BASE64Encoder encoder=new BASE64Encoder();
        System.out.println(new String(encoder.encode(bytes)));
    }
}
