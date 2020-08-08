package sugar.tools;
/*
 *@auther suger
 *2019
 *10:09
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class testHttp {
    @Test
    public void test1() throws Exception{
        URL url=new URL("http://localhost:8888/EasyPicker/course/check?range=parents&contentid=-1&username=admin");
        HttpRequest httpRequest = HttpRequest.get(url);
        String body = httpRequest.body("utf-8");
        System.out.println(body);
    }

    @Test
    public void test2()throws Exception{
        CharSequence baseUrl="http://localhost:8888/EasyPicker/course/check";
        Map params=new HashMap();
        params.put("range","parents");
        params.put("contentid",-1);
        params.put("username","admin");

        HttpRequest httpRequest = HttpRequest.get(baseUrl, params, true);

        System.out.println(HttpRequest.get(new URL(httpRequest.toString().split(" ")[1])).body());
    }

    @Test
    public void test3(){
        CharSequence baseUrl="http://localhost:8888/EasyPicker/test/testuser";
        Map params=new HashMap();
       params.put("name","小明");
       params.put("age",2);
        HttpRequest post = HttpRequest.post(baseUrl).form(params);
        System.out.println(post.body());
    }

    @Test
    public void testSendCode(){
        String code=randomString.getRandomNumberStr(4);
        System.out.println(code);
        Map<String,Object> params=new HashMap<String, Object>();
        params.put("mobile","15196520474");
        params.put("tpl_id",162172);
        params.put("tpl_value","#code#="+code);
        params.put("key","03ba30b3e30513481849e9e228003fa5");
        CharSequence baseUrl="http://v.juhe.cn/sms/send";
        HttpRequest post = HttpRequest.post(baseUrl ).form(params);
        System.out.println(post.toString());
        String body = post.body("utf-8");
        System.out.println(body);
    }

    @Test
    public void testFastJSON(){
        String testStr="{\"name\":\"小明\"}";
        JSONObject test=JSON.parseObject(testStr);
        System.out.println(test.getString("name"));
//        test.get
    }

    @Test
    public void testWxSend(){
        CharSequence baseUrl="https://student.wozaixiaoyuan.com/sign/doSign.json";
        HttpRequest httpRequest = HttpRequest.get(baseUrl);
        Map<String,Object> params=new HashMap<>();
        params.put("id","201731061422");
        params.put("longitude",123);
        params.put("latitude",3232);
        params.put("type",0);

        System.out.println(httpRequest.body());
    }

    @Test
    public void testGetHL(){
        CharSequence url ="http://web.juhe.cn:8080/finance/exchange/rmbquot?key=66f93391c110a4ad0391d405212a8d6c";//请求接口地址
        HttpRequest res = HttpRequest.get(url);
//        System.out.println(res.body("utf-8"));
        JSONObject resBody=JSON.parseObject(res.body("utf-8"));

    }

    @Test
    public void testGetShortLink() throws UnsupportedEncodingException {
        CharSequence url="http://api.ft12.com/api.php";
        Map<String,String> params=new HashMap<String, String>();
        params.put("format","json");
        params.put("apikey","Xy14ryO1ZjDGVgx3ZE@ddd");
        params.put("url","http://sugarat.top/EasyPicker/home/admin?parent=C桌面应用&child=实验8泛型的定义及其实现");

        HttpRequest httpRequest = HttpRequest.post(url, params, true);
        System.out.println(httpRequest.body("utf-8"));
        System.out.println(httpRequest.toString());
    }
}
