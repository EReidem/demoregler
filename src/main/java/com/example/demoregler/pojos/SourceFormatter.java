package com.example.demoregler.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SourceFormatter {
    protected String type;
    protected int length;
    protected String template;
    protected boolean isRegex;
    protected String replacement;
    protected String expression;
    protected String fromFormat;
    protected String fromTimeZone;
    protected String toFormat;
    protected String toTimeZone;
}
