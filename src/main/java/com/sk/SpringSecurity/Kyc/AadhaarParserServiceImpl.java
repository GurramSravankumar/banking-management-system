package com.sk.SpringSecurity.Kyc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;


@Service
public class AadhaarParserServiceImpl implements AadhaarParserService {

    @Override
    public Map<String, String> parse(String extractedText) {

        Map<String, String> data = new HashMap<>();


        

        Pattern dobPattern =
                Pattern.compile("\\d{2}/\\d{2}/\\d{4}");

        Matcher dobMatcher =
                dobPattern.matcher(extractedText);


        if (dobMatcher.find()) {

            data.put(
                    "dob",
                    dobMatcher.group()
            );
        }



        

        Pattern aadhaarPattern =
                Pattern.compile("\\b\\d{4}\\s\\d{4}\\s\\d{4}\\b");

        Matcher aadhaarMatcher =
                aadhaarPattern.matcher(extractedText);


        if (aadhaarMatcher.find()) {

            String aadhaarNumber =
                    aadhaarMatcher.group()
                            .replace(" ", "");


            data.put(
                    "aadhaarNumber",
                    aadhaarNumber
            );
        }


        return data;
    }
}