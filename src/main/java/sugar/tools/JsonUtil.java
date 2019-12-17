package sugar.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author sugar
 * 2019/10/31
 * 19:08
 * json工具类
 */

public class JsonUtil {
    private static final ObjectMapper mapper=new ObjectMapper();

    /**
     * 对象转json字符串
     */
    public static String objToStr(Object obj) throws JsonProcessingException {
        return mapper.writeValueAsString(obj);
    }

    /**
     * 字符串转对象
     * @param str 待转换字符串
     * @param keyClass 目标类对象z
     */
    public static Object strToObj(String str,Class keyClass) throws IOException {
        return mapper.readValue(str,keyClass);
    }
}
