package com.smi.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.smi.model.SmsHistory;
import com.smi.utils.FileUtils;

/**
 * 在发送完成的短信无法保存到redis时，会将此短信缓存到本地文件中。本地有一个线程会定时检查与redis的连接情况，当连接正常时会将缓存的这些已发送短信继续存入redis
 * @author Ryan Wu
 *
 */
public class SmsFileBuffer {
	private static SmsFileBuffer instance = new SmsFileBuffer();
	
	public static SmsFileBuffer getInstance() {
		return instance;
	}
	
	
	private File smsBufFile;
	private Logger logger = Logger.getLogger(this.getClass());
	
	
	private SmsFileBuffer() {
		smsBufFile = new File(FileUtils.getJarDirPath() + "smsBuffer.txt");
		
		//启动定期检查短信本地缓存文件中是否有短信，若有且redis连接正常，则将短信迁移到redis中保存
		(new Thread(new Runnable() {
			private int time2SleepInSecond = 30 * 60;
			
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(time2SleepInSecond * 1000l);
					} catch (InterruptedException e) {}
					
					try {
						List<SmsHistory> bufSms = getBufferedSms();
						if(bufSms != null && bufSms.size() > 0) {
							//TODO
						}
					} catch (ClassNotFoundException | IOException e) {
						logger.error("获取短信本地缓存文件内容失败：" + e.getMessage());
					} 
				}
				
			}})).start();
	}
	
	
	
	public synchronized boolean bufferSms(SmsHistory sms) {
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(smsBufFile, true);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(sms);
			oos.flush();
		} catch (IOException e) {
			logger.error("缓存短信（reqNo=" + sms.getReqNo() + ", phone=" + sms.getPhoneNo() + ", content=" + sms.getContent() + "）到本地文件失败：" + e.getMessage());
			return false;
		} finally {
			if(oos != null)
				try {
					oos.close();
				} catch (IOException e) {
					logger.error("关闭ObjectOutputStream流异常：" + e.getMessage());
				}
			if(fos!= null)
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("关闭FileOutputStream流异常：" + e.getMessage());
				}
		}
		return true;
		
	}
	
	/**
	 * 获取所有缓存在本地短信缓存文件中的短信，若读取出的短信数量大于0，则会清空此文件内容
	 * @return  返回null表示缓存文件不存在
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private synchronized List<SmsHistory> getBufferedSms() throws IOException, ClassNotFoundException {
		List<SmsHistory> result = null;
		
		if(smsBufFile.exists()) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(smsBufFile);
				ois = new ObjectInputStream(fis);
				result = new ArrayList<SmsHistory>();
				
				Object sms = ois.readObject();				
				while(sms != null) {
					result.add((SmsHistory)sms);
					sms = ois.readObject();
				}
			} finally {
				if(ois != null) {
					try {
						ois.close();
					} catch (Exception e) {
						logger.error("关闭ObjectInputStream流异常：" + e.getMessage());
					}
				}
				
				if(fis != null) {
					try {
						fis.close();
					} catch (Exception e) {
						logger.error("关闭FileInputStream流异常：" + e.getMessage());
					}
				}
			}
			
			//清空缓存文件
			if(result.size() > 0) {
				FileWriter fw = null;
				try {
					fw = new FileWriter(smsBufFile);
					fw.write("");
					fw.flush();
				} catch (Exception e) {
					logger.error("清空短信本地缓存文件失败（" + e.getMessage() + "），故将删除此文件。");
					smsBufFile.delete();
				} finally {
					try {
						if(fw != null)
							fw.close();
					} catch (Exception e) {}
				}
			}
		}
		
		return result;
	}
	
}
