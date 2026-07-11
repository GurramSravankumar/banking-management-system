package com.sk.SpringSecurity.Kyc;

import org.springframework.web.multipart.MultipartFile;

public interface OcrService {
	String extractText(MultipartFile file);

}
