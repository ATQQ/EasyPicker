package sugar.tools;

import java.io.File;

/**
 * @author sugar
 * 2020/5/9
 * 下午10:43
 * 文件操作的工具类
 */

public class FileUtil {
    /**
     * 判断文件是否存在
     * @param path
     * @return
     */
    public static boolean isExist(String path){
        return new File(path).exists();
    }

    /**
     * 判断目录是否存在(不存在则创建)
     * @param path
     */
    public static boolean dirIsExist(String path){
        File t = new File(path);
        if(!t.exists()){
            t.mkdirs();
            return false;
        }
        return true;
    }
}
