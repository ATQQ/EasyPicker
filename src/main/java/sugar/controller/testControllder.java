package sugar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import sugar.bean.testUser;

/*
 *@auther suger
 *2019
 *11:03
 */
@RestController
@RequestMapping(value = "test")

public class testControllder {
    @RequestMapping(value = "picker/{account}")
    public String testRedirect(@PathVariable final String account){
        System.out.println(account);
        return "index";
    }

    @RequestMapping(value = "testuser")
    @ResponseBody
    public String testPost(testUser record){
        System.out.println(record);
        return "success";
    }

    @RequestMapping(value = "testuser2")
    @ResponseBody
    public String testPost2(@RequestBody String test){
        System.out.println(test);
        return "success";
    }
}
