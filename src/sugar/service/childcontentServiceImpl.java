package sugar.service;
/*
 *@auther suger
 *2019
 *10:01
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sugar.bean.Childcontent;
import sugar.bean.ChildcontentExample;
import sugar.mapper.ChildcontentMapper;

import java.util.List;

@Service
public class childcontentServiceImpl implements childcontentService {

    @Autowired
    private ChildcontentMapper childcontentMapper;

    @Override
    public Childcontent checkDataByTaskid(Integer taskid) {
        ChildcontentExample childcontentExample=new ChildcontentExample();
        childcontentExample.or().andTasksidEqualTo(taskid);
        List<Childcontent> childcontentList = childcontentMapper.selectByExample(childcontentExample);
        return childcontentList.isEmpty()?null:childcontentList.get(0);
    }

    @Override
    public boolean updateData(Childcontent newData,Integer type) {
        ChildcontentExample childcontentExample=new ChildcontentExample();
//        查询是否存在
        Childcontent record = checkDataByTaskid(newData.getTasksid());
        if(record==null){
            childcontentMapper.insert(newData);
            return true;
        }
        switch (type){
//          ddl
            case 1:
                record.setDdl(newData.getDdl());
                childcontentMapper.updateByPrimaryKey(record);
                return true;
//          people
            case 2:
                record.setPeople(newData.getPeople());
                childcontentMapper.updateByPrimaryKey(record);
                return true;

//          template
            case 3:
                record.setTemplate(newData.getTemplate());
                childcontentMapper.updateByPrimaryKey(record);
                return true;
            default:
                    return false;
        }

    }
}
