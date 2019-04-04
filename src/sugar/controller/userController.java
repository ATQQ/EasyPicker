package sugar.controller;
/*
 *@auther suger
 *2019
 *17:01
 */

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sugar.bean.User;
import sugar.service.userService;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping(value = "user",produces = "application/json;charset=utf-8")
public class userController {

    @Autowired
    private userService userService;

    @Autowired
    private HttpSession httpSession;

    @ResponseBody
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(@RequestBody User user){
        JSONObject jsonObject=new JSONObject();
        //查询用户信息
        User checkUser = userService.checkUser(user.getUsername());
        if(checkUser==null){//没有查询到此用户
            jsonObject.put("status",-1);
            jsonObject.put("errmsg","用户不存在");
        }else if (checkUser.getPassword().equals(user.getPassword())){//密码正确
            jsonObject.put("status",1);
            //存放附加数据
            JSONObject t=new JSONObject();
            //返回验证凭据
            t.put("token",httpSession.getId());
            //用户状态
            t.put("status",checkUser.getStatus());
            //用户权限
            t.put("power",checkUser.getPower());

            jsonObject.put("data",t);
            //当前用户存入session
            httpSession.setAttribute("nowUser",checkUser);
        }else{//密码错误
            jsonObject.put("status",0);
            jsonObject.put("errmsg","密码错误");
        }
        return jsonObject.toJSONString();
    }
}
