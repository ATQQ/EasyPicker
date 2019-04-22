package sugar.service;

import sugar.bean.Course;

import java.util.List;

public interface courseService {
    /**
     * 添加课程父节点/子节点
     * @param name 节点名称
     * @param type 节点类型1 父节点 0 子节点
     * @param parentid 子节点父亲id
     * @return
     */
    public String addCourse(String name,Integer type ,Integer parentid,String username);

    /**
     * 查询
     * @param range parents/所有父节点 children/父节点的所有子节点
     * @param parentid 父节点id
     * @param username 管理员账号
     * @return
     */
    public List<Course> selectCourse(String range,Integer parentid,String username);


    /**
     * 删除课程/任务
     * @param type 删除资源类型 课程 or 任务
     * @param id 待删除资源的id
     * @return
     */
    public String delCourse(Integer type,Integer id);
}
