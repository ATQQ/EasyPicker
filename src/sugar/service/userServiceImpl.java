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
    public Integer addUser(User record) {
        record.setDate(new Date());
        record.setPower(6);
        record.setStatus(1);
        UserExample example=new UserExample();
        if(record.getMobile()!=null){
            example.or().andMobileEqualTo(record.getMobile());
            if(!checkUserByExample(example).isEmpty()){
                //手机号已经存在
                return 402;
            }
        }
        if(checkUser(record.getUsername())!=null){
            //账号已存在
            return 401;
        }
        userMapper.insert(record);
        return 200;
    }

    @Override
    public List<User> checkUserByExample(UserExample example) {
        return userMapper.selectByExample(example);
    }
}
