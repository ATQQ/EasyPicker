package sugar.controller;
/*
 *@auther suger
 *2019
 *12:09
 */

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sugar.bean.Course;
import sugar.service.courseService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("course")
public class courseController {

    @Autowired
    private courseService courseService;

    @Autowired
    private HttpSession httpSession;


    /**
     * 添加课程
     * @param course
     * @return
     */
    @RequestMapping(value = "add",method = RequestMethod.PUT,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addCourse(@RequestBody Course course){
        String res = courseService.addCourse(course.getName(), course.getType(), course.getParent(),course.getUsername());

        return res;
    }


    /**
     * 查询课程
     * @param range parents/children
     * @param contentid 课程id
     * @param username 用户名
     * @return
     */
    @RequestMapping(value = "check",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String selectCourse(@RequestParam("range") String range,@RequestParam("contentid") Integer contentid ,String username){
        JSONArray jsonArray=new JSONArray();
        JSONObject jsonObject=new JSONObject();
        List<Course> courseList = courseService.selectCourse(range, contentid,username);
        if (courseList==null||courseList.isEmpty()){
            jsonObject.put("status",0);
        }
        else {
            for (Course key:
                 courseList) {
                JSONObject courseobj=new JSONObject();
                courseobj.put("name",key.getName());
                courseobj.put("id",key.getId());
                jsonArray.add(courseobj);
            }
            jsonObject.put("status",1);
            jsonObject.put("data",jsonArray);
        }
        return jsonObject.toJSONString();
    }

    @RequestMapping(value = "del",method = RequestMethod.DELETE,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String delCourse(@RequestBody Course course){
        return  courseService.delCourse(course.getType(),course.getId());
    }
}
