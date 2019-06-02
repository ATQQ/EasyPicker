package sugar.tools;
/*
 *@auther suger
 *2019
 *14:07
 */

public class randomString {
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
