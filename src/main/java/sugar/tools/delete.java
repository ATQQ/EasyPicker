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
            return true;
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

    /**
     * 删除指定目录所有的文件及子目录文件
     * @param path
     * @return
     */
    public static boolean deleteDir(String path){
        System.out.println(path);
        File file=new File(path);
        if(file.isDirectory()){
            String[] fileList = file.list();
            for(int i=0;i<fileList.length;i++){
                boolean success=deleteDir(path+"/"+fileList[i]);
            }
        }
        return file.delete();
    }
}
