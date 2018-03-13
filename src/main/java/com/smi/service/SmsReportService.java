package com.smi.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.FinalValue;
import com.smi.dao.SmsReportMapper;


@Service
public class SmsReportService {
	private Logger logger = Logger.getLogger(this.getClass());
	
	private Pattern reqNoPattern = Pattern.compile("^(20[12]\\d(?:0[1-9]|1[012]))[0-9a-zA-Z]+");
	
	@Autowired
	private SmsReportMapper smsReportMapper;
	
	/**
	 * 批量保存状态报告(内部会根据状态报告的请求序列号对状态报告进行分表保存)
	 * @param list
	 * @param currentTime 星美短信服务平台接收状态报告的时间
	 * @return
	 * @throws Exception
	 */
	public int batchSave(List<Map> list, Date currentTime) throws Exception {
		Map<String, List<Map>> reportGroups = new HashMap<String, List<Map>>(3);
		for(Map report : list) {
			String msgId = report.get("msgid").toString();
			Matcher matcher = reqNoPattern.matcher(msgId);
			if(matcher.matches()) {
				String yearMoth = matcher.group(1);
				List<Map> tmp = (List<Map>)reportGroups.get(yearMoth);
				if(tmp == null) {
					tmp = new ArrayList<Map>();
					reportGroups.put(yearMoth, tmp);
				}
				
				report.put("currentTime", currentTime);				
				tmp.add(report);
			} else {
				logger.error("reportError=> 返回的状态报告的msgid不是星美短信服务平台发送的msgid：" + report.toString());
			}
		}
		
		int count = 0;
		for(String key : reportGroups.keySet()) {
			count += this.batchSaveInner(FinalValue.SMS_REPORT_TABLE_PREFIX + key, reportGroups.get(key));
		}
		
		return count;
		
	}
	
	/**
	 * 将短信状态报告保存到指定的数据库表中去(若表不存在，则会自动创建)
	 * @param tableName
	 * @param list
	 * @return
	 * @throws Exception
	 */
	private int batchSaveInner(String tableName, List<Map> list) throws Exception {
		try {
			if(list != null && list.size() > 0) {
				return smsReportMapper.batchSave(tableName, list);
			}
		} catch(Exception e) {
			if(e.getMessage().indexOf(tableName) >= 0 && e.getMessage().indexOf("doesn't exist") >= 0) {
				smsReportMapper.createTable(tableName);
				return smsReportMapper.batchSave(tableName, list);
			}
			throw e;
		}
		
		return 0;
		
	}

}
