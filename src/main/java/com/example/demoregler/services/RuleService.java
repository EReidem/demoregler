package com.example.demoregler.services;

import com.example.demoregler.pojos.RuleRoot;
import com.example.demoregler.services.excel.ExcelGenerator;
import com.example.demoregler.services.mappingjsonjava.RuleObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * This class creates excelfiles (sorted by vendors)
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Service
public class RuleService {

    //this represents a RuleRoot, that has converted one JSON-object to POJO
    private RuleObjectMapper ruleObjectMapper = new RuleObjectMapper();

    /**
     * This method creates all excelfiles from uploaded JSON-file
     * @param path to the uploaded json-file, that excelfiles are created from
     */
    public void createExcelFiles(Path path) {

        try{

        String filePath = path.toString();
        //array of RuleRoots from uploaded JSON-file
        RuleRoot[] ruleRoots = ruleObjectMapper.getRuleRootPOJO(filePath);

        ExcelGenerator excelGenerator = new ExcelGenerator();

        /*
           We store all unik vendornames (vendors are named by numbers in the rules,
           for instance 7 in: 7. CGI -Change - Jira to Remedy - Attachments Public)
           from all the rules in array. In that way we can store each rule in the excelfile
           with the corresponding vendorname.
           Methos exctractnumber finds the number in the name.
         */
        Set<Integer> unikVendorNumbers = new HashSet<>();
        for (RuleRoot ruleRoot : ruleRoots) {
            int vendorNumber = Integer.parseInt(extractNumber(ruleRoot.getName()));
            unikVendorNumbers.add(vendorNumber);
        }

        /*
         We create a map, where keys are vendornames (=vendornumbers) og values are
         empty arraylists af RuleRoots. Arraylists are filled later.
         */
        Map<Integer, List<RuleRoot>> allRuleRoots = new TreeMap<>();
        for (Integer i : unikVendorNumbers) {
            allRuleRoots.put(i, new ArrayList<>());
        }


        /*
          We loop all the rules from JSON-file.
          - we store the vendornumber
          - keys in map are vendornumbers and we store the rule in the correct key
            (value for each key is a arraylist of RuleRoot)
         */
        for (RuleRoot ruleRoot : ruleRoots) {
            int vendorNumber = Integer.parseInt(extractNumber(ruleRoot.getName()));
            List<RuleRoot> listWithRulesPerVendor;
            if (allRuleRoots.containsKey(vendorNumber)) {
                listWithRulesPerVendor = allRuleRoots.get(vendorNumber);
                listWithRulesPerVendor.add(ruleRoot);
            }
        }

        /*
          Now are all rules in the map sorted by the vendornumbers (keys).
          We loop each list of rules (each list belongs to certain vendor)
          and creates an excelfile per vendor.
          Each rule:
          - we exctract the vendorname from the name. We split the name by "to" (fx
          7. CGI -Change - Jira to Remedy - Attachments Public), so the name is stored
          in an array with 2 elements: before to (7. CGI -Change - Jira)
          and after to (Remedy - Attachments Public).
          The first part of this array is splitted by "-" and we store the last element
          (fx 7. CGI -Change - Jira gives Jira)  and the second part is also splitted by "-".
          We store the first element (Remedy - Attachments Public gives Remedy).
          The vendorName is the one, that is not REMEDY, SKAT or UFST.
          If the names does not contain "to", the name is unknown.
         */
        for (List<RuleRoot> ruleRootList : allRuleRoots.values()) {

            String vendorName = "";
            String[] vendorNameSplit = null;
            boolean namefound = false;
            int index = 0;
            while (!namefound && index < ruleRootList.size()) {

                if (ruleRootList.get(index).getName().contains("to")) {
                    vendorNameSplit = ruleRootList.get(index).getName().split("to");
                    namefound = true;
                }else{ vendorName = "unknownVendor";}
            index++;
            }

            if(vendorNameSplit != null && vendorNameSplit.length > 0) {
                String[] firstPart = vendorNameSplit[0].split("-");
                String name1 = firstPart[firstPart.length - 1].toUpperCase();
                String name2 = "";
                if (vendorNameSplit.length > 1) {
                    String[] lastPart = vendorNameSplit[1].split("-");
                    name2 = lastPart[0].toUpperCase();
                }
                if (name1.contains("UFST") || name1.contains("SKAT") || name1.contains("REMEDY"))
                    vendorName = name2;
                else vendorName = name1;
            }


            /*
              We create the excelfile for one vendor
              with the rules in the arraylist and the vendorName
             */
            excelGenerator.createWorkBook(ruleRootList, vendorName);
            }
        } catch (IOException e) {
                e.printStackTrace();
            }
    }


    /**
     * This method loops the vendorname og stores the first appearance of a number (1 og 2 chars)
     * Vendors are named by numbers.
     * //https://stackoverflow.com/questions/18590901/check-if-a-string-contains-numbers-java/18590949
     * @param str is vendorname, fx 7. CGI -Change - Jira to Remedy - Attachments Public
     * @return the number, we have exctracted from the name
     */

    public String extractNumber(String str) {

        if (str == null || str.isEmpty()) return "0";

        StringBuilder sb = new StringBuilder();

        char[] chars = str.toCharArray();
        for(int i = 0; i < chars.length-1; i++){
            if(Character.isDigit(chars[i])){
                sb.append(chars[i]);
                //this in case of number with two chars (for instance 20)
                if(Character.isDigit(chars[i+1])) {
                    sb.append(chars[i + 1]);
                    break;
                }
            break;}

        }
        if(sb.length() == 0) sb.append("0");
        return sb.toString();
    }


}
