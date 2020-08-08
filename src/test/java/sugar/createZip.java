package sugar;
/*
 *@auther suger
 *2019
 *18:38
 */

import com.qiniu.common.QiniuException;
import org.junit.Test;
import sugar.tools.FileUtil;
import sugar.tools.QiNiuUtil;
import sugar.tools.compressFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.zip.ZipOutputStream;

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

    @Test
    public void checkFilesCount() throws QiniuException {
        System.out.println(FileUtil.fileCount("/home/sugar/Downloads/tomcat9/webapps/upload/test2/test2/good_Template"));
        System.out.println(QiNiuUtil.getFileCount("test2/test2/good/"));
    }

    @Test
    public void qiniuZip() throws QiniuException {
        String url = QiNiuUtil.makeZip("test2/test2/good/","test2");
        while (QiNiuUtil.getMakeCode(url).getInteger("code")!=0){
            System.out.println("no");
        }
    }
}
