package sugar.service;

import sugar.bean.Report;

import java.util.List;

public interface reportService {
    /**
     * 新增/保存报告
     * @param report
     * @return
     */
    public String addReport(Report report);

    /**
     * 通过用户名称查询所有的提交的文件信息
     * @param username
     * @return
     */
    public List<Report> checkAllData(String username);

    /**
     * 通过文件默认id删除指定文件
     * @param id
     * @return
     */
    public Boolean delReportByid(Integer id);
}
