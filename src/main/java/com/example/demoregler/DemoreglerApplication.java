package com.example.demoregler;

import com.example.demoregler.pojos.RuleRoot;
import com.example.demoregler.services.excel.ExcelGenerator;
import com.example.demoregler.services.mappingjsonjava.RuleObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.*;

/**
 * @author Paivi Eversbusch
 * @since 18.3.21
 * @version 1.0
 */
@SpringBootApplication
public class DemoreglerApplication {

    public static void main(String[] args){
        SpringApplication.run(DemoreglerApplication.class, args);

        //dette skal ikke kaldes her, men er første test - dette skal til controller
        RuleObjectMapper ruleObjectMapper = new RuleObjectMapper();

        try {

            String path = "C:\\C DATAMATIKER 2021\\demoregler\\src\\main\\resources\\testFiles\\SKAT enabled Routing rules Production 15.03.2021.json";
            // HVis jeg vil lave det til list:
            // List<RuleRoot> ruleRoot2 = ruleObjectMapper.getRuleRootPOJO(path);
            //denne skla jeg loope
            //json-fil = flere leverandører, per miljø. Så nummeret i navnet adskiller dem til forskellige excel-filer.
            RuleRoot[] ruleRoots = ruleObjectMapper.getRuleRootPOJO(path);

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
            for(Integer i : unikVendorNumbers){
                allRuleRoots.put(i, new ArrayList<>());
            }


            //looper først alle regler, henter vendornummer fra navnet
            // og tilføjer regler til mappen, hvor key er vendornummer
            for(RuleRoot ruleRoot : ruleRoots){
                //nummeret på reglen
                int vendorNumber = Integer.parseInt(extractNumber(ruleRoot.getName()));
                //sættes til den rigtig list, som indeholder regler, hvor key er vendorNumber
                List<RuleRoot> listWithRulesPerVendor;
                if(allRuleRoots.containsKey(vendorNumber)){
                    listWithRulesPerVendor = allRuleRoots.get(vendorNumber);
                    listWithRulesPerVendor.add(ruleRoot);
                }
            }

            //alle regler er nu her i mappen.
            // Key = nummer på integration  og regler knyttte til key
            // er gemt i list af regler
            //looper en list ad gangen og laver excel-fil per leverandør

            for(List<RuleRoot> ruleRootList : allRuleRoots.values()){

                //List<String> ruleNamesToIndex = new ArrayList<>();
                //for
                //ruleNamesToIndex.add(ruleRootList.get())

                //lave dette til en metode for sig
                String vendorName = "";
                String[] vendorNameSplit = null;
                boolean namefound = false;
                int index = 0;
                while(!namefound) {
                    if (ruleRootList.get(index).getName().contains("to")) {
                        vendorNameSplit = ruleRootList.get(index).getName().split("to");
                        namefound = true;
                    }
                    index++;
                }
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


                excelGenerator.createWorkBook(ruleRootList, vendorName);


            }

        }catch (IOException io){
            System.out.println(io);
        }

    }
    //https://stackoverflow.com/questions/18590901/check-if-a-string-contains-numbers-java/18590949
    //hvad med to cifrede, fx 11? Ret dette. TODO
    public static String extractNumber(final String str) {

        if(str == null || str.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for(char c : str.toCharArray()){
            if(Character.isDigit(c)){
                sb.append(c);
                found = true;
            } else if(found){
                // If we already found a digit before and this char is not a digit, stop looping
                break;
            }
        }

        return sb.toString();
    }

}
