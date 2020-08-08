package sugar.exception;
/*
 *@auther suger
 *2019
 *17:15
 */

import com.alibaba.fastjson.JSONObject;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

//捕获异常
@ControllerAdvice
public class globalException {

    /**
     * 捕获运行时异常
     * @param e
     * @return
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public String runtimeException(RuntimeException e){
        JSONObject res=new JSONObject();
        res.put("code",500);
        res.put("errMsg",e.getMessage());
        System.out.println(e.getMessage());
        e.printStackTrace();
        return res.toJSONString();
    }


    /**
     * 捕获自定义异常
     * @param e
     * @return
     */
    @ExceptionHandler(myException.class)
    @ResponseBody
    public String diyException(myException e){
        JSONObject res=new JSONObject();
        res.put("code",400);
        res.put("errMsg",e.getMsg());
        System.out.println(e.getMsg());
        e.printStackTrace();
        return res.toJSONString();
    }
}
