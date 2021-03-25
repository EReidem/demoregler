package com.example.demoregler.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class VirtualAttributeMapping {

    protected String type;
    protected List<SourceFormatter> sourceFormatters;
    protected String targetAttribute;
    protected String template;
    protected List<String> sourceAttributes;
    protected List<String> targetAttributes;
    protected List<TranslationTable> translationTable;
    protected String fallbackValue;

}
