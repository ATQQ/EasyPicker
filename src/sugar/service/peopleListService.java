package sugar.service;

import sugar.bean.Peoplelist;

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

    /**
     *
     * @param username 管理员账号用户名
     * @param parentName 父类名称
     * @param childName 子类名称
     * @return 人员列表
     */
    public List<Peoplelist> getAllDataByAdmin(String username, String parentName, String childName);

    /**
     * 查询提交用户的状态
     * @param record
     * @return
     */
    public Peoplelist checkPeopleStatus(Peoplelist record);

    /**
     * 通过主键更新提交者信息
     * @param record 最新的记录
     * @return
     */
    public Boolean updatePeopleByPrimary(Peoplelist record);
}
