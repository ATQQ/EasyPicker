package sugar.service;
/*
 *@auther suger
 *2019
 *20:35
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sugar.bean.Report;
import sugar.bean.ReportExample;
import sugar.mapper.ReportMapper;
import sugar.tools.delete;

import java.util.List;

@Service
public class reportServiceImpl implements reportService{

    @Autowired
    private ReportMapper reportMapper;


    @Override
    public String addReport(Report report) {
        reportMapper.insert(report);
        return "1";
    }

    @Override
    public List<Report> checkAllData(String username) {
        ReportExample reportExample=new ReportExample();
        reportExample.or().andUsernameEqualTo(username);
        return reportMapper.selectByExample(reportExample);
    }

    @Override
    public Boolean delReportByid(Integer id) {
        Report report = reportMapper.selectByPrimaryKey(id);
        //获取项目根路径
        String rootpath=System.getProperty("rootpath");
        String realPath=rootpath+"../upload/"+report.getUsername()+"/"+report.getCourse()+"/"+report.getTasks()+"/"+report.getFilename();

        return delete.deleteFile(realPath) && reportMapper.deleteByPrimaryKey(id) == 1;
    }
}
