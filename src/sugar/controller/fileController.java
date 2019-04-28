package sugar.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sugar.bean.Report;
import sugar.service.reportService;
import sugar.tools.compressFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Date;

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

    @Autowired
    private reportService reportService;

    @ResponseBody
    @RequestMapping(value = "test",produces = "application/json;charset=utf-8")
    public String test(){
        return "1";
    }

    /**
     * 保存文件
     * @param request
     * @return
     */
    @RequestMapping(value = "save",produces = "application/json;charset=utf-8")
    @ResponseBody
    public String saveFile(HttpServletRequest request,@RequestParam("task") String task,@RequestParam("course") String course,@RequestParam("username") String username){

        Report report=new Report();
        JSONObject jsonObject=new JSONObject();

        //获取项目根路径
        String rootpath=System.getProperty("rootpath");

        MultipartHttpServletRequest req= (MultipartHttpServletRequest) request;
        MultipartFile multipartFile=req.getFile("file");

        //保存路径
        String realPath=rootpath+"../upload/"+username+"/"+course+"/"+task;

        //文件名
        String filename = multipartFile.getOriginalFilename();

        //文件类型
        String contentType=filename.substring(filename.lastIndexOf("."));
        System.out.println(course+task+filename);
        try{
            //判断文件夹是否存在
            File dir=new File(realPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(realPath, filename);
            multipartFile.transferTo(file);//写出文件
            jsonObject.put("status",1);
            jsonObject.put("filename",filename);
        }catch (Exception e){
            e.printStackTrace();
        }

        return jsonObject.toJSONString();
    }


    /**
     * 文件下载
     * @param report
     * @return
     * @throws IOException
     */
    @RequestMapping("download")
    public ResponseEntity<byte[]> export(Report report) throws IOException {
        if(report==null){
            return null;
        }

        String filepath=System.getProperty("rootpath")+"../upload/"+report.getUsername()+"/"+report.getCourse()+"/"+report.getTasks()+"/"+report.getFilename();
        HttpHeaders headers=new HttpHeaders();
        File file=new File(filepath);

        String fileName=new String(report.getFilename().getBytes("UTF-8"),"iso-8859-1");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }

    /**
     * 压缩包文件下载
     * @param report
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "downloadZip",method = RequestMethod.GET)
    public ResponseEntity<byte[]> exportZip(Report report) throws Exception {
        //文件夹路径
        String baseFolder=System.getProperty("rootpath")+"../upload/"+report.getUsername()+"/"+report.getCourse()+"/"+report.getTasks();
        //生成的压缩包路径
        String targetPath=System.getProperty("rootpath")+"../upload/"+report.getUsername()+"/"+report.getCourse()+"/"+report.getTasks()+".zip";

        compressFile.compressDitToZip(baseFolder,targetPath);
        HttpHeaders headers=new HttpHeaders();
        File file=new File(targetPath);

        String fileName=new String(new String(report.getTasks()+".zip").getBytes("UTF-8"),"iso-8859-1");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }
}
