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
import sugar.tools.encryption;
import sugar.tools.randomString;
import sugar.tools.tokenUtil;

import javax.servlet.http.HttpSession;

import static sugar.tools.commonFun.sendCode;

@Controller
@RequestMapping(value = "user", produces = "application/json;charset=utf-8")
public class userController {

    @Autowired
    private userService userService;

    /**
     * 用户登录
     *
     * @param user
     * @param httpSession
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(@RequestBody User user, HttpSession httpSession) throws Exception {
        //查询用户信息
        User checkUser = userService.checkUser(user.getUsername());
        //没有查询到此用户
        if (checkUser == null) {
            return commonFun.res(20010, null, "用户不存在");
        }
        //密码正确
        else if (checkUser.getPassword().equals(encryption.getAfterData(user.getPassword()))) {
            //存放附加数据
            JSONObject data = new JSONObject();
            //返回验证凭据
            String token = tokenUtil.getInstance().makeToken();
            data.put("token", token);
            httpSession.setAttribute("token", token);
            //用户状态
            data.put("status", checkUser.getStatus());
            //用户权限
            data.put("power", checkUser.getPower());

            //当前用户存入session
            httpSession.setAttribute("nowUser", checkUser);
            return commonFun.res(200, data, "登录成功");
        } else {//密码错误
            return commonFun.res(20011, null, "密码错误");
        }
    }

    /**
     * 查询用户账号是否存在
     *
     * @requestParam username 用户名
     */
    @ResponseBody
    @RequestMapping(value = "check", method = RequestMethod.POST)
    public boolean checkUser(@RequestBody User user) {
        User checkUser = userService.checkUser(user.getUsername());
        if (checkUser == null) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 新增用户
     * @param code 验证码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "user", method = RequestMethod.POST)
    public String register(@RequestBody String requestBody, HttpSession session) throws Exception {
        Integer resCode;
        String resMsg;
        JSONObject jsonData=JSON.parseObject(requestBody);
        User user=new User();
        String code=jsonData.getString("code");
        user.setUsername(jsonData.getString("username"));
        user.setPassword(jsonData.getString("password"));
        user.setMobile(jsonData.getString("mobile"));
        System.out.println(user);
        System.out.println(code);
        //密码加密
        user.setPassword(encryption.getAfterData(user.getPassword()));
        //判断是否需要绑定手机号
        if (user.getMobile() != null && code != null && user.getMobile().length() == 11 && code.length() == 4) {
            if (session.getAttribute("verifyMsg") == null) {
                resCode = 20020;
            } else {
                JSONObject verifyMsg = JSON.parseObject((String) session.getAttribute("verifyMsg"));
                //判断验证码是否正确
                if (!user.getMobile().equals(verifyMsg.getString("mobile")) || !code.equals(verifyMsg.getString("code"))) {
                    //验证码错误
                    resCode = 20020;
                } else {
                    resCode = userService.addUser(user);
                }
            }
        } else {
            user.setMobile(null);
            resCode = userService.addUser(user);
        }

        switch (resCode) {
            case 20013:
                resMsg = "Account already exist";
                break;
            case 20012:
                resMsg = "Mobile already exist";
                break;
            case 20020:
                resMsg = "Error code";
                break;
            case 200:
                //移除当前的认证信息
                session.removeAttribute("verifyMsg");
                resMsg = "OK";
                break;
            default:
                resCode = 555;
                resMsg = "unKnown Error";
                break;
        }
        return commonFun.res(resCode, null, resMsg);
    }

    /**
     * 获取验证码
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "getCode", method = RequestMethod.GET)
    public String getCode(String mobile, HttpSession session) {
        String code = randomString.getRandomNumberStr(4);

        if (mobile == null || mobile.length() != 11) {
            return commonFun.res(20021, null, "手机号格式错误");
        }
        Integer senCode = sendCode(mobile, code);
        if (senCode != 0) {
            return commonFun.res(20022, null, "短信欠费了,咱不能绑定手机,请联系网站管理员");
        }
        JSONObject verifyMsg = new JSONObject();
        verifyMsg.put("code", code);
        verifyMsg.put("mobile", mobile);
        session.setAttribute("verifyMsg", verifyMsg.toJSONString());
        return commonFun.res(200, null, "获取成功");
    }


    /**
     * 更新用户手机号码或者重置密码
     *
     * @param code
     * @param user
     * @param session
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "update/{code}", method = RequestMethod.PUT)
    public String updateUser(@PathVariable(name = "code") String code, @RequestBody User user, HttpSession session) {
        String errMsg = "";
        Integer errCode;
        if (session.getAttribute("verifyMsg") == null || code == null) {
            errCode = 20023;
        } else {
            JSONObject json = JSON.parseObject((String) session.getAttribute("verifyMsg"));
            if (json.getString("code").equals(code) && user.getMobile().equals(json.getString("mobile"))) {
                errCode = userService.updateUser(user);
            } else {
                errCode = 20020;
            }
        }
        switch (errCode) {
            case 20020:
                errMsg = "验证码错误";
                break;
            case 20023:
                errMsg = "请求状态异常";
                break;
            case 200:
                session.removeAttribute("verifyMsg");
                errMsg = "OK";
                break;
            case 20014:
                errMsg = "手机号不存在";
                break;
            case 20012:
                errMsg = "手机号已存在";
                break;
            default:
                break;
        }
        return commonFun.res(errCode, null, errMsg);
    }
}
