package sugar.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sugar.tools.getNowDate;
import sugar.tools.randomString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
/*
 *@auther suger
 *2019
 *9:58
 */
@Controller
@RequestMapping("file")
public class fileController {


    @Autowired
    private HttpSession httpSession;

    @ResponseBody
    @RequestMapping(value = "test",produces = "application/json;charset=utf-8")
    public String test(){
        return "1";
    }

    /**
     * 保存文件excell/image
     * @param request
     * @return
     */
    @RequestMapping(value = "save",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String saveFile(HttpServletRequest request){

        JSONObject jsonObject=new JSONObject();
        //获取项目根路径
        String rootpath=System.getProperty("rootpath");

        MultipartHttpServletRequest req= (MultipartHttpServletRequest) request;
        MultipartFile multipartFile=req.getFile("file");

        //保存路径
        String realPath=rootpath+"upload/excells";

        //文件名
        String filename = multipartFile.getOriginalFilename();

        //文件类型
        String contentType=filename.substring(filename.lastIndexOf("."));

        //生成随机的文件名
        String newName=randomString.getRandomString(6)+getNowDate.timestamp();
        newName+=contentType;

        try{
            //判断文件夹是否存在
            File dir=new File(realPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(realPath, newName);
            multipartFile.transferTo(file);//写出文件

        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObject.toJSONString();
    }


    @RequestMapping("download")
    public ResponseEntity<byte[]> export(String filename) throws IOException {
        System.out.println(filename);
        if(filename==null){
            return null;
        }
        String filepath=System.getProperty("rootpath")+"upload/excells/"+filename;
        HttpHeaders headers=new HttpHeaders();
        File file=new File(filepath);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", filename);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }
}
