package sugar.exception;

import lombok.Getter;

/**
 * @author sugar
 * 2019/10/30
 * 9:13
 * 自定义服务异常
 */

@Getter
public class ServiceException extends RuntimeException{
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 附加信息
     */
    private String message;

    /**
     * 数据
     */
    private Object data;


    public ServiceException(Object data, String message) {
        this.code = 400;
        this.message = message;
        this.data = data;
    }

    public ServiceException(Object data, String message, Integer code) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ServiceException(String message) {
        this.code = 400;
        this.message = message;
    }

    public ServiceException(String message, int code) {
        this.code = code;
        this.message = message;
    }
}
