package com.example.demoregler.services.mappingjsonjava;

import com.example.demoregler.pojos.RuleRoot;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * "The Jackson RuleObjectMapper class (com.fasterxml.jackson.databind.RuleObjectMapper)
 * is the simplest way to parse JSON with Jackson.
 * The Jackson RuleObjectMapper can parse JSON from a string,
 * stream or file, and create a Java object (and vice versa)"
 * http://tutorials.jenkov.com/java-json/jackson-objectmapper.html#jackson-objectmapper-example
 */

@Setter
@Getter
//@NoArgsConstructor
@AllArgsConstructor
@Service
public class RuleObjectMapper{

    /**
     * This method returns array of JSON-objects in the JSON-file that contains array of JSON-objects.
     * Each JSON-object in the array is converted to POJO called RuleRoot.
     * JSON-keys = fields in POJO; Json-values = values of fields in POJO
     * @param path
     * @return
     * @throws IOException so Exception is catched in method call
     */
    public RuleRoot[] getRuleRootPOJO(String path) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        /* If you want to return List<RuleRoot>:
           Arrays.asList(mapper.readValue(new FileInputStream(path), RuleRoot[].class));
         */
        return mapper.readValue(new FileInputStream(path), RuleRoot[].class);
    }
}
