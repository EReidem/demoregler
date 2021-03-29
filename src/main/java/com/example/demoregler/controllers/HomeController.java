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

    /**
     * This method receives an uploaded file from the html-page from the use and stores it.
     * Then it creates excelfiles from that json-file.
     * @param file uploaded filw from the HTML-page "upload"
     * @param redirectAttributes gives message about succes / error when uploading file
     * @return back to front page. Redirect empties the body (uploaded file)
     */
    @PostMapping("/uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {

        //deleting existing files, if there are any
        fileDeleteService.deleteExistingFiles();

        //this in the case of that the user clicks submit before selecting af file to upload
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("msg", "Please select a file to upload");
            return "redirect:/";
        }

        /*
          input type at HTML-page is file and it's sent to Controller with PostMapping
          UploadedFileStoreService stores it.
         */
        Path upLoadedPath = uploadedFileStoreService.uploadFile(file);

        /*
           If the file is not empty, RuleService creates excelfiles from it
           The user gets a message about succes.
         */
        if(upLoadedPath != null) {
            ruleService.createExcelFiles(upLoadedPath);
            redirectAttributes.addFlashAttribute("msg",
                    "You successfully uploaded " + file.getOriginalFilename());
        }
        return "redirect:/";
    }

    /**
     * This method returns zip-file, that includes all files from directory "excelFiles"
     * Those files we earlier (postmapping uploadFile) generated from the uploaded file.
     * Method has a @ResponseBody, that returns Seriazible, but it also could be byte[].
     * But Serializable is more generic.
     * "produces" in GetMapping creates zip-fil, that contains excel-files.
     * //https://stackoverflow.com/questions/27952949/spring-rest-create-zip-file-and-send-it-to-the-client/40498539
     * @return zip-file with al excel-files
     * @throws IOException
     */

    @GetMapping(value = "/myFiles", produces ="application/zip")
    public @ResponseBody Serializable getFile() throws IOException {

        //all files from directory "excelFiles", where generated files are stored in
        File path = new File("C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\excelFiles\\");
        File [] files = path.listFiles();

        assert files != null;
        if(files.length > 0) {

            //this creates a ZipOutputStream
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
            ZipOutputStream zipOutputStream = new ZipOutputStream(bufferedOutputStream);

            //we loop each excelfile and store it in ZipOutputStream
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
            the user comes to an empty page.
            TODO: errorhandling when the user uploades other than JSON-file
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
