package com.sk.SpringSecurity.Kyc;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

@Service
public class OcrServiceImpl implements OcrService {

    @Override
    public String extractText(MultipartFile file) {

        try {

            
            Tesseract tesseract = new Tesseract();

            
            tesseract.setDatapath("D:\\Tessoract\\tessdata");

            
            tesseract.setLanguage("eng");

            
            File tempFile = File.createTempFile("ocr_", file.getOriginalFilename());

            file.transferTo(tempFile);

            
            String extractedText = tesseract.doOCR(tempFile);
            
            System.out.println(
            		"========= OCR START ========="
            		);

            		System.out.println(extractedText);

            		System.out.println(
            		"========= OCR END ========="
            		);

            
            tempFile.delete();

            return extractedText;

        } catch (IOException | TesseractException e) {
            throw new RuntimeException("Failed to extract text from document.", e);
        }
    }
}