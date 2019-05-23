package sugar.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import sugar.bean.Report;
import sugar.service.reportService;
import sugar.tools.compressFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
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
     * 保存模板
     * @param request
     * @return
     */
    @RequestMapping(value = "saveTemplate",produces = "application/json;charset=utf-8",method = RequestMethod.POST)
    @ResponseBody
    public String saveTemplate(HttpServletRequest request,@RequestParam("parent") String parent,@RequestParam("child") String child,@RequestParam("username") String username){
        JSONObject jsonObject=new JSONObject();
        //获取项目根路径
        String rootpath=System.getProperty("rootpath");

        MultipartHttpServletRequest req= (MultipartHttpServletRequest) request;
        MultipartFile multipartFile=req.getFile("file");

        //保存路径
        String realPath=rootpath+"../upload/"+username+"/"+parent+"/"+child+"_Template";

        //文件名
        String filename = multipartFile.getOriginalFilename();

        //文件类型
        String contentType=filename.substring(filename.lastIndexOf("."));
        System.out.println(realPath+"/"+filename);
        try{
            //判断文件夹是否存在
            File dir=new File(realPath);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file = new File(realPath, filename);
            multipartFile.transferTo(file);//写出文件
            jsonObject.put("status",true);
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


    /**
     * 生成指定的压缩文件
     * @param report
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "createZip",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String downloadFileZip(Report report, HttpServletRequest request, HttpServletResponse response) throws Exception {
        JSONObject res=new JSONObject();
        //文件夹路径
        String baseFolder=System.getProperty("rootpath")+"../upload/"+report.getUsername()+"/"+report.getCourse()+"/"+report.getTasks();
        //生成的压缩包路径
        String targetPath=System.getProperty("rootpath")+"../upload/"+report.getUsername()+"/"+report.getCourse()+"/"+report.getTasks()+".zip";
        //生成压缩包
        compressFile.compressDitToZip(baseFolder,targetPath);

        res.put("status",true);
        return res.toJSONString();
    }

    /**
     * 下载文件(通用)
     * @param report
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "down",method = RequestMethod.GET)
    public void downloadFile(Report report, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //设置响应头和客户段保存文件名
        String fileName=new String(report.getFilename().getBytes("UTF-8"),"iso-8859-1");
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition","attachment;filename="+fileName);

        String filepath=System.getProperty("rootpath")+"../upload/"+report.getUsername()+"/"+report.getCourse()+"/"+report.getTasks()+"/"+report.getFilename();
        long read_byte=0l;
        //打开本地的文件流
        InputStream in=new BufferedInputStream(new FileInputStream(filepath));
        //激活下载操作
        OutputStream os=new BufferedOutputStream(response.getOutputStream());

        byte[] buffer=new byte[1024*1024*10];

        int length=-1;
        while((length=in.read(buffer))!=-1){
            os.write(buffer,0,length);
            read_byte+=buffer.length;
        }
        in.close();
        os.flush();
        os.close();
    }


    /**
     * 上传人员名单文件 txt/xls/xlsx
     */
    @RequestMapping(value = "people",method = RequestMethod.POST,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String uploadPeopleFile(HttpServletRequest request,@RequestParam("parent")String parent,@RequestParam("child") String child,@RequestParam("username") String username){
        JSONObject res=new JSONObject();

        //项目路径
        String rootPath=System.getProperty("rootpath");

        MultipartHttpServletRequest req= (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = req.getFile("file");
        //保存的路径
        String savePath=rootPath+"../upload/"+username+"/"+parent+"/"+child+"_peopleFile";

//        源文件名
        String filename=multipartFile.getOriginalFilename();

        //文件类型
        String fileType=filename.substring(filename.lastIndexOf("."));
        System.out.println(savePath+"/"+filename);
        try{
            if(fileType.equals(".xls")||fileType.equals(".xlsx")||fileType.equals(".txt")){
                res.put("status",true);
                File dir=new File(savePath);
                if(!dir.exists()){
                    dir.mkdirs();
                }
                //写出文件
                File file=new File(savePath,filename);
                multipartFile.transferTo(file);
            }else{
                //格式不符合要求
                res.put("status",false);
            }
        }catch (Exception e){
            e.printStackTrace();
            res.put("status",false);
        }
        return res.toJSONString();
    }
}
