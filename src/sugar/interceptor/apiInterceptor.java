package sugar.interceptor;
/*
 *@auther suger
 *2019
 *16:52
 */

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import sugar.exception.myException;
public class apiInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");
        String tokenServer= (String) request.getSession().getAttribute("token");
        String method = request.getMethod();
        String url=request.getServletPath();

        //用户提交文件
        if(url.contains("save")){
            return true;
        }

        if(method.equals("GET")){
            //获取所有的提交的文件信息
            //获取所有的父子类列表
            if(url.contains("/report/report")||url.contains("/course/node")){
                if(token!=null&&tokenServer!=null&&tokenServer.equals(token)){
                    return true;
                }else{
                    throw new myException("no power");
                }
            }
        }

        //拦截更新请求
        if(method.equals("PUT")){
            if(url.contains("/course/add")||url.contains("childContent/childContext")){
                if(token!=null&&tokenServer!=null&&tokenServer.equals(token)){
                    return true;
                }else{
                    throw new myException("no power");
                }
            }
        }

        //拦截删除请求
        if(method.equals("DELETE")){
            //判断token是否正确
            if(token!=null&&tokenServer!=null&&tokenServer.equals(token)){
                return true;
            }else{
                throw new myException("no power");
            }

        }
        return true;
    }
}
