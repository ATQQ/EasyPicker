package sugar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

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
}
