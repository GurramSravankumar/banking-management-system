package com.sk.SpringSecurity.Kyc;

import java.util.Map;

public interface AadhaarParserService {
	Map<String,String> parse(String extractedText);

}
