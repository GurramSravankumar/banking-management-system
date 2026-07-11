package com.sk.SpringSecurity.Kyc;

import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageServiceImpl implements FileStorageService {

	private final String UPLOAD_DIR = "uploads";
	@Override
	public String saveFile(MultipartFile file, String folderName) {
		try {
			 
            Path folderPath = Paths.get(UPLOAD_DIR, folderName);
            Files.createDirectories(folderPath);

            
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            
            Path filePath = folderPath.resolve(fileName);

            
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            
            return filePath.toString();
		}catch (IOException e) {
            throw new RuntimeException("Failed to save file : " + e.getMessage());
        }
		
	}

}
