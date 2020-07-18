package sugar.controller;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
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
import sugar.service.peopleListService;
import sugar.service.reportService;
import sugar.tools.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;

/*
 *@auther suger
 *2019
 *9:58
 */
@Controller
@RequestMapping("file")
public class fileController {


    @Autowired
    private reportService reportService;

    @Autowired
    private peopleListService peopleListService;

    /**
     * 上传文件的Base目录
     */
    private final String BASE_File_PATH = System.getProperty("pickerDevUploadDir")+"../upload/";
    /**
     * 保存提交的用户文件
     * @param request
     * @return
     */
    @RequestMapping(value = "save", produces = "application/json;charset=utf-8")
    @ResponseBody
    public String saveFile(HttpServletRequest request, @RequestParam("task") String task, @RequestParam("course") String course, @RequestParam("account") String username, @RequestParam("username") String name) throws Exception {

        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = req.getFile("file");
        //保存路径
        String realPath = BASE_File_PATH + username + "/" + course + "/" + task;
        //源文件名
        String filename = multipartFile.getOriginalFilename();
        //文件类型
        String contentType = filename.substring(filename.lastIndexOf("."));
        filename = filename.substring(0, filename.lastIndexOf("."));
        //判断文件夹是否存在,不存在则创建
        FileUtil.dirIsExist(realPath);
        //判断文件是否存在
        if(FileUtil.isExist(realPath + "/" + filename + contentType)){
            //如果存在则加上姓名判断一次
            filename = filename + "-" + name;
            if(FileUtil.isExist(realPath + "/" + filename + contentType)){
                //如果还存在就加上时间戳
                filename = filename + "-" + getNowDate.timestamp();
            }
        }
        filename = filename + contentType;
        //写出文件
        multipartFile.transferTo(new File(realPath + "/" + filename));
        JSONObject resData = new JSONObject();
        resData.put("filename", filename);
        return commonFun.res(200, resData, "上传成功");
    }

    /**
     * 保存模板
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "saveTemplate", produces = "application/json;charset=utf-8", method = RequestMethod.POST)
    @ResponseBody
    public String saveTemplate(HttpServletRequest request, @RequestParam("parent") String parent, @RequestParam("child") String child, @RequestParam("username") String username) throws Exception {
        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = req.getFile("file");
        //保存的绝对路径
        String realPath = BASE_File_PATH + username + "/" + parent + "/" + child + "_Template";
        //文件名
        String filename = multipartFile.getOriginalFilename();
        JSONObject resData = new JSONObject();
        //创建文件夹
        FileUtil.dirIsExist(realPath);
        //写出文件
        multipartFile.transferTo(new File(realPath, filename));
        resData.put("filename", filename);
        return commonFun.res(200, resData, "上传成功");
    }

    /**
     * 文件下载
     *
     * @param report
     * @return
     * @throws IOException
     */
    @RequestMapping("download")
    public ResponseEntity<byte[]> export(Report report) throws IOException {
        if (report == null) {
            return null;
        }
        String filepath = BASE_File_PATH + report.getUsername() + "/" + report.getCourse() + "/" + report.getTasks() + "/" + report.getFilename();
        HttpHeaders headers = new HttpHeaders();
        File file = new File(filepath);
        String fileName = new String(report.getFilename().getBytes("UTF-8"), "iso-8859-1");
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }

