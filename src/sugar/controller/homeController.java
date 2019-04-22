package sugar.controller;
/*
 *@auther suger
 *2019
 *16:47
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class homeController {

    /**
     * 转到默认页面
     * @return
     */
    @RequestMapping("/home")
    public String loadIndex(){
        System.out.println("home");
        return "admin/home";
    }

    /**
     * 转到指定管理员的分享页面的
     * @return
     */
    @RequestMapping("/home/{account}")
    public String loadUserIndex(){
        return "admin/index";
    }


}
