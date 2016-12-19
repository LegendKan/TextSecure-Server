package org.whispersystems.textsecuregcm.sms;

import static com.codahale.metrics.MetricRegistry.name;

import java.util.Map;

import org.whispersystems.textsecuregcm.configuration.YunpianConfiguration;
import org.whispersystems.textsecuregcm.util.Constants;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.google.common.base.Optional;
import com.yunpian.sdk.YunpianClient;
import com.yunpian.sdk.model.Result;
import com.yunpian.sdk.model.SmsSingleSend;
import com.yunpian.sdk.model.VoiceSend;

public class YunpianSmsSender {
	
	private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);
	private final Meter          smsMeter       = metricRegistry.meter(name(getClass(), "sms", "delivered"));
	private final Meter          voxMeter       = metricRegistry.meter(name(getClass(), "vox", "delivered"));
	
	private final String            apiKey;
	
	public YunpianSmsSender(YunpianConfiguration config){
		this.apiKey = config.getApiKey();
	}
	
	public void deliverSmsVerification(String destination, Optional<String> clientType, String verificationCode) throws Exception{
		YunpianClient clnt = new YunpianClient(this.apiKey).init();
		Map<String, String> param = clnt.newParam(2);
        param.put("mobile", destination);
        if ("ios".equals(clientType.orNull())) {
        	param.put("text", String.format(SmsSender.SMS_IOS_VERIFICATION_TEXT, verificationCode, verificationCode));
        }else{
        	param.put("text", String.format(SmsSender.SMS_VERIFICATION_TEXT, verificationCode));
        }
        
        Result<SmsSingleSend> r = clnt.sms().single_send(param);
        int code = r.getCode();
        if(code != 0){
        	throw new Exception("Send Failed: "+r.getMsg());
        }
        clnt.close();
        
        smsMeter.mark();
	}
	
	public void deliverVoxVerification(String destination, String verificationCode) throws Exception{
		YunpianClient clnt = new YunpianClient(this.apiKey).init();
		Map<String, String> param = clnt.newParam(2);
        param.put("mobile", destination);
        param.put("code", verificationCode);
        Result<VoiceSend> r = clnt.voice().send(param);
        int code = r.getCode();
        if(code != 0){
        	throw new Exception("Call Failed: "+r.getMsg());
        }
        clnt.close();
        
        voxMeter.mark();
	}
	  

}
