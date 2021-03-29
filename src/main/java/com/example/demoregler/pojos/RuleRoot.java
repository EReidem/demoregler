package com.example.demoregler.pojos;

//

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the main JSON-object in JSON-array (in the JSON-file)
 */

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RuleRoot implements FieldNames {

    private String name;
    //ruleId also exists in LinkedRules - should not exist twice in a rule
    private String ruleId;
    private String flowId;
    private int version;
    private int priority;
    private String sourceSystemId;
    private String sourceEntityType;
    private String targetSystemId;
    private String targetEntityType;
    private String targetMessageType;
    private String operationCondition;
    private List<EntityCondition> entityConditions;
    private List<ConversationCondition> conversationConditions;
    private List<DelayCondition> delayConditions;
    private String action;
    private List<Mapping> mappings;
    private List<ConversationAttributeMapping> conversationAttributeMappings;
    private AttachmentMapping attachmentMapping;
    private String changeDescription;
    private Timestamp createdTime;
    private String createdBy;
    private boolean active;
    private Timestamp activatedTime;
    private String activatedBy;
    private boolean routeOneTargetPerAttachment;
    private boolean saveAttachmentsToConversation;
    private List<VirtualAttributeMapping> virtualAttributeMappings;
    private ResponseConditions responseConditions;
    private TimeDelayCondition timeDelayCondition;
    private String useCaseType;
    private String routeMultipleTargetsBy;
    private String routeMultipleTargetDelimitedBy;

    public List<String> getFieldNames(){
        List<String> fieldNames = new ArrayList<>();
        Field[] allFields = this.getClass().getDeclaredFields();
        for (Field field : allFields) {
            fieldNames.add(StringUtils.capitalize(field.getName()));
        }
        return fieldNames;
    }
}
