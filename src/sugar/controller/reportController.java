package sugar.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sugar.bean.Report;
import sugar.service.reportService;
import sugar.tools.commonFun;
import java.util.Date;
import java.util.List;

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
        report.setDate(new Date());
        reportService.addReport(report);
        return commonFun.res(200,null,"OK");
    }

    /**
     * 管理员获取所有的文件信息
     * @param username
     * @return
     */
    @RequestMapping(value = "report",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkData(String username){
        JSONObject res=new JSONObject();
        List<Report> reports = reportService.checkAllData(username);
        res.put("reportList",reports);
        return commonFun.res(200,res,"OK");
    }

    /**
     * 管理员删除指定文件
     * @param id 文件 id
     * @return
     */
    @RequestMapping(value = "report",method = RequestMethod.DELETE,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String delReport(@RequestBody Report record){
        reportService.delReportByid(record.getId());
        return commonFun.res(200,null,null);
    }
}
