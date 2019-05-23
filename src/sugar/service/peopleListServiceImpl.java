package sugar.service;
/*
 *@auther suger
 *2019
 *18:44
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sugar.bean.Peoplelist;
import sugar.bean.PeoplelistExample;
import sugar.mapper.PeoplelistMapper;

import java.util.ArrayList;
import java.util.List;

@Service
public class peopleListServiceImpl implements peopleListService {

    @Autowired
    private PeoplelistMapper peoplelistMapper;

    @Override
    public List<String> addPeoples(String username, String parentName, String childName, List<String> names) {
        List<String> failNames=new ArrayList<String>();
        for (String key:names
             ) {
            PeoplelistExample peoplelistExample=new PeoplelistExample();
            peoplelistExample.or().andAdminUsernameEqualTo(username).andParentNameEqualTo(parentName).andChildNameEqualTo(childName).andPeopleNameEqualTo(key);
            List<Peoplelist> peoplelists = peoplelistMapper.selectByExample(peoplelistExample);
            if(peoplelists.isEmpty()){
                Peoplelist record=new Peoplelist();
                record.setAdminUsername(username);
                record.setPeopleName(key);
                record.setChildName(childName);
                record.setParentName(parentName);
                record.setStatus(0);
                peoplelistMapper.insert(record);
            }else {
                failNames.add(key);
            }

        }
        return failNames;
    }

    @Override
    public boolean addPeople(String username, String parentName, String childName, String name) {
        return false;
    }
}
