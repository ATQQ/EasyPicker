package sugar.controller;
/*
 *@auther suger
 *2019
 *20:58
 */

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import sugar.tools.HttpRequest;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("api")
public class ApiController {
    @ResponseBody
    @RequestMapping(value = "get/shortLink",produces = "application/json;charset=utf-8")
    public String getShortLink(String url){
        System.out.println(url);
        CharSequence httpUrl="http://api.ft12.com/api.php";
        Map<String,String> params=new HashMap<String, String>();
        params.put("format","json");
        params.put("apikey","Xy14ryO1ZjDGVgx3ZE@ddd");
        params.put("url",url);
        HttpRequest httpRequest = HttpRequest.post(httpUrl, params, false);
        return httpRequest.body("utf-8");
    }
}
