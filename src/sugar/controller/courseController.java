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

    @RequestMapping(value = "add",method = RequestMethod.PUT,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String addCourse(@RequestBody Course course){
        String res = courseService.addCourse(course.getName(), course.getType(), course.getParent());

        return res;
    }

    @RequestMapping(value = "check",method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String selectCourse(@RequestParam("range") String range,@RequestParam("contentid") Integer contentid ){
        JSONArray jsonArray=new JSONArray();
        JSONObject jsonObject=new JSONObject();
        List<Course> courseList = courseService.selectCourse(range, contentid);
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
}
