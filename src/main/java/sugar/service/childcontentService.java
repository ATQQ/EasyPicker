package sugar.service;

import sugar.bean.Childcontent;

public interface childcontentService {

    /**
     * 通过任务id查询任务附加信息
     * @param taskid 任务id
     * @return Childcontent
     */
    public Childcontent checkDataByTaskid(Integer taskid);

    /**
     * 更新任务附加信息
     * @param newData 新数据
     * @param type 类型 1:ddl 2:people 3:template
     * @return bool
     */
    public boolean updateData(Childcontent newData,Integer type);

}
