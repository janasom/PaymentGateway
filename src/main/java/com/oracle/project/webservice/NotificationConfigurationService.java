package com.oracle.project.webservice;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.marketpay.notification.CreateNotificationConfigurationRequest;
import com.adyen.model.marketpay.notification.CreateNotificationConfigurationResponse;
import com.adyen.model.marketpay.notification.NotificationConfigurationDetails;
import com.adyen.model.marketpay.notification.NotificationConfigurationDetails.SslProtocolEnum;
import com.adyen.model.marketpay.notification.NotificationEventConfiguration;
import com.adyen.model.marketpay.notification.NotificationEventConfiguration.EventTypeEnum;
import com.adyen.model.marketpay.notification.NotificationEventConfiguration.IncludeModeEnum;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.service.Modification;
import com.adyen.service.Notification;
import com.adyen.service.exception.ApiException;
import com.adyen.service.resource.notification.CreateNotificationConfiguration;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oracle.project.DTO.PaymentCaptureDto;

@Service
public class NotificationConfigurationService {

	
	String api_key="AQEyhmfuXNWTK0Qc+iSfgGU7q+G9QYVMA5BDWWlFw2q9l2VOjQWZZAguTS+CTjcICV6DKa8QwV1bDb7kfNy1WIxIIkxgBw==-q/edryH2AxkHKqnyvvO8aqMRgUpEE1OzBKp8py/OHWc=-?8[RzK[z5_7a&GGg";
	//String marchentaccount="OracleFinancialservicesECOM";
	
	public String setNotificationConfig() {
		String responsestr="";
	try {
		Client client = new Client(api_key, Environment.TEST); 
		Notification notificationConfig=new Notification(client);
		
		CreateNotificationConfigurationRequest notificationConfigRequest=new CreateNotificationConfigurationRequest();
		NotificationConfigurationDetails configurationDetails=new NotificationConfigurationDetails();
		configurationDetails.active(true);
		configurationDetails.description("Ths is for capture event notification");
		NotificationEventConfiguration notificationEventConfig=new NotificationEventConfiguration();
		notificationEventConfig.eventType(EventTypeEnum.ACCOUNT_HOLDER_VERIFICATION);
		notificationEventConfig.setIncludeMode(IncludeModeEnum.INCLUDE);
		List<NotificationEventConfiguration> eventConfig=new ArrayList<NotificationEventConfiguration>();
		eventConfig.add(notificationEventConfig);
		configurationDetails.setEventConfigs(eventConfig);
		configurationDetails.setNotifyURL("http://152.67.2.3/getnotifications");
		configurationDetails.setNotifyUsername("");
		configurationDetails.setNotifyPassword("");
		configurationDetails.setSslProtocol(SslProtocolEnum.TLSV12);
		
		
		notificationConfigRequest.configurationDetails(configurationDetails);
		
		System.out.println("/NotificationConfig Request Object:\n" + notificationConfigRequest.toString());
		CreateNotificationConfigurationResponse captureModificationResult= notificationConfig.createNotificationConfiguration(notificationConfigRequest);
		Gson gson = new GsonBuilder().create();
		String captureModificationResponseStringified = gson.toJson(captureModificationResult);
		System.out.println("/NotificationConfig response:\n" + captureModificationResponseStringified);
		responsestr=captureModificationResponseStringified;
	}catch (ApiException | IOException e) {
		System.out.println("error is::"+e.toString());
	}
	 catch (Exception e) {
		System.out.println("error is::"+e.toString());
		e.printStackTrace();
	}
		return responsestr;
	}
}
