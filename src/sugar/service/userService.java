package sugar.service;

import sugar.bean.User;
import sugar.bean.UserExample;

import java.util.List;

public interface userService {
    /**
     * 通过用户名查询用户信息
     * @param username
     * @return
     */
    public User checkUser(String username);


    /**
     * 通过制定规则查询所需用户列表
     * @param example
     * @return
     */
    public List<User> checkUserByExample(UserExample example);
    /**
     * 新增注册用户
     * @param record
     * @return
     */
    public Integer addUser(User record);

    /**
     * 更新最新的用户非空字段内容
     * @param record
     * @return
     */
    public Integer updateUser(User record);

}
