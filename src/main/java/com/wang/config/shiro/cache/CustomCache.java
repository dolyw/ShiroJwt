package com.wang.config.shiro.cache;

import com.wang.config.jwt.JWTUtil;
import com.wang.util.JedisUtil;
import com.wang.util.convert.SerializableUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import java.util.*;

/**
 * TODO：重写Shiro的Cache保存读取
 * @author Wang926454
 * @date 2018/9/4 17:31
 */
public class CustomCache<K,V> implements Cache<K,V> {

    /**
     * 缓存的key前缀
     */
    private String keyPrefix = "shiro_cache:";

    /**
     * TODO：缓存的key名称获取为shiro_cache:account
     * @param key
     * @return java.lang.String
     * @author Wang926454
     * @date 2018/9/4 18:33
     */
    private String getKey(Object key){
        return this.keyPrefix + JWTUtil.getAccount(key.toString());
    }

    /**
     * 获取缓存
     */
    @Override
    public Object get(Object key) throws CacheException {
        return JedisUtil.getObject(this.getKey(key));
    }

    /**
     * 保存缓存
     */
    @Override
    public Object put(Object key, Object value) throws CacheException {
        JedisUtil.setObject(this.getKey(key), value, JedisUtil.EXRP_MINUTE);
        return JedisUtil.getObject(this.getKey(key));

    }

    /**
     * 移除缓存
     */
    @Override
    public Object remove(Object key) throws CacheException {
        Object object = JedisUtil.getObject(this.getKey(key));
        JedisUtil.delKey(this.getKey(key));
        return object;
    }

    /**
     * 清空所有缓存
     */
    @Override
    public void clear() throws CacheException {
        JedisUtil.getJedis().flushDB();
    }

    /**
     * 缓存的个数
     */
    @Override
    public int size() {
        Long size = JedisUtil.getJedis().dbSize();
        return size.intValue();
    }

    /**
     * 获取所有的key
     */
    @Override
    public Set keys() {
        Set<byte[]> keys = JedisUtil.getJedis().keys(new String("*").getBytes());
        Set<Object> set = new HashSet<Object>();
        for (byte[] bs : keys) {
            set.add(SerializableUtil.unserializable(bs));
        }
        return set;
    }

    /**
     * 获取所有的value
     */
    @Override
    public Collection values() {
        Set keys = this.keys();
        List<Object> values = new ArrayList<Object>();
        for (Object key : keys) {
            values.add(JedisUtil.getObject(this.getKey(key)));
        }
        return values;
    }
}
