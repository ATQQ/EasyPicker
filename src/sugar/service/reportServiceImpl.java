package sugar.service;
/*
 *@auther suger
 *2019
 *20:35
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sugar.bean.Report;
import sugar.mapper.ReportMapper;

@Service
public class reportServiceImpl implements reportService{

    @Autowired
    private ReportMapper reportMapper;


    @Override
    public String addReport(Report report) {
        reportMapper.insert(report);
        return "1";
    }
}
