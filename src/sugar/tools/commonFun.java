package sugar.tools;
/*
 *@auther suger
 *2019
 *20:32
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class commonFun {

    /**
     * ajax请求回调内容模板
     * @param code 状态码
     * @param data 回调内容
     * @param errMsg 错误信息内容
     * @return
     */
    public static String res(Integer code, JSONObject data,String errMsg){
        JSONObject res=new JSONObject();
        res.put("code",code);
        res.put("data",data);
        res.put("errMsg",errMsg);
        return res.toJSONString();
    }

    /**
     * 发送验证码
     * @param mobile
     * @return
     */
    public static Integer sendCode(String mobile,String code){
        System.out.println(code);
        System.out.println(mobile);
        Map<String,Object> params=new HashMap<String, Object>();
        params.put("mobile",mobile);
        params.put("tpl_id",162172);
        params.put("tpl_value","#code#="+code);
        params.put("key","03ba30b3e30513481849e9e228003fa5");
        CharSequence baseUrl="http://v.juhe.cn/sms/send";
        HttpRequest post = HttpRequest.post(baseUrl ).form(params);

        JSONObject res = JSON.parseObject(post.body("utf-8"));

        return res.getInteger("error_code");

//        return (int) Math.round(Math.random()*100)%2;
    }
}
