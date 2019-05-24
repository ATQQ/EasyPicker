package sugar.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sugar.bean.Peoplelist;
import sugar.service.peopleListService;

import java.util.List;

/**
 * @auther suger
 *2019
 *18:57
 */
@Controller
@RequestMapping(value = "people",produces = "application/json;charset=utf-8")
public class peopleListController {

    @Autowired
    private peopleListService peopleListService;


    @RequestMapping(value = "peopleList",method = RequestMethod.GET)
    @ResponseBody
    public String checkPeopleList(@RequestParam("username") String adminUsername,@RequestParam("parent") String parentName,@RequestParam("child") String childName){
        JSONObject res=new JSONObject();
        try{
            List<Peoplelist> allDataByAdmin = peopleListService.getAllDataByAdmin(adminUsername, parentName, childName);
            //去除id
            JSONArray datas=new JSONArray();
            for (Peoplelist key:
                    allDataByAdmin
                 ) {
                JSONObject tempVal=new JSONObject();
                key.setId(null);
                tempVal.put("name",key.getPeopleName());
                tempVal.put("status",key.getStatus());
                tempVal.put("date",key.getLastDate()==null?false:key.getLastDate());
                datas.add(tempVal);
            }
            res.put("datas",datas);
            res.put("status",true);
        }catch (Exception e){
            res.put("status",false);
            e.printStackTrace();
        }

        return res.toJSONString();
    }
}
