package org.funivan.intellij.FastCoddy.CodeBuilders;

import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.TemplateItem;

/**
 * Represent one item from typed string
 *
 * @author Ivan Shcherbak <alotofall@gmail.com>
 */
class LocalShortcutItem {
    private TemplateItem templateItem;
    private Integer key;
    protected String code;

    LocalShortcutItem(Integer key, String code, TemplateItem templateItem) {
        this.key = key;
        this.code = code;
        this.templateItem = templateItem;
    }

    Integer getKey() {
        return key;
    }

    public String getCode() {
        return code;
    }

    TemplateItem getTemplateItem() {
        return templateItem;
    }
}
