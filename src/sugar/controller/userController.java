package sugar.controller;
/*
 *@auther suger
 *2019
 *17:01
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sugar.bean.User;
import sugar.service.userService;
import sugar.tools.commonFun;
import sugar.tools.randomString;
import sugar.tools.tokenUtil;

import javax.servlet.http.HttpSession;
import sugar.tools.encryption;

import static sugar.tools.commonFun.sendCode;

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
            if(session.getAttribute("verifyMsg")==null){
                resCode=403;
            }else {
                JSONObject verifyMsg = JSON.parseObject((String) session.getAttribute("verifyMsg"));
                //判断验证码是否正确
                if(!user.getMobile().equals(verifyMsg.getString("mobile"))||!code.equals(verifyMsg.getString("code"))){
                    //验证码错误
                    resCode=403;
                }else {
                    resCode=userService.addUser(user);
                }
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
                //移除当前的认证信息
                session.removeAttribute("verifyMsg");
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

        JSONObject data=new JSONObject();
        if(mobile==null||mobile.length()!=11){
            return commonFun.res(401,null,"手机号格式错误");
        }
        Integer senCode = sendCode(mobile, code);
        if(senCode!=0){
            return commonFun.res(666,null,"短信欠费了,咱不能绑定手机,请联系网站管理员");
        }
        JSONObject verifyMsg=new JSONObject();
        verifyMsg.put("code",code);
        verifyMsg.put("mobile",mobile);
        session.setAttribute("verifyMsg",verifyMsg.toJSONString());
        return commonFun.res(200,null,"获取成功");
    }


    /**
     * 更新用户手机号码或者重置密码
     * @param code
     * @param user
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "update/{code}",method = RequestMethod.PUT)
    public String updateUser(@PathVariable(name = "code") String code,@RequestBody User user,HttpSession session){
        String errMsg="";
        Integer errCode=400;
        if(session.getAttribute("verifyMsg")==null||code==null){
            errCode=400;
        }else {
            JSONObject json  =JSON.parseObject( (String) session.getAttribute("verifyMsg"));
            if(json.getString("code").equals(code)&&user.getMobile().equals(json.getString("mobile"))){
                errCode = userService.updateUser(user);
            }else {
                errCode=401;
            }
        }
        switch (errCode){
            case 401:
                errMsg="验证码错误";
                break;
            case 400:
                errMsg="请求状态异常";
                break;
            case 200:
                session.removeAttribute("verifyMsg");
                errMsg="OK";
                break;
            case 404:
                errMsg="手机号不存在";
                break;
            case 405:
                errMsg="手机号已存在";
                break;
                default:
                    break;
        }
        return commonFun.res(errCode,null,errMsg);
    }
}
