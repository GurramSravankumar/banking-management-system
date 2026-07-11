package com.sk.SpringSecurity.Kyc;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.sk.SpringSecurity.Entity.User;
import com.sk.SpringSecurity.Entity.UserVerification;
import com.sk.SpringSecurity.service.CurrentUserService;
import com.sk.SpringSecurity.dtos.KycResponseDTO;
import com.sk.SpringSecurity.dtos.KycUploadRequestDTO;
import com.sk.SpringSecurity.enums.KycStatus;
import com.sk.SpringSecurity.repositories.UserVerificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KycServiceImp implements KycService {

    private final UserVerificationRepository userVerificationRepository;
    private final CurrentUserService currentUserService;

    private final FileStorageService fileStorageService;
    private final OcrService ocrService;
    private final AadhaarParserService aadhaarParserService;
    private final PanParserService panParserService;
    private final DocumentVerificationService documentVerificationService;


    @Override
    public KycResponseDTO uploadKyc(KycUploadRequestDTO request) {


        User user = currentUserService.getCurrentUser();


        if(user == null){

            return KycResponseDTO.builder()
                    .message("User not found")
                    .build();
        }



        UserVerification verification =
                userVerificationRepository
                        .findByUser(user)
                        .orElseThrow(()-> new RuntimeException("User not found"));


        if(verification.getKycStatus() == KycStatus.VERIFIED){
            return KycResponseDTO.builder()
                    .message("KYC already submitted")
                    .kycStatus(
                       verification.getKycStatus().name()
                    )
                    .build();
        }



        String aadhaarPath =
                fileStorageService.saveFile(
                        request.getAadhaarDocument(),
                        "aadhaar");


        String panPath =
                fileStorageService.saveFile(
                        request.getPanDocument(),
                        "pan");


        String profilePhotoPath =
                fileStorageService.saveFile(
                        request.getProfilePhoto(),
                        "profile");



        verification.setAadhaarDocumentPath(aadhaarPath);
        verification.setPanDocumentPath(panPath);
        verification.setProfilePhotoPath(profilePhotoPath);



        String aadhaarText =
                ocrService.extractText(
                        request.getAadhaarDocument()
                );


        String panText =
                ocrService.extractText(
                        request.getPanDocument()
                );



        Map<String,String> aadhaarData =
                aadhaarParserService
                        .parse(aadhaarText);


        Map<String,String> panData =
                panParserService
                        .parse(panText);



        boolean aadhaarVerified =
                documentVerificationService
                        .verifyAadhaar(
                                user,
                                aadhaarData
                        );


        boolean panVerified =
                documentVerificationService
                        .verifyPan(
                                user,
                                panData
                        );



        verification.setAadhaarVerified(aadhaarVerified);
        verification.setPanVerified(panVerified);
        
        System.out.println("==============================");

        System.out.println("AADHAAR DATA : " + aadhaarData);

        System.out.println("PAN DATA : " + panData);

        System.out.println("AADHAAR RESULT : " + aadhaarVerified);

        System.out.println("PAN RESULT : " + panVerified);

        System.out.println("==============================");



        if(aadhaarVerified && panVerified){

            verification.setKycStatus(
                    KycStatus.UNDER_REVIEW
            );

        }else{

            verification.setKycStatus(
                    KycStatus.REJECTED
            );
        }



        userVerificationRepository
                .save(verification);



        return KycResponseDTO.builder()

                .message("KYC uploaded successfully")

                .kycStatus(
                    verification
                    .getKycStatus()
                    .name()
                )

                .aadhaarUploaded(true)

                .panUploaded(true)

                .profilePhotoUploaded(true)

                .build();
    }
}
