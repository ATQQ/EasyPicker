package sugar.tools;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.processing.OperationManager;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.storage.model.FileListing;
import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import com.qiniu.util.StringUtils;
import com.qiniu.util.UrlSafeBase64;

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
    //间隔符
    private static final String QN_SEPARATOR = "/";

    /**
     * txt换行符
     */
    private static final String QN_NEWLINE = "\n";
    /**
     * 索引文件名称
     */
    private static final String TXT_NAME = "index.txt";

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

    /**
     * 获取文件下载链接
     *
     * @param key             文件key
     * @param expireInSeconds 链接有效时间
     */
    public static String getDownloadUrl(String key, long expireInSeconds) throws UnsupportedEncodingException {
        String fileName = key;
        String encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        String publicUrl = String.format("%s/%s", domainOfBucket, encodedFileName);
        return auth.privateDownloadUrl(publicUrl, expireInSeconds);
    }

    /**
     * 大量文件压缩
     * @param prefix
     * @param zipName
     * @throws QiniuException
     */
    public static String makeZip(String prefix, String zipName) throws QiniuException {
        FileListing fileListing = bucketManager.listFiles(bucket, prefix, null, 100, null);
        FileInfo[] files = fileListing.items;

        String content = "";
        for (FileInfo file : files) {
            //拼接原始链接
            String url = domainOfBucket + QN_SEPARATOR + file.key;
            //链接加密并进行Base64编码，别名去除前缀目录。
            String safeUrl = "/url/" + UrlSafeBase64.encodeToString(auth.privateDownloadUrl(url)) + "/alias/" + UrlSafeBase64.encodeToString(file.key.substring(prefix.length()));
            content += ((StringUtils.isNullOrEmpty(content) ? "" : QN_NEWLINE) + safeUrl);
        }

        //索引文件路径
        String txtkey = prefix + TXT_NAME;

        //生成索引文件的token
        String upToken = auth.uploadToken(bucket, txtkey, 3600, new StringMap().put("insertOnly", 0));

        //上传索引文件
        uploadManager.put(content.getBytes(), txtkey, upToken);

        //默认utf-8，但是中文显示乱码，修改为gbk
        String fops = "mkzip/4/encoding/" + UrlSafeBase64.encodeToString("gbk") + "|saveas/" + UrlSafeBase64.encodeToString(bucket + ":" + prefix + zipName + ".zip");

        OperationManager operater = new OperationManager(auth, cfg);

        StringMap params = new StringMap();
        //压缩完成后，七牛回调URL
//        params.put("notifyURL", NOTIFY_URL);

        String id = operater.pfop(bucket, txtkey, fops, params);
        return "http://api.qiniu.com/status/get/prefop?id=" + id;
    }

    /**
     * 获取压缩文件接口回调的状态
     * @param mkStatusUrl 压缩文件回调URl
     * @return
     */
    public static int getMakeCode(String mkStatusUrl){
        HttpRequest request =HttpRequest.get(mkStatusUrl);
        String callback=request.body("utf-8");
        JSONObject parser=JSONObject.parseObject(callback);
        return parser.getInteger("code");
    }
}
