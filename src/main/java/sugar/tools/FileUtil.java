package sugar.tools;

import java.io.File;
import java.util.Objects;

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

    /**
     * 获取目录中的文件数量
     * @param path
     * @return
     */
    public static long fileCount(String path){
        File t = new File(path);
        // 不存在，返回0
        if(!t.exists()){
            return 0;
        }
        // 不是目录，返回0
        if(!t.isDirectory()){
            return 0;
        }
        return Objects.requireNonNull(t.listFiles()).length - Objects.requireNonNull(t.listFiles(File::isDirectory)).length;
    }
}
