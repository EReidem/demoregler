package com.example.demoregler.controllers;

import com.example.demoregler.services.RuleService;
import com.example.demoregler.services.excel.FileDeleteService;
import com.example.demoregler.services.excel.UploadedFileStoreService;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.*;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


@Controller
public class HomeController {

    private final RuleService ruleService;
    private final UploadedFileStoreService uploadedFileStoreService;
    private final FileDeleteService fileDeleteService;

    @Autowired
    public HomeController(RuleService ruleService, UploadedFileStoreService uploadedFileStoreService, FileDeleteService fileDeleteService) {
        this.ruleService = ruleService;
        this.uploadedFileStoreService = uploadedFileStoreService;
        this.fileDeleteService = fileDeleteService;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        fileDeleteService.deleteExistingFiles();

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "Please select a file to upload");
            return "redirect:/";
        }

        Path upLoadedPath = uploadedFileStoreService.uploadFile(file);
        if(upLoadedPath != null) {
            ruleService.createExcelFiles(upLoadedPath);
            redirectAttributes.addFlashAttribute("msg",
                    "You successfully uploaded " + file.getOriginalFilename());
        }

        return "redirect:/";
    }

    // stien for sikkerheds skyld:
    // String name = "C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\excelFiles\\ JIRA .xlsx";


    /**
     * This method returns zip-file, that includes al files from directory "excelFiles"
     * Those files are generated from the uploaded file.
     * Method has a @ResponseBody, that returns Seriazible, but it also could be byte[] in this case.
     * But Serializable is more generic.
     * "produces" in GetMapping creates zip-fil, that contains excel-files.
     * TODO: files are not deleted after the download, but overwrited each time the use uploads a new json-file.
     *
     * //https://stackoverflow.com/questions/27952949/spring-rest-create-zip-file-and-send-it-to-the-client/40498539
     * @return zip-file with al excel-files
     * @throws IOException
     */

    @GetMapping(value = "/myFiles", produces ="application/zip")
    public @ResponseBody Serializable getFile() throws IOException {

        //all files from excelFiles, where to generated files are stored
        File path = new File("C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\excelFiles\\");
        File [] files = path.listFiles();

        assert files != null;
        if(files.length > 0) {

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
            ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

            for (File f : files) {
                zipOutputStream.putNextEntry(new ZipEntry(f.getName()));
                FileInputStream fileInputStream = new FileInputStream(f);
                IOUtils.copy(fileInputStream, zipOutputStream);
                fileInputStream.close();
                zipOutputStream.closeEntry();
            }
            zipOutputStream.finish();
            zipOutputStream.flush();
            IOUtils.closeQuietly(zipOutputStream);
            IOUtils.closeQuietly(bufferedOutputStream);
            IOUtils.closeQuietly(byteArrayOutputStream);

            return byteArrayOutputStream.toByteArray();
        }
        else {
            /*
            "produces" in GetMapping generates zip and it can't been changed in runtime,
            but if there are not any files in zip (user doesn't upload any file),
            the user comes to an empty page
            */
            return null;
        }
    }

    /*
    Alternative method for returning one file:
    produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    returntype: FileSystemResource
    return new FileSystemResource(path to file);
     */


}
