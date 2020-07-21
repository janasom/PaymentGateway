package com.oracle.project.webservice;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.adyen.Client;
import com.adyen.Config;
import com.adyen.enums.Environment;
import com.adyen.httpclient.HTTPClientException;
import com.adyen.httpclient.HttpURLConnectionClient;
import com.adyen.model.Address;
import com.adyen.model.Name;
import com.adyen.model.Name.GenderEnum;
import com.adyen.model.marketpay.AccountHolderDetails;
import com.adyen.model.marketpay.CreateAccountHolderRequest;
import com.adyen.model.marketpay.CreateAccountHolderRequest.LegalEntityEnum;
import com.adyen.model.marketpay.CreateAccountHolderResponse;
import com.adyen.model.marketpay.IndividualDetails;
import com.adyen.model.marketpay.PersonalData;
import com.adyen.service.Account;
import com.adyen.service.exception.ApiException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class OnboardingService {

	
	String api_key="AQEyhmfuXNWTK0Qc+iSfgGU7q+G9QYVMA5BDWWlFw2q9l2VOjQWZZAguTS+CTjcICV6DKa8QwV1bDb7kfNy1WIxIIkxgBw==-q/edryH2AxkHKqnyvvO8aqMRgUpEE1OzBKp8py/OHWc=-?8[RzK[z5_7a&GGg";
	//String marchentaccount="OracleFinancialservicesECOM";	
	public String createAccountHolder() {
		String responsestr="";
	try {
		Client client = new Client(api_key, Environment.TEST); 
		Account account=new Account(client);
		CreateAccountHolderRequest accountHolderRequest=new CreateAccountHolderRequest();
		accountHolderRequest.setAccountHolderCode("ACC165742");
			AccountHolderDetails accountHolderDetails=new AccountHolderDetails();
			accountHolderDetails.setEmail("hello@company.com");
				IndividualDetails individualDetails=new IndividualDetails();
					Name name=new Name();
					name.setFirstName("Hello");
					name.setInfix("Mr.");
					name.setLastName("company");
					name.setGender(GenderEnum.MALE);
				individualDetails.setName(name);
					PersonalData personalData=new PersonalData();
					personalData.setDateOfBirth("22-07-1987");
					personalData.setNationality("Indian");
				individualDetails.setPersonalData(personalData);
			accountHolderDetails.setIndividualDetails(individualDetails);
				Address address=new Address();
				address.setCountry("India");
				address.setCity("Mumbai");
				address.setHouseNumberOrName("3rd floor,Building No-153, Sec-21");
				address.setStreet("Station Road");
				address.setStateOrProvince("Maharastra");
				address.setPostalCode("400028");
			accountHolderDetails.setAddress(address);
		accountHolderRequest.setAccountHolderDetails(accountHolderDetails);
		accountHolderRequest.setLegalEntity(LegalEntityEnum.INDIVIDUAL);
		
		
		
		System.out.println("/CreateAccount Request Object:\n" + accountHolderRequest.toString());
		CreateAccountHolderResponse createAccountResult=account.createAccountHolder(accountHolderRequest);
		Gson gson = new GsonBuilder().create();
		String createAccountStringified = gson.toJson(createAccountResult);
		System.out.println("/CreateAccount response:\n" + createAccountStringified);
		responsestr=createAccountStringified;
	}catch (ApiException | IOException e) {
		System.out.println("error is::"+e.toString());
	}
		return responsestr;
	}
	public String getOnboardingUrl() {
		String responsestr="";
	try {
		
		HttpURLConnectionClient httpClient=new HttpURLConnectionClient();
		String requestUrl="https://cal-test.adyen.com/cal/services/Hop/v1/getOnboardingUrl";
		Map<String,String> params=new HashMap<String,String>();
		params.put("accountHolderCode", "Acc223351");
		params.put("returnUrl", "http://152.67.2.3/");
		params.put("platformName", "orapayment.com");
		Config config=new  Config();
		config.setApiKey(api_key);
		config.setEnvironment(Environment.TEST);
		config.setUsername("admin");
		config.setPassword("Ssaheb123@2020");
		String result;
			result = httpClient.post(requestUrl, params, config);
		System.out.println("/CreateAccount response:\n" + result);
		responsestr=result;
	}catch (HTTPClientException | IOException e) {
		System.out.println("error is::"+e.toString());
	}
		return responsestr;
	}

}
