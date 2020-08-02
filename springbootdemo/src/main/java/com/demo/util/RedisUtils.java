package com.demo.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/*
 * @author 白俊杰
 * @Date 2019/11/1
 * @Description
 */

@Component
public class RedisUtils implements Cache {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private String name = "one";

	public void setName(String name) {
		this.name = name;
	}

	public RedisTemplate<String, Object> getRedisTemplate() {
		return redisTemplate;
	}

	public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public Object getNativeCache() {
		// TODO Auto-generated method stub
		return null;
	}

	/***
	 * 获取当前key的过期时间
	 * @return
	 */
	public Long getKeyTime(String key){
		return redisTemplate.getExpire(key);
	}

	public void setMap(String key, Map<String, Object> map) {

		byte[] keys = key.toString().getBytes();
		redisTemplate.execute(new RedisCallback<Boolean>() {

			@Override
			public Boolean doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				Map<byte[], byte[]> maps = new HashMap<>();
				for (Map.Entry<String, Object> s : map.entrySet()) {
					maps.put(toArraybyte(s.getKey()), toArraybyte(s.getValue()));
				}
				connection.hMSet(keys, maps);
				return true;
			}
		});
	}

	//map 的存取

	public Map<String, Object> getMap(String key) {
		byte[] keys = key.getBytes();
		Map<String, Object> map = redisTemplate.execute(new RedisCallback<Map<String, Object>>() {

			@Override
			public Map<String, Object> doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				Map<String, Object> ObjMap = new HashMap<>();

				Map<byte[], byte[]> byteMap = connection.hGetAll(keys);

				for (Map.Entry<byte[], byte[]> s : byteMap.entrySet()) {
					ObjMap.put(toObject(s.getKey()).toString(), toObject(s.getValue()));
				}
				return ObjMap;
			}

		});
		return map;
	}

	//管道存储
	public void Pipeline(Map<String, Object> map) {

		redisTemplate.execute(new RedisCallback<Long>() {

			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				connection.openPipeline();

				for (Map.Entry<String, Object> s : map.entrySet()) {
					connection.set(s.getKey().getBytes(), toArraybyte(s.getValue()));
				}
				connection.closePipeline();
				return 1L;
			}
		});
	}

	@Override
	public ValueWrapper get(Object key) {
		// TODO Auto-generated method stub
		Object obj = redisTemplate.execute(new RedisCallback<Object>() {
			String keys = key.toString();
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				byte[] getzu = connection.get(keys.getBytes());
				return toObject(getzu);
			}
		});
		return (obj != null ? new SimpleValueWrapper(obj) : null);
	}

	/***
	 * 获取Object 类型的value
	 * @param key
	 * @return
	 */
	public Object getforString(Object key) {
		Object obj = redisTemplate.execute(new RedisCallback<Object>() {
			String keys = key.toString();
			@Override
			public Object doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				byte[] getzu = connection.get(keys.getBytes());
				return toObject(getzu);
			}
		});
		return obj;
	}

	@Override
	public <T> T get(Object key, Class<T> type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T get(Object key, Callable<T> valueLoader) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(Object o, Object o1) {

	}

	public void puts(Object key, Object value, Long liveTimes) {
		// TODO Auto-generated method stub

		redisTemplate.execute(new RedisCallback<Long>() {
			byte[] keys = key.toString().getBytes();
			Object objs = value;
			final long liveTime = liveTimes;
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				connection.set(keys, toArraybyte(objs));
				if (liveTime > 0) {
					connection.expire(keys, liveTime);

				}
				return 1L;
			}
		});
	}

	// 存list
	public void listPut(String key, List<? extends Object> list) {

		redisTemplate.execute(new RedisCallback<Long>() {
			byte[] keys = key.toString().getBytes();
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				for (Object o : list) {
					connection.lPush(keys, toArraybyte(o));
				}
				return 1L;
			}
		});
	}

	//取list
	public List<Object> getList(String key) {
		byte[] keys = key.toString().getBytes();
		List<Object> objall = redisTemplate.execute(new RedisCallback<List<Object>>() {

			@Override
			public List<Object> doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				long end = connection.lLen(keys); // 获取集合的总长度
				List<byte[]> byteji = connection.lRange(keys, 0, end);
				List<Object> obj = new ArrayList<Object>();
				for (byte[] b : byteji) {
					obj.add(toObject(b));
				}
				return obj;
			}
		});
		return objall;
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void evict(Object key) {
		// TODO Auto-generated method stub
		redisTemplate.execute(new RedisCallback<Long>() {
			byte[] zu = key.toString().getBytes();

			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				connection.del(zu);
				return 1L;
			}
		});
	}

	// 清库
	@Override
	public void clear() {
		// TODO Auto-generated method stub
		redisTemplate.execute(new RedisCallback<Long>() {
			@Override
			public Long doInRedis(RedisConnection connection) throws DataAccessException {
				// TODO Auto-generated method stub
				connection.flushDb();
				return 1L;
			}
		});
	}
	// 序列化

	private byte[] toArraybyte(Object obj) {
		byte[] zu = null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try {
			ObjectOutputStream objs = new ObjectOutputStream(out);
			objs.writeObject(obj);
			objs.flush();
			zu = out.toByteArray();
			objs.close();
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return zu;
	}

	//反序列化
	private Object toObject(byte[] zu) {
		if(zu == null){
			return  null;
		}
		Object obj = null;
		ByteArrayInputStream in = new ByteArrayInputStream(zu);
		try {
			ObjectInputStream input = new ObjectInputStream(in);
			try {
				obj = input.readObject();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			input.close();
			in.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return obj;
	}
}