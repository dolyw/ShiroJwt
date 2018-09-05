package com.wang.util;

import com.wang.util.convert.SerializableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * TODO：JedisUtil(推荐存Byte数组，存Json字符串效率更慢)
 * @author Wang926454
 * @date 2018/9/4 15:45
 */
public final class JedisUtil {

    private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    /**
     * Redis服务器IP
     */
    private static String ADDR = "127.0.0.1";

    /**
     * Redis的端口号
     */
    private static int PORT = 6379;

    /**
     * 访问密码
     */
    private static String AUTH = null;

    /**
     * 可用连接实例的最大数目，默认值为8
     * 如果赋值为-1，则表示不限制
     * 如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)
     */
    private static int MAX_ACTIVE = 1024;

    /**
     * 控制一个pool最多有多少个状态为idle(空闲的)的jedis实例，默认值也是8
     */
    private static int MAX_IDLE = 200;

    /**
     * 等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时
     * 如果超过等待时间，则直接抛出JedisConnectionException
     */
    private static int MAX_WAIT = 10000;

    /**
     * 连接超时时间
     */
    private static int TIMEOUT = 10000;

    /**
     * 在borrow一个jedis实例时，是否提前进行validate操作
     * 如果为true，则得到的jedis实例均是可用的
     */
    private static boolean TEST_ON_BORROW = true;

    /**
     * redis过期时间，以秒为单位，一分钟
     */
    public final static int EXRP_MINUTE = 60;

    /**
     * redis过期时间，以秒为单位，一小时
     */
    public final static int EXRP_HOUR = 60 * 60;

    /**
     * redis过期时间，以秒为单位，一天
     */
    public final static int EXRP_DAY = 60 * 60 * 24;

    /**
     * redis-OK
     */
    public final static String OK = "OK";

    /**
     * 连接池
     */
    private static JedisPool jedisPool = null;

    /**
     * 初始化Redis连接池
     */
    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(MAX_ACTIVE);
            config.setMaxIdle(MAX_IDLE);
            config.setMaxWaitMillis(MAX_WAIT);
            config.setTestOnBorrow(TEST_ON_BORROW);
            jedisPool = new JedisPool(config, ADDR, PORT, TIMEOUT, AUTH);
        } catch (Exception e) {
            logger.error("初始化Redis连接池异常" + e.getMessage());
        }
    }

    /**
     * TODO：获取Jedis实例
     * @param 
     * @return redis.clients.jedis.Jedis
     * @author Wang926454
     * @date 2018/9/4 15:47
     */
    public synchronized static Jedis getJedis() {
        try {
            if (jedisPool != null) {
                Jedis resource = jedisPool.getResource();
                return resource;
            } else {
                return null;
            }
        } catch (Exception e) {
            logger.error("获取Jedis实例异常:" + e.getMessage());
        }
        return null;
    }

    /**
     * TODO：释放Jedis资源
     * @param 
     * @return void
     * @author Wang926454
     * @date 2018/9/5 9:16
     */
    public static void closePool() {
        jedisPool.close();
    }

    /**
     * TODO：获取redis键值-object
     * @param key
     * @return java.lang.Object
     * @author Wang926454
     * @date 2018/9/4 15:47
     */
    public static Object getObject(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            byte[] bytes = jedis.get(key.getBytes());
            if(StringUtil.isNotNull(bytes)) {
                return SerializableUtil.unserializable(bytes);
            }
        } catch (Exception e) {
            logger.error("getObject获取redis键值异常:key=" + key + " cause:" + e.getMessage());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * TODO：设置redis键值-object
     * @param key
	 * @param value
     * @return java.lang.String
     * @author Wang926454
     * @date 2018/9/4 15:49
     */
    public static String setObject(String key, Object value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(key.getBytes(), SerializableUtil.serializable(value));
        } catch (Exception e) {
            logger.error("setObject设置redis键值异常:key=" + key + " value=" + value + " cause:" + e.getMessage());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * TODO：设置redis键值-object-expiretime
     * @param key
	 * @param value
	 * @param expiretime
     * @return java.lang.String
     * @author Wang926454
     * @date 2018/9/4 15:50
     */
    public static String setObject(String key, Object value, int expiretime) {
        String result = "";
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.set(key.getBytes(), SerializableUtil.serializable(value));
            if(OK.equals(result)) {
                jedis.expire(key.getBytes(), expiretime);
            }
            return result;
        } catch (Exception e) {
            logger.error("setObject设置redis键值异常:key=" + key + " value=" + value + " cause:" + e.getMessage());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * TODO：获取redis键值-Json
     * @param key
     * @return java.lang.Object
     * @author Wang926454
     * @date 2018/9/4 15:47
     */
    public static String getJson(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.get(key);
        } catch (Exception e) {
            logger.error("getJson获取redis键值异常:key=" + key + " cause:" + e.getMessage());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * TODO：设置redis键值-Json
     * @param key
     * @param value
     * @return java.lang.String
     * @author Wang926454
     * @date 2018/9/4 15:49
     */
    public static String setJson(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.set(key, value);
        } catch (Exception e) {
            logger.error("setJson设置redis键值异常:key=" + key + " value=" + value + " cause:" + e.getMessage());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * TODO：设置redis键值-Json-expiretime
     * @param key
     * @param value
     * @param expiretime
     * @return java.lang.String
     * @author Wang926454
     * @date 2018/9/4 15:50
     */
    public static String setJson(String key, String value, int expiretime) {
        String result = "";
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            result = jedis.set(key, value);
            if(OK.equals(result)) {
                jedis.expire(key, expiretime);
            }
            return result;
        } catch (Exception e) {
            logger.error("setJson设置redis键值异常:key=" + key + " value=" + value + " cause:" + e.getMessage());
        } finally {
            if(jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * TODO：删除key
     * @param key
     * @return java.lang.Long
     * @author Wang926454
     * @date 2018/9/4 15:50
     */
    public static Long delKey(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.del(key.getBytes());
        }catch(Exception e) {
            logger.error("删除key:" + key + "异常:" + e.getMessage());
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * TODO：key是否存在
     * @param key
     * @return java.lang.Boolean
     * @author Wang926454
     * @date 2018/9/4 15:51
     */
    public static Boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.exists(key.getBytes());
        }catch(Exception e) {
            logger.error("查询key:" + key + "异常:" + e.getMessage());
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
