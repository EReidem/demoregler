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

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Service
public class RuleService {

    private RuleObjectMapper ruleObjectMapper = new RuleObjectMapper();

    public void createExcelFiles(Path path) {

        try{

        String filePath = path.toString();
        RuleRoot[] ruleRoots = ruleObjectMapper.getRuleRootPOJO(filePath);

        ExcelGenerator excelGenerator = new ExcelGenerator();

        //henter alle unikke leverandørnumre fra reglenavne
        Set<Integer> unikVendorNumbers = new HashSet<>();
        for (RuleRoot ruleRoot : ruleRoots) {
            //trækkes ud fra navnet
            int vendorNumber = Integer.parseInt(extractNumber(ruleRoot.getName()));
            unikVendorNumbers.add(vendorNumber);
        }

        //denne mappe indeholder nummer som key og en tom arraylist som value
        Map<Integer, List<RuleRoot>> allRuleRoots = new TreeMap<>();
        for (Integer i : unikVendorNumbers) {
            allRuleRoots.put(i, new ArrayList<>());
        }


        //looper først alle regler, henter vendornummer fra navnet
        // og tilføjer regler til mappen, hvor key er vendornummer
        for (RuleRoot ruleRoot : ruleRoots) {
            //nummeret på reglen
            int vendorNumber = Integer.parseInt(extractNumber(ruleRoot.getName()));
            //sættes til den rigtig list, som indeholder regler, hvor key er vendorNumber
            List<RuleRoot> listWithRulesPerVendor;
            if (allRuleRoots.containsKey(vendorNumber)) {
                listWithRulesPerVendor = allRuleRoots.get(vendorNumber);
                listWithRulesPerVendor.add(ruleRoot);
            }
        }

        //alle regler er nu her i mappen.
        // Key = nummer på integration  og regler knyttte til key
        // er gemt i list af regler
        //looper en list ad gangen og laver excel-fil per leverandør

        for (List<RuleRoot> ruleRootList : allRuleRoots.values()) {

            //List<String> ruleNamesToIndex = new ArrayList<>();
            //for
            //ruleNamesToIndex.add(ruleRootList.get())


            //lave dette til en metode for sig
            String vendorName = "";
            String[] vendorNameSplit = null;
            boolean namefound = false;
            int index = 0;
            while (!namefound && index < ruleRootList.size()) {

                if (ruleRootList.get(index).getName().contains("to")) {
                    vendorNameSplit = ruleRootList.get(index).getName().split("to");
                    namefound = true;
                }else{ /* if(!ruleRootList.get(index).getName().contains("to")){
                    System.out.println("navnet " + ruleRootList.get(index).getName());*/
                    vendorName = "unknownVendor";
                    //break;
                }

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


                excelGenerator.createWorkBook(ruleRootList, vendorName);
            }
        } catch (IOException e) {
                e.printStackTrace();
            }
    }


    //https://stackoverflow.com/questions/18590901/check-if-a-string-contains-numbers-java/18590949

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
