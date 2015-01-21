package org.funivan.intellij.FastCoddy.CodeBuilders;

import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.TemplateItem;
import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration;

import java.util.HashMap;

/**
 * Represent one item from typed string
 *
 * User: funivan
 * Date: 12/23/13
 */
public class LocalShortcutItem {
    protected TemplateItem templateItem;
    protected Integer key;
    protected String code;
    protected HashMap<String, VariableConfiguration> variablesConfiguration;

    public LocalShortcutItem(Integer key, String code, TemplateItem templateItem) {
        this.key = key;
        this.code = code;
        this.templateItem = templateItem;
    }

    public Integer getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    public TemplateItem getTemplateItem() {
        return templateItem;
    }
}
