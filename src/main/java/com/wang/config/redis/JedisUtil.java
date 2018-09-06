package com.wang.config.redis;

import com.wang.model.common.Constant;
import com.wang.util.StringUtil;
import com.wang.util.convert.SerializableUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;

/**
 * TODO：JedisUtil(推荐存Byte数组，存Json字符串效率更慢)
 * @author Wang926454
 * @date 2018/9/4 15:45
 */
@Component
public class JedisUtil {

    /**
     * Logger
     */
    private static Logger logger = LoggerFactory.getLogger(JedisUtil.class);

    /**
     * 静态注入JedisPool连接池
     * 本来是正常注入JedisUtil，可以在Controller和Service层使用，但是重写Shiro的CustomCache无法注入JedisUtil
     * 现在改为静态注入JedisPool连接池，JedisUtil直接调用静态方法即可
     * https://blog.csdn.net/W_Z_W_888/article/details/79979103
     */
    private static JedisPool jedisPool;

    @Autowired
    public void setJedisPool(JedisPool jedisPool) {
        JedisUtil.jedisPool = jedisPool;
    }

    /**
     * TODO：获取Jedis实例
     * @param 
     * @return redis.clients.jedis.Jedis
     * @author Wang926454
     * @date 2018/9/4 15:47
     */
    public static synchronized Jedis getJedis() {
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
        try {
            jedisPool.close();
        }catch (Exception e){
            logger.error("释放Jedis资源异常" + e.getMessage());
        }
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
            if(Constant.OK.equals(result)) {
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
            if(Constant.OK.equals(result)) {
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

    /**
     * TODO：模糊查询获取key集合
     * @param key
     * @return java.util.Set<java.lang.String>
     * @author Wang926454
     * @date 2018/9/6 9:43
     */
    public static Set<String> keysS(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.keys(key);
        }catch(Exception e) {
            logger.error("模糊查询key值:" + key + "异常:" + e.getMessage());
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }

    /**
     * TODO：模糊查询获取key集合
     * @param key
     * @return java.util.Set<java.lang.String>
     * @author Wang926454
     * @date 2018/9/6 9:43
     */
    public static Set<byte[]> keysB(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.keys(key.getBytes());
        }catch(Exception e) {
            logger.error("模糊查询key值:" + key + "异常:" + e.getMessage());
        }finally{
            if(jedis != null) {
                jedis.close();
            }
        }
        return null;
    }
}
