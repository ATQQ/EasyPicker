package sugar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/*
 *@auther suger
 *2019
 *22:49
 */
@Controller
public class adminController {

    @RequestMapping("/admin")
    public String loadAdminHome(){
        System.out.println("admin");
        return "admin/admin";
    }
}
