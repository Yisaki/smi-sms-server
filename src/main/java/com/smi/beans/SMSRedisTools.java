package com.smi.beans;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import com.smi.model.SmsHistory;
import com.smi.mq.SmiRabbitMQ;

@Component
public class SMSRedisTools {
	private Logger logger = Logger.getLogger(this.getClass());
	
	private final byte[] listKey = "SMI_SMS".getBytes(); //已发送短信在Redis中保存的队列的KEY
	
	@Value("${spring.redis.host}")
	private String host;
	@Value("${spring.redis.port}")
	private Integer port;
	@Value("${rabbitmq.qos}")
	private Integer qos;
	
	private ThreadLocal<Jedis> curJedis;
	
	private JedisPool pool;
	
	
	
	/**
	 * 初始化连接池。在调用此类的其它方法前此方法必须已被调用过一次
	 */
	public synchronized void initPool() {
		if(pool == null) {
			JedisPoolConfig config = new JedisPoolConfig();
			config.setBlockWhenExhausted(true);
			config.setJmxEnabled(true);
			config.setJmxNameBase("Jedis_Jxm_SMS");
			config.setJmxNamePrefix("SMI_");
			config.setMaxTotal(qos * 2 + 1); //每个线程池的线程数 * 2 + 将redis中短信保存到数据库的线程数
			config.setMaxIdle(config.getMaxTotal());
			config.setMinIdle(0);
			config.setTestOnCreate(true);
			config.setTestWhileIdle(true);
			config.setTestOnReturn(false);
			config.setTestOnBorrow(false);
			pool = new JedisPool(config, host, port);
			
			curJedis = new ThreadLocal<Jedis>();
			
		}
	}
	
	
	/**
	 * 关闭Redis连接池
	 */
	public synchronized void closePool() {
		if(pool != null && !pool.isClosed()) {
			pool.close();
			pool.destroy();
			pool = null;
		}
	}
	
	
	/**
	 * 获取一个连接
	 */
	private Jedis getJedis() {
		if(curJedis.get() == null) {
			curJedis.set(pool.getResource());
		}
		
		return curJedis.get();
	}
	
	
	/**
	 * 释放连接
	 */
	private void returnJedis() {
		if(curJedis.get() != null) {
			pool.returnResource(curJedis.get());
			curJedis.set(null);
		}
	}
	
	
	/**
	 * 将短信保存到redis中去。此方法支持多线程并发调用且会并发执行
	 * @param sms
	 * @return true：保存成功，false：保存失败
	 */
	public boolean saveSMS(SmsHistory sms) {
		try {
			if(!this.saveSMS(SmiRabbitMQ.serialize(sms))) {
				logger.error("保存短信(reqNo=" + sms.getReqNo() + ")到redis失败");
				return false;
			}
			
		} catch (IOException e) {
			logger.error("保存短信(reqNo=" + sms.getReqNo() + ")到redis失败：" + e.getMessage());
		}
		
		return true;
	}
	
	
	public boolean saveSMS(byte[] smsBytes) {
		try {
			if(smsBytes != null && smsBytes.length > 0) {
				this.getJedis().rpush(listKey, smsBytes);
				return true;
			} 
		} catch (Exception e) {
			logger.error("保存短信字节数据到redis失败：" + e.getMessage());
		} finally {
			this.returnJedis();
		}
		
		return false;
	}
	
	
	/**
	 * 从redis中取得指定数量的短信。此方法支持并发调用（内部设置了串行锁）
	 * @param maxCount 要获取的最大短信数
	 * @return  实际获取到的能成功反序列化的短信列表。不会为null
	 */
	public synchronized List<SmsHistory> fetchSMS(int maxCount) {
		if(maxCount > 0) {
			List<byte[]> tmp = null;
			try {
				tmp = this.getJedis().lrange(listKey, 0, maxCount - 1);
				if(tmp != null && tmp.size() > 0) {
					this.getJedis().ltrim(listKey, tmp.size(), Long.MAX_VALUE);
				}
			} catch (Exception e) {
				logger.error("获取Jedis中的短信失败", e);
			} finally {
				this.returnJedis();
			}
			
			if(tmp != null && tmp.size() > 0) {
				List<SmsHistory> result = new ArrayList<SmsHistory>(tmp.size());
				for(byte[] bs: tmp) {
					try {
						result.add((SmsHistory)SmiRabbitMQ.unserialize(bs));
					} catch (ClassNotFoundException | IOException e) {
						logger.error("反序列化字节序列为短信时失败：" + e.getMessage());						
					}
				}
				
				return result;
			}
		}
		
		return new ArrayList<SmsHistory>(0);
	}
}