package com.imooc.cache;

import com.imooc.util.JedisUtil;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import java.util.Collection;
import java.util.Set;

@Component
public class RedisCache<K, V> implements Cache<K, V> {

    @Autowired
    private JedisUtil jedisUtil;

    private final String CACHE_PREFIX = "imooc-cache:";

    private byte[] getKey(K k) {
        if (k instanceof String) {
            return (CACHE_PREFIX + k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    public V get(K k) throws CacheException {
        System.out.println("从redis cache中获取权限数据");
        byte[] value = jedisUtil.get(getKey(k));
        if (value != null)
            return (V) SerializationUtils.deserialize(value);
        return null;
    }

    public V put(K k, V v) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key, value);
        jedisUtil.expire(key, 600);
        return v;
    }

    public V remove(K k) throws CacheException {
        V v = get(k);

        jedisUtil.del(getKey(k));
        return v;
    }

    public void clear() throws CacheException {
        // don't implement
    }

    public int size() {
        return 0;
    }

    public Set<K> keys() {
        return null;
    }

    public Collection<V> values() {
        return null;
    }
}
