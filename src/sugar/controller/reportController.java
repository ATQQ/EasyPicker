package sugar.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import sugar.bean.Report;
import sugar.service.reportService;

import java.util.Date;

/*
 *@auther suger
 *2019
 *23:13
 */
@Controller
@RequestMapping("report")
public class reportController {

    @Autowired
    private reportService reportService;

    @RequestMapping(value = "save",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addReport(@RequestBody Report report){
        JSONObject jsonObject=new JSONObject();
        report.setDate(new Date());
        reportService.addReport(report);
        jsonObject.put("status",1);
        return jsonObject.toJSONString();
    }
}
