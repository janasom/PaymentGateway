package com.oracle.project.DTO;

import java.util.Map;




public class PaymentCaptureDto {
    
	private String originalReference;
	private String reference;
	private String merchantAccount;
	private Map<String, Object> modificationAmount;
	public String getOriginalReference() {
		return originalReference;
	}
	public void setOriginalReference(String originalReference) {
		this.originalReference = originalReference;
	}
	public String getReference() {
		return reference;
	}
	public void setReference(String reference) {
		this.reference = reference;
	}
	public String getMerchantAccount() {
		return merchantAccount;
	}
	public void setMerchantAccount(String merchantAccount) {
		this.merchantAccount = merchantAccount;
	}
	public Map<String, Object> getModificationAmount() {
		return modificationAmount;
	}
	public void setModificationAmount(Map<String, Object> modificationAmount) {
		this.modificationAmount = modificationAmount;
	}
	@Override
	public String toString() {
		return "PaymentCaptureDto [originalReference=" + originalReference + ", reference=" + reference
				+ ", merchantAccount=" + merchantAccount + ", modificationAmount=" + modificationAmount + "]";
	}
	
	
	
}
