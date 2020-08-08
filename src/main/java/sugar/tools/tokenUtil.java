package sugar.tools;
/*
 *@auther suger
 *2019
 *17:49
 */

import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.util.Random;

public class tokenUtil {

    private tokenUtil(){

    }

    private static final tokenUtil instance=new tokenUtil();

    public static tokenUtil getInstance(){
        return instance;
    }

    public String makeToken(){
        String token=(System.currentTimeMillis()+new Random().nextInt(666666))+"";
        try{
            MessageDigest md=MessageDigest.getInstance("md5");
            byte md5[]=md.digest(token.getBytes());

            BASE64Encoder encoder=new BASE64Encoder();
            return encoder.encode(md5);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
