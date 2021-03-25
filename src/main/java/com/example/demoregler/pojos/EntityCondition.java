package com.example.demoregler.pojos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EntityCondition implements SelectedFields, FieldNames {

    private String type;
    private String attribute;
    private String operation;
    private String value;
    private boolean hasAttachments;

    /**
     *
     * @return names for the selected fields to excel file
     */
    public List<String> getSelectedFields(){
        List<String> fields = new ArrayList<>();
        fields.add(this.type);
        fields.add(this.attribute);
        fields.add(this.operation);
        fields.add(this.value);
        return fields;
        }
    public List<String> getFieldNames(){
        List<String> fieldNames = new ArrayList<>();
        Field[] allFields = this.getClass().getDeclaredFields();
        for (Field field : allFields) {
            fieldNames.add(StringUtils.capitalize(field.getName()));
        }
        return fieldNames;
    }
}
