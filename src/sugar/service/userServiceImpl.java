package sugar.service;
/*
 *@auther suger
 *2019
 *16:57
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sugar.bean.User;
import sugar.bean.UserExample;
import sugar.mapper.UserMapper;

import java.util.Date;
import java.util.List;

@Service
public class userServiceImpl implements userService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User checkUser(String username) {
        UserExample example=new UserExample();
        example.or().andUsernameEqualTo(username);

        List<User> userList = userMapper.selectByExample(example);
        if(userList.isEmpty()){
            return null;
        }else{
            return userList.get(0);
        }
    }

    @Override
    public boolean addUser(User record) {
        record.setDate(new Date());
        record.setPower(6);
        record.setStatus(1);
        if(checkUser(record.getUsername())==null){
            userMapper.insert(record);
            return true;
        }
        return false;
    }
}
