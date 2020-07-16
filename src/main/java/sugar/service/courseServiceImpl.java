package sugar.service;
/*
 *@auther suger
 *2019
 *3:01
 */

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sugar.bean.Childcontent;
import sugar.bean.ChildcontentExample;
import sugar.bean.Course;
import sugar.bean.CourseExample;
import sugar.mapper.ChildcontentMapper;
import sugar.mapper.CourseMapper;

import java.util.List;

@Service
public class courseServiceImpl implements courseService {
    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private ChildcontentMapper childcontentMapper;

    @Override
    public String addCourse(String name, Integer type, Integer parentid, String username) {
        Course course = new Course();
        course.setName(name);
        course.setType(type);
        course.setParent(parentid);
        course.setUsername(username);
        CourseExample courseExample = new CourseExample();

        if (parentid != null) {
            courseExample.or().andNameEqualTo(name).andTypeEqualTo(type).andParentEqualTo(parentid);
        } else {
            courseExample.or().andNameEqualTo(name).andTypeEqualTo(type).andUsernameEqualTo(username);
        }
        List<Course> courseList = courseMapper.selectByExample(courseExample);

        JSONObject jsonObject = new JSONObject();
        //如果该数据已经存在
        if (courseList.size() != 0) {
            jsonObject.put("status", false);
        } else {
            courseMapper.insert(course);
            courseList = courseMapper.selectByExample(courseExample);
            jsonObject.put("status", true);
            jsonObject.put("id", courseList.get(0).getId());
        }

        return jsonObject.toJSONString();
    }

    @Override
    public List<Course> selectCourse(String range, Integer parentid, String username) {
        CourseExample courseExample = new CourseExample();
        switch (range) {
            case "parents":
                courseExample.or().andParentIsNull().andUsernameEqualTo(username);
                return courseMapper.selectByExample(courseExample);
            case "children":
                courseExample.or().andParentEqualTo(parentid).andUsernameEqualTo(username);
                return courseMapper.selectByExample(courseExample);
            default:
                break;
        }
        return null;
    }

    @Override
    public String delCourse(Integer type, Integer id) {
        CourseExample courseExample = new CourseExample();
        JSONObject jsonObject = new JSONObject();
        switch (type) {
            case 1:
                courseMapper.deleteByPrimaryKey(id);
                courseExample.or().andParentEqualTo(id);
                courseMapper.deleteByExample(courseExample);
                jsonObject.put("status",true);
                break;
            case 0:
                ChildcontentExample childcontentExample = new ChildcontentExample();
                childcontentExample.or().andTasksidEqualTo(id);
                List<Childcontent> childcontentList = childcontentMapper.selectByExample(childcontentExample);
                if (!childcontentList.isEmpty()) {
                    childcontentMapper.deleteByExample(childcontentExample);
                }
                courseMapper.deleteByPrimaryKey(id);
                jsonObject.put("status", true);
                break;
            default:
                jsonObject.put("status", false);
                break;
        }
        return jsonObject.toJSONString();
    }

    @Override
    public Course checkCourseByName(Integer type, String username, String name) {
        CourseExample courseExample = new CourseExample();
        courseExample.or().andUsernameEqualTo(username).andNameEqualTo(name).andTypeEqualTo(type);
        List<Course> courseList = courseMapper.selectByExample(courseExample);
        return courseList.isEmpty() ? null : courseList.get(0);
    }

    @Override
    public List<Course> checkCourseByUsername(String username) {
        CourseExample courseExample = new CourseExample();
        courseExample.or().andUsernameEqualTo(username);
        return courseMapper.selectByExample(courseExample);
    }

    @Override
    public Course getChildCourse(String username, Integer parentId, String name) {
        CourseExample courseExample = new CourseExample();
        courseExample.or().andUsernameEqualTo(username).andParentEqualTo(parentId).andNameEqualTo(name);
        return courseMapper.selectByExample(courseExample).get(0);
    }
}
