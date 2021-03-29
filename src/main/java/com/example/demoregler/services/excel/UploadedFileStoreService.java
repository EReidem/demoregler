package com.example.demoregler.services.excel;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class UploadedFileStoreService {

    /**
     * This method gets a file from the user by PotsMapping to the Controller.
     * File is uploaded to sourceFiles-directory and method returns path to it.
     * https://mkyong.com/spring-boot/spring-boot-file-upload-example/
     * @param uploadedFile from the user from GUI
     * @return path to the uploaded file
     */
    public Path uploadFile(MultipartFile uploadedFile) {

        Path pathToSourceFile = null;

        if (!uploadedFile.isEmpty()) {
            try {
                final String uploadedFolder = "C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\sourceFiles\\";

                String fileName = uploadedFile.getOriginalFilename();
                //content is stored as bytes
                byte[] bytes = uploadedFile.getBytes();
                //path is cleaned
                pathToSourceFile = Paths.get(uploadedFolder + File.separator + StringUtils.cleanPath(Objects.requireNonNull(fileName)));
                Files.write(pathToSourceFile, bytes);

            } catch(Exception e){
                e.printStackTrace();
            }
        }
        return pathToSourceFile;
    }
}
