package com.sk.SpringSecurity.Kyc;

import java.util.Map;

import com.sk.SpringSecurity.Entity.User;

public interface DocumentVerificationService {
	   
	    boolean verifyAadhaar(User user, Map<String, String> aadhaarData);

	    boolean verifyPan(User user, Map<String, String> panData);
	

}
