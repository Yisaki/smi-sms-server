package com.smi.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.smi.FinalValue;
import com.smi.dao.SmsReq2PhoneMapper;
import com.smi.model.Req2Phone;

@Service
public class SmsReq2PhoneService {

	@Autowired
	private SmsReq2PhoneMapper smsReq2PhoneMapper;

	/**
	 * 批量保存。内部会按年月将数据保存到不同的表中去
	 * @param list 待保存的序列号与手机号的对应关系列表
	 * @return 返回实际保存到数据库中的数据数
	 */
	public int batchSave(List<Req2Phone> list) {
		Map<String, List<Req2Phone>> phoneGroups = new HashMap<String, List<Req2Phone>>();
		//下面对数据按请求序列号中的年月进行分组
		for(Req2Phone rp : list) {
			String yearMoth = rp.getReqNo().substring(0, 6);
			List<Req2Phone> tmp = (List<Req2Phone>)phoneGroups.get(yearMoth);
			if(tmp == null) {
				tmp = new ArrayList<Req2Phone>();
				phoneGroups.put(yearMoth, tmp);
			}
			tmp.add(rp);
		}
		
		int count = 0;
		for(String key: phoneGroups.keySet()) {
			count += this.batchSaveInner(FinalValue.SMS_REQ2PHONE_TABLE_PREFIX + key, phoneGroups.get(key));
		}
		
		return count;
	}
	
	/**
	 * 将数据保存到指定表中去(若表不存在，则会自动创建)
	 * @param tableName
	 * @param list
	 * @return
	 */
	private int batchSaveInner(String tableName, List<Req2Phone> list) {
		try {
			if(list != null && list.size() > 0) {
				return smsReq2PhoneMapper.batchSave(tableName, list);
			}
		} catch(Exception e) {
			if(e.getMessage().indexOf(tableName) >= 0 && e.getMessage().indexOf("doesn't exist") >= 0) {
				smsReq2PhoneMapper.createTable(tableName);
				return smsReq2PhoneMapper.batchSave(tableName, list);
			}
			throw e;
		}
		
		return 0;
	}

}
