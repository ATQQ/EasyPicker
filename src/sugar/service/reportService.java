package sugar.service;

import sugar.bean.Report;

public interface reportService {
    /**
     * 新增/保存报告
     * @param report
     * @return
     */
    public String addReport(Report report);
}
