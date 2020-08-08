package sugar.controller;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sugar.bean.Peoplelist;
import sugar.service.peopleListService;
import sugar.tools.commonFun;
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


    /**
     * 获取限制人员名单列表
     * @param adminUsername
     * @param parentName
     * @param childName
     * @return
     */
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
                tempVal.put("id",key.getId());
                tempVal.put("name",key.getPeopleName());
                tempVal.put("status",key.getStatus());
                tempVal.put("date",key.getLastDate()==null?false:key.getLastDate());
                datas.add(tempVal);
            }
            res.put("data",datas);
            res.put("code",200);
        }catch (Exception e){
            res.put("code",201);
            e.printStackTrace();
        }

        return res.toJSONString();
    }


    /**
     * 提交用户查询个人提交情况
     * @param adminUsername
     * @param parentName
     * @param childName
     * @param peopleName
     * @return
     */
    @RequestMapping(value = "people",method = RequestMethod.GET)
    @ResponseBody
    public String checkPeople(@RequestParam("username") String adminUsername,
                              @RequestParam("parent") String parentName,
                              @RequestParam("child") String childName,
                              @RequestParam("name")String peopleName){
        JSONObject res=new JSONObject();
        Peoplelist record=new Peoplelist();
        record.setParentName(parentName);
        record.setChildName(childName);
        record.setPeopleName(peopleName);
        record.setAdminUsername(adminUsername);
        record = peopleListService.checkPeopleStatus(record);
        //没有记录
        if(record==null){
            return commonFun.res(20030,null,"用户不在提交名单中");
        }

        Boolean isSubmit=record.getStatus()==1;
        res.put("isSubmit",isSubmit);
        return commonFun.res(200,res,isSubmit?"已提交":"未提交");
    }


    /**
     * 从名单中移除指定人员
     * @param record
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "people",method = RequestMethod.DELETE)
    public String deletePeople(@RequestBody Peoplelist record){
        Boolean isSuccess = peopleListService.deletePeopleByPrimaryKey(record.getId());
        int code=200;
        if(!isSuccess){
            code=201;
        }
        JSONObject res=new JSONObject();
        res.put("code",code);
        res.put("errMsg","");
        return res.toJSONString();
    }
}
