package com.oracle.project.webservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.adyen.Client;
import com.adyen.enums.Environment;
import com.adyen.model.Amount;
import com.adyen.model.PaymentRequest;
import com.adyen.model.PaymentResult;
import com.adyen.model.checkout.CheckoutPaymentsAction;
import com.adyen.model.checkout.LineItem;
import com.adyen.model.checkout.PaymentMethod;
import com.adyen.model.checkout.PaymentMethodDetails;
import com.adyen.model.checkout.PaymentMethodsRequest;
import com.adyen.model.checkout.PaymentMethodsResponse;
import com.adyen.model.checkout.PaymentsRequest;
import com.adyen.model.checkout.PaymentsResponse;
import com.adyen.service.Checkout;
import com.adyen.service.Payment;
import com.adyen.service.exception.ApiException;
import com.adyen.service.resource.payment.Authorise;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oracle.project.model.PaymentDetailsCustom;

@Service
public class AdyenServices {

	String adyenurl="https://checkout-test.adyen.com/v52/paymentMethods";
	String api_key="AQEyhmfuXNWTK0Qc+iSfgGU7q+G9QYVMA5BDWWlFw2q9l2VOjQWZZAguTS+CTjcICV6DKa8QwV1bDb7kfNy1WIxIIkxgBw==-q/edryH2AxkHKqnyvvO8aqMRgUpEE1OzBKp8py/OHWc=-?8[RzK[z5_7a&GGg";
	String marchentaccount="OracleFinancialservicesECOM";
	String type="dotpay";
	public String getPaymentMethods() {
		String responsestr="";
		Client client = new Client(api_key, Environment.TEST); 
		Checkout checkout = new Checkout(client);

		PaymentMethodsRequest paymentMethodsRequest = new PaymentMethodsRequest();
		paymentMethodsRequest.setMerchantAccount(marchentaccount);

		Amount amount = new Amount();
		/*if (type.equals("dotpay")) {
			amount.setCurrency("PLN");
		} else {
			amount.setCurrency("EUR");
		}*/
		amount.setCurrency("INR");
		amount.setValue(1L);
		paymentMethodsRequest.setAmount(amount);
		paymentMethodsRequest.setCountryCode("IN");
		paymentMethodsRequest.setChannel(PaymentMethodsRequest.ChannelEnum.WEB);
		paymentMethodsRequest.setShopperReference("somnath.jana@gmail.com");
		System.out.println("/paymentMethods context:\n" + paymentMethodsRequest.toString());

		try {
			PaymentMethodsResponse response = checkout.paymentMethods(paymentMethodsRequest);
			System.out.println("Payment method groups::"+response.getPaymentMethods());
			Gson gson = new GsonBuilder().create();
			//Gson gson = new Gson();
			String paymentMethodsResponseStringified = gson.toJson(response);
			
			System.out.println("/paymentMethods response:\n" + paymentMethodsResponseStringified);
			//return paymentMethodsResponseStringified;
			responsestr=paymentMethodsResponseStringified;
		} catch (ApiException | IOException e) {
			System.out.println("error is::"+e.toString());
		}
		
	
		return responsestr;
	}
	
	public String makePayment() {
		PaymentsRequest paymentsRequest=new PaymentsRequest();
		Client client = new Client(api_key, Environment.TEST);
		Checkout checkout = new Checkout(client);
		PaymentMethod paymentMethod=new PaymentMethod();
		PaymentMethodDetails paymentMethodDetails=new PaymentMethodDetails() {
            String type;
			@Override
			public String getType() {
				return type;
			}

			@Override
			public void setType(String type) {
				this.type=type;
			}
		};
		
		/*PaymentDetailsCustom paymentMethodDetails=new PaymentDetailsCustom() ;
			
		paymentMethodDetails.setType("scheme");
		paymentMethodDetails.setNumber("4212345678901237");
		paymentMethodDetails.setExpiryMonth("10");
		paymentMethodDetails.setExpiryYear("2020");
		paymentMethodDetails.setHolderName("John Smith");
		paymentMethodDetails.setCvc("737");*/
		paymentMethodDetails.setType("scheme");
		paymentsRequest.setPaymentMethod(paymentMethodDetails);
		
		paymentsRequest.addCardData("4212345678901237", "10", "2020", "737", "John Smith");

		String type = paymentsRequest.getPaymentMethod().getType();
        System.out.println("payment method type::"+type);
		setAmount(paymentsRequest, type);
		paymentsRequest.setChannel(PaymentsRequest.ChannelEnum.WEB);
		paymentsRequest.setMerchantAccount(marchentaccount);
		paymentsRequest.setReturnUrl("http://localhost:8085/getEmpData");

		paymentsRequest.setReference("Java Integration Test Reference");
		paymentsRequest.setShopperReference("Java Checkout Shopper");

		paymentsRequest.setCountryCode("NL");

		if (type.equals("alipay")) {
			paymentsRequest.setCountryCode("CN");

		} else if (type.contains("klarna")) {
			paymentsRequest.setShopperEmail("myEmail@adyen.com");
			paymentsRequest.setShopperLocale("en_US");

			addLineItems(paymentsRequest);

		} else if (type.equals("directEbanking") || type.equals("giropay")) {
			paymentsRequest.countryCode("DE");

		} else if (type.equals("dotpay")) {
			paymentsRequest.countryCode("PL");
			paymentsRequest.getAmount().setCurrency("PLN");

		} else if (type.equals("scheme")) {
			paymentsRequest.setOrigin("http://localhost:8080");
			//paymentsRequest.putAdditionalDataItem("allow3DS2", "true");

		} else if (type.equals("ach")) {
			paymentsRequest.countryCode("US");
		}

		System.out.println("/payments request:\n" + paymentsRequest.toString());

		try {
			PaymentsResponse response = checkout.payments(paymentsRequest);
			PaymentsResponse formattedResponse =formatResponseForFrontend(response);

			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String paymentsResponse = gson.toJson(formattedResponse);
			System.out.println("/payments response:\n" + paymentsResponse);
			return paymentsResponse;
		} catch (ApiException | IOException e) {
			System.out.println("Exception in makePayments:::"+e);
			return e.toString();
		}
	}


