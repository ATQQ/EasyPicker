package sugar.controller;
/*
 *@auther suger
 *2019
 *17:01
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sugar.bean.User;
import sugar.service.userService;
import sugar.tools.commonFun;
import sugar.tools.randomString;
import sugar.tools.tokenUtil;

import javax.servlet.http.HttpSession;
import sugar.tools.encryption;
@Controller
@RequestMapping(value = "user",produces = "application/json;charset=utf-8")
public class userController {

    @Autowired
    private userService userService;

    /**
     * 用户登录
     * @param user
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "login",method = RequestMethod.POST)
    public String login(@RequestBody User user,HttpSession httpSession) throws Exception {
        JSONObject jsonObject=new JSONObject();
        //查询用户信息
        User checkUser = userService.checkUser(user.getUsername());
        if(checkUser==null){//没有查询到此用户
            jsonObject.put("status",-1);
            jsonObject.put("errmsg","用户不存在");
        }else if (checkUser.getPassword().equals(encryption.getAfterData(user.getPassword()))){//密码正确
            jsonObject.put("status",1);
            //存放附加数据
            JSONObject t=new JSONObject();
            //返回验证凭据
            String token=tokenUtil.getInstance().makeToken();
            t.put("token",token);
            httpSession.setAttribute("token",token);
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

    @ResponseBody
    @RequestMapping(value = "check",method = RequestMethod.POST)
    public boolean checkUser(@RequestBody User user){
        User checkUser = userService.checkUser(user.getUsername());
        if(checkUser==null){
            return false;
        }else {
            return true;
        }
    }

    /**
     * 新增用户
     * @param user
     * @param code 验证码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "user",method = RequestMethod.POST)
    public String register(User user,@RequestParam(required = false) String code,HttpSession session) throws Exception {
        JSONObject res=new JSONObject();
        Integer resCode=200;
        String resMsg="OK";

        //密码加密
        user.setPassword(encryption.getAfterData(user.getPassword()));
        //判断是否需要绑定了手机号
        if(user.getMobile()!=null&&code!=null&&user.getMobile().length()==11&&code.length()==4){
            JSONObject verifyMsg = JSON.parseObject((String) session.getAttribute("verifyMsg"));
            //判断验证码是否正确
            if(!user.getMobile().equals(verifyMsg.getString("mobile"))||!code.equals(verifyMsg.getString("code"))){
                //验证码错误
                resCode=403;
            }else {
                resCode=userService.addUser(user);
            }
        }else {
            user.setMobile(null);
            resCode=userService.addUser(user);
        }


        switch (resCode){
            case 401:
                resMsg="Account already exist";
                break;
            case 402:
                resMsg="Mobile already exist";
                break;
            case 403:
                resMsg="Error code";
                break;
            case 200:
                resMsg="OK";
                break;
            default:
                resCode=555;
                resMsg="unKnown Error";
                break;
        }
        return commonFun.res(resCode,null,resMsg);
    }

    /**
     * 获取验证码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getCode",method = RequestMethod.GET)
    public String getCode(String mobile,HttpSession session){
        String code= randomString.getRandomNumberStr(4);
        System.out.println(code);
        System.out.println(mobile);
        JSONObject data=new JSONObject();
        if(mobile==null||mobile.length()!=11){
            return commonFun.res(401,null,"手机号格式错误");
        }
        JSONObject verifyMsg=new JSONObject();
        verifyMsg.put("code",code);
        verifyMsg.put("mobile",mobile);
        session.setAttribute("verifyMsg",verifyMsg.toJSONString());
        return commonFun.res(200,null,"获取成功");
    }
}
