package com.example.demoregler.services.excel;

import com.example.demoregler.pojos.EntityCondition;
import com.example.demoregler.pojos.RuleRoot;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RuleSheetGenerator {

    //static rowNumber controls the rownumber on this sheet
    public static int rowNumber = 0;
    private RuleSheetGeneratorUtilities ruleSheetGeneratorUtilities;

    /**
     * This method creates the sheet and cells in the sheet.
     * @param wb          workbook (per vendor)
     * @param sheetNumber sheetnames are numbered
     * @param rule        RuleRoot is a main POJO for JSON objekt.This method creates 1 sheet for 1 rule.
     */
    public String createSheet(XSSFWorkbook wb, int sheetNumber, RuleRoot rule) {

        XSSFSheet sheet = wb.createSheet(Integer.toString(sheetNumber));

        /*
         The sheet includes fieldnames from several classes.
         */
        List<String> ruleRootFields = rule.getFieldNames();
        List<String> attachmentFields = rule.getAttachmentMapping().getFieldNames();

        List<String> entityConditionFields = new ArrayList<>();
        if(rule.getEntityConditions() != null){
            entityConditionFields = rule.getEntityConditions().get(0).getFieldNames();}
        else {
            for(int i = 0; i < 4; i++){
                entityConditionFields.add("");
            }
        }

        /* StringBuilder's append is the same as +=
          we create a concanated String of delayConditions where line break
          separetes values from different delayConditions.
          DelayCondition has only one field called "attribute"
        */
        StringBuilder delayConditions = new StringBuilder();
        if(rule.getDelayConditions() != null) {
            for (int i = 0; i < rule.getDelayConditions().size(); i++) {
                delayConditions.append(rule.getDelayConditions().get(i).getAttribute());
                if (i != rule.getDelayConditions().size() - 1) delayConditions.append("\n");
            }
        }

        String e = "emptyCell";

        //[row][column]. Each {} is one row in excel-file
        String[][] values = {
                {e, ruleRootFields.get(0), rule.getName(), ruleRootFields.
                        get(5), "Inbound", e, "Outbound", ruleRootFields.get(7)},

                {e, ruleRootFields.get(4), Integer.toString(rule.getPriority()),
                        rule.getSourceSystemId(), "--->", "OneIO", "--->", rule.getTargetSystemId()},

                {e, ruleRootFields.get(3), Integer.toString(rule.getVersion())},

                {e, "Updated", (ruleSheetGeneratorUtilities.convertTimestamp(rule.getActivatedTime()) + " " + rule.getActivatedBy()), e, e,
                        "Delay (wait for)"},

                {e, "Attachment information", e, e, e, delayConditions.toString()},
                {e, attachmentFields.get(0), Boolean.toString(rule.getAttachmentMapping().isRemoveAttachmentsFromConversation())},
                {e, attachmentFields.get(1), Boolean.toString(rule.getAttachmentMapping().isMapAttachmentsFromSourceMessage())},
                {e, attachmentFields.get(2), Boolean.toString(rule.getAttachmentMapping().isFilterFilesWithoutExtension())},
                {e, attachmentFields.get(3), Boolean.toString(rule.getAttachmentMapping().isMapAttachmentsFromConversation())},
                {},
                {e, e, ruleRootFields.get(11)},
                {e, e, e, entityConditionFields.get(0), entityConditionFields.get(1), entityConditionFields.get(2), entityConditionFields.get(3)}
        };

        ruleSheetGeneratorUtilities.generateRows(wb, sheet,0, values);

        //Values of fields in each EntityCondition are generated one row at a time
        if(rule.getEntityConditions() != null) {
            List<EntityCondition> econditions = rule.getEntityConditions();
            List<String> ecValues = new ArrayList<>();
            for (EntityCondition ec : econditions) {
                ecValues.addAll(ec.getSelectedFields());
                ruleSheetGeneratorUtilities.generateRowFromList(wb, sheet, 3, ecValues);
                rowNumber++;
                ecValues.clear();
            }
        }

        // this creates one row: -o-o-o-o etc.
        ruleSheetGeneratorUtilities.generateRowWithPattern(sheet);

        /*
         This generates bold-cells. We have the whole row bold (row 0, 10, 11)
         and only one column bold (row 1-4)
         Nested loop: first loop is row, the second loop is columns on each row.
         */
        for(int row = 0; row < 12; row++){
            if(sheet.getRow(row) != null) {
                //row 0, 10, 11
                if (row == 0 || row > 9) {
                    for (int column = 0; column < 8; column++) {
                        if (sheet.getRow(row).getCell(column) != null) {
                            Cell celll = sheet.getRow(row).getCell(column);
                            ruleSheetGeneratorUtilities.setBold(wb, celll);
                        }
                    }
                }
                //row 1-4
                else if(row < 5){
                    if (sheet.getRow(row).getCell(1) != null) {
                        Cell cell = sheet.getRow(row).getCell(1);
                        ruleSheetGeneratorUtilities.setBold(wb, cell);
                    }
                }
            }
        }

        //default style for cells (wrap, alignment). Has to be defined after creating cells
        ruleSheetGeneratorUtilities.styleColumnWidth(sheet);

        return rule.getName();
    }

    public void createSheetIndex(XSSFSheet sheet, List<String> sheetNames){
        ruleSheetGeneratorUtilities.generateIndexRows(sheet, sheetNames);
    }

}
