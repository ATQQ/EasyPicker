package sugar.tools;
/*
 *@auther suger
 *2019
 *14:16
 */

import java.text.SimpleDateFormat;
import java.util.Date;

public class getNowDate {
   public static String date(){
       SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");//设置日期格式
       String nowTime=df.format(new Date());
       System.out.println(nowTime);// new Date()为获取当前系统时间
       return nowTime;
   }
    public static String time(){
        SimpleDateFormat df = new SimpleDateFormat("HH-mm-ss");//设置日期格式
        String nowTime=df.format(new Date());
        System.out.println(nowTime);// new Date()为获取当前系统时间
        return nowTime;
    }

    public static String complete(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd,HH-mm-ss");//设置日期格式
        String nowTime=df.format(new Date());
        System.out.println(nowTime);// new Date()为获取当前系统时间
        return nowTime;
    }

    /**
     * 获取时间戳(时间连成的字符串)
     * @return
     */
    public static String timestamp(){
       SimpleDateFormat df =new SimpleDateFormat("yyyyMMddHHmmss");
       return df.format(new Date());
    }
}
