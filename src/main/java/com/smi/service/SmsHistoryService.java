package com.smi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.FinalValue;
import com.smi.dao.SmsHistoryMapper;
import com.smi.model.SmsHistory;

@Service
public class SmsHistoryService {

	@Autowired
	private SmsHistoryMapper smsHistoryMapper;

	/**
	 * 批量保存短信。内部会按年月将短信保存到不同的短信表中去
	 * @param list 待保存的短信列表
	 * @return 返回实际保存到数据库中的短信数
	 */
	public int batchSave(List<SmsHistory> list) {
		Map<String, List<SmsHistory>> smsGroups = new HashMap<String, List<SmsHistory>>();
		//下面对短信按请求序列号中的年月进行分组(注：短信状态表中的分表逻辑也必须要按请求序列号中的年月进行分表，否则可能会出现某一短信的日志记录和状态报告出现在不同年月的日志表和状态报告表中)
		for(SmsHistory sms : list) {
			String yearMoth = sms.getReqNo().substring(0, 6);
			List<SmsHistory> tmp = (List<SmsHistory>)smsGroups.get(yearMoth);
			if(tmp == null) {
				tmp = new ArrayList<SmsHistory>();
				smsGroups.put(yearMoth, tmp);
			}
			tmp.add(sms);
		}
		
		int count = 0;
		for(String key: smsGroups.keySet()) {
			count += this.batchSaveInner(FinalValue.SMS_LOG_TABLE_PREFIX + key, smsGroups.get(key));
		}
		
		return count;
	}
	
	/**
	 * 将短信保存到指定表中去(若表不存在，则会自动创建)
	 * @param tableName
	 * @param list
	 * @return
	 */
	private int batchSaveInner(String tableName, List<SmsHistory> list) {
		try {
			if(list != null && list.size() > 0) {
				return smsHistoryMapper.batchSave(tableName, list);
			}
		} catch(Exception e) {
			if(e.getMessage().indexOf(tableName) >= 0 && e.getMessage().indexOf("doesn't exist") >= 0) {
				smsHistoryMapper.createTable(tableName);
				return smsHistoryMapper.batchSave(tableName, list);
			}
			throw e;
		}
		
		return 0;
	}

}
