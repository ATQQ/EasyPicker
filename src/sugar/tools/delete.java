package sugar.tools;
/*
 *@auther suger
 *2019
 *23:26
 */

import java.io.File;

public class delete {
    /**
     * 删除指定路径的文件
     * @param path 待删除文件的路径
     * @return boolean
     */
    public static boolean deleteFile(String path){
        File file =new File(path);
        if (!file.exists()){
            System.out.println("路径不存在");
            return false;
        }else{
            file.delete();
            if(!file.exists())
            System.out.println("删除成功");
            else {
                System.out.println("删除失败");
            }
            return true;
        }
    }
}
