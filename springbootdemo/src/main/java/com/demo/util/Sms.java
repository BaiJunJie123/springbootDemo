package com.demo.util;
import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;


public class Sms{
	public static String sendCode(String userPhone,int code) {
		DefaultProfile profile = DefaultProfile.getProfile("cn-hangzhou", "LTAI4G25NkR9wY8QZcmum4PH", "fosbO8eCoyTV1BFCl8ETXHlPMb7gaS");
		IAcsClient client = new DefaultAcsClient(profile);
		CommonRequest request = new CommonRequest();
		request.setSysMethod(MethodType.POST);
		request.setSysDomain("dysmsapi.aliyuncs.com");
		request.setSysVersion("2017-05-25");
		request.setSysAction("SendSms");
		request.putQueryParameter("RegionId", "cn-hangzhou");
		request.putQueryParameter("PhoneNumbers", userPhone);
		request.putQueryParameter("SignName", "研凯软件");
		request.putQueryParameter("TemplateCode", "SMS_194052173");
		request.putQueryParameter("TemplateParam", "{\"code\":\""+code+"\"}");
		try {
			CommonResponse response = client.getCommonResponse(request);
			JSONObject object = JSONObject.parseObject(response.getData());
			System.out.println(response.getData());
			return object.get("Code").toString();
		} catch (ClientException e) {
			e.printStackTrace();
		}
		return "";
	}

}
