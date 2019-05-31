package sugar.tools;
/*
 *@auther suger
 *2019
 *10:09
 */

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.net.URL;
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
}
