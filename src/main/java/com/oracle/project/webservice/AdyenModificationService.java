package com.oracle.project.webservice;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.modification.CancelRequest;
import com.adyen.model.modification.CaptureRequest;
import com.adyen.model.modification.ModificationResult;
import com.adyen.model.modification.RefundRequest;
import com.adyen.service.Checkout;
import com.adyen.service.Modification;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oracle.project.DTO.PaymentCaptureDto;

@Service
public class AdyenModificationService {

	String api_key="AQEyhmfuXNWTK0Qc+iSfgGU7q+G9QYVMA5BDWWlFw2q9l2VOjQWZZAguTS+CTjcICV6DKa8QwV1bDb7kfNy1WIxIIkxgBw==-q/edryH2AxkHKqnyvvO8aqMRgUpEE1OzBKp8py/OHWc=-?8[RzK[z5_7a&GGg";
	String marchentaccount="OracleFinancialservicesECOM";	
	public String getCapture(PaymentCaptureDto paymentCaptureDto) {
		String responsestr="";
	try {
		Client client = new Client(api_key, Environment.TEST); 
		Modification modification=new Modification(client);
		CaptureRequest captureRequest=new CaptureRequest();
		//captureRequest.setOriginalReference("853593763238096B");
		//captureRequest.fillAmount("1000", "EUR");
		//captureRequest.setMerchantAccount(marchentaccount);
		//captureRequest.setReference("ODR2322218999");
		captureRequest.setOriginalReference(paymentCaptureDto.getOriginalReference());
		captureRequest.fillAmount((String)paymentCaptureDto.getModificationAmount().get("value"),
				(String)paymentCaptureDto.getModificationAmount().get("currency"));
		captureRequest.setMerchantAccount(paymentCaptureDto.getMerchantAccount());
		captureRequest.setReference(paymentCaptureDto.getReference());
		System.out.println("/Capture Request Object:\n" + captureRequest.toString());
		ModificationResult captureModificationResult=modification.capture(captureRequest);
		Gson gson = new GsonBuilder().create();
		String captureModificationResponseStringified = gson.toJson(captureModificationResult);
		System.out.println("/Capture response:\n" + captureModificationResponseStringified);
		responsestr=captureModificationResponseStringified;
	}catch (ApiException | IOException e) {
		System.out.println("error is::"+e.toString());
	}
		return responsestr;
	}
	
	public String refundTransaction() {
		String responsestr="";
	try {
		Client client = new Client(api_key, Environment.TEST); 
		//Checkout checkout = new Checkout(client);
		Modification modification=new Modification(client);
    
		RefundRequest refundRequest=new RefundRequest();
		refundRequest.setOriginalReference("882594225917568F");
		refundRequest.fillAmount("100", "EUR");
		refundRequest.setMerchantAccount(marchentaccount);
		refundRequest.setReference("ODR33211");
		System.out.println("/Refund Request Object:\n" + refundRequest.toString());
		ModificationResult refundModificationResult=modification.refund(refundRequest);
		Gson gson = new GsonBuilder().create();
		String refundModificationResponseStringified = gson.toJson(refundModificationResult);
		System.out.println("/refund response:\n" + refundModificationResponseStringified);
		responsestr=refundModificationResponseStringified;
	}catch (ApiException | IOException e) {
		System.out.println("error is::"+e.toString());
	}
		return responsestr;
	}
	
	public String cancelTransaction() {
		String responsestr="";
	try {
		Client client = new Client(api_key, Environment.TEST); 
		//Checkout checkout = new Checkout(client);
		Modification modification=new Modification(client);
    
		CancelRequest cancelRequest=new CancelRequest();
		cancelRequest.setOriginalReference("852594226021243K");
		//captureRequest.fillAmount("1000", "EUR");
		cancelRequest.setMerchantAccount(marchentaccount);
		cancelRequest.setReference("ODR33211");
		System.out.println("/Cancel Request Object:\n" + cancelRequest.toString());
		ModificationResult cancelModificationResult=modification.cancel(cancelRequest);
		Gson gson = new GsonBuilder().create();
		String cancelModificationResponseStringified = gson.toJson(cancelModificationResult);
		System.out.println("/Cancel response:\n" + cancelModificationResponseStringified);
		responsestr=cancelModificationResponseStringified;
	}catch (ApiException | IOException e) {
		System.out.println("error is::"+e.toString());
	}
		return responsestr;
	}
	
	

}
