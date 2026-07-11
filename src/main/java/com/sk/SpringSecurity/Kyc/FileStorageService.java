package com.sk.SpringSecurity.Kyc;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
	String saveFile(MultipartFile file , String folderName) ;

}
