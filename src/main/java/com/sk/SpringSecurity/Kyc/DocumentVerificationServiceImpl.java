package com.sk.SpringSecurity.Kyc;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sk.SpringSecurity.Entity.User;


@Service
public class DocumentVerificationServiceImpl implements DocumentVerificationService {

    @Override
    public boolean verifyAadhaar(User user, Map<String, String> aadhaarData) {

        String aadhaarNumber = aadhaarData.get("aadhaarNumber");
        String aadhaarDob = aadhaarData.get("dob");

        if (aadhaarNumber == null || aadhaarDob == null) {
            return false;
        }

        boolean aadhaarMatches = user.getAadhaarNumber().equals(aadhaarNumber);

        String userDob = user.getDateOfBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        boolean dobMatches = userDob.equals(aadhaarDob);

        return aadhaarMatches && dobMatches;
    }


    @Override
    public boolean verifyPan(User user, Map<String, String> panData) {

        String panDob = panData.get("dob");

        if (panDob == null) {
            return false;
        }

        String userDob = user.getDateOfBirth()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        return userDob.equals(panDob);
    }
}