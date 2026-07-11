package com.sk.SpringSecurity.Kyc;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;


@Service
public class PanParserServiceImpl implements PanParserService {

    @Override
    public Map<String, String> parse(String extractedText) {

        Map<String, String> data = new HashMap<>();


        

        Pattern panPattern =
                Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]");

        Matcher panMatcher =
                panPattern.matcher(extractedText);


        if (panMatcher.find()) {

            data.put(
                    "panNumber",
                    panMatcher.group()
            );
        }



        

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


        return data;
    }
}