package sugar.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sugar.bean.Childcontent;
import sugar.service.childcontentService;
import sugar.tools.commonFun;

import java.util.Date;

/*
 *@auther suger
 *2019
 *10:38
 */
@Controller
@RequestMapping(value = "childContent",produces = "application/json;charset=utf-8")
public class childcontentController {

    @Autowired
    private childcontentService childcontentService;

    /**
     * 获取子类附加内容
     * @param taskid
     * @return
     */
    @RequestMapping(value = "childContent",method = RequestMethod.GET)
    @ResponseBody
    public String getDataByTaskid(Integer taskid){
        JSONObject res=new JSONObject();
        Childcontent childcontent = childcontentService.checkDataByTaskid(taskid);
        if(childcontent==null){
            return commonFun.res(20050,null,"无附加属性");
        }else{
            res.put("ddl",childcontent.getDdl()==null?false:childcontent.getDdl());
            res.put("template",childcontent.getTemplate()==null?false:childcontent.getTemplate());
            res.put("people",childcontent.getPeople()==null?false:childcontent.getPeople());
            return commonFun.res(200,res,"OK");
        }
    }

    /**
     * 更改子类附加内容
     * @param data
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "childContext",method = RequestMethod.PUT)
    public String updateDataByType(@RequestBody String data){
        Childcontent record=new Childcontent();
        JSONObject submit= JSON.parseObject(data);
//        设置任务id
        record.setTasksid(submit.getInteger("taskid"));
        Integer type=submit.getInteger("type");
        switch (type){
            case 1:
                record.setDdl(submit.getDate("ddl"));
                break;
            case 2:
                record.setPeople(submit.getString("people"));
                break;
            case 3:
                record.setTemplate(submit.getString("template"));
                break;
                default:
                    break;
        }


        if(childcontentService.updateData(record,type)){
            return commonFun.res(200,null,"OK");
        }else{
            return commonFun.res(20051,null,"参数错误");
        }
    }
}
