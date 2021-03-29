package com.example.demoregler.services.excel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

/**
 * https://mkyong.com/spring-boot/spring-boot-file-upload-example/
 */
@Service
public class UploadedFileStoreService {

    public Path uploadFile(MultipartFile file) {

        Path returnpathToFile = null;

        if (!file.isEmpty()) {
            try {
                final String uploadedFolder = "C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\sourceFiles\\";

                String fileName = file.getOriginalFilename();
                byte[] bytes = file.getBytes();
                returnpathToFile = Paths.get(uploadedFolder + File.separator + StringUtils.cleanPath(Objects.requireNonNull(fileName)));
                Files.write(returnpathToFile, bytes);

            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return returnpathToFile;
    }




}
