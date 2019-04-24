package sugar.service;

import sugar.bean.User;

public interface userService {
    /**
     * 查询用户信息
     * @param username
     * @return
     */
    public User checkUser(String username);

    /**
     * 新增注册用户
     * @param record
     * @return
     */
    public boolean addUser(User record);

}