	public static PaymentsResponse formatResponseForFrontend(PaymentsResponse unformattedResponse) throws IOException {

		PaymentsResponse.ResultCodeEnum resultCode = unformattedResponse.getResultCode();
		if (resultCode != null) {
			PaymentsResponse newPaymentsResponse = new PaymentsResponse();
			newPaymentsResponse.setResultCode(resultCode);

			CheckoutPaymentsAction action = unformattedResponse.getAction();
			if (action != null) {
				newPaymentsResponse.setAction(action);
			}
			return newPaymentsResponse;
		} else {
			throw new IOException();
		}
	}
	
	private static void setAmount(PaymentsRequest paymentsRequest, String type) {
		Amount amount = new Amount();

		String currency;

		switch (type) {
			case "alipay":
				currency = "CNY";
				break;
			case "dotpay":
				currency = "PLN";
				break;
			case "boletobancario":
				currency = "BRL";
				break;
			case "ach":
				currency = "USD";
				break;
			default:
				currency = "EUR";
		}

		amount.setCurrency(currency);
		amount.setValue(1000L);
		paymentsRequest.setAmount(amount);
	}

	private static void addLineItems(PaymentsRequest paymentsRequest) {
		String item1 = "{\n" +
				"                \"quantity\": \"1\",\n" +
				"                \"amountExcludingTax\": \"450\",\n" +
				"                \"taxPercentage\": \"1111\",\n" +
				"                \"description\": \"Sunglasses\",\n" +
				"                \"id\": \"Item #1\",\n" +
				"                \"taxAmount\": \"50\",\n" +
				"                \"amountIncludingTax\": \"500\",\n" +
				"                \"taxCategory\": \"High\"\n" +
				"            }";
		String item2 = "{\n" +
				"                \"quantity\": \"1\",\n" +
				"                \"amountExcludingTax\": \"450\",\n" +
				"                \"taxPercentage\": \"1111\",\n" +
				"                \"description\": \"Headphones\",\n" +
				"                \"id\": \"Item #2\",\n" +
				"                \"taxAmount\": \"50\",\n" +
				"                \"amountIncludingTax\": \"500\",\n" +
				"                \"taxCategory\": \"High\"\n" +
				"            }";

		Gson gson = new GsonBuilder().create();
		LineItem lineItem1 = gson.fromJson(item1, LineItem.class);
		LineItem lineItem2 = gson.fromJson(item2, LineItem.class);

		paymentsRequest.addLineItemsItem(lineItem1);
		paymentsRequest.addLineItemsItem(lineItem2);
	}
	
