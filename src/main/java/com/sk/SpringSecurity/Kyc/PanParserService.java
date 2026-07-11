package com.sk.SpringSecurity.Kyc;

import java.util.Map;

public interface PanParserService {
    Map<String, String> parse(String extractedText);

}
