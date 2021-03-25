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
public class AttachmentMapping implements FieldNames{

    private boolean filterFilesWithoutExtension;
    private boolean mapAttachmentsFromConversation;
    private boolean removeAttachmentsFromConversation;
    private boolean mapAttachmentsFromSourceMessage;

    public List<String> getFieldNames(){
        List<String> fieldNames = new ArrayList<>();
        Field[] allFields = this.getClass().getDeclaredFields();
        for (Field field : allFields) {
            fieldNames.add(StringUtils.capitalize(field.getName()));

            //Modifier, because getFields() gets only public fields
            /*if (Modifier.isPrivate(field.getModifiers())) {
                FieldNames.add(StringUtils.capitalize(field.getName()));
            }*/
        }
        return fieldNames;
    }

}