	public String makeCardPayment() {
       String resStr="";
		try {
		Client client = new Client(api_key,Environment.TEST);
		 
		Checkout checkout = new Checkout(client);
		PaymentsRequest paymentsRequest = new PaymentsRequest();
		paymentsRequest.setMerchantAccount(marchentaccount);
		Amount amount = new Amount();
		amount.setCurrency("EUR");
		amount.setValue(1500L);
		paymentsRequest.setAmount(amount);
		String encryptedCardNumber = "test_4111111111111111";
		String encryptedExpiryMonth = "test_03";
		String encryptedExpiryYear = "test_2030";
		String encryptedSecurityCode = "test_737";
		paymentsRequest.setReference("ODR33211");
		paymentsRequest.addEncryptedCardData(encryptedCardNumber,encryptedExpiryMonth, encryptedExpiryYear, encryptedSecurityCode,"John Smith");
		paymentsRequest.setReturnUrl("http://localhost:8085/getEmpData");
		System.out.println("/payments request:\n"+paymentsRequest.toString());
		PaymentsResponse paymentsResponse = checkout.payments(paymentsRequest);
		PaymentsResponse formattedResponse =formatResponseForFrontend(paymentsResponse);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String paymentsResponse1 = gson.toJson(formattedResponse);
		System.out.println("/payments response:\n" + paymentsResponse1);
		resStr=paymentsResponse1;
		}catch (ApiException | IOException e) {
			System.out.println("Exception in makecardPayments:::"+e);
			return e.toString();
		}
		return resStr;
	}
	
	public String makeRecurringCardPayment() {
	       String resStr="";
			try {
			Client client = new Client(api_key,Environment.TEST);
			 
			Checkout checkout = new Checkout(client);
			PaymentsRequest paymentsRequest = new PaymentsRequest();
			paymentsRequest.setMerchantAccount(marchentaccount);
			Amount amount = new Amount();
			amount.setCurrency("EUR");
			amount.setValue(1000L);
			paymentsRequest.setAmount(amount);
			String encryptedCardNumber = "test_4111111111111111";
			String encryptedExpiryMonth = "test_03";
			String encryptedExpiryYear = "test_2030";
			String encryptedSecurityCode = "test_737";
			paymentsRequest.setReference("Your order number");
			paymentsRequest.setStorePaymentMethod(true);
			paymentsRequest.setShopperReference("somnath.jana@gmail.com");
			paymentsRequest.addEncryptedCardData(encryptedCardNumber,encryptedExpiryMonth, encryptedExpiryYear, encryptedSecurityCode,"John Smith");
			paymentsRequest.setReturnUrl("http://localhost:8085/getEmpData");
			System.out.println("/payments request:\n"+paymentsRequest.toString());
			PaymentsResponse paymentsResponse = checkout.payments(paymentsRequest);
			PaymentsResponse formattedResponse =formatResponseForFrontend(paymentsResponse);

			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String paymentsResponse1 = gson.toJson(formattedResponse);
			System.out.println("/payments response:\n" + paymentsResponse1);
			resStr=paymentsResponse1;
			}catch (ApiException | IOException e) {
				System.out.println("Exception in makecardPayments:::"+e);
				return e.toString();
			}
			return resStr;
		}
	
	public String authorise() {
		String resStr="";
		
		
		/*String authoriseUrl="https://pal-test.adyen.com/pal/servlet/Payment/v52/authorise";
		StringBuilder sb = new StringBuilder();
		try {
		URL url = new URL(authoriseUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Cache-Control","no-cache");
		conn.setRequestProperty("X-API-Key",api_key);
		
		
		String input = "{\"card\": {\"number\": \"4111111111111111\",\"expiryMonth\": \"10\",\"expiryYear\": \"2020\",\"cvc\": \"737\",\"holderName\": \"John Smith\"},\"amount\": {\"value\": 1500,\"currency\": \"EUR\" },\"reference\": \"ODR232221\",\"merchantAccount\": \"OracleFinancialservicesECOM\"}";			

		OutputStream os;				
		os = conn.getOutputStream();
		os.write(input.getBytes());
		os.flush();

		
		String line;
		BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
		while ((line = br.readLine()) != null) 
		{
			sb.append(line+"\n");
		}
		br.close();
		conn.disconnect();
		resStr=sb.toString();
		}catch(Exception ex) {
			System.out.println("Exception in adyen call is::"+ex);
		}*/
		try {
		Client client = new Client(api_key, Environment.TEST);
		Payment pay=new Payment(client);
		PaymentRequest paymentRequest=new PaymentRequest();
		paymentRequest.setCardData("4111111111111111", "John Smith", "10", "2020", "737");
		Amount amount=new Amount();
		amount.setCurrency("EUR");
		amount.setValue(1500L);
		paymentRequest.setAmount(amount);
		paymentRequest.setReference("ODR232221");
		paymentRequest.setMerchantAccount(marchentaccount);
		System.out.println("/authorise request:\n" + paymentRequest.toString());
		PaymentResult paymentResult=pay.authorise(paymentRequest);
		
		
		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String paymentsResponse1 = gson.toJson(paymentResult);
		System.out.println("/authorise response:\n" + paymentsResponse1);
		
		}catch(ApiException | IOException e) {
			System.out.println("exception from authorise  is::"+e);
		}
		return resStr;
	}
	
}
