package sugar.tools;

/**
 * @author sugar
 * 2019/11/1
 * 上午9:29
 * 生成随机字符串
 */

public class RandomUtil {

    private static int getRandom(int count) {
        return (int) Math.round(Math.random() * (count));
    }

    private static String string = "abcdefghijklmnopqrstuvwxyz";

    private static String number="0123456789";
    public static String getRandomString(int length) {
        StringBuffer sb = new StringBuffer();
        int len = string.length();
        for (int i = 0; i < length; i++) {
            sb.append(string.charAt(getRandom(len - 1)));
        }
        return sb.toString();
    }

    public static String getRandomNumberStr(int length){
        StringBuffer sb=new StringBuffer();
        int len=number.length();
        for(int i=0;i<length;i++){
            sb.append(number.charAt(getRandom(len-1)));
        }
        return sb.toString();
    }
}
