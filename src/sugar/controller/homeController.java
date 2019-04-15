package sugar.controller;
/*
 *@auther suger
 *2019
 *16:47
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("home")
@Controller
public class homeController {

//    @RequestMapping("")
//    public String loadNull(){
//        return "index";
//    }
    /**
     * 转到默认页面
     * @return
     */
    @RequestMapping("/")
    public String loadIndex(){
        return "index";
    }

    /**
     * 转到指定管理员的页面
     * @return
     */
    @RequestMapping("/{account}")
    public String loadUserIndex(){
        return "index";
    }
}
