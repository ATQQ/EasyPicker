package sugar.tools;


import java.io.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
/*
 *@author sugar
 *2019/8/14
 *15:24
 */

public class LocalCache {

    /**
     * 默认缓存容量
     */
    private static int DEFAULT_CAPACITY =512;

    /**
     * 最大缓存容量
     */
    private static int MAX_CAPACITY=100000;

    /**
     * 缓存刷新周期/频率
     */
    private static int MONITOR_DURATION=2;

    // 启动监控线程
    static {
        new Thread(new TimeoutTimerThread()).start();
    }

    /**
     *  使用默认容量创建一个Map
     */
    private static ConcurrentHashMap<String, CacheEntity> cache = new ConcurrentHashMap<String, CacheEntity>(
            DEFAULT_CAPACITY);

    /**
     * 将key-value 保存到本地缓存并制定该缓存的过期时间
     * @param key
     * @param value
     * @param expireTime
     *            过期时间，如果是-1 则表示永不过期
     * @return
     */
    public boolean putValue(String key, Object value, int expireTime) {
        return putCloneValue(key, value, expireTime);
    }

    /**
     * 将值通过序列化clone 处理后保存到缓存中，可以解决值引用的问题
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    private boolean putCloneValue(String key, Object value, int expireTime) {
        try {
            if (cache.size() >= MAX_CAPACITY) {
                return false;
            }
            // 序列化赋值
            CacheEntity entityClone = clone(new CacheEntity(value, System.nanoTime(), expireTime));
            cache.put(key, entityClone);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 序列化 克隆处理
     * @param object
     * @return
     */
    private <T extends Serializable> T clone(T object) {
        T cloneObject = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(object);
            oos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            cloneObject = (T) ois.readObject();
            ois.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cloneObject;
    }

    /**
     * 从本地缓存中获取key对应的值，如果该值不存则则返回null
     * @param key
     * @return
     */
    public Object getValue(String key) {
        if(key==null){
            return null;
        }
        CacheEntity res = cache.get(key);
        return res==null?null:res.getValue();
    }

    /**
     * 清空所有
     */
    public void clear() {
        cache.clear();
    }

    /**
     * 过期处理线程
     */
    static class TimeoutTimerThread implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
//                    System.out.println("Cache monitor");
                    TimeUnit.SECONDS.sleep(MONITOR_DURATION);
                    checkTime();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 过期缓存的具体处理方法
         *
         * @throws Exception
         */
        private void checkTime() throws Exception {
            // "开始处理过期 ";

            for (String key : cache.keySet()) {
                CacheEntity tce = cache.get(key);
                long timoutTime = TimeUnit.NANOSECONDS.toSeconds(System.nanoTime() - tce.getGmtModify());
                // " 过期时间 : "+timoutTime);
                if (tce.getExpire()==-1||tce.getExpire() > timoutTime) {
                    continue;
                }
                System.out.println(" 清除过期缓存 ： " + key);
                // 清除过期缓存和删除对应的缓存队列
                cache.remove(key);
            }
        }
    }
}
