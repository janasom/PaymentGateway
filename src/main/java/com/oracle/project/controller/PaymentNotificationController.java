package com.oracle.project.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.adyen.Util.HMACValidator;
import com.adyen.model.notification.NotificationRequest;
import com.adyen.model.notification.NotificationRequestItem;
import com.adyen.notification.NotificationHandler;

@RestController
public class PaymentNotificationController {

	
	@RequestMapping(value="/getnotifications", method=RequestMethod.POST)
	public String getNotifications(@RequestBody String data) {
		String response="";
		try {
		System.out.println("Notification Data is::"+data);
		String hmacKey = "68960C0E9F995F43478DF1F0CBC05DD78332BE32734BDA9BBCCEB0300BCAB92E";
		HMACValidator hmacValidator = new HMACValidator();
		NotificationHandler notificationHandler = new NotificationHandler();
		NotificationRequest notificationRequest = notificationHandler.handleNotificationJson(data);
		List<NotificationRequestItem> notificationRequestItems = notificationRequest.getNotificationItems();
		for ( NotificationRequestItem notificationRequestItem : notificationRequestItems ) {
		    if ( hmacValidator.validateHMAC(notificationRequestItem, hmacKey) ) {
		        String eventCode = notificationRequestItem.getEventCode();
		        System.out.println("Eventcode :"+eventCode);
		    } else {
		        System.out.print("Non valid NotificationRequest");
		    }
		}
		response="[accepted]";
		}catch(Exception ex) {
			System.out.println("Exception in getNotifcation::"+ex);
		}
		return response;
	}
}
