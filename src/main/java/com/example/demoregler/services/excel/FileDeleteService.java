package com.example.demoregler.services.excel;

import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileDeleteService {

    public void deleteExistingFiles() {

        File pathTosourceFiles = new File("C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\sourceFiles\\");
        File[] sourceFiles = pathTosourceFiles.listFiles();
        assert sourceFiles != null;
        if (sourceFiles.length > 0) {
            for (File f : sourceFiles) {
                //this gives a warning "result of delete" is ignored
                //it returns boolean, but I don't need it at this moment.
                f.delete();
            }
        }

        File pathToExcelFiles = new File("C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\excelFiles\\");
        File[] excelFiles = pathToExcelFiles.listFiles();
        assert excelFiles != null;
        if (excelFiles.length > 0) {
            for (File f : excelFiles) {
                //this gives a warning "result of delete" is ignored
                //it returns boolean, but I don't need it at this moment.
                f.delete();
            }
        }
    }
}
