package sugar.controller;
/*
 *@auther suger
 *2019
 *12:09
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sugar.bean.Course;
import sugar.service.courseService;
import sugar.tools.commonFun;

import java.util.List;

@Controller
@RequestMapping("course")
public class courseController {

    @Autowired
    private courseService courseService;


    /**
     * 添加课程
     *
     * @param course
     * @return
     */
    @RequestMapping(value = "add", method = RequestMethod.PUT, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addCourse(@RequestBody Course course) {
        JSONObject res = JSON.parseObject(courseService.addCourse(course.getName(), course.getType(), course.getParent(), course.getUsername()));

        return commonFun.res(200, res, "OK");
    }


    /**
     * 查询课程
     *
     * @param range     parents/children
     * @param contentid 课程id
     * @param username  用户名
     * @return
     */
    @RequestMapping(value = "check", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String selectCourse(String range, Integer contentid, String username) {
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        if(range==null||contentid==null||username==null){
            return commonFun.res(200, jsonObject, "OK");
        }
        List<Course> courseList = courseService.selectCourse(range, contentid, username);
        for (Course key :
                courseList) {
            JSONObject courseobj = new JSONObject();
            courseobj.put("name", key.getName());
            courseobj.put("id", key.getId());
            jsonArray.add(courseobj);
        }
        jsonObject.put("courseList", jsonArray);

        return commonFun.res(200, jsonObject, "OK");
    }

    /**
     * 删除指定的课程
     *
     * @param course
     * @return
     */
    @RequestMapping(value = "del", method = RequestMethod.DELETE, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String delCourse(@RequestBody Course course) {
        JSONObject res=JSON.parseObject(courseService.delCourse(course.getType(), course.getId()));
        return commonFun.res(200,res,"OK");
    }


    /**
     * 获取单个任务信息
     *
     * @param type     类型
     * @param parent   父节点名称
     * @param child    子节点名称
     * @param username 管理员账号
     * @return JSON
     */
    @RequestMapping(value = "course", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkCourse(Integer type, String parent, String child, String username) {
        JSONObject res = new JSONObject();
        switch (type) {
            case 3:
                Course cParent = courseService.checkCourseByName(1, username, parent);
                if (cParent == null) {
                    res.put("status", false);
                } else {
                    int pId = cParent.getId();
                    Course cChild = courseService.getChildCourse(username,pId,child);
                    if (cChild == null) {
                        res.put("status", false);
                    } else {
                        res.put("status", true);
                        res.put("data", cChild);
                    }
                }
                break;
            case 2:
                Course ccParent = courseService.checkCourseByName(1, username, parent);
                if (ccParent == null) {
                    res.put("status", false);
                } else {
                    res.put("status", true);
                    res.put("data", ccParent);
                }
            default:
                break;
        }
        return commonFun.res(200,res,"OK");
    }

    /**
     * 获取该管理员所有的父子类列表
     * @param username
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "node", method = RequestMethod.GET, produces = "application/json;charset=utf-8")
    public String checkCourseByUsername(String username) {
        JSONObject res = new JSONObject();
        List<Course> courseList = courseService.checkCourseByUsername(username);
        res.put("courseList", courseList);
        return commonFun.res(200,res,"OK");
    }
}
