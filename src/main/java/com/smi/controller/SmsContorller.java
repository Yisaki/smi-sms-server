package com.smi.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.smi.FinalValue;
import com.smi.model.MQInfo;
import com.smi.model.SmsAccount;
import com.smi.service.BizConfigChecker;
import com.smi.service.CachMgrService;
import com.smi.service.SmsProducerService;
import com.smi.service.SmsReportService;
import com.smi.utils.IpUtils;
import com.smi.vo.ResultVo;
import com.smi.vo.ReturnMsg;

@RestController
@RequestMapping("/sms")
public class SmsContorller {
	private Logger logger = Logger.getLogger(this.getClass());
		
	@Autowired
	private SmsProducerService smsProducerService;
	
	
	@Autowired
	private SmsReportService smsReportService;

	
	@Autowired
	private CachMgrService cachMgrService;
	
	
	/**
	 * 短信发送（与/batch相同）。注：当前短信服务平台未对短信接口被恶意调用（高并发高频率调用）进行有效控制，故各业务系统要自己提供短信功能被恶意调用的防范机制
	 * @param req  
	 * @param phone  手机号码(若手机号码是用英文逗号连接多个手机号，则手机号个数最多不能超过FinalValue.MAX_PHONES_PER_REQUEST条)
	 * @param content  短信内容
	 * @param channel  渠道号(各业务系统使用的渠道号与IP绑定，绑定关系配置在sms_biz_config表中，渠道号则配置在)
	 * @return
	 */
	@RequestMapping(value ="/send", method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ReturnMsg send(
			HttpServletRequest req,
			@RequestParam(value = "phone", required = true) String phone,
			@RequestParam(value = "content", required = true) String content,
			@RequestParam(value = "channel", required = true) String channel) {

		return sendMsg(req, phone, content, channel, "/send");

	}

	
	/**
	 * 发送短信（与/send相同）。注：当前短信服务平台未对短信接口被恶意调用（高并发高频率调用）进行有效控制，故各业务系统要自己提供短信功能被恶意调用的防范机制
	 * @param req
	 * @param phone	手机号码(若手机号码是用英文逗号连接多个手机号，则手机号个数最多不能超过FinalValue.MAX_PHONES_PER_REQUEST条)
	 * @param content	短信内容
	 * @param channel	渠道号(各业务系统使用的渠道号与IP绑定，绑定关系配置在sms_biz_config表中)
	 * @return
	 */
	@RequestMapping(value ="/batch", method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ReturnMsg batch(
			HttpServletRequest req,
			@RequestParam(value = "phone", required = true) String phone,
			@RequestParam(value = "content", required = true) String content,
			@RequestParam(value = "channel", required = true) String channel) {
		
		return sendMsg(req, phone, content, channel, "/batch");
	}
	
	
	/**
	 * 短信发送接口,主要提供给开放平台用。注：此接口返回结果与/send和/batch接口返回的结果结构不一样。当前短信服务平台未对短信接口被恶意调用（高并发高频率调用）进行有效控制，故各业务系统要自己提供短信功能被恶意调用的防范机制
	 * @param req
	 * @param phone	手机号码(若手机号码是用英文逗号连接多个手机号，则手机号个数最多不能超过FinalValue.MAX_PHONES_PER_REQUEST条)
	 * @param content	短信内容
	 * @param channel	渠道号(各业务系统使用的渠道号与IP绑定，绑定关系配置在sms_biz_config表中)
	 * @return
	 */
	@RequestMapping(value ="/dispatch", method={RequestMethod.POST,RequestMethod.GET})
	@ResponseBody
	public ResultVo dispatch(
			HttpServletRequest req,
			@RequestParam(value = "phone", required = true) String phone,
			@RequestParam(value = "content", required = true) String content,
			@RequestParam(value = "channel", required = true) String channel) {

		ResultVo result = new ResultVo();		
		
		ReturnMsg msg = sendMsg(req, phone, content, channel, "/dispatch");
		
		Map data = new HashMap();
		data.put("status", FinalValue.RtnCode.RTN_REC_OK.equals(msg.getCode()) ? 1 : 0);
		data.put("reqNo",msg.getReqNo());
		result.setCode(1);
		result.setData(data);
		
		return result;
	}
	
	
	private ReturnMsg sendMsg(HttpServletRequest req, String phone, String content, String channel, String fromUrl) {
		String clientIP = IpUtils.getClientIp(req);
		Object[] conf = BizConfigChecker.check(clientIP, channel);
		if(conf == null) {
			ReturnMsg msg = new ReturnMsg();
			msg.setCode(FinalValue.RtnCode.SEND_STATUS_REJECT);
			msg.setMsg("非法请求");
			return msg;
		}
		
		String reqNo = smsProducerService.makeReqNo();

		logger.info(fromUrl + " =>[reqNo:" + reqNo + ", Content:" + content + ", phones:" + phone + ", channel:" + channel + ", ip:" + clientIP + "]");

		
		return smsProducerService.sendMsg(reqNo, channel, content, phone, (SmsAccount)conf[0], (MQInfo)conf[1], clientIP);
	}
	
	
	/**
	 * 接收短信报告并保存
	 * @param request
	 * @return
	 */
	@RequestMapping(value ="/report", method = { RequestMethod.POST })
	@ResponseBody
	public Map report(HttpServletRequest req)
	{
		Date now = new Date();
		Map resp = new HashMap(1);
		resp.put("status", "success");
		
		String report = req.getParameter("report");
		if(report != null) {
			String reqNo = smsProducerService.makeReqNo();
			
			logger.info("/report =>(请求号=" + reqNo + ")[report:" + report + ", ip:" + IpUtils.getClientIp(req) + "]");
			
			List<Map> reports = (List<Map>)JSONObject.parseObject(report, Map.class).get("reports");
			if(reports != null && reports.size() > 0) {
				try {
					smsReportService.batchSave(reports, now);
				} catch (Exception e) {
					logger.error("保存短信状态报告(请求号=" + reqNo + ")失败：" + e.getMessage());
					resp.put("status", "fail");
					return resp;
				}
			}
		}
		
		return resp;
	}
	
	
	/**
	 * 重新加载数据中最新的配置信息到内存
	 * @return
	 */
	@RequestMapping(value ="/reloadConfigs", method = { RequestMethod.GET})
	@ResponseBody
	public String refreshConfig() {
		cachMgrService.refreshCache();
		
		return "Reload configs success!";
	}
}
