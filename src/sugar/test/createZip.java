package sugar.test;
/*
 *@auther suger
 *2019
 *18:38
 */

import org.junit.Test;

import java.io.*;
import java.util.zip.ZipOutputStream;

import sugar.tools.compressFile;

public class createZip {

    @Test
    public void createZip(){
        String rootPath="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\upload";
        String ndir="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\upload\\sugar";
//        System.out.println(rootPath);
        File targetFile=new File("666.zip");
        File dir=new File(ndir);
        try {
            ZipOutputStream outputStream=new ZipOutputStream(new FileOutputStream(targetFile));
            compressFile.compressDitToZip(rootPath,dir,outputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Test
    public  void myTest()throws Exception{
//        String rootPath="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\upload";
        String rootPath="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\upload\\sugar";
        compressFile.compressDitToZip(rootPath,rootPath+"/../66.zip");
    }
}