    /**
     * 压缩包文件下载
     *
     * @param report
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "downloadZip", method = RequestMethod.GET)
    public ResponseEntity<byte[]> exportZip(Report report) throws Exception {
        String filePath = report.getUsername() + "/" + report.getCourse() + "/" + report.getTasks();
        //文件夹路径
        String baseFolder = BASE_File_PATH +filePath ;
        //生成的压缩包路径
        String targetPath = BASE_File_PATH + filePath + ".zip";
        compressFile.compressDitToZip(baseFolder, targetPath);
        HttpHeaders headers = new HttpHeaders();
        File file = new File(targetPath);
        String fileName = new String(new String(report.getTasks() + ".zip").getBytes("UTF-8"), "iso-8859-1");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileName);
        return new ResponseEntity<byte[]>(FileUtils.readFileToByteArray(file),
                headers, HttpStatus.CREATED);
    }


    /**
     * 生成指定的压缩文件
     *
     * @param report
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "createZip", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String downloadFileZip(@RequestBody Report report) throws Exception {
        String filePath = report.getUsername() + "/" + report.getCourse() + "/" + report.getTasks();
        //文件夹路径
        String baseFolder = BASE_File_PATH + filePath;
        //生成的压缩包路径
        String targetPath = BASE_File_PATH + filePath + ".zip";
        //生成压缩包
        compressFile.compressDitToZip(baseFolder, targetPath);
        return commonFun.res(200,null,null);
    }

    /**
     * 下载文件(通用)
     *
     * @param report
     * @param request
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "down", method = RequestMethod.GET)
    public void downloadFile(Report report, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //设置响应头和客户段保存文件名
        String fileName = new String(report.getFilename().getBytes("UTF-8"), "iso-8859-1");
        response.setCharacterEncoding("utf-8");
        response.setContentType("multipart/form-data");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        String filepath = BASE_File_PATH + report.getUsername() + "/" + report.getCourse() + "/" + report.getTasks() + "/" + report.getFilename();
        //打开本地的文件流
        InputStream in = new BufferedInputStream(new FileInputStream(filepath));
        //激活下载操作
        OutputStream os = new BufferedOutputStream(response.getOutputStream());
        byte[] buffer = new byte[1024 * 1024 * 10];
        int length = -1;
        while ((length = in.read(buffer)) != -1) {
            os.write(buffer, 0, length);
        }
        in.close();
        os.flush();
        os.close();
    }


    /**
     * 上传人员名单文件 txt/xls/xlsx
     */
    @RequestMapping(value = "people", method = RequestMethod.POST, produces = "application/json;charset=utf-8")
    @ResponseBody
    public String uploadPeopleFile(HttpServletRequest request, @RequestParam("parent") String parent, @RequestParam("child") String child, @RequestParam("username") String username) throws Exception {
        MultipartHttpServletRequest req = (MultipartHttpServletRequest) request;
        MultipartFile multipartFile = req.getFile("file");

        //保存的路径
        String savePath = BASE_File_PATH + username + "/" + parent + "/" + child + "_peopleFile";

//        源文件名
        String filename = multipartFile.getOriginalFilename();

        //文件类型
        String fileType = filename.substring(filename.lastIndexOf("."));

        JSONObject res = new JSONObject();
        int code = 200;

        if (fileType.equals(".xls") || fileType.equals(".xlsx") || fileType.equals(".txt")) {
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            //写出文件
            File file = new File(savePath, filename);
            multipartFile.transferTo(file);

            List<String> names = readFile.read(savePath + "/" + filename);
            List<String> peoples = peopleListService.addPeoples(username, parent, child, names);
            res.put("failCount", peoples.size());
//                如果有未成功导入的数据,生成文件
            if (peoples.size() > 0) {
                writeFile.xls(peoples, savePath + "/" + filename.substring(0, filename.lastIndexOf(".")) + "_fail.xls");
            }
        } else {
            //格式不符合要求
            code = 20040;
        }
        return commonFun.res(code, res, code==200?"上传成功":"格式不合格");
    }

    @RequestMapping(value = "qiniu/token", method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String getQiniuToken(){
        JSONObject res= new JSONObject();
        res.put("data",QiNiuUtil.getUploadToken());
        return commonFun.res(200,res,"获取成功");
    }

    @RequestMapping(value = "qiniu/exist", method = RequestMethod.GET,produces = "application/json;charset=utf-8")
    @ResponseBody
    public String checkFileIsExist(@RequestParam String key){
        JSONObject res = new JSONObject();
        res.put("isExist",QiNiuUtil.fileIsExist(key));
        return commonFun.res(200,res,"获取成功");
    }
}
