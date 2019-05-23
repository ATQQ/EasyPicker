package sugar.test;
/*
 *@auther suger
 *2019
 *22:35
 */
import sugar.tools.delete;
import org.junit.Test;

public class deleteTest {

    @Test
    public void testDelDir(){
        String path="D:\\documents\\study\\documents\\IDEAProject\\reportsPicker\\out\\artifacts\\upload\\admin\\C#程序设计基础\\课程2_Template";
        delete.deleteDir(path);
    }
}
