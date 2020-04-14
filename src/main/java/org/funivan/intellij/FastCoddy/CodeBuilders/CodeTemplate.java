package org.funivan.intellij.FastCoddy.CodeBuilders;

import org.funivan.intellij.FastCoddy.CodeBuilders.Configuration.VariableConfiguration;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Final configuration of template
 * Contain expanded template, variables configuration and some statistic data (usedShortCodesNum and initialString)
 *
 * @author Ivan Scherbak <dev@funivan>
 */
public class CodeTemplate {

    private String initialString = "";

    private Integer usedShortCodesNum = 0;

    protected String code;

    private LinkedHashMap<String, VariableConfiguration> variablesConfiguration;

    CodeTemplate(String code, LinkedHashMap<String, VariableConfiguration> variablesConfiguration) {
        this.code = code;
        this.variablesConfiguration = variablesConfiguration;
    }

    public String getCode() {
        return code;
    }

    public Boolean isEmpty() {
        return code.isEmpty();
    }


    LinkedHashMap<String, VariableConfiguration> getVariablesConfiguration() {
        return variablesConfiguration;
    }

    public String getInitialString() {
        return initialString;
    }

    void setInitialString(String initialString) {
        this.initialString = initialString;
    }

    public Integer getUsedShortCodesNum() {
        return usedShortCodesNum;
    }

    void setUsedShortCodesNum(Integer usedShortCodesNum) {
        this.usedShortCodesNum = usedShortCodesNum;
    }
}
