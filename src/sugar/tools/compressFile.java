package sugar.tools;
/*
 *@auther suger
 *2019
 *19:19
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class compressFile {

    public static void compressDitToZip(String baseDir, File dir, ZipOutputStream out) {
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();//列出该目录下所有文件
            System.out.println(files.length);
            if (files.length == 1) {
                //如果文件夹为空
                ZipEntry entry = new ZipEntry("test");
                try {
                    out.putNextEntry(entry);
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 压缩指定文件夹下所有的文件(文件夹中不包含子文件夹)
     *
     * @param baseFolder     带解压的文件夹
     * @param targetFileName 生成的压缩包文件的路径
     */
    public static void compressDitToZip(String baseFolder, String targetFileName) throws Exception {
        File targetFile = new File(targetFileName);
        File directory = new File(baseFolder);
        File[] files = directory.listFiles();
        FileInputStream in = null;
        byte[] buffer = new byte[4096];
        int byte_read;
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(targetFile));
        if (files.length != 0) {
            for (File key : files
            ) {
                if(!key.isDirectory()){
                    in = new FileInputStream(key);
                    ZipEntry entry = new ZipEntry(key.getName());
                    out.putNextEntry(entry);
                    while ((byte_read = in.read(buffer)) != -1) {
                        out.write(buffer);
                    }
                }
            }
        }
        out.close();
        in.close();
    }
}
