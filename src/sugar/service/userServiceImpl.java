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
import sugar.tools.encryption;

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
                return 20012;
            }
        }
        if(checkUser(record.getUsername())!=null){
            //账号已存在
            return 20013;
        }
        userMapper.insert(record);
        return 200;
    }

    @Override
    public List<User> checkUserByExample(UserExample example) {
        return userMapper.selectByExample(example);
    }

    @Override
    public Integer updateUser(User record) {
        UserExample userExample=new UserExample();
        //通过手机号更改密码
        if(record.getMobile()!=null&&record.getUsername()==null){
            userExample.or().andMobileEqualTo(record.getMobile());
            List<User> users = checkUserByExample(userExample);
            if(users.isEmpty()){
                //手机号不存在
                return 20014;
            }
            User newUser=new User();
            newUser.setId(users.get(0).getId());
            try{
                newUser.setPassword(encryption.getAfterData(record.getPassword()));
                userMapper.updateByPrimaryKeySelective(newUser);
            }catch (Exception e){
                e.printStackTrace();
                return 500;
            }
            return 200;
        }


        //绑定手机号
        if(record.getUsername()!=null&&record.getMobile()!=null){
            userExample.or().andMobileEqualTo(record.getMobile());
            List<User> users = checkUserByExample(userExample);
            if(!users.isEmpty()){
                //手机号已存在
                return 20012;
            }
            userExample=new UserExample();
            userExample.or().andUsernameEqualTo(record.getUsername());
            record.setId(users.get(0).getId());
            userMapper.updateByPrimaryKeySelective(record);
            return 200;
        }

        return 450;
    }
}
