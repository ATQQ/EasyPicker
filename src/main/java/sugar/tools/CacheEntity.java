package sugar.tools;
/*
 *@author sugar
 *2019/8/14
 *15:19
 */

import java.io.Serializable;

public class CacheEntity implements Serializable {

    private static final long serialVersionUID = -5431498217838646933L;

    /**
     * 值
     */
    private Object value;

    /**
     * 保存的时间戳
     */
    private long gmtModify;

    /**
     * 有效时长
     */
    private int expire;

    public CacheEntity(Object value, long gmtModify, int expire) {
        this.value = value;
        this.gmtModify = gmtModify;
        this.expire = expire;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public long getGmtModify() {
        return gmtModify;
    }

    public void setGmtModify(long gmtModify) {
        this.gmtModify = gmtModify;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}
