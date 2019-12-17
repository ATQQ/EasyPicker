package sugar.tools;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author sugar
 * 2019/12/18
 * 上午1:35
 * 七牛云操作工具类
 */

public class QiNiuUtil {
    //构造一个带指定 Region 对象的配置类
    private static final Configuration cfg = new Configuration(Region.huanan());
    //...其他参数参考类注释
    private static final UploadManager uploadManager = new UploadManager(cfg);
    //...生成上传凭证，然后准备上传
    private static final String accessKey = "******";
    private static final String secretKey = "******";
    private static final String bucket = "*****";
    private static final Auth auth = Auth.create(accessKey, secretKey);
    private static final String upToken = auth.uploadToken(bucket);
    private static final BucketManager bucketManager = new BucketManager(auth, cfg);
    private static final String domainOfBucket = "http://q2o2b9g26.bkt.clouddn.com";

    /**
     * 上传文件指定存储空间
     *
     * @param filename
     * @param uploadBytes
     * @return
     */
    public static String uploadFile(String filename, byte[] uploadBytes) {
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = filename;
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(uploadBytes);
        try {
            Response response = uploadManager.put(byteInputStream, key, upToken, null, null);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
            return putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
                System.err.println(ex2.error());
            }
        }
        return "error";
    }

    /**
     * 获取指定文件基本信息
     *
     * @param key
     * @return
     */
    public static FileInfo getFileInfo(String key) {
        try {
            return bucketManager.stat(bucket, key);
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        return null;
    }

    /**
     * 判断是否存在
     *
     * @param key
     * @return
     */
    public static boolean fileIsExist(String key) {
        return getFileInfo(key) != null;
    }

    /**
     * 删除指定的文件
     *
     * @param key
     * @return
     */
    public static boolean deleteFile(String key) {
        try {
            bucketManager.delete(bucket, key);
        } catch (QiniuException ex) {
            System.err.println(ex.response.toString());
        }
        return true;
    }

    public static String getDownloadUrl(String key, long expireInSeconds) throws UnsupportedEncodingException {
        String fileName = key;
        String encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }
}
