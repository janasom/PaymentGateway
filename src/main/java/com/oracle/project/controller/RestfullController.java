package com.oracle.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oracle.project.DTO.PaymentCaptureDto;
import com.oracle.project.webservice.AdyenModificationService;
import com.oracle.project.webservice.AdyenServices;
import com.oracle.project.webservice.NotificationConfigurationService;
import com.oracle.project.webservice.OnboardingService;

@RestController
public class RestfullController {

	@Autowired
	AdyenServices adynServices;
	
	@Autowired
	AdyenModificationService adyenModificationService;
	
	@Autowired
	NotificationConfigurationService notificationConfigurationService;
	
	@Autowired
	OnboardingService onboardingService;
	
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String defaultMetod() {
		System.out.println("Inside default controller");
		//String empjson="[{\"EmpName\":\"Somnath Jana\",\"EmpDept\":\"IT\",\"EmpAge\":\"27\"},{\"EmpName\":\"Sanu Das\",\"EmpDept\":\"Finance\",\"EmpAge\":\"25\"},{\"EmpName\":\"Goutam Paul\",\"EmpDept\":\"Management\",\"EmpAge\":\"29\"}]";
	   return "Hello controller";
	}
	
	
	@RequestMapping(value="/getEmpData", method=RequestMethod.GET)
	public String getEmpDataData() {
		System.out.println("Inside controller");
		String empjson="[{\"EmpName\":\"Somnath Jana\",\"EmpDept\":\"IT\",\"EmpAge\":\"27\"},{\"EmpName\":\"Sanu Das\",\"EmpDept\":\"Finance\",\"EmpAge\":\"25\"},{\"EmpName\":\"Goutam Paul\",\"EmpDept\":\"Management\",\"EmpAge\":\"29\"}]";
	   return empjson;
	}
	
	
	@GetMapping("/getPaymentMethods")
	@Cacheable(value = "getPaymentMethods")
	//@CacheEvict(value = "getPaymentMethods")
	public String getPaymentMethods() {
		return adynServices.getPaymentMethods();
	}
	
	@RequestMapping(value="/makePayments", method=RequestMethod.GET)
	public String makePayments() {
		//return adynServices.makePayment();
		return adynServices.makeCardPayment();
		//return adynServices.makeRecurringCardPayment();
	}
	
	@RequestMapping(value="/authorise", method=RequestMethod.GET)
	public String authorise() {
		return adynServices.authorise();
	}
	
	@RequestMapping(value="/capturereq", method=RequestMethod.POST)
	public String capture(@RequestBody String data ) {
		System.out.println("Request Data is::"+data);
		PaymentCaptureDto paymentCaptureDto=new PaymentCaptureDto();
		try {
			 paymentCaptureDto=new ObjectMapper().readValue(data, PaymentCaptureDto.class);
			 System.out.println("Object is:::"+paymentCaptureDto);
		} catch (JsonMappingException e) {
			System.out.println(e);
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			System.out.println(e);
			e.printStackTrace();
		}
		return adyenModificationService.getCapture(paymentCaptureDto);
	}
	
	@RequestMapping(value="/refundreq", method=RequestMethod.GET)
	public String refund() {
		return adyenModificationService.refundTransaction();
	}
	
	@RequestMapping(value="/cancelreq", method=RequestMethod.GET)
	public String cancel() {
		return adyenModificationService.cancelTransaction();
	}
	@RequestMapping(value="/notificationConfig", method=RequestMethod.GET)
	public String notificationConfig() {
		return notificationConfigurationService.setNotificationConfig();
	}
	@RequestMapping(value="/createaccount", method=RequestMethod.GET)
	public String createAccount() {
		return onboardingService.createAccountHolder();
	}
	
	@RequestMapping(value="/getonboardingurl", method=RequestMethod.GET)
	public String getOnBoardingUrl() {
		return onboardingService.getOnboardingUrl();
	}
	
}
