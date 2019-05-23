package sugar.test;
/*
 *@auther suger
 *2019
 *18:27
 */

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sugar.service.userService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath*:config/applicationContext_*.xml")
public class serviceTest {

    @Autowired
    private userService userService;

    @Test
    public void test1(){
        System.out.println(userService.checkUser("admin"));
        System.out.println("ssss");
    }

}
