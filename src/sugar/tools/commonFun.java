package sugar.tools;
/*
 *@auther suger
 *2019
 *20:32
 */

import com.alibaba.fastjson.JSONObject;

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
}
