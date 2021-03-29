package com.example.demoregler.services.excel;

import com.example.demoregler.pojos.RuleRoot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class creates a workbook (vendor = excel) with multiple sheets (sheet = rule)
 * Apache POI: https://www.tutorialspoint.com/apache_poi/apache_poi_quick_guide.htm
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExcelGenerator {

    private RuleSheetGenerator ruleSheetGenerator = new RuleSheetGenerator(new RuleSheetGeneratorUtilities());

    /**
     * This method creates one workbook (one vendor)
     * We loop all rules and create sheets (one sheet per rule).
     * We store rule names,so that we can create index sheet in front
     * static RuleSheetGenerator controls rownumber and has to be reset to 0 between sheets
     * The workbook is stored with vendorName as the name of the file
     * @param ruleRootList contains rules connected to one vendor
     * @throws IOException
     */
    public void createWorkBook(List<RuleRoot> ruleRootList, String vendorName) throws IOException {

        XSSFWorkbook wb = new XSSFWorkbook();
        List<String> ruleNamesToIndex = new ArrayList<>();

        for(int i = 0; i < ruleRootList.size(); i++){
            ruleSheetGenerator.createSheet(wb, i, ruleRootList.get(i));
            ruleNamesToIndex.add(ruleRootList.get(i).getName());
            RuleSheetGenerator.rowNumber = 0;
        }

        //this creates index-sheet for the workbook
        //Rulenames link to the sheet in question
        XSSFSheet sheet = wb.createSheet("Index");
        wb.setSheetOrder(sheet.getSheetName(), 0);
        ruleSheetGenerator.createSheetIndex(wb, sheet, ruleNamesToIndex);

        //var is explicit type
        final var path = "C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\files\\excelFiles\\" + vendorName + ".xlsx";
        FileOutputStream fileOut = new FileOutputStream(path);
        wb.write(fileOut);
        fileOut.close();
    }


}
