package sugar.service;

import java.util.List;

public interface peopleListService {

    /**
     * 批量添加人员名单
     * @param username 管理员账号
     * @param parentName 父类名称
     * @param childName 子类名称
     * @param names 人员姓名列表
     * @return 添加失败的人员名单
     */
    public List<String> addPeoples(String username,String parentName,String childName,List<String> names);


    /**
     *  单个添加人员
     * @param username 管理员账号
     * @param parentName 父类名称
     * @param childName 子类名称
     * @param name  人员名称
     * @return 是否已经存在
     */
    public boolean addPeople(String username,String parentName,String childName,String name);
}
